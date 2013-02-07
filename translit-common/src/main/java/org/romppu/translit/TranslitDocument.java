package org.romppu.translit;

import java.text.MessageFormat;
import java.util.*;

/**
 * The TranslitDocument represents a content. Main goal of this class is a parsing of
 * a specified text with a specified dictionary and its conversion to an internal format of the TranslitDocument.
 * After parsing, the content may be retrieved from the TranslitDocument by the {@link #getString(TranslitDictionary.Side)} method.
 */
public class TranslitDocument {

    private static final String ERR_INVALID_DATA_RANGE = "Invalid data range ({0},{1}). Collection rowCount: {2}";
    private static final String ERR_INVALID_DATA_POS = "Invalid position ({0}). Position must be in range [{1}-{2}]";

    private TranslitDictionary dictionary;

    private Vector<Element> elements = new Vector();
    private MatchSelectionStrategy matchSelectionStrategy;

    /**
     * Creates a new instance of TranslitDocument with the specified {@see dictionary}
     *
     * @param dictionary translit dictionary
     */
    public TranslitDocument(TranslitDictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Parses the specified text with specified parameters and creates a new instance of the TranslitDocument.
     * The EagerMatchSelectionStrategy will be used as default selection strategy.
     *
     * @param dict translit dictionary
     * @param text to transliteration
     * @param side text will be transliterated from the specified side into an opposite side
     * @return new instance of TranslitDocument
     * @throws TranslitDocumentException
     */
    public static TranslitDocument create(
            TranslitDictionary dict,
            String text,
            TranslitDictionary.Side side)
            throws TranslitDocumentException {
        return create(dict, text, side, null);
    }

    /**
     * Parses the specified {@see text} with the specified {@see dictionary} and {@see strategy}
     * and creates a new instance of the TranslitDocument
     *
     * @param dict     TranslitDictionary
     * @param text     to transliteration
     * @param side     text will be transliterated from the specified side into an opposite side
     * @param strategy match selection strategy
     * @return new instance of TranslitDocument
     * @throws TranslitDocumentException
     */
    public static TranslitDocument create(
            TranslitDictionary dict,
            String text,
            TranslitDictionary.Side side,
            MatchSelectionStrategy strategy)
            throws TranslitDocumentException {
        TranslitDocument doc = new TranslitDocument(dict);
        doc.setMatchSelectionStrategy(strategy);
        ParsingContext parsingContext = doc.parse(text, side);
        doc.elements.addAll(parsingContext.elements());
        return doc;
    }

    /**
     * Sets the {@see matchSelectionStrategy} property value
     *
     * @param matchSelectionStrategy
     */
    private void setMatchSelectionStrategy(MatchSelectionStrategy matchSelectionStrategy) {
        this.matchSelectionStrategy = matchSelectionStrategy;
    }

    /**
     * Returns {@see matchSelectionStrategy} property, if value of the property is null then
     * creates a new instance of the EagerMatchSelectionStrategy
     *
     * @return matchSelectionStrategy property.
     */
    public MatchSelectionStrategy getMatchSelectionStrategy() {
        if (matchSelectionStrategy == null)
            matchSelectionStrategy = new EagerMatchSelectionStrategy();
        return matchSelectionStrategy;
    }

    /**
     * Returns the element at the specified position
     *
     * @param pos element position
     * @return element
     * @throws TranslitDocumentException
     */
    public Element getElement(int pos) throws TranslitDocumentException {
        validatePosition(pos);
        return elements.get(pos);
    }

    /**
     * Returns the content at the specified position in this document
     *
     * @param pos  element position
     * @param side in dictionary (left or right)
     * @return String content of element
     * @throws TranslitDocumentException
     */
    public String getElementData(int pos, TranslitDictionary.Side side) throws TranslitDocumentException {
        validatePosition(pos);
        return getElement(pos).getStringValue(new StringBuildingContext(side));
    }

    /**
     * Removes all elements
     */
    public void clear() {
        elements.removeAllElements();
    }

    /**
     * Returns document content as string
     *
     * @param side text will be transliterated from the specified side into an opposite side
     * @return transliterated text
     * @throws TranslitDocumentException
     */
    public String getString(TranslitDictionary.Side side) throws TranslitDocumentException {
        return buildString(0, elements.size(), side);
    }

    /**
     * Returns elements as string
     * @param elementList to process
     * @param side text will be transliterated from the specified side into an opposite side
     * @return transliterated text
     * @throws TranslitDocumentException
     */
    public String getString(List<Element> elementList, TranslitDictionary.Side side) throws TranslitDocumentException {
        return buildString(elementList, side);
    }

    /**
     * Inserts the specified {@see text} at the specified {@see index} of the document's elements.
     * The {@see text} will be transliterated from the specified {@see side} into an opposite side.
     * The {@see index} means an element index, it is not index in a text content. To retrieve the element index by position in the content use {@link #convertToElementIndex(int, org.romppu.translit.TranslitDictionary.Side)} method
     *
     * @param index element index
     * @param text  to insert
     * @param side  text will be transliterated from the specified side into an opposite side
     * @return result of insert operation
     * @throws TranslitDocumentException
     */
    public Mutation insertAt(int index, String text, TranslitDictionary.Side side) throws TranslitDocumentException {
        if (index > elements.size() || index < 0) {
            throw new TranslitDocumentException("Invalid index " + index);
        }
        Mutation mutation = leftShift(index, side);
        removeElements(mutation.getLeftIndex(), mutation.getOldElements().size());
        mutation.getStringBuffer().append(text);
        ParsingContext parsingContext = parse(mutation.getStringBuffer().toString(), side);
        mutation.getNewElements().addAll(parsingContext.elements());
        mutation.offset = index;
        if (mutation.getNewElements().size() - 1 < mutation.getOldElements().size()) {
            mutation.offset = mutation.offset - (mutation.getOldElements().size() - (mutation.getNewElements().size() - 1));
        }
        elements.addAll(mutation.getLeftIndex(), mutation.getNewElements());
        return mutation;
    }

    private Mutation leftShift(int index, TranslitDictionary.Side side) {
        int longestWord = getDictionary().getLongestWordLen(side);
        Mutation mutation = new Mutation();
        mutation.setLeftIndex(index);
        if (elements.size() > 0) {
            StringBuildingContext buildingContext = new StringBuildingContext(side);
            while (mutation.getStringBuffer().length() < longestWord
                    && mutation.getLeftIndex() > 0
                    && elements.get(mutation.getLeftIndex() - 1) instanceof IndexElement) {
                mutation.setLeftIndex(mutation.getLeftIndex() - 1);
                Element element = elements.get(mutation.getLeftIndex());
                mutation.getOldElements().add(0, element);
                mutation.getStringBuffer().insert(0, element.getStringValue(buildingContext));
            }
        }
        return mutation;
    }

    /**
     * Returns document size
     *
     * @return size of elements in the document
     */
    public int getSize() {
        return elements.size();
    }

    /**
     * Converts the specified position to the element index with the specified side
     *
     * @param position position in string
     * @param side     LEFT or RIGHT
     * @return element index
     */
    public int convertToElementIndex(int position, TranslitDictionary.Side side) {
        int currentPosition = 0;
        if (position == 0) return currentPosition;
        StringBuildingContext stringBuildingContext = new StringBuildingContext(side);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String elementValue = element.getStringValue(stringBuildingContext);
            currentPosition += elementValue.length();
            if (currentPosition >= position) return i;
        }
        return elements.size();
    }

