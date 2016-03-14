package name.haochenxie.jutilities.labelprinter;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class LabelPrintingQueueTrial {

    // @Test
    public void tryPrintCalibrationSheet() throws Exception {
        LabelPrintingQueue queue = new LabelPrintingQueue(null);
        queue.printCalibrationSheet(true);
    }

    // @Test
    public void tryPrint() throws Exception {
        SimpleSheetLayout aOne65 = new SimpleSheetLayout(10.92, 4.75, 38.1, 21.2, 2.5, 13, 65);
        LabelPrintingQueue queue = new LabelPrintingQueue(aOne65);
        LabelPrintable label = (area, g) -> {
            g.setColor(Color.black);
            g.drawRect(0, 0, (int) area.width, (int) area.height);
            return true;
        };
        queue.addJob(label);
        queue.print(true);
    }

}
