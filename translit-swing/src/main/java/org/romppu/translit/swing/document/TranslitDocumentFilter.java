package org.romppu.translit.swing.document;

import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.TranslitDocumentException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author RP
 */
public class TranslitDocumentFilter extends DocumentFilter {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private TranslitDocument translitDocument;
    private boolean translitMode;

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        try {
            TranslitDictionary.Side side = isTranslitMode() ? TranslitDictionary.Side.RIGHT : TranslitDictionary.Side.LEFT;
            TranslitDocument.Mutation mutation = translitDocument.insertAt(offset, text, side);
            fb.replace(mutation.getOffset(), length + (offset - mutation.getOffset()),
                    mutation.lastNewElement().getStringValue(new TranslitDocument.StringBuildingContext(TranslitDictionary.Side.LEFT)),
                    attrs);
        } catch (TranslitDocumentException e) {
            throw new BadLocationException(text, offset);
        }
    }

    public void remove(FilterBypass fb, int offset, int length) throws
            BadLocationException {

        translitDocument.removeElements(offset, length);
        fb.remove(offset, length);
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
}
