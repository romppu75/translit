package org.romppu.translit.dictionary;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The TranslitDictionary interface is representing a dictionary for transliteration,
 * it defines some useful methods which are providing access to data of the dictionary.
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
     * Returns word at the specified index from the specified {@link Side}
     * @param idx
     * @param side
     * @return
     */
    public String getValueAt(int idx, Side side);

    public void addPair(String left, String right);

    public void removeAt(int idx);

    public List<String> getOppositeList(String value, Side side);

    /**
     * Returns amount of dictionary words.
      * @return
     */
    public int getSize();

    public int getLongestWordLen(Side side);

    /**
     * Returns version info
     * @return version number in format major.minor
     */
    public String getVersion();

    public String getInfoString();

    public void save(OutputStream stream) throws Exception;

    public void load(InputStream stream) throws Exception;

    public String getFilenameExtension();
}
