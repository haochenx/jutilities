package name.haochenxie.jutilities.labelprinter;

import lombok.*;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

@Getter @Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class SimpleSheetLayout implements SheetLayout {

    private double topPadding; // in mm
    private double leftPadding; // in mm

    private double cellWidth; // in mm
    private double cellHeight; // in mm

    private double columnSpace; // in mm
    private int labelCountPerColumn;

    private int labelCount;

    @Override
    public Iterator<Rectangle2D.Double> iterator() {
        return new Iterator<Rectangle2D.Double>() {

            private int cur = 0;

            @Override
            public boolean hasNext() {
                return cur < labelCount;
            }

            @Override
            public Rectangle2D.Double next() {
                int column = cur / labelCountPerColumn;
                int row = cur % labelCountPerColumn;

                ++cur;
                return new Rectangle2D.Double(
                        translate(column * (columnSpace + cellWidth) + leftPadding),
                        translate(row * cellHeight + topPadding),
                        translate(cellWidth),
                        translate(cellHeight));
            }
        };

    }

    /**
     * translate mm to points
     */
    private static double translate(double x) {
        return x * 2.83464567;
    }

}
