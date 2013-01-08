package org.romppu.translit;

/**
 * The TranslitDictionary interface is representing a dictionary for transliteration,
 * it defines some useful methods which are providing access to data of dictionary.
 */
public interface TranslitDictionary {

    /**
     * <p>A=Z</p>
     * <p>A on the left side</p>
     * <p>Z on the right side</p>
     * <p>Character A will be transliterated to Z</p>
     */
    public enum Side {
        LEFT,
        RIGHT;

        public Side invert() {
            return this.equals(LEFT) ? RIGHT : LEFT;
        }
    }

    /**
     * Returns word at the specified index at the specified {@link Side}
     * @param idx
     * @param side
     * @return
     */
    public String getValueAt(int idx, Side side);

    /**
     * Returns amount of dictionary words.
      * @return
     */
    public int getSize();

    public String getExcludeMarkerBegin();

    public String getExcludeMarkerEnd();
}