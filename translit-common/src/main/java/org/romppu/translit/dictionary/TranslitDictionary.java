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

    public enum ExclusionMarker {
        START,
        END;
    }

    /**
     * Returns index of the specified string at the specified side. -1 will be returned if string doesn't exist in the dictionary.
     * @param string
     * @param side
     * @return
     */
    public int indexOf(String string, Side side);

    /**
     * Returns word at the specified index from the specified {@link Side}
     * @param idx
     * @param side
     * @return
     */
    public String getValueAt(int idx, Side side);

    /**
     * Adds new pair (right=left) to the dictionary
     * @param left
     * @param right
     */
    public void addPair(String left, String right);

    /**
     * Removes pair at the specified index
     * @param index
     */
    public void removeAt(int index );

    /**
     * Gets list of opposites values of the specified value on the specified side
     * @param value
     * @param side
     * @return
     */
    public List<String> getOppositeList(String value, Side side);

    /**
     * Returns amount of dictionary words.
      * @return
     */
    public int getSize();

    /**
     * Gets longest word on the specified side
     * @param side
     * @return
     */
    public int getLongestWordLen(Side side);

    /**
     * Gets description string of the dictionary
     * @return
     */
    public String getDescription();

    /**
     * Sets description string
     * @param description
     */
    public void setDescription(String description);

    /**
     * Returns version info
     * @return version number in format major.minor
     */
    public String getVersion();

    public String getInitialParam();

    public void save(OutputStream stream) throws Exception;

    public void load(InputStream stream) throws Exception;

    public String getFilenameExtension();

    public String getExclusionMarker(ExclusionMarker exclusionMarker);
}
