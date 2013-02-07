import org.romppu.translit.TranslitDictionary;
import org.romppu.translit.TranslitDictionaryHolder;
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
            String originText = "SCH'i - pisch'a nasha!";
            System.out.println("Transliterate text:" + originText);
            TranslitDocument document = TranslitDocument.create(TranslitDictionaryHolder.getInstance().getDictionary(), "SCH'i - pisch'a nasha!", TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
            String newString = " da kasha";
            int insertionIndex = document.convertToElementIndex(6, TranslitDictionary.Side.RIGHT);
            System.out.println("Inserting " + newString + " at " + insertionIndex);
            document.insertAt(insertionIndex, newString, TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
