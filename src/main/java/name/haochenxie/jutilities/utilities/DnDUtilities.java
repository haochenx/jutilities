package name.haochenxie.jutilities.utilities;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haochen on 1/3/16.
 */
public class DnDUtilities {

    public interface DnDFileSource {

        public List<File> getTransferFileList() throws IOException;

    }

    public static void makeFileSource(Component comp, DnDFileSource fileSource) {
        DragGestureListener gestureListener = e -> {
            try {
                DragSourceListener sourceListener = new DragSourceAdapter() { };
                Transferable transferable = new Transferable() {

                    public final DataFlavor[] FLAVORS =
                            new DataFlavor[]{ DataFlavor.javaFileListFlavor };

                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return FLAVORS;
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return Arrays.asList(FLAVORS).contains(flavor);
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        return fileSource.getTransferFileList();
                    }

                };

                e.startDrag(DragSource.DefaultCopyNoDrop, transferable, sourceListener);
            } catch (InvalidDnDOperationException ex) {
                // NO-OP since it is usually not necessary to react to the situation where InvalidDnDOperationException
                // is thrown
            }
        };

        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                comp, DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
    }

}
