package org.romppu.translit;

import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.dictionary.TranslitDictionaryFactory;

/**
 * Static singleton for holding an instance  of {@link TranslitDictionary]
 * created by default implementation of {@link TranslitDictionaryFactory}
 * User: roman
 * Date: 7.2.2013
 * Time: 10:00
 */
public class TranslitDictionaryHolder {

    private final TranslitDictionary dictionary;

    private TranslitDictionaryHolder() {
        TranslitDictionaryFactory factory = TranslitDictionaryFactory.newInstance();
        dictionary = factory.newTranslitDictionary();
    }

    public static TranslitDictionaryHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public TranslitDictionary getDictionary() {
        return dictionary;
    }

    private static class InstanceHolder {
        private static TranslitDictionaryHolder INSTANCE = new TranslitDictionaryHolder();
    }

}
