package name.haochenxie.jutilities.labelprinter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.PrinterException;

public interface SheetLayout extends Iterable<Rectangle2D.Double> {

    /**
     * @return how many labels on one sheet
     */
    public int getLabelCount();

    public default void printSheetPreview() throws PrinterException {
        LabelPrintingQueue queue = new LabelPrintingQueue(this);
        LabelPrintable label = (area, g) -> {
            g.setColor(Color.black);
            g.drawRect(0, 0, (int) area.width, (int) area.height);
            return true;
        };
        queue.addJob(label);
        queue.print(true);
    }

}
