package org.romppu.translit;


import org.romppu.translit.profile.TranslitProfile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 * Main goal of <code>XmlTranslitDictionary</code> is an implementing of {@link TranslitDictionary}
 * interface which is used by {@link TranslitDocument}
 * The XmlTranslitDictionary deals with xml file which is represented by {@link org.romppu.translit.profile.TranslitProfile}
 */
public class XmlTranslitDictionary implements TranslitDictionary {

    private TranslitProfile translitProfile;
    private String documentPath;

    public XmlTranslitDictionary() throws Exception {
    }

    /**
     * Constructs new instance of XmlTranslitDictionary with the specified {@see documentPath} and
     * invokes {@link #load()}
     *
     * @param documentPath
     * @throws JAXBException
     */
    public XmlTranslitDictionary(String documentPath) throws JAXBException {
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
        System.out.println("Loading profile from " + getDocumentPath());
        JAXBContext jc = JAXBContext.newInstance(TranslitProfile.class.getPackage().getName());
        Unmarshaller u = jc.createUnmarshaller();
        translitProfile = (TranslitProfile) u.unmarshal(new File(getDocumentPath()));
        System.out.println("Profile version is " + translitProfile.getVersion() + ", done.");
    }

    /**
     * Saves dictionary data to xml file
     *
     * @throws IOException
     * @throws TransformerException
     */
    public void save() throws IOException, TransformerException, JAXBException {
        System.out.println("Saving profile to " + getDocumentPath());
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
    }

    /**
     * Wrapper for {@link org.romppu.translit.profile.TranslitProfile#getExcludeMarkerBegin()}
     *
     * @return {@link TranslitProfile#excludeMarkerBegin} property value
     */
    @Override
    public String getExcludeMarkerBegin() {
        return translitProfile.getExcludeMarkerBegin();
    }

    /**
     * Wrapper of {@link TranslitProfile#setExcludeMarkerBegin(String)}
     *
     * @param newValue
     */
    public void setExcludeMarkerBegin(String newValue) {
        translitProfile.setExcludeMarkerBegin(newValue);
    }

    /**
     * Wrapper for {@link org.romppu.translit.profile.TranslitProfile#getExcludeMarkerEnd()}
     *
     * @return {@link TranslitProfile#excludeMarkerEnd} property value
     */
    @Override
    public String getExcludeMarkerEnd() {
        return translitProfile.getExcludeMarkerEnd();
    }

    /**
     * Wrapper of {@link TranslitProfile#setExcludeMarkerEnd(String)}
     *
     * @param newValue
     */
    public void setExcludeMarkerEnd(String newValue) {
        translitProfile.setExcludeMarkerEnd(newValue);
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


    /**
     * Wrapper of {@link TranslitProfile#setVersion(String)}
     *
     * @param newValue
     */
    public void setVersion(String newValue) {
        translitProfile.setVersion(newValue);
    }

}
