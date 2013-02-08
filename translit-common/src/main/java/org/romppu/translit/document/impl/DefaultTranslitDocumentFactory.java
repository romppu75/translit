package org.romppu.translit.document.impl;

import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.document.TranslitDocumentFactory;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 9:07
 */
public class DefaultTranslitDocumentFactory extends TranslitDocumentFactory {

    @Override
    public TranslitDocument newTranslitDocument(TranslitDictionary dictionary) {
        return new DefaultTranslitDocument(dictionary);
    }
}
