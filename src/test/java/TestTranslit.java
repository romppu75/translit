import org.romppu.translit.document.ITranslitProfile;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.profile.XmlTranslitProfile;

/**
 * User: roman
 * Date: 4.1.2013
 * Time: 15:05
 */
public class TestTranslit {

    public static void main(String... params) {
        try {
            XmlTranslitProfile profile = new XmlTranslitProfile("cyrillic_default.xml");
            TranslitDocument document = TranslitDocument.parse(profile, "SCH'i da kasha - pisch'a nasha!", ITranslitProfile.Side.RIGHT);
            System.out.println(document.getString(ITranslitProfile.Side.LEFT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
