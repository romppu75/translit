package org.romppu.translit.document;

import java.text.MessageFormat;
import java.util.*;

/**
 * The TranslitDocument is represents a the content.
 * The main goal of this class is a parsing of a specified text content and its conversion
 * to an internal format with a certain profile.
 */
public class TranslitDocument {

    public interface DecisionMaker {
        public Match checkMatchesAndReturnMatch(ParserContext context);
    }

    enum ElementType {
        INDEX,
        SEPARATOR,
        BLOCK_BEGIN,
        BLOCK_END,
        DATA
    }

    private static final String ERR_INVALID_ELEMENT = "Invalid element at position {0}, type {1}";
    private static final String ERR_INVALID_DATA_RANGE = "Invalid data range ({0},{1}). Collection rowCount: {2}";
    private static final String ERR_INVALID_DATA_POS = "Invalid position ({0}). Position must be in range [{1}-{2}]";
    private ITranslitProfile translitProfile;

    private Vector<Element> elements = new Vector();
    private DecisionMaker decisionMaker;

    public TranslitDocument(ITranslitProfile translitProfile) {
        this.translitProfile = translitProfile;
    }

    public static TranslitDocument parse(
            ITranslitProfile profile,
            String text,
            ITranslitProfile.Side side)
            throws TranslitDocumentException {
        return parse(profile, text, side, null);
    }

    /**
     * Parses a specified text and creates an instance of TranslitDocument
     * @param profile
     * @param text
     * @param side
     * @param decisionMaker
     * @return new instance of  TranslitDocument
     * @throws TranslitDocumentException
     */
    public static TranslitDocument parse(
            ITranslitProfile profile,
            String text,
            ITranslitProfile.Side side,
            DecisionMaker decisionMaker)
            throws TranslitDocumentException {
        TranslitDocument doc = new TranslitDocument(profile);
        doc.setDecisionMaker(decisionMaker);
        ParserContext parserContext = doc.parse(text, side);
        doc.elements.addAll(parserContext.elements());
        return doc;
    }

