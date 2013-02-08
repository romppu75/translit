import org.romppu.translit.document.TranslitDocumentFactory;
import org.romppu.translit.swing.document.TranslitDocumentFilter;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 7.2.2013
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class Transliterator extends JFrame {

    private JTextArea textArea = new JTextArea();
    private JToolBar toolBar = new JToolBar();
    private JToggleButton button = new JToggleButton("Translit mode");
    private TranslitDocumentFilter documentFilter = new TranslitDocumentFilter();

    public Transliterator() {
        super("Transliterator");
        setSize(400, 200);
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        toolBar.add(button);
        getContentPane().add(toolBar, BorderLayout.NORTH);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                documentFilter.setTranslitMode(button.isSelected());
            }
        });
        documentFilter.setTranslitDocument(TranslitDocumentFactory.newInstance().newTranslitDocument());
        PlainDocument document = new PlainDocument();
        document.setDocumentFilter(documentFilter);
        textArea.setDocument(document);
    }

    public static void main(String... params) {
        new Transliterator().setVisible(true);
    }
}
