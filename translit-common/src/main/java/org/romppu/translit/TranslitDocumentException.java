package org.romppu.translit;

/**
 * This exception is designed for use by the {@link org.romppu.translit.document.TranslitDocument}
 */
public class TranslitDocumentException extends Exception {
    public TranslitDocumentException(String params) {
        super(params);
    }
}
