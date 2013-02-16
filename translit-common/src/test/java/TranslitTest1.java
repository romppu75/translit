import org.romppu.translit.TranslitDocumentException;
import org.romppu.translit.dictionary.TranslitDictionary;
import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.document.TranslitDocumentFactory;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 16.2.2013
 * Time: 7:53
 * To change this template use File | Settings | File Templates.
 */
public class TranslitTest1 {

    public static void main(String... params) {
        try {
            TranslitDocument document = TranslitDocumentFactory.newInstance().newTranslitDocument();
            document.insertAt(0, "SCH'i da kasha - pisch'a nasha.", TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
        } catch (TranslitDocumentException e) {
            e.printStackTrace();
        }
    }
}
