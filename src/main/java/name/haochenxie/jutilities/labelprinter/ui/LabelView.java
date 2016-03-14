package name.haochenxie.jutilities.labelprinter.ui;

import lombok.*;
import name.haochenxie.jutilities.labelprinter.LabelArea;
import name.haochenxie.jutilities.labelprinter.LabelPrintable;

import javax.swing.*;
import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
public class LabelView extends JLabel {

    @Getter @Setter
    private LabelPrintable label;

    @Getter @Setter
    private double labelWidth;

    @Getter @Setter
    private double labelHeight;

    @Getter @Setter
    private double scale;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (labelWidth * scale), (int) (labelHeight * scale));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        g.scale(scale, scale);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(0, 0, (int) labelWidth, (int) labelHeight);
        LabelArea area = new LabelArea(labelWidth, labelHeight, 0);

        g.setColor(Color.black);
        label.print(area, (Graphics2D) g.create());
    }

}
