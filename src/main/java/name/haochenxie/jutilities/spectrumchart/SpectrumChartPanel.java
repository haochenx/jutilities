package name.haochenxie.jutilities.spectrumchart;

import com.google.common.base.Joiner;
import name.haochenxie.jutilities.utilities.DnDUtilities;
import name.haochenxie.jutilities.utilities.SwingImageUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by haochen on 1/1/16.
 */
public class SpectrumChartPanel extends JPanel implements DnDUtilities.DnDFileSource {

    public static final int SCALE_LABEL_GAP = 10;

    private int width = 600;
    private int height = 100;
    private int strokeWidth = 1;
    private Style style = Style.COLOR_BLACK_BG;

    private int horizontalMargin = 40;
    private int verticalMargin = 20;

    private double scaleStart = 400;
    private double scaleEnd = 700;

    private String name = "";

    private List<Double> data = Collections.emptyList();

    public static enum Style {
        COLOR_BLACK_BG(Color.BLACK, Color.WHITE, 0),
        COLOR_WHITE_BG(Color.WHITE, Color.BLACK, 0),
        BW(Color.WHITE, Color.BLACK, 1);

        public final Color bgColor;
        public final Color scaleColor;
        public final int strokeWidthDelta;

        Style(Color bgColor, Color scaleColor, int strokeWidthDelta) {
            this.bgColor = bgColor;
            this.scaleColor = scaleColor;
            this.strokeWidthDelta = strokeWidthDelta;
        }

        public Color calculateLineColor(double line) {
            switch (this) {
                case BW:
                    return Color.BLACK;
                case COLOR_BLACK_BG:
                case COLOR_WHITE_BG:
                    int[] rgb = SpectrumAlgorithms.waveLengthToRGB(line);
                    return new Color(rgb[0], rgb[1], rgb[2]);
                default:
                    throw new IllegalArgumentException();
            }
        }

    }

    public SpectrumChartPanel() {
        init();
    }

    private void init() {
        // enable Drag and Drop
        DnDUtilities.makeFileSource(this, this);

        // add the "click to change style" feature
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Style[] styles = Style.values();
                Style nextStyle = styles[
                        (Arrays.asList(styles).indexOf(SpectrumChartPanel.this.style) + 1) % styles.length];
                setStyle(nextStyle);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics g = g0.create();

        Rectangle bounds = getBounds();

        // paint background
        g.setColor(style.bgColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        int w = width;
        int h = height;

        int hmargin = horizontalMargin;
        int vmargin = verticalMargin;

        int h0 = h * 2 / 3;
        int h1 = (int) (h0 * .8);

        g.translate(hmargin, vmargin - ((g.getFontMetrics().getHeight() + SCALE_LABEL_GAP) / 2));

        Graphics g1 = g.create();
        g1.translate(0, h / 2);

        // paint scale
        paintScale(g1, w, h0);

        // paint data
        paintData(g1, w, h1);
    }

    private void paintScale(Graphics g0, int scaleWidth, int scaleHeight) {
        Graphics g = g0.create();

        int w = scaleWidth;
        int h = scaleHeight;
        int s = 1; // stroke width

        int h0 = h;
        int h1 = (int) (h0 * .5);
        int h2 = (int) (h1 * .5);
        int hM = (int) (h0 * .1);

        // scale color
        g.setColor(style.scaleColor);

        // start and end
        g.fillRect(0, -h0 / 2, s, h0);
        g.fillRect(w, -h0 / 2, s, h0);

        // 100, 50, 10 marks
        BiConsumer<String, Integer> labelPainter = (label, x) -> {
            int len = g.getFontMetrics().stringWidth(label);
            int y = h / 2 + SCALE_LABEL_GAP + g.getFontMetrics().getHeight();
            g.drawString(label, x - len / 2, y);
        };

        paintGraduations(g, w, s, h1, 100, labelPainter);
        paintGraduations(g, w, s, h2, 50, labelPainter);
        paintGraduations(g, w, s, hM, 10, null);
    }

    private void paintGraduations(Graphics g, int scaleWidth, int strokeWidth, int height, int step,
                                  BiConsumer<String, Integer> labelPainter) {
        for (int mark = (int) (Math.ceil(scaleStart / step) * step); mark <= scaleEnd; mark += step) {
            double pos = (mark - scaleStart) / (scaleEnd - scaleStart);
            int x = (int) (pos * scaleWidth);

            g.fillRect(x, -height / 2, strokeWidth, height);

            if (labelPainter != null) {
                labelPainter.accept(Integer.toString(mark), x);
            }
        }
    }

    private void paintData(Graphics g0, int scaleWidth, int lineHeight) {
        Graphics g = g0.create();

        int w = scaleWidth;
        int h = lineHeight;
        int s = strokeWidth + style.strokeWidthDelta;

        for (double line : data) {
            double pos = (line - scaleStart) / (scaleEnd - scaleStart);
            int x = (int) (pos * w);
            Color color = style.calculateLineColor(line);

            g.setColor(color);
            g.fillRect(x - s / 2, - h / 2, s, h);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width + 2 * horizontalMargin, height + 2 * verticalMargin);
    }

    public void setData(List<Double> data) {
        this.data = data;
        repaint();
    }

    public void setCoreSize(int width, int height, int strokeWidth) {
        this.width = width;
        this.height = height;
        this.strokeWidth = strokeWidth;

        invalidate();
        repaint();
    }

    public void setMargin(int horizontalMargin, int verticalMargin) {
        this.horizontalMargin = horizontalMargin;
        this.verticalMargin = verticalMargin;

        invalidate();
        repaint();
    }

    public void setScale(double scaleStart, double scaleEnd) {
        this.scaleStart = scaleStart;
        this.scaleEnd = scaleEnd;

        repaint();
    }

    public void setStyle(Style style) {
        this.style = style;

        repaint();
    }

    @Override
    public void setName(String name) {
        this.name = name.trim();
    }

    public JFrame display() {
        return display("");
    }

    public JFrame display(String initialData) {
        JFrame frame = new JFrame("Spectrum Chart");
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);

        JTextArea txtData = new JTextArea();
        txtData.setRows(4);
        frame.add(new JScrollPane(txtData), BorderLayout.SOUTH);

        txtData.setToolTipText("Click the image to change style; " +
                "drag the image to save it.");
        txtData.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                action();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action();
            }

