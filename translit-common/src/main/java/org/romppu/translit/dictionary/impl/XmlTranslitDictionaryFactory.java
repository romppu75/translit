package org.romppu.translit.dictionary.impl;

import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.dictionary.TranslitDictionaryFactory;

import javax.xml.bind.JAXBException;
import java.text.MessageFormat;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 8:24
 */
public class XmlTranslitDictionaryFactory extends TranslitDictionaryFactory {

    private static final String DEFAULT_PATH = "/translitdict_default.xml";
    private final static String ERR_INITIALIZING = "Cannot load translit dictionary from path {0}. TRANSLIT_DICT system property or environment variable specified correctly?";

    public XmlTranslitDictionaryFactory() {
    }

    @Override
    public TranslitDictionary newTranslitDictionary() {
        String translitDictionaryPath = System.getenv(TRANSLIT_DICT);
        if (translitDictionaryPath == null) {
            translitDictionaryPath = System.getProperty(TRANSLIT_DICT);
        }
        if (translitDictionaryPath == null) {
            translitDictionaryPath = DEFAULT_PATH;
        }
        try {
            return new XmlTranslitDictionary(translitDictionaryPath);
        } catch (JAXBException e) {
            throw new RuntimeException(MessageFormat.format(ERR_INITIALIZING, translitDictionaryPath));
        }
    }
}
