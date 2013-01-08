package org.romppu.translit;

import java.text.MessageFormat;
import java.util.*;

/**
 * The TranslitDocument represents a content. Main goal of this class is a parsing of
 * specified text with specified dictionary and its conversion to internal format.
 * After parsing, the content may be retrieved from document by the {@link #getString(TranslitDictionary.Side)} method.
 */
public class TranslitDocument {

    private static final String ERR_INVALID_ELEMENT = "Invalid element at position {0}, type {1}";
    private static final String ERR_INVALID_DATA_RANGE = "Invalid data range ({0},{1}). Collection rowCount: {2}";
    private static final String ERR_INVALID_DATA_POS = "Invalid position ({0}). Position must be in range [{1}-{2}]";

    private TranslitDictionary dictionary;

    private Vector<Element> elements = new Vector();
    private MatchSelectionStrategy matchSelectionStrategy;

    public TranslitDocument(TranslitDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public static TranslitDocument parse(
            TranslitDictionary profile,
            String text,
            TranslitDictionary.Side side)
            throws TranslitDocumentException {
        return parse(profile, text, side, null);
    }

    /**
     * Parses of the specified {@see text} with the specified {@see dictionary}
     * and creating a new instance of TranslitDocument
     *
     * @param dict
     * @param text
     * @param side
     * @param matchSelectionStrategy
     * @return new instance of  TranslitDocument
     * @throws TranslitDocumentException
     */
    public static TranslitDocument parse(
            TranslitDictionary dict,
            String text,
            TranslitDictionary.Side side,
            MatchSelectionStrategy matchSelectionStrategy)
            throws TranslitDocumentException {
        TranslitDocument doc = new TranslitDocument(dict);
        doc.setMatchSelectionStrategy(matchSelectionStrategy);
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
     * Returns {@see matchSelectionStrategy} property, if value is null then creates new instance of EagerMatchSelectionStrategy
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
     * @param pos
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
     * @param pos
     * @param side
     * @return
     * @throws TranslitDocumentException
     */
    public String getElementData(int pos, TranslitDictionary.Side side) throws TranslitDocumentException {
        validatePosition(pos);
        return getElement(pos).buildValue(new BuildingContext(true, side));
    }

    /**
     * Removes elements from document in the specified range
     *
     * @param start
     * @param end
     * @throws TranslitDocumentException
     */
    public void remove(int start, int end) throws TranslitDocumentException {
        validateElementsRange(start, end);
        Vector<Element> elementsToRemove = new Vector();
        for (int i = start; i < end; i++) elementsToRemove.add(elements.get(i));
        elements.removeAll(elementsToRemove);
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
     * @param side
     * @return
     * @throws TranslitDocumentException
     */
    public String getString(TranslitDictionary.Side side) throws TranslitDocumentException {
        return buildString(0, elements.size(), false, side);
    }

    /**
     * Inserts the specified {@see string} at the specified {@see position} of the document. The {@see string} will be parsed with the specified {@see side}
     * The {@see position} means an element position, it is not position in text content.
     * todo test. Improve parsing. maxBack constant must be removed.
     *
     * @param position
     * @param string
     * @param side
     * @throws TranslitDocumentException
     */
    public void insertAt(int position, String string, TranslitDictionary.Side side) throws TranslitDocumentException {
        if (elements.size() == 0
                || position == 0
                || !(elements.get(position - 1 == 0 ? 0 : position - 1) instanceof IndexElement)) {
            ParsingContext context = parse(string, side);
            elements.addAll(position, context.elements());
        } else {
            int startIndex = position;
            int maxBack = 4;
            while (position - startIndex != maxBack
                    && startIndex - 1 != -1
                    && (elements.get(startIndex - 1) instanceof IndexElement)) startIndex--;
            String prevString = buildString(startIndex, position, false, side);
            removeElements(startIndex, position - startIndex);
            ParsingContext context = parse(prevString + string, side);
            elements.addAll(startIndex, context.elements());
        }
    }

    /**
     * Removes the specified {@see amount} of elements from the specified {@see position} of the document.
     * @param position
     * @param amount
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
        BuildingContext buildingContext = new BuildingContext(markersShowed, side);
        StringBuffer buf = new StringBuffer();
        for (Iterator<Element> i = list.iterator(); i.hasNext(); ) {
            Element e = i.next();
            String newChar = e.buildValue(buildingContext);
            buf.append(newChar);
        }
        return buf.toString();
    }

    protected String buildString(int start, int end, boolean markersShowed, TranslitDictionary.Side side) throws TranslitDocumentException {
        validateElementsRange(start, end);
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
            return stringPart +";idx=" + index;
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

    class BuildingContext {

        final private boolean markersShowed;

        final private TranslitDictionary.Side side;

        private boolean inExclusionBlock;

        public BuildingContext(boolean markersShowed, TranslitDictionary.Side side) {
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

    abstract class Element {
        public abstract String buildValue(BuildingContext buildingContext);
    }


    class IndexElement extends Element {

        private int index;

        public IndexElement(int index) {
            this.index = index;
        }

        @Override
        public String buildValue(BuildingContext buildingContext) {
            return getDictionary().getValueAt(index,
                    buildingContext.isInExclusionBlock()
                            ? buildingContext.getSide().invert() : buildingContext.getSide());
        }
    }

    class DataElement extends Element {

        private String data;

        public DataElement(String data) {
            this.data = data;
        }

        @Override
        public String buildValue(BuildingContext buildingContext) {
            return data;
        }
    }

    class ExclusionMarkerElement extends Element {

        private boolean isStartMarker;

        public ExclusionMarkerElement(boolean isStartMarker) {
            this.isStartMarker = isStartMarker;
        }

        @Override
        public String buildValue(BuildingContext buildingContext) {
            buildingContext.setInExclusionBlock(isStartMarker);
            return isStartMarker ? getDictionary().getExcludeMarkerBegin() : getDictionary().getExcludeMarkerEnd();
        }
    }


}
