package name.haochenxie.jutilities.labelprinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public interface LabelPrintable {

    /**
     * @return a boolean indicating whether this label could fit in the {@code area}
     */
    public boolean print(LabelArea area, Graphics2D g);

    /**
     * assuming that every label on each sheet has the same shape
     */
    public default void preview(SheetLayout layout) {
        Rectangle2D.Double area = layout.iterator().next();

        JDialog dialog = new JDialog();
        JLabel label = new JLabel() {
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        repaint();
                    }
                });
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension((int) area.width, (int) area.height);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Rectangle bounds = getBounds();
                g.setColor(Color.WHITE);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g.setColor(Color.BLACK);

                LabelPrintable.this.print(new LabelArea(area.width, area.height, 0), (Graphics2D) g.create());
            }
        };

        dialog.setLayout(new BorderLayout());
        dialog.add(label, BorderLayout.CENTER);
        dialog.pack();

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    /**
     * translate mm to points
     */
    public static double translate(double x) {
        return x * 2.83464567;
    }

}
