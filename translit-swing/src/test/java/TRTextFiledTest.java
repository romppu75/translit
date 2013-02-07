import org.romppu.translit.TranslitDictionaryHolder;
import org.romppu.translit.swing.document.TRTextField;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 7.2.2013
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class TRTextFiledTest extends JFrame {

    public TRTextFiledTest() {
        super("Press Control+P to switch input mode");
        setSize(400, 200);
        //default blue
        UIManager.getLookAndFeelDefaults().put(TRTextField.TRANSLIT_MODE_BORDER, BorderFactory.createLineBorder(Color.red));
        //default control T
        UIManager.getLookAndFeelDefaults().put(TRTextField.MODE_CHANGE_KEYSTROKE, KeyStroke.getKeyStroke("control P"));

        TRTextField field = new TRTextField();

        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(field, BorderLayout.NORTH);
        field.setDictionary(TranslitDictionaryHolder.getInstance().getDictionary());
    }

    public static void main(String... params) {
        new TRTextFiledTest().setVisible(true);
    }
}
