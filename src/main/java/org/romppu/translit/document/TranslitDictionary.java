package org.romppu.translit.document;

/**
 * The TranslitDictionary interface is represents a dictionary for transliteration,
 * it defines some useful methods which are providing access to data of dictionary.
 */
public interface TranslitDictionary {

    public enum Side {
        LEFT,
        RIGHT;

        public Side invert() {
            return this.equals(LEFT) ? RIGHT : LEFT;
        }
    }

    public String getValueAt(int idx, Side side);

    public int getSize();

    public String getExcludeMarkerBegin();

    public String getExcludeMarkerEnd();
}