    /**
     * Converts the specified element index to the text position with the specified side
     * @param startIndex starts counting from the specified element index
     * @param indexToConvert index of element which will be converted to text position
     * @param side     LEFT or RIGHT
     * @return element index
     */
    public int convertToTextPosition(int startIndex, int indexToConvert, TranslitDictionary.Side side) {
        int currentPosition = 0;
        StringBuildingContext stringBuildingContext = new StringBuildingContext(side);
        for (int i = startIndex; i < indexToConvert; i++) {
            Element element = elements.get(i);
            String elementValue = element.getStringValue(stringBuildingContext);
            currentPosition += elementValue.length();
        }
        return currentPosition;
    }


    /**
     * Removes the specified {@see amount} of elements from the specified {@see position} of the document.
     *
     * @param position removes from
     * @param amount   elements amount
     */
    public void removeElements(int position, int amount) {
        Vector<Element> toRemove = new Vector<Element>();
        for (int i = position; i < position + amount; i++) {
            toRemove.add(elements.get(i));
        }
        elements.removeAll(toRemove);
    }

    /**
     * Returns the dictionary property
     *
     * @return dictionary
     */
    public TranslitDictionary getDictionary() {
        return dictionary;
    }

    private String buildString(List<Element> list, TranslitDictionary.Side side) throws TranslitDocumentException {
        StringBuildingContext stringBuildingContext = new StringBuildingContext(side);
        StringBuffer stringBuffer = new StringBuffer();
        for (Iterator<Element> i = list.iterator(); i.hasNext(); ) {
            Element e = i.next();
            String newChar = e.getStringValue(stringBuildingContext);
            stringBuffer.append(newChar);
        }
        return stringBuffer.toString();
    }

    protected String buildString(int start, int end, TranslitDictionary.Side side) throws TranslitDocumentException {
        //validateElementsRange(start, end);
        return buildString(elements.subList(start, end), side);
    }

