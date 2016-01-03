package name.haochenxie.jutilities.utilities;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by haochen on 1/1/16.
 */
public class SwingImageUtilities {

    public static BufferedImage toBufferedImage(JComponent comp) {
        Dimension size = comp.getPreferredSize();
        BufferedImage buff = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buff.createGraphics();
        comp.paint(g);
        return buff;
    }

    public static Element toSVGNode(JComponent comp) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document doc = domImpl.createDocument(svgNS, "svg", null);
        SVGGraphics2D g = new SVGGraphics2D(doc);
        comp.paint(g);
        return g.getRoot();
    }

    public static File saveToPNG(File file, RenderedImage image) throws IOException {
        if (!ImageIO.write(image, "PNG", file)) {
            throw new IOException("No proper image writer found.");
        };
        return file;
    }

    public static File saveToSVG(File file, Element svg) throws IOException {
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(svg), new StreamResult(file));
            return file;
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

}
