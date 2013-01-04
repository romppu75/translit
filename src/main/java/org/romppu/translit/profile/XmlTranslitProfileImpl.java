package org.romppu.translit.profile;


import org.romppu.translit.document.ITranslitProfile;

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
 * Created by IntelliJ IDEA.
 * User: roman
 * Date: 26.10.2011
 * Time: 8:16
 * To change this template use File | Settings | File Templates.
 */
public class XmlTranslitProfileImpl implements ITranslitProfile {

    private TranslitProfile translitProfile;
    private String documentPath;

    public XmlTranslitProfileImpl() throws Exception {
    }

    public XmlTranslitProfileImpl(String documentPath) throws JAXBException {
        setDocumentPath(documentPath);
        load();
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    /**
     * Loading profile from xml file
     * @throws Exception
     */
    public void load() throws JAXBException {
        System.out.println("Loading profile from " + getDocumentPath());
        JAXBContext jc = JAXBContext.newInstance( getClass().getPackage().getName() );
        Unmarshaller u = jc.createUnmarshaller();
        translitProfile = (TranslitProfile)u.unmarshal( new File(getDocumentPath()) );
        System.out.println("Profile version is " + translitProfile.getVersion() + ", done.");
    }

    /**
     * Saving profile to xml file
     * @throws IOException
     * @throws TransformerException
     */
    public void save() throws IOException, TransformerException, JAXBException {
        System.out.println("Saving profile to " + getDocumentPath());
        JAXBContext jc = JAXBContext.newInstance(getClass().getPackage().getName());
        Marshaller m = jc.createMarshaller();
        FileOutputStream os = new FileOutputStream(getDocumentPath());
        m.marshal( translitProfile, new OutputStreamWriter( os, Charset.forName("UTF8")));
    }

    /**
     * Finding pairs on defined <code>side</code> by specified <code>value</code>
     * @param side
     * @param value
     * @return
     */
    public Vector<TranslitProfile.Pair> findOpposites(String value, Side side) {
        Vector<TranslitProfile.Pair> toReturn = new Vector<TranslitProfile.Pair>();
        for (TranslitProfile.Pair pair: translitProfile.getPair()) {
            if (side == Side.LEFT && value.equals(pair.getLeft())) {
                toReturn.add(pair);
            } else if (side == Side.RIGHT && value.equals(pair.getRight())) {
                toReturn.add(pair);
            }
        }
        return toReturn;
    }

    public String getValueAt(int idx, Side side) {
        TranslitProfile.Pair pair = translitProfile.getPair().get(idx);
        return side == Side.LEFT ? pair.getLeft() : pair.getRight();
    }
    /**
     * Returns number of pairs
     * @return number of pairs
     */
    public int getSize() {
        return translitProfile.getPair().size();
    }

    /**
     * Adding new pair to profile
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
     * @return
     */
    public String getExcludeMarkerBegin() {
        return translitProfile.getExcludeMarkerBegin();
    }

    /**
     * Wrapper of {@link TranslitProfile#setExcludeMarkerBegin(String)}
     * @param newValue
     */
    public void setExcludeMarkerBegin(String newValue) {
        translitProfile.setExcludeMarkerBegin(newValue);
    }

    /**
     * Wrapper for {@link org.romppu.translit.profile.TranslitProfile#getExcludeMarkerEnd()}
     * @return
     */
    public String getExcludeMarkerEnd() {
        return translitProfile.getExcludeMarkerEnd();
    }

    /**
     * Wrapper of {@link TranslitProfile#setExcludeMarkerEnd(String)}
     * @param newValue
     */
    public void setExcludeMarkerEnd(String newValue) {
        translitProfile.setExcludeMarkerEnd(newValue);
    }
    /**
     * Wrapper of {@link org.romppu.translit.profile.TranslitProfile#getVersion()}
     * @return
     */
    public String getVersion() {
        if (translitProfile == null) return null;
        return translitProfile.getVersion();
    }


    /**
     * Wrapper of {@link TranslitProfile#setVersion(String)}
     * @param newValue
     */
    public void setVersion(String newValue) {
        translitProfile.setVersion(newValue);
    }

    /**
     * Wrapper of {@link org.romppu.translit.profile.TranslitProfile#getSeparator()}
     * @return
     */
    public String getSeparator() {
        return translitProfile.getSeparator();
    }

    /**
     * Wrapper of {@link TranslitProfile#setSeparator(String)}
     * @param separator
     */
    public void setSeparator(String separator) {
        translitProfile.setSeparator(separator);
    }

}