    /**
     * Setting {@see decisionMaker} property value
     * @param decisionMaker
     */
    private void setDecisionMaker(DecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    /**
     * Getting {@see decisionMaker} property, if value is null then creates new instance of DefaultDecisionMaker
     * @return decisionMaker property.
     */
    public DecisionMaker getDecisionMaker() {
        if (decisionMaker == null)
            decisionMaker = new DefaultDecisionMaker();
        return decisionMaker;
    }

    /**
     * todo test test test
     * Inserts the specified text at the specified position
     * @param pos
     * @param text string value which must be inserted
     * @param side format of new element
     * @return
     * @throws TranslitDocumentException
     */
    public String insertAt(int pos, String text, ITranslitProfile.Side side) throws TranslitDocumentException {
        StringBuffer parseBuf = new StringBuffer();
        Vector<Element> elementsToRemove = new Vector();
        int len = 0;
        int p = pos - 1;
        parseBuf.insert(0, text);
        while (p > -1 && len < 4) {
            String elemData = getElementData(p, side);
            parseBuf.insert(0, elemData);
            len += elemData.length();
            elementsToRemove.add(elements.get(p));
            p--;
        }
        List<Element> newElements = parse(parseBuf.toString(), side).elements();
        elements.removeAll(elementsToRemove);
        elements.addAll(p + 1, newElements);
        return buildString(newElements, false, side.invert());
    }

    /**
     * Getting an element at specified position
     * @param pos
     * @return element
     * @throws TranslitDocumentException
     */
    public Element getElement(int pos) throws TranslitDocumentException {
        validatePosition(pos);
        return elements.get(pos);
    }

    /**
     * Getting content at specified position
     * @param pos
     * @param side
     * @return
     * @throws TranslitDocumentException
     */
    public String getElementData(int pos, ITranslitProfile.Side side) throws TranslitDocumentException {
        validatePosition(pos);
        Element e = getElement(pos);
        if (e.is(ElementType.INDEX))
            return translitProfile.getValueAt(e.getInt(), side);
        else
            return e.getString();
    }

    /**
     * Removing elements from document
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
     * Removing all elements
     */
    public void clear() {
        elements.removeAllElements();
    }

    /**
     * Getting current document as string in specified format (side)
     * @param side
     * @return
     * @throws TranslitDocumentException
     */
    public String getString(ITranslitProfile.Side side) throws TranslitDocumentException {
        return buildString(0, elements.size(), false, side);
    }

    /**
     * Getting translitProfile property
     * @return
     */
    public ITranslitProfile getProfile() {
        return translitProfile;
    }

    private String buildString(List<Element> list, boolean showBlocks, ITranslitProfile.Side side) throws TranslitDocumentException {
        boolean blockFlag = false;
        StringBuffer buf = new StringBuffer();
        int pos = 0;
        for (Iterator<Element> i = list.iterator(); i.hasNext();) {
            Element e = i.next();
            switch (e.getType()) {
                case INDEX:
                    buf.append(translitProfile.getValueAt(e.getInt(), decideDataType(side, blockFlag)));
                    break;
                case DATA:
                    buf.append(e.getString());
                    break;
                case SEPARATOR:
                    if (showBlocks) buf.append(e.getProfile().getSeparator());
                    break;
                case BLOCK_BEGIN:
                    if (showBlocks) buf.append(e.getProfile().getExcludeMarkerBegin());
                    blockFlag = true;
                    break;
                case BLOCK_END:
                    if (showBlocks) buf.append(e.getProfile().getExcludeMarkerEnd());
                    blockFlag = false;
                    break;
                default:
                    throw new TranslitDocumentException(MessageFormat.format(ERR_INVALID_ELEMENT,
                            new Object[]{new Integer(pos), e.getType()}));
            }
            pos++;
        }
        return buf.toString();
    }

    private ITranslitProfile.Side decideDataType(ITranslitProfile.Side side, boolean blockFlag) {
        if (!blockFlag) return side;
        return side.invert();
    }

    protected String buildString(int start, int end, boolean blocks, ITranslitProfile.Side side) throws TranslitDocumentException {
        validateElementsRange(start, end);
        return buildString(elements.subList(start, end), blocks, side);
    }

    private ParserContext parse(String text, ITranslitProfile.Side side) {
        ParserContext context = new ParserContext(text, side);
        while (context.position < text.length()) {
            SortedSet matchSet = createSortedIndexSet();
            String part = text.substring(context.position, text.length());
            for (int i = 0; i < translitProfile.getSize(); i++) {
                String data = translitProfile.getValueAt(i, side);
                if (part.startsWith(data)) {
                    matchSet.add(new Match(i, data));
                }
            }
            if (!matchSet.isEmpty()) {
                context.matchesVector.add(matchSet);
                Match decidedMatch = getDecisionMaker().checkMatchesAndReturnMatch(context);
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

    class Match {
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

    class ParserContext {
        private ITranslitProfile.Side side;
        private String text;
        private Vector elements = new Vector();
        private int position;
        private Vector<SortedSet<Match>> matchesVector = new Vector();

        private ParserContext(String text, ITranslitProfile.Side side) {
            this.side = side;
            this.text = text;
        }

        public TranslitDocument getDocument() {
            return TranslitDocument.this;
        }

        public ITranslitProfile getProfile() {
            return TranslitDocument.this.translitProfile;
        }

        public int getPosition() {
            return position;
        }

        public ITranslitProfile.Side getSide() {
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
            if (translitProfile.getSeparator().charAt(0) == data)
                this.type = ElementType.SEPARATOR;
            else if (translitProfile.getExcludeMarkerBegin().charAt(0) == data)
                this.type = ElementType.BLOCK_BEGIN;
            else if (translitProfile.getExcludeMarkerEnd().charAt(0) == data)
                this.type = ElementType.BLOCK_END;
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

        public ITranslitProfile getProfile() {
            return TranslitDocument.this.translitProfile;
        }
    }

}