    private ParsingContext parse(String text, TranslitDictionary.Side side) {
        ParsingContext context = new ParsingContext(text, side);
        while (context.position < text.length()) {
            SortedSet matchSet = newSynchronizedSortedSet();
            String part = text.substring(context.position, text.length());
            for (int i = 0; i < dictionary.getSize(); i++) {
                String dictionaryValue = dictionary.getValueAt(i, side);
                if (part.startsWith(dictionaryValue)) {
                    matchSet.add(new Match(i, dictionaryValue));
                }
            }
            if (!matchSet.isEmpty()) {
                context.indexElementsCount++;
                context.matchesVector.add(matchSet);
                Match selectedMatch = getMatchSelectionStrategy().selectMatch(context);
                context.position += selectedMatch.length();
                context.elements.add(new IndexElement(selectedMatch.index()));
            } else {
                String data = String.valueOf(part.charAt(0));
                context.elements.add(new CharacterElement(data));
                context.position++;
            }
        }
        return context;
    }


    private void validatePosition(int pos) throws TranslitDocumentException {
        if (pos + 1 > elements.size() || pos < 0)
            throw new TranslitDocumentException(MessageFormat.format(ERR_INVALID_DATA_POS,
                    new Object[]{new Integer(pos), new Integer(0), new Integer(elements.size() > 0 ? elements.size() - 1 : 0)}));
    }

    private void validateElementsRange(int start, int end) throws TranslitDocumentException {
        if (start > end || end > elements.size() || start < 0 || end < 0)
            throw new TranslitDocumentException(MessageFormat.format(ERR_INVALID_DATA_RANGE,
                    new Object[]{new Integer(start), new Integer(end), new Integer(elements.size())}));
    }

    private SortedSet<Match> newSynchronizedSortedSet() {
        return new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Match) o1).length().compareTo(((Match) o2).length());
            }
        });
    }

    public static class Mutation {
        private int offset;
        private ArrayList<Element> oldElements = new ArrayList<Element>();
        private ArrayList<Element> newElements = new ArrayList<Element>();
        private final StringBuffer stringBuffer = new StringBuffer(4);
        private int leftIndex;

        public ArrayList<Element> getOldElements() {
            return oldElements;
        }

        protected void setOldElements(ArrayList<Element> oldElements) {
            this.oldElements = oldElements;
        }

        public int getLeftIndex() {
            return leftIndex;
        }

        protected void setLeftIndex(int leftIndex) {
            this.leftIndex = leftIndex;
        }

        public StringBuffer getStringBuffer() {
            return stringBuffer;
        }

        public ArrayList<Element> getNewElements() {
            return newElements;
        }

        public void setNewElements(ArrayList<Element> newElements) {
            this.newElements = newElements;
        }

        public Element lastElement() {
            return newElements.get(newElements.size() - 1);
        }

        public int getOffset() {
            return offset;
        }
    }

    public class Match {
        private int index;
        private String stringPart;

        private Match(int index, String stringPart) {
            this.index = index;
            this.stringPart = stringPart;
        }

        public int index() {
            return index;
        }

        public String getStringPart() {
            return stringPart;
        }

        public Integer length() {
            return stringPart.length();
        }

        public String toString() {
            return stringPart + ";idx=" + index;
        }
    }

    public class ParsingContext {
        private TranslitDictionary.Side side;
        private int indexElementsCount;
        private String text;
        private Vector elements = new Vector();
        private int position;
        private Vector<SortedSet<Match>> matchesVector = new Vector();

        private ParsingContext(String text, TranslitDictionary.Side side) {
            this.side = side;
            this.text = text;
        }

        public TranslitDocument getDocument() {
            return TranslitDocument.this;
        }

        public TranslitDictionary getDictionary() {
            return TranslitDocument.this.dictionary;
        }

        public int getPosition() {
            return position;
        }

        public TranslitDictionary.Side getSide() {
            return side;
        }

        public List<Element> elements() {
            return elements.subList(0, elements.size());
        }

        public String getText() {
            return text;
        }

        public Vector<SortedSet<Match>> getMatchesVector() {
            return matchesVector;
        }

        public SortedSet<Match> getCurrentMatchSet() {
            return matchesVector.lastElement();
        }
    }

    public static class StringBuildingContext {

        final private TranslitDictionary.Side side;

        public StringBuildingContext(TranslitDictionary.Side side) {
            this.side = side;
        }

        public TranslitDictionary.Side getSide() {
            return side;
        }

    }

    public abstract static class Element {
        public abstract String getStringValue(StringBuildingContext stringBuildingContext);
    }

    public class IndexElement extends Element {

        private int index;

        public IndexElement(int index) {
            this.index = index;
        }

        @Override
        public String getStringValue(StringBuildingContext buildingContext) {
            return getDictionary().getValueAt(index, buildingContext.getSide());
        }
    }

    public class CharacterElement extends Element {

        private String data;

        public CharacterElement(String data) {
            this.data = data;
        }

        @Override
        public String getStringValue(StringBuildingContext stringBuildingContext) {
            return data;
        }
    }


}
