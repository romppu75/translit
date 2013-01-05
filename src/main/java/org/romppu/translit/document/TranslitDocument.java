package org.romppu.translit.document;

import java.text.MessageFormat;
import java.util.*;

/**
 * The TranslitDocument represents a content. Main goal of this class is a parsing of
 * specified text with specified dictionary and its conversion to internal format.
 * After parsing, the content may be retrieved from document by the {@link #getString(org.romppu.translit.document.TranslitDictionary.Side)} method.
 */
public class TranslitDocument {

    enum ElementType {
        INDEX,
        EXCLUDE_START,
        EXCLUDE_END,
        DATA
    }

    private static final String ERR_INVALID_ELEMENT = "Invalid element at position {0}, type {1}";
    private static final String ERR_INVALID_DATA_RANGE = "Invalid data range ({0},{1}). Collection rowCount: {2}";
    private static final String ERR_INVALID_DATA_POS = "Invalid position ({0}). Position must be in range [{1}-{2}]";

    private TranslitDictionary dictionary;

    private Vector<Element> elements = new Vector();
    private MatchSelector matchSelector;

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
     * Parsing of specified text and creating a new instance of TranslitDocument
     *
     * @param profile
     * @param text
     * @param side
     * @param matchSelector
     * @return new instance of  TranslitDocument
     * @throws TranslitDocumentException
     */
    public static TranslitDocument parse(
            TranslitDictionary profile,
            String text,
            TranslitDictionary.Side side,
            MatchSelector matchSelector)
            throws TranslitDocumentException {
        TranslitDocument doc = new TranslitDocument(profile);
        doc.setMatchSelector(matchSelector);
        ParsingContext parsingContext = doc.parse(text, side);
        doc.elements.addAll(parsingContext.elements());
        return doc;
    }

    /**
     * Sets the {@see matchSelector} property value
     *
     * @param matchSelector
     */
    private void setMatchSelector(MatchSelector matchSelector) {
        this.matchSelector = matchSelector;
    }

    /**
     * Returns {@see matchSelector} property, if value is null then creates new instance of EagerMatchSelector
     *
     * @return matchSelector property.
     */
    public MatchSelector getMatchSelector() {
        if (matchSelector == null)
            matchSelector = new EagerMatchSelector();
        return matchSelector;
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
        Element e = getElement(pos);
        if (e.is(ElementType.INDEX))
            return dictionary.getValueAt(e.getInt(), side);
        else
            return e.getString();
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
     * Returns the dictionary property
     *
     * @return dictionary
     */
    public TranslitDictionary getDictionary() {
        return dictionary;
    }

    private String buildString(List<Element> list, boolean showBlocks, TranslitDictionary.Side side) throws TranslitDocumentException {
        boolean inExclusionBlock = false;
        StringBuffer buf = new StringBuffer();
        int pos = 0;
        for (Iterator<Element> i = list.iterator(); i.hasNext(); ) {
            Element e = i.next();
            switch (e.getType()) {
                case INDEX:
                    buf.append(dictionary.getValueAt(e.getInt(), inExclusionBlock?side.invert():side));
                    break;
                case DATA:
                    buf.append(e.getString());
                    break;
                case EXCLUDE_START:
                    if (showBlocks) buf.append(e.getDictionary().getExcludeMarkerBegin());
                    inExclusionBlock = true;
                    break;
                case EXCLUDE_END:
                    if (showBlocks) buf.append(e.getDictionary().getExcludeMarkerEnd());
                    inExclusionBlock = false;
                    break;
                default:
                    throw new TranslitDocumentException(MessageFormat.format(ERR_INVALID_ELEMENT,
                            new Object[]{new Integer(pos), e.getType()}));
            }
            pos++;
        }
        return buf.toString();
    }

    protected String buildString(int start, int end, boolean blocks, TranslitDictionary.Side side) throws TranslitDocumentException {
        validateElementsRange(start, end);
        return buildString(elements.subList(start, end), blocks, side);
    }

    private ParsingContext parse(String text, TranslitDictionary.Side side) {
        ParsingContext context = new ParsingContext(text, side);
        while (context.position < text.length()) {
            SortedSet matchSet = createSortedIndexSet();
            String part = text.substring(context.position, text.length());
            for (int i = 0; i < dictionary.getSize(); i++) {
                String data = dictionary.getValueAt(i, side);
                if (part.startsWith(data)) {
                    matchSet.add(new Match(i, data));
                }
            }
            if (!matchSet.isEmpty()) {
                context.matchesVector.add(matchSet);
                Match decidedMatch = getMatchSelector().selectMatch(context);
                context.position += decidedMatch.length();
                context.elements.add(new Element(decidedMatch.index()));
            } else {
                context.elements.add(new Element(part.charAt(0)));
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

    private SortedSet<Match> createSortedIndexSet() {
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

    class Element {
        private final ElementType type;
        private Object value;

        public Element(char data) {
            this.value = data;
            if (dictionary.getExcludeMarkerBegin().charAt(0) == data)
                this.type = ElementType.EXCLUDE_START;
            else if (dictionary.getExcludeMarkerEnd().charAt(0) == data)
                this.type = ElementType.EXCLUDE_END;
            else
                this.type = ElementType.DATA;
        }

        public Element(String data) {
            this.value = data;
            this.type = ElementType.DATA;
        }

        public Element(int idx) {
            this.value = idx;
            this.type = ElementType.INDEX;
        }

        public Element(ElementType type) {
            this.type = type;
        }

        public boolean is(ElementType type) {
            return this.type.equals(type);
        }

        public ElementType getType() {
            return this.type;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getInt() {
            return ((Integer) value).intValue();
        }

        public String getString() {
            if (value instanceof Character) return String.valueOf(value);
            else return (String) value;
        }

        public TranslitDocument getDocument() {
            return TranslitDocument.this;
        }

        public TranslitDictionary getDictionary() {
            return TranslitDocument.this.dictionary;
        }
    }

}