            private void action() {
                String dataStr = txtData.getText();

                String[] lines = Arrays.stream(dataStr.split("\n"))
                        .map(String::trim)
                        .filter(((Predicate<String>) String::isEmpty).negate())
                        .map(str -> str.replaceFirst("//.*", ""))
                        .toArray(String[]::new);

                dataStr = Joiner.on(" ").join(lines).trim();

                String regex = "\\[(.*)\\]";
                Pattern namePattern = Pattern.compile(regex);

                List<Double> data = Pattern.compile("(,\\s*|\\s+)").splitAsStream(dataStr)
                        .filter(str -> {
                            Matcher m = namePattern.matcher(str);
                            if (m.matches()) {
                                String name = m.group(1);
                                SpectrumChartPanel.this.setName(name);
                                return false;
                            } else {
                                return true;
                            }

                        })
                        .flatMap(str -> {
                            try {
                                return Stream.of(Double.parseDouble(str));
                            } catch (NumberFormatException e) {
                                return Stream.empty();
                            }
                        })
                        .collect(Collectors.toList());

                setData(data);
            }

        });

        txtData.setText(initialData);

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        return frame;
    }

    public File saveToPNG(File file) throws IOException {
        return SwingImageUtilities.saveToPNG(file, SwingImageUtilities.toBufferedImage(this));
    }

    public File saveToPNG(String path) throws IOException {
        return SwingImageUtilities.saveToPNG(new File(path), SwingImageUtilities.toBufferedImage(this));
    }

    public File saveToSVG(File file) throws IOException {
        return SwingImageUtilities.saveToSVG(file, SwingImageUtilities.toSVGNode(this));
    }

    public File saveToSVG(String path) throws IOException {
        return SwingImageUtilities.saveToSVG(new File(path), SwingImageUtilities.toSVGNode(this));
    }

    @Override
    public List<File> getTransferFileList() throws IOException {
        File file = Files.createTempFile(
                String.format("%sspectrum-chart-", name.isEmpty() ? "" : name + "-"),
                ".png").toFile();
        SpectrumChartPanel.this.saveToPNG(file);
        return Arrays.asList(file);
    }

    public static void main(String[] args) {
        List<Double> sampleData = Arrays.asList(
                465., 496., 513., 568., 572., 589., 617.
                );

        SpectrumChartPanel instance = new SpectrumChartPanel();
        instance.display("[SAMPLE] " + Joiner.on(", ").join(sampleData) + " // sample data");
    }

}
