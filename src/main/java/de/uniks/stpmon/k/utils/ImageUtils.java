package de.uniks.stpmon.k.utils;

import de.uniks.stpmon.k.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ImageUtils {

    public static BufferedImage scaledImage(String relativePath, double scale) {
        try (InputStream inputStream = Objects.requireNonNull(Main.class.getResourceAsStream(relativePath))) {
            BufferedImage image = ImageIO.read(inputStream);
            int w = (int) (image.getWidth() * scale);
            int h = (int) (image.getHeight() * scale);
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            return scaleOp.filter(image, new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image scaledImageFX(String relativePath, double scale) {
        return SwingFXUtils.toFXImage(scaledImage(relativePath, scale), null);
    }

    public static void copyData(WritableRaster target, BufferedImage source,
                                int writeX, int writeY,
                                int readX, int readY,
                                int width, int height) {
        Raster raster = source.getRaster();
        Object tdata = null;

        for (int i = 0; i < height; i++) {
            tdata = raster.getDataElements(readX, readY + i, width, 1, tdata);
            target.setDataElements(writeX, writeY + i, width, 1, tdata);
        }
    }


}
