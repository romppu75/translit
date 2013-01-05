import org.romppu.translit.document.TranslitDictionary;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.profile.XmlTranslitDictionary;

/**
 * User: roman
 * Date: 4.1.2013
 * Time: 15:05
 */
public class TestTranslit {

    public static void main(String... params) {
        try {
            XmlTranslitDictionary profile = new XmlTranslitDictionary("cyrillic_default.xml");
            TranslitDocument document = TranslitDocument.parse(profile, "SCH'i da kasha - pisch'a nasha!", TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
