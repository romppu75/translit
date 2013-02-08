package org.romppu.translit.document;

import org.romppu.translit.TranslitDocumentException;
import org.romppu.translit.dictionary.TranslitDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 8:45
 */
public abstract class TranslitDocument {

    public abstract String getString(TranslitDictionary.Side side) throws TranslitDocumentException;
    public abstract Element getElement(int pos) throws TranslitDocumentException;
    public abstract TranslitDictionary getDictionary();
    public abstract Mutation insertAt(int index, String text, TranslitDictionary.Side side) throws TranslitDocumentException;
    public abstract void removeElements(int position, int amount);
    public abstract int getSize();
    public abstract int convertToElementIndex(int position, TranslitDictionary.Side side);
    public abstract int convertToTextPosition(int startIndex, int indexToConvert, TranslitDictionary.Side side);

    public static class Mutation {
        private int offset;
        private ArrayList<Element> oldElements = new ArrayList<Element>();
        private ArrayList<Element> newElements = new ArrayList<Element>();
        private final StringBuffer stringBuffer = new StringBuffer(4);
        private int leftIndex;

        public ArrayList<Element> oldElements() {
            return oldElements;
        }

        protected void setOldElements(ArrayList<Element> oldElements) {
            this.oldElements = oldElements;
        }

        public int getLeftIndex() {
            return leftIndex;
        }

        public void setLeftIndex(int leftIndex) {
            this.leftIndex = leftIndex;
        }

        public StringBuffer getStringBuffer() {
            return stringBuffer;
        }

        public ArrayList<Element> newElements() {
            return newElements;
        }

        protected void setNewElements(ArrayList<Element> newElements) {
            this.newElements = newElements;
        }

        public Element lastNewElement() {
            return newElements.get(newElements.size() - 1);
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }

    public class Match {
        private int index;
        private String stringPart;

        public Match(int index, String stringPart) {
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

        public ParsingContext(String text, TranslitDictionary.Side side) {
            this.side = side;
            this.text = text;
        }

        public TranslitDocument getDocument() {
            return TranslitDocument.this;
        }

        public TranslitDictionary getDictionary() {
            return TranslitDocument.this.getDictionary();
        }

        public int getPosition() {
            return position;
        }

        public TranslitDictionary.Side getSide() {
            return side;
        }

        public List<Element> elements() {
            return elements;
        }

        public String getText() {
            return text;
        }

        public Vector<SortedSet<Match>> matches() {
            return matchesVector;
        }

        public SortedSet<Match> currentMatchSet() {
            return matchesVector.lastElement();
        }

        public void setPosition(int position) {
            this.position = position;
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
