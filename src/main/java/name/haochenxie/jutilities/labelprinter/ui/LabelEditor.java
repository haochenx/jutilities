package name.haochenxie.jutilities.labelprinter.ui;

import name.haochenxie.jutilities.labelprinter.LabelPrintable;
import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStream;

import static name.haochenxie.jutilities.labelprinter.LabelPrintable.translate;

public class LabelEditor extends JFrame {

    private static String sampleCode;

    private double labelWidth = translate(38.1);

    private double labelHeight = translate(21.2);

    private double scale = 2;

    private LabelView lvLabel;

    private RSyntaxTextArea txtCode;

    private String code = sampleCode;

    static {
        try {
            InputStream stream = LabelEditor.class.getResourceAsStream("../sample.js");
            String code = IOUtils.toString(stream);
            sampleCode = code;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private LabelPrintable label = (area, g) -> {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");

            engine.eval(code);
            ((Invocable) engine).invokeFunction("drawLabel", area, g);
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }

        return true;
    };

    public LabelEditor() throws HeadlessException {
        super();
        initUi();
    }

    public void initUi() {
        setLayout(new BorderLayout());

        JPanel previewPane = new JPanel();
        JPanel codePane = new JPanel();

        add(previewPane, BorderLayout.NORTH);
        add(codePane, BorderLayout.CENTER);

        { // for imagePane
            previewPane.setBorder(BorderFactory.createTitledBorder(String.format("Label preview (%.2fx)", scale)));
            previewPane.setLayout(new BorderLayout());

            lvLabel = new LabelView(label, labelWidth, labelHeight, scale);
            previewPane.add(lvLabel, BorderLayout.CENTER);
        }

        { // for codePane
            codePane.setBorder(BorderFactory.createTitledBorder("Label Drawing Code (JavaScript)"));
            codePane.setLayout(new BorderLayout());

            {
                txtCode = new RSyntaxTextArea(40, 72);
                txtCode.setText(code);

                txtCode.setFont(new Font("Consolas", Font.PLAIN, 16));
                txtCode.setHighlightCurrentLine(false);
                txtCode.setCodeFoldingEnabled(true);
                txtCode.setTabsEmulated(true);
                txtCode.setTabSize(2);
//                txtCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);

                RTextScrollPane scrollPane = new RTextScrollPane(txtCode);
                scrollPane.setLineNumbersEnabled(true);
                codePane.add(scrollPane, BorderLayout.CENTER);
            }

            {
                txtCode.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        handleUpdate();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        handleUpdate();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        handleUpdate();
                    }

                    private void handleUpdate() {
                        code = txtCode.getText();
                        lvLabel.repaint();
                    }
                });
            }

//            {
//                JPanel toolboxPane = new JPanel();
//                toolboxPane.setLayout(new BoxLayout(toolboxPane, BoxLayout.X_AXIS));
//                codePane.add(toolboxPane, BorderLayout.NORTH);
//
//                String[] examples = EXAMPLE_LIST;
//
//                toolboxPane.add(new JLabel("Example: "));
//                JComboBox<String> cmbExamples = new JComboBox<>(examples);
//                cmbExamples.addActionListener($ -> handleLoadExample((String) cmbExamples.getSelectedItem()));
//                toolboxPane.add(cmbExamples);
//            }
        }

        pack();
    }

    public void display() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        LabelEditor frame = new LabelEditor();
        frame.display();
    }

}
