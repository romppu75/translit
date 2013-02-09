package org.romppu.translit.dictionary.impl;

import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.dictionary.TranslitDictionaryFactory;

import java.text.MessageFormat;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 8:24
 */
public class XmlTranslitDictionaryFactory extends TranslitDictionaryFactory {

    private static final String DEFAULT_PATH = "/dictionary_ru_def.xml";
    private final static String ERR_INITIALIZING = "Cannot load translit dictionary from path {0}";

    @Override
    public TranslitDictionary newTranslitDictionary() {
        String translitDictionaryPath = System.getenv(TRANSLIT_DICT);
        if (translitDictionaryPath == null) {
            translitDictionaryPath = System.getProperty(TRANSLIT_DICT);
        }
        if (translitDictionaryPath == null) {
            translitDictionaryPath = DEFAULT_PATH;
        }
        return newTranslitDictionary(translitDictionaryPath);
    }

    @Override
    public TranslitDictionary newTranslitDictionary(String params) {
        try {
            return new XmlTranslitDictionary(params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(MessageFormat.format(ERR_INITIALIZING, params));
        }
    }
}
