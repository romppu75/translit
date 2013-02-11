package org.romppu.translit.swing.document;

import org.romppu.translit.TranslitDocumentException;
import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.document.TranslitDocument;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 11.2.2013
 * Time: 19:05
 * To change this template use File | Settings | File Templates.
 */
public class TranslitStyledDocument extends DefaultStyledDocument {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private TranslitDocument translitDocument;
    private boolean translitMode;
    private Color translitForeground;
    private Color textForeground;

    public void remove(int offs, int len) throws BadLocationException {
        translitDocument.removeElements(offs, len);
        super.remove(offs, len);
    }

    public void insertString(int offset, String str, AttributeSet attrs) throws BadLocationException {
        try {
            TranslitDictionary.Side side = isTranslitMode() ? TranslitDictionary.Side.RIGHT : TranslitDictionary.Side.LEFT;
            TranslitDocument.Mutation mutation = getTranslitDocument().insertAt(offset, str, side);
            int removed = (offset - mutation.getOffset());
            if (removed > 0) {
                super.remove(mutation.getOffset(), removed);
            }
            if (!mutation.lastNewElement().isTransliteration()) {
                StyleConstants.setForeground((MutableAttributeSet)attrs, getTextForeground());
            } else {
                StyleConstants.setForeground((MutableAttributeSet)attrs, getTranslitForeground());
            }
            super.insertString(mutation.getOffset(),
                    mutation.lastNewElement().getStringValue(new TranslitDocument.StringBuildingContext(TranslitDictionary.Side.LEFT)),
                    attrs);
        } catch (TranslitDocumentException e) {
            throw new BadLocationException(str, offset);
        }
    }

    public TranslitDocument getTranslitDocument() {
        return translitDocument;
    }

    public void setTranslitDocument(TranslitDocument translitDocument) {
        TranslitDocument old = getTranslitDocument();
        this.translitDocument = translitDocument;
        changeSupport.firePropertyChange("translitDocument", old, translitDocument);
    }

    public boolean isTranslitMode() {
        return translitMode;
    }

    public void setTranslitMode(boolean translitMode) {
        boolean old = isTranslitMode();
        this.translitMode = translitMode;
        changeSupport.firePropertyChange("translitMode", old, translitMode);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(prop, listener);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(prop, listener);
    }

    public Color getTranslitForeground() {
        return translitForeground;
    }

    public void setTranslitForeground(Color translitForeground) {
        this.translitForeground = translitForeground;
    }

    public Color getTextForeground() {
        return textForeground;
    }

    public void setTextForeground(Color textForeground) {
        this.textForeground = textForeground;
    }
}
