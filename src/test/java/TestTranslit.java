import org.romppu.translit.TranslitDictionary;
import org.romppu.translit.TranslitDocument;
import org.romppu.translit.XmlTranslitDictionary;

/**
 * User: roman
 * Date: 4.1.2013
 * Time: 15:05
 */
public class TestTranslit {

    public static void main(String... params) {
        try {
            XmlTranslitDictionary dictionary = new XmlTranslitDictionary("cyrillic_default.xml");
            TranslitDocument document = TranslitDocument.parse(dictionary, "SCH'i da kasha - pisch'a nasha!", TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
            System.out.println(document.getString(TranslitDictionary.Side.RIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
