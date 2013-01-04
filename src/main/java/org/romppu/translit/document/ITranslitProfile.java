package org.romppu.translit.document;

/**
 * User: roman
 * Date: 4.1.2013
 * Time: 15:18
 */
public interface ITranslitProfile {

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

    public String getSeparator();
}
