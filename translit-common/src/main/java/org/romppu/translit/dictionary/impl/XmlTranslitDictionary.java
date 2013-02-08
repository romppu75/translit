package org.romppu.translit.dictionary.impl;


import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.profile.TranslitProfile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Main goal of <code>XmlTranslitDictionary</code> is an implementing of {@link org.romppu.translit.dictionary.TranslitDictionary}
 * interface which is used by {@link org.romppu.translit.document.TranslitDocument}
 * The XmlTranslitDictionary deals with xml file which is represented by {@link org.romppu.translit.profile.TranslitProfile}
 */
public class XmlTranslitDictionary implements TranslitDictionary {

    private TranslitProfile translitProfile;
    private String documentPath;
    private Map<Side, Integer> longestWordLen = new Hashtable<Side, Integer>();

    public XmlTranslitDictionary()  {
        longestWordLen.put(Side.LEFT, 0);
        longestWordLen.put(Side.RIGHT, 0);
    }

    /**
     * Constructs new instance of XmlTranslitDictionary with the specified {@see documentPath} and
     * invokes {@link #load()}
     *
     * @param documentPath
     * @throws JAXBException
     */
    public XmlTranslitDictionary(String documentPath) throws JAXBException {
        this();
        setDocumentPath(documentPath);
        load();
    }

    /**
     * Returns {@see documentPath} property value.
     * The {@see documentPath} property preserve value of path to xml file.
     *
     * @return {@see documentPath} property value
     */
    public String getDocumentPath() {
        return documentPath;
    }

    /**
     * Sets value of {@see documentPath} property
     *
     * @param documentPath
     */
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    /**
     * Loads dictionary data from xml file
     *
     * @throws Exception
     */
    public void load() throws JAXBException {
        System.out.println("Loading dictionary from " + getDocumentPath());
        JAXBContext jc = JAXBContext.newInstance(TranslitProfile.class.getPackage().getName());
        Unmarshaller u = jc.createUnmarshaller();
        translitProfile = (TranslitProfile) u.unmarshal(getClass().getResourceAsStream(getDocumentPath()));
        updateLongestWordLen();
        System.out.println("Dictionary version is " + translitProfile.getVersion() + ", done.");
    }

    /**
     * Saves dictionary data to xml file
     *
     * @throws IOException
     * @throws TransformerException
     */
    public void save() throws IOException, TransformerException, JAXBException {
        System.out.println("Saving dictionary " + getDocumentPath());
        JAXBContext jc = JAXBContext.newInstance(TranslitProfile.class.getPackage().getName());
        Marshaller m = jc.createMarshaller();
        FileOutputStream os = new FileOutputStream(getDocumentPath());
        m.marshal(translitProfile, new OutputStreamWriter(os, Charset.forName("UTF8")));
    }

    /**
     * Finds pairs on the specified <code>side</code> by the specified <code>value</code>
     *
     * @param side
     * @param value
     * @return
     */
    public Vector<TranslitProfile.Pair> findOpposites(String value, Side side) {
        Vector<TranslitProfile.Pair> toReturn = new Vector<TranslitProfile.Pair>();
        for (TranslitProfile.Pair pair : translitProfile.getPair()) {
            if (side == Side.LEFT && value.equals(pair.getLeft())) {
                toReturn.add(pair);
            } else if (side == Side.RIGHT && value.equals(pair.getRight())) {
                toReturn.add(pair);
            }
        }
        return toReturn;
    }

    /**
     * Returns value at the specified position from the specified side
     *
     * @param idx
     * @param side
     * @return
     */
    public String getValueAt(int idx, Side side) {
        TranslitProfile.Pair pair = translitProfile.getPair().get(idx);
        return side == Side.LEFT ? pair.getLeft() : pair.getRight();
    }

    /**
     * Returns number of pairs
     *
     * @return number of pairs
     */
    public int getSize() {
        return translitProfile.getPair().size();
    }

    @Override
    public int getLongestWordLen(Side side) {
        return longestWordLen.get(side);
    }

    /**
     * Adds new pair to profile
     *
     * @param left
     * @param right
     */
    public void addPair(String left, String right) {
        TranslitProfile.Pair pair = new TranslitProfile.Pair();
        pair.setLeft(left);
        pair.setRight(right);
        translitProfile.getPair().add(pair);
        updateLongestWordLen();
    }

    /**
     * Wrapper of {@link org.romppu.translit.profile.TranslitProfile#getVersion()}
     *
     * @return {@link org.romppu.translit.profile.TranslitProfile#version} property value
     */
    public String getVersion() {
        if (translitProfile == null) return null;
        return translitProfile.getVersion();
    }

    @Override
    public String getInfoString() {
        return getDocumentPath();
    }


    /**
     * Wrapper of {@link TranslitProfile#setVersion(String)}
     *
     * @param newValue
     */
    public void setVersion(String newValue) {
        translitProfile.setVersion(newValue);
    }

    private void updateLongestWordLen() {
        for (TranslitProfile.Pair pair: translitProfile.getPair()) {
            if (pair.getLeft().length() > longestWordLen.get(Side.LEFT))
                longestWordLen.put(Side.LEFT, pair.getLeft().length());
            if (pair.getRight().length() > longestWordLen.get(Side.RIGHT))
                longestWordLen.put(Side.RIGHT, pair.getRight().length());
        }
    }

}
