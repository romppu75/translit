package org.romppu.translit.dictionary;

import java.util.ServiceLoader;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 8:02
 */
public abstract class TranslitDictionaryFactory {

    public static final String TRANSLIT_DICT = "TRANSLIT_DICT";

    public static TranslitDictionaryFactory newInstance() {
        try {
            String factoryClassName = System.getProperty(TranslitDictionaryFactory.class.getName());
            if (factoryClassName == null) {
                ServiceLoader<TranslitDictionaryFactory> loader = ServiceLoader.load(TranslitDictionaryFactory.class);
                if (!loader.iterator().hasNext())
                    throw new Exception("Cannot find implementation of a " + TranslitDictionaryFactory.class + ". Please specify a " + TranslitDictionaryFactory.class.getName() + " system property");
                return loader.iterator().next();
            }
            return (TranslitDictionaryFactory)Class.forName(factoryClassName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize TranslitDictionaryFactory", e);
        }
    }

    public abstract TranslitDictionary newTranslitDictionary();
    public abstract TranslitDictionary newTranslitDictionary(String params);
}
