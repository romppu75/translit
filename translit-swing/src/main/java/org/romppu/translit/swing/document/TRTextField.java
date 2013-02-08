package org.romppu.translit.swing.document;

import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.document.TranslitDocumentFactory;
import org.romppu.translit.TranslitDocumentException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * JTextField with transliteration support.
 * Transliteration mode may be switched on/off by pressing Ctrl+T
 */
public class TRTextField extends JTextField {

    public static final String TRANSLIT_MODE_BORDER = "TRANSLIT_MODE_BORDER";
    public static final String MODE_CHANGE_KEYSTROKE = "MODE_CHANGE_KEYSTROKE";
    private KeyStroke DEFAULT_KEYSTROKE = KeyStroke.getKeyStroke("control T");
    private Border DEFAULT_TRANSLIT_MODE_BORDER = BorderFactory.createLineBorder(Color.BLUE);
    private Border border;
    private KeyStroke keyStroke;
    private TranslitDictionary dictionary;
    private TranslitDocumentFilter filter = new TranslitDocumentFilter();

    public TRTextField() {
        UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
        border = uiDefaults.getBorder(TRANSLIT_MODE_BORDER);
        if (border == null) border = DEFAULT_TRANSLIT_MODE_BORDER;
        keyStroke = (KeyStroke)uiDefaults.get(MODE_CHANGE_KEYSTROKE);
        if (keyStroke == null) keyStroke = DEFAULT_KEYSTROKE;
        addPropertyChangeListener("document", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() instanceof AbstractDocument) {
                    ((AbstractDocument) evt.getNewValue()).setDocumentFilter(filter);
                }
            }
        });
        setDocument(new PlainDocument());

        registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTranslitMode(!isTranslitMode());
            }
        }, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        addPropertyChangeListener("translitMode", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (isTranslitMode()) {
                    putClientProperty("border", getBorder());
                    setBorder(border);
                } else {
                    setBorder((Border) getClientProperty("border"));
                }
                filter.setTranslitMode(isTranslitMode());
                synchronized (getTreeLock()) {
                    revalidate();
                    repaint();
                }
            }
        });

        addPropertyChangeListener("dictionary", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    String text = getText();
                    if (filter.getTranslitDocument() != null) {
                        text = filter.getTranslitDocument().getString(TranslitDictionary.Side.LEFT);
                    }
                    TranslitDocument document = TranslitDocumentFactory.newInstance().newTranslitDocument(dictionary);
                    document.insertAt(0, text, TranslitDictionary.Side.LEFT);
                    filter.setTranslitDocument(document);
                } catch (TranslitDocumentException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public TranslitDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(TranslitDictionary dictionary) {
        TranslitDictionary old = getDictionary();
        this.dictionary = dictionary;
        firePropertyChange("dictionary", old, dictionary);
    }

    public boolean isTranslitMode() {
        return filter.isTranslitMode();
    }

    public void setTranslitMode(boolean translitMode) {
        boolean old = isTranslitMode();
        filter.setTranslitMode(translitMode);
        firePropertyChange("translitMode", old, translitMode);
    }

    public TranslitDocument getTranslitDocument() {
        return filter.getTranslitDocument();
    }
}
