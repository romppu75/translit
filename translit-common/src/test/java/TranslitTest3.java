import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.dictionary.impl.XmlTranslitDictionary;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.document.TranslitDocumentFactory;
import org.romppu.translit.document.impl.DefaultTranslitDocument;

/**
 * @author RP
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
