package org.romppu.translit;

import org.romppu.translit.profile.TranslitProfile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;

/**
 * User: roman
 * Date: 4.1.2013
 * Time: 13:47
 */
public class Text2XmlProfile {

    public static void main(String... params) {
        if (params.length == 0) {
            System.out.println("Usage: Text2XmlProfile filename ");
            System.exit(-1);
        }
        File file = new File(params[0]);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("File " + params[0] + " not found!");
            System.exit(-1);
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            TranslitProfile profile = new TranslitProfile();
            profile.setName(params[0].substring(0, params[0].lastIndexOf(".")));
            profile.setLeftDescription("cyrillic");
            profile.setRightDescription("latin");
            String line = null;
            while ((line = reader.readLine()) != null) {
                TranslitProfile.Pair pair = new TranslitProfile.Pair();
                String[] pairs = line.split("=");
                pair.setLeft(pairs[0].trim());
                pair.setRight(pairs[1].trim());
                profile.getPair().add(pair);
            }
            JAXBContext jc = JAXBContext.newInstance("org.romppu.translit.profile");
            Marshaller m = jc.createMarshaller();
            FileOutputStream os = new FileOutputStream( profile.getName() + ".xml");
            m.marshal( profile, new OutputStreamWriter( os, Charset.forName("UTF8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
