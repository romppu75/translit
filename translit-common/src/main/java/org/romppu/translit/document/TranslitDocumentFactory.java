package org.romppu.translit.document;

import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.dictionary.TranslitDictionaryFactory;

import java.util.ServiceLoader;

/**
 * User: roman
 * Date: 8.2.2013
 * Time: 9:05
 */
public abstract class TranslitDocumentFactory {

    public static TranslitDocumentFactory newInstance() {
        try {
            String factoryClassName = System.getProperty(TranslitDocumentFactory.class.getName());
            if (factoryClassName == null) {
                ServiceLoader<TranslitDocumentFactory> loader = ServiceLoader.load(TranslitDocumentFactory.class);
                if (!loader.iterator().hasNext())
                    throw new Exception("Cannot find implementation of a " + TranslitDocumentFactory.class + ". Please specify a " + TranslitDocumentFactory.class.getName() + " system property");
                return loader.iterator().next();
            }
            return (TranslitDocumentFactory)Class.forName(factoryClassName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize TranslitDocumentFactory", e);
        }
    }

    public TranslitDocument newTranslitDocument() {
        return newTranslitDocument(TranslitDictionaryFactory.newInstance().newTranslitDictionary());
    }

    public abstract TranslitDocument newTranslitDocument(TranslitDictionary dictionary);
}
