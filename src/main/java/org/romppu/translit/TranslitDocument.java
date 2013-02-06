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
        return getElement(pos).getStringValue(new StringBuildingContext(true, side));
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
        return buildString(0, elements.size(), false, side);
    }

    /**
     * Returns elements as string
     * @param elementList to process
     * @param side text will be transliterated from the specified side into an opposite side
     * @return transliterated text
     * @throws TranslitDocumentException
     */
    public String getString(List<Element> elementList, TranslitDictionary.Side side) throws TranslitDocumentException {
        return buildString(elementList, false, side);
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
        Mutation result = new Mutation();
        if (index > elements.size()) {
            index = elements.size();
        }
        if (index < 0) {
            index = 0;
        }
        int longestWord = getDictionary().getLongestWordLen(side);
        result.setStartIndex(leftShift(index, longestWord));
        result.setEndIndex(rightShift(index, longestWord));
        String prevString = index - result.startIndex > 0 ? buildString(result.startIndex, index, false, side) : "";
        String nextString = result.endIndex - index > 0 ? buildString(index, result.endIndex, false, side) : "";
        removeElements(result.startIndex, result.endIndex - result.startIndex);
        ParsingContext parsingContext = parse(prevString + text + nextString, side);
        result.setElementList(parsingContext.elements());
        elements.addAll(result.startIndex, result.getElementList());
        return result;
    }

    private int leftShift(int index, int longestWord) {
        int leftIndex = index;
        while (index - leftIndex != longestWord
                && leftIndex - 1 > -1
                && (elements.size() > 0 && elements.get(leftIndex - 1) instanceof IndexElement))
            leftIndex--;

        return leftIndex;
    }

    private int rightShift(int index, int longestWord) {
        int rightIndex = index;
        while (rightIndex - index != longestWord
                && rightIndex + 1 < elements.size()
                && (elements.get(rightIndex + 1) instanceof IndexElement)) rightIndex++;

        return rightIndex;
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
        StringBuildingContext stringBuildingContext = new StringBuildingContext(true, side);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String elementValue = element.getStringValue(stringBuildingContext);
            currentPosition += elementValue.length();
            if (currentPosition >= position) return i;
        }
        return -1;
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
        StringBuildingContext stringBuildingContext = new StringBuildingContext(true, side);
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

    private String buildString(List<Element> list, boolean markersShowed, TranslitDictionary.Side side) throws TranslitDocumentException {
        StringBuildingContext stringBuildingContext = new StringBuildingContext(markersShowed, side);
        StringBuffer buf = new StringBuffer();
        for (Iterator<Element> i = list.iterator(); i.hasNext(); ) {
            Element e = i.next();
            String newChar = e.getStringValue(stringBuildingContext);
            buf.append(newChar);
        }
        return buf.toString();
    }

    protected String buildString(int start, int end, boolean markersShowed, TranslitDictionary.Side side) throws TranslitDocumentException {
        //validateElementsRange(start, end);
        return buildString(elements.subList(start, end), markersShowed, side);
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
                context.matchesVector.add(matchSet);
                Match selectedMatch = getMatchSelectionStrategy().selectMatch(context);
                context.position += selectedMatch.length();
                context.elements.add(new IndexElement(selectedMatch.index()));
            } else {
                String data = String.valueOf(part.charAt(0));
                if (data.equals(getDictionary().getExcludeMarkerBegin()) || data.equals(getDictionary().getExcludeMarkerEnd())) {
                    context.elements.add(new ExclusionMarkerElement(data.equals(getDictionary().getExcludeMarkerBegin())));
                } else {
                    context.elements.add(new DataElement(data));
                }
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
        return Collections.synchronizedSortedSet(new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Match) o1).length().compareTo(((Match) o2).length());
            }
        }));
    }

    public static class Mutation {
        private List<Element> elementList;
        private int startIndex;
        private int endIndex;

        public List<Element> getElementList() {
            return elementList;
        }

        protected void setElementList(List<Element> elementList) {
            this.elementList = elementList;
        }

        public int getStartIndex() {
            return startIndex;
        }

        protected void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        protected void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
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

        final private boolean markersShowed;

        final private TranslitDictionary.Side side;

        private boolean inExclusionBlock;

        public StringBuildingContext(boolean markersShowed, TranslitDictionary.Side side) {
            this.markersShowed = markersShowed;
            this.side = side;
        }

        public boolean isMarkersShowed() {
            return markersShowed;
        }

        public TranslitDictionary.Side getSide() {
            return side;
        }

        public boolean isInExclusionBlock() {
            return inExclusionBlock;
        }

        public void setInExclusionBlock(boolean inExclusionBlock) {
            this.inExclusionBlock = inExclusionBlock;
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
        public String getStringValue(StringBuildingContext stringBuildingContext) {
            return getDictionary().getValueAt(index,
                    stringBuildingContext.isInExclusionBlock()
                            ? stringBuildingContext.getSide().invert() : stringBuildingContext.getSide());
        }
    }

    public class DataElement extends Element {

        private String data;

        public DataElement(String data) {
            this.data = data;
        }

        @Override
        public String getStringValue(StringBuildingContext stringBuildingContext) {
            return data;
        }
    }

    public class ExclusionMarkerElement extends Element {

        private boolean isStartMarker;

        public ExclusionMarkerElement(boolean isStartMarker) {
            this.isStartMarker = isStartMarker;
        }

        @Override
        public String getStringValue(StringBuildingContext stringBuildingContext) {
            stringBuildingContext.setInExclusionBlock(isStartMarker);
            return isStartMarker ? getDictionary().getExcludeMarkerBegin() : getDictionary().getExcludeMarkerEnd();
        }
    }


}
