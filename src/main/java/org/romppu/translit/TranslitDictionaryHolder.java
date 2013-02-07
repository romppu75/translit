package org.romppu.translit;

import javax.xml.bind.JAXBException;
import java.text.MessageFormat;

/**
 * User: roman
 * Date: 7.2.2013
 * Time: 10:00
 */
public class TranslitDictionaryHolder {
    private final static String ERR_INITIALIZING = "Cannot load translit dictionary from path {0}. TRANSLIT_DICT system property or environment variable may be specified";
    private static final String DEFAULT_PATH = "/translitdict_default.xml";

    public static final String TRANSLIT_DICT = "TRANSLIT_DICT";
    private final XmlTranslitDictionary dictionary;

    private TranslitDictionaryHolder() {
        String translitDictionaryPath = System.getenv(TRANSLIT_DICT);
        if (translitDictionaryPath == null) {
            translitDictionaryPath = System.getProperty(TRANSLIT_DICT);
        }
        if (translitDictionaryPath == null) {
            translitDictionaryPath = DEFAULT_PATH;
        }
        try {
            dictionary = new XmlTranslitDictionary(translitDictionaryPath);
        } catch (JAXBException e) {
            throw new RuntimeException(MessageFormat.format(ERR_INITIALIZING, translitDictionaryPath));
        }
    }

    public static TranslitDictionaryHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public XmlTranslitDictionary getDictionary() {
        return dictionary;
    }

    private static class InstanceHolder {
        private static TranslitDictionaryHolder INSTANCE = new TranslitDictionaryHolder();
    }

}
