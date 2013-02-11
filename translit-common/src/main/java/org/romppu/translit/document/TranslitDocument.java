package org.romppu.translit.document;

import org.romppu.translit.TranslitDocumentException;
import org.romppu.translit.dictionary.TranslitDictionary;

import java.util.ArrayList;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 8:45
 */
public abstract class TranslitDocument {

    /**
     * Returns document content as string
     *
     * @param side text will be transliterated from the specified side into an opposite side
     * @return transliterated text
     * @throws TranslitDocumentException
     */
    public abstract String getString(TranslitDictionary.Side side) throws TranslitDocumentException;

    /**
     * Returns document content as string with exclusion markers
     *
     * @param side text will be transliterated from the specified side into an opposite side
     * @return transliterated text
     * @throws TranslitDocumentException
     */
    public abstract String getMarkedString(TranslitDictionary.Side side) throws TranslitDocumentException;

    /**
     * Returns the element at the specified position
     *
     * @param pos element position
     * @return element
     * @throws TranslitDocumentException
     */
    public abstract Element getElement(int pos) throws TranslitDocumentException;

    /**
     * Returns the dictionary property
     *
     * @return dictionary
     */
    public abstract TranslitDictionary getDictionary();

    /**
     * Inserts the specified {@see text} at the specified {@see index} of the document's elements.
     * The {@see text} will be transliterated from the specified {@see side} into an opposite side.
     * The {@see index} means an element index, it is not index in a text content. To retrieve the element index by position in the content use {@link #convertToElementIndex(int, org.romppu.translit.dictionary.TranslitDictionary.Side)} method
     *
     * @param index element index
     * @param text  to insert
     * @param side  text will be transliterated from the specified side into an opposite side
     * @return result of insert operation
     * @throws TranslitDocumentException
     */
    public abstract Mutation insertAt(int index, String text, TranslitDictionary.Side side) throws TranslitDocumentException;

    /**
     * Removes the specified {@see amount} of elements from the specified {@see position} of the document.
     *
     * @param position removes from
     * @param amount   elements amount
     */
    public abstract void removeElements(int position, int amount);


    /**
     * Indicates that the element at the specified position is transliteration or not.
     * @param idx
     * @return
     */
    public abstract boolean isTranslitAt(int idx) throws TranslitDocumentException;
    /**
     * Returns document size
     *
     * @return amount of elements in the document
     */
    public abstract int getSize();

    /**
     * Converts the specified position to the element index with the specified side
     *
     * @param position position in string
     * @param side     LEFT or RIGHT
     * @return element index
     */
    public abstract int convertToElementIndex(int position, TranslitDictionary.Side side);

    /**
     * Converts the specified element index to the text position with the specified side
     * @param startIndex starts counting from the specified element index
     * @param indexToConvert index of element which will be converted to text position
     * @param side     LEFT or RIGHT
     * @return element index
     */
    public abstract int convertToTextPosition(int startIndex, int indexToConvert, TranslitDictionary.Side side);

    /**
     * Removes all elements from the document
     */
    public abstract void clear();

    /**
     * Used by the {link #insertAt} method. Contains document's changes occurred after insertion.
     */
    public static class Mutation {
        private int offset;
        private ArrayList<Element> oldElements = new ArrayList<Element>();
        private ArrayList<Element> newElements = new ArrayList<Element>();
        private final StringBuffer stringBuffer = new StringBuffer(4);
        private int leftShift;

        public ArrayList<Element> oldElements() {
            return oldElements;
        }

        protected void setOldElements(ArrayList<Element> oldElements) {
            this.oldElements = oldElements;
        }

        public int getLeftShift() {
            return leftShift;
        }

        public void setLeftShift(int leftShift) {
            this.leftShift = leftShift;
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


    public static class StringBuildingContext {

        final private TranslitDictionary.Side side;
        final private boolean markersShowed;

        public StringBuildingContext(TranslitDictionary.Side side) {
            this.side = side;
            this.markersShowed = false;
        }

        public StringBuildingContext(TranslitDictionary.Side side, boolean markersShowed) {
            this.side = side;
            this.markersShowed = markersShowed;
        }

        public TranslitDictionary.Side getSide() {
            return side;
        }

        public boolean isMarkersShowed() {
            return markersShowed;
        }
    }

    public abstract static class Element {
        public abstract String getStringValue(StringBuildingContext stringBuildingContext);
    }



}
