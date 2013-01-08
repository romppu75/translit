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
            String newString = "(jut' adskaya!)";
            int insertionIndex = document.convertToElementIndex(15, TranslitDictionary.Side.RIGHT);
            System.out.println("Inserting " + newString + " at " + insertionIndex);
            document.insertAt(insertionIndex, newString, TranslitDictionary.Side.RIGHT);
            System.out.println(document.getString(TranslitDictionary.Side.LEFT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
