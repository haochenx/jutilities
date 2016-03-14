package name.haochenxie.jutilities.labelprinter;

import com.google.common.collect.Lists;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.*;
import java.util.List;

public class LabelPrintingQueue {

    public static final double CALIX_IDEAL_VALUE = 20.0;

    public static final double CALIY_IDEAL_VALUE = 20.0;

    private SheetLayout sheetLayout;

    private Deque<LabelPrintable> queue;

    private double caliX = CALIX_IDEAL_VALUE;

    private double caliY = CALIY_IDEAL_VALUE;

    public LabelPrintingQueue(SheetLayout sheetLayout) {
        this.queue = new ArrayDeque<>();
        this.sheetLayout = sheetLayout;
    }

    /**
     * the actual offset from the edges of the calibration line (20 mm is the ideal value)
     */
    public void setCalibration(double caliX, double caliY) {
        this.caliX = caliX;
        this.caliY = caliY;
    }

    /**
     * @see #setCalibration(double, double)
     */
    public double getCalibrationX() {
        return caliX;
    }

    /**
     * @see #setCalibration(double, double)
     */
    public double getCalibrationY() {
        return caliY;
    }

    /**
     * add a sheet-full of {@code label}
     */
    public void addJob(LabelPrintable label) {
        addJob(label, sheetLayout.getLabelCount());
    }

    public void addJob(LabelPrintable label, int count) {
        addJob(Collections.nCopies(count, label));
    }

    public void addJob(Collection<LabelPrintable> labels) {
        queue.addAll(labels);
    }

    protected Printable renderQueue() {
        List<LabelPrintable> labels = new ArrayList<>(queue);
        queue.clear();

        List<Rectangle2D.Double> layoutCache = buildLayoutCache();

        int pageLabelCount = sheetLayout.getLabelCount();
        int pageCount = (int) Math.ceil((double) labels.size() / pageLabelCount);

        return (graphics, pageFormat, pageIndex) -> {
            // TODO check pageFormat
            if (pageIndex < pageCount) {
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(getCaliDeltaX(), getCaliDeltaY());

                List<LabelPrintable> pageLabels =
                        labels.subList(pageIndex * pageLabelCount,
                                Math.min((pageIndex + 1) * pageLabelCount, labels.size()));

                Iterator<LabelPrintable> labelIterator = pageLabels.iterator();
                Iterator<Rectangle2D.Double> areaIterator = layoutCache.iterator();
                int idx = 0;

                AffineTransform defaultTransform = g2d.getTransform();
                while (labelIterator.hasNext() && areaIterator.hasNext()) {
                    LabelPrintable label = labelIterator.next();
                    Rectangle2D.Double pageArea = areaIterator.next();

                    LabelArea area = new LabelArea(pageArea.width, pageArea.height, idx++);

                    g2d.setClip(null);
                    g2d.setTransform(defaultTransform);
                    g2d.clip(pageArea);
                    g2d.translate(pageArea.x, pageArea.y);

                    label.print(area, g2d);
                }

                return Printable.PAGE_EXISTS;
            } else {
                return Printable.NO_SUCH_PAGE;
            }
        };
    }

    private double getCaliDeltaX() {
        return caliX - CALIX_IDEAL_VALUE;
    }

    private double getCaliDeltaY() {
        return caliY - CALIY_IDEAL_VALUE;
    }

    private List<Rectangle2D.Double> buildLayoutCache() {
        return Lists.newArrayList(sheetLayout);
    }

    /**
     * translate mm to points
     */
    private static double translate(double x) {
        return x * 2.83464567;
    }

    private static int translatei(double x) {
        return (int) (x * 2.83464567);
    }

    public boolean print(boolean showPrintDialog) throws PrinterException {
        boolean doPrint = true;
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        if (showPrintDialog) {
            doPrint = printerJob.printDialog();
        }

        if (doPrint) {
            Printable document = renderQueue();

            printerJob.setPrintable(document);
            printerJob.print();
        }

        return doPrint;
    }

    public boolean printCalibrationSheet(boolean showPrintDialog) throws PrinterException {
        boolean doPrint = true;
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        if (showPrintDialog) {
            doPrint = printerJob.printDialog();
        }

        if (doPrint) {
            Printable document = (g0, pageFormat, pageIndex) -> {
                if (pageIndex < 1) {
                    Graphics2D g = (Graphics2D) g0;

                    g.translate(getCaliDeltaX(), getCaliDeltaY());

                    g.setColor(Color.BLACK);
                    g.drawLine(translatei(10), translatei(CALIX_IDEAL_VALUE), translatei(40), translatei(CALIX_IDEAL_VALUE));
                    g.drawLine(translatei(CALIY_IDEAL_VALUE), translatei(10), translatei(CALIY_IDEAL_VALUE), translatei(40));

                    return Printable.PAGE_EXISTS;
                } else {
                    return Printable.NO_SUCH_PAGE;
                }
            };

            printerJob.setPrintable(document);
            printerJob.print();
        }

        return doPrint;
    }

}
