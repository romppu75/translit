import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.dictionary.impl.XmlTranslitDictionary;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.document.TranslitDocumentFactory;
import org.romppu.translit.document.impl.DefaultTranslitDocument;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 16.2.2013
 * Time: 8:05
 * To change this template use File | Settings | File Templates.
 */
public class TranslitTest3 {
    public static void main(String... params) {
        try {
            TranslitDocument document = new DefaultTranslitDocument(new XmlTranslitDictionary("/dictionary_def.xml"));
            document.insertAt(0, "SCH'i da kasha - pisch'a nasha.", TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
