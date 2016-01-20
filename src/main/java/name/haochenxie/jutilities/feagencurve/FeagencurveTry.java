package name.haochenxie.jutilities.feagencurve;

import com.google.common.collect.Lists;
import fj.P;
import fj.P2;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Haochen Xie on 1/18/16.
 *
 * Feagencurve = Feature Generated Curve
 */
public class FeagencurveTry extends JPanel {

    private List<P2<Double, Double>> data;

    public FeagencurveTry(List<P2<Double, Double>> data) {
        this.data = data;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);

        double w, h;
        Graphics g = g0.create();

        Rectangle bounds = getBounds();
        w = h = Math.min(bounds.getWidth(), bounds.getHeight());

        // center and box the image
        g.translate((int) (bounds.getWidth() - w) / 2 , (int) (bounds.getHeight() - h) / 2);
        g.setColor(Color.white);
        g.fillRect(0, 0, (int) w, (int) h);
        g.setColor(Color.black);
        g.drawRect(1, 1, (int) w - 2, (int) h - 2);

        // draw the target curve
        paintCurve((Graphics2D) translated(g, (int) w / 2, (int) h / 2),
                w, h, data);
    }

    /**
     *
     * @param g
     * @param width
     * @param height
     * @param data          each point is expected to be in [-1, 1]*[-1,1]
     */
    private void paintCurve(Graphics2D g, double width, double height, List<P2<Double, Double>> data) {
        if (data.isEmpty()) {
            return;
        }

        data = data.stream().map(p -> scale(p, width / 2)).collect(Collectors.toList());

        // draw data points
        for (int i = 0; i < data.size(); i++) {
            g.setColor((i % 3 == 0) ? Color.red : Color.blue);
            int psize = (i % 3 == 0) ? 4 : 2;

            P2<Double, Double> p = data.get(i);
            g.fillRect((int) (double) p._1() - psize / 2, (int) (double) p._2() - psize / 2,
                    psize, psize);
        }

        g.setColor(Color.black);
        P2<Double, Double> point0 = data.get(0);
        Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        path.moveTo(point0._1(), point0._2());
        data = data.subList(1, data.size());

        Lists.partition(data, 3).stream()
                .filter(l -> l.size() == 3)
                .forEach(points -> {
                    double qfactor = 1.3;
                    P2<Double, Double> p1 = scale(points.get(0), qfactor);
                    P2<Double, Double> p2 = scale(points.get(1), qfactor);
                    P2<Double, Double> p3 = points.get(2);

                    path.curveTo(
                            p1._1(), p1._2(),
                            p2._1(), p2._2(),
                            p3._1(), p3._2());
                });

        g.draw(path);
    }

    P2<Double, Double> scale(P2<Double, Double> p, double factor) {
        return P.p(p._1() * factor, p._2() * factor);
    }

    private Graphics translated(Graphics g0, int x, int y) {
        Graphics g = g0.create();
        g.translate(x, y);
        return g;
    }

    public static void main(String[] args) {
        main2(args);
    }

    public static void main1(String[] args) {
        Random rng = new Random();
        Function<Integer, FeagencurveTry> factory = keyPointCount -> {
            int ipPointCount = keyPointCount * 3 + 1;
            FeagencurveTry instance = new FeagencurveTry(createSampleData(rng, ipPointCount));

            instance.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    List<P2<Double, Double>> data = createSampleData(rng, ipPointCount);
                    instance.setData(data);
                    instance.repaint();
                }
            });

            return instance;
        };

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        IntStream.range(3, 10).boxed()
                .map(factory)
                .forEach(frame::add);
        frame.pack();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main2(String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout(5, 5));

        FeagencurveTry instance = new FeagencurveTry(Collections.emptyList());
        frame.add(instance, BorderLayout.CENTER);

        JTextArea input = new JTextArea();
        input.setRows(10);
        frame.add(input, BorderLayout.SOUTH);

        input.getDocument().addDocumentListener(new DocumentListener() {
            { op(); }

            @Override
            public void insertUpdate(DocumentEvent e) {
                op();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                op();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                op();
            }

            private void op() {
                String text = input.getText();
                byte[] digest = DigestUtils.sha256(text);
                List<P2<Double,Double>> data = extractData(digest);
                instance.setData(data);
                instance.repaint();
            }
        });

        frame.setTitle("Feagencurve Demo w/ SHA256 of text (Prototype)");
        frame.pack();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private static List<P2<Double, Double>> extractData(byte[] digest) {
        List<P2<Double, Double>> data = Lists.partition(IntStream.range(0, digest.length)
                .mapToDouble(i -> digest[i])
                .map(d -> d / 0xFF)
                .boxed().collect(Collectors.toList()), 2).stream()
                .map(l -> P.p(l.get(0) * 2, l.get(1) * 2))
                .collect(Collectors.toList());

        return data;
    }

    private static List<P2<Double, Double>> createSampleData(Random rng, int size) {
        return Lists.partition(IntStream.range(0, size * 2)
                    .mapToDouble($ -> rng.nextDouble())
                    .mapToObj(d -> d * 2 - 1)
                    .collect(Collectors.toList()), 2).stream()
                    .map(l -> P.p(l.get(0), l.get(1)))
                    .collect(Collectors.toList());
    }

    public void setData(List<P2<Double,Double>> data) {
        this.data = data;
    }
}
