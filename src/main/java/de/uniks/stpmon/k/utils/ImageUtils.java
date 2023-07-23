package de.uniks.stpmon.k.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageUtils {

    public static BufferedImage scaledImage(BufferedImage image, double scale) {
        int w = (int) (image.getWidth() * scale);
        int h = (int) (image.getHeight() * scale);
        if ( w <= 0 || h <= 0 ){
            return  image;
        }
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return scaleOp.filter(image, new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
    }

    public static Image scaledImageFX(BufferedImage image, double scale) {
        return SwingFXUtils.toFXImage(scaledImage(image, scale), null);
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

    public static BufferedImage blackOutImage(BufferedImage image) {
        // Get the image dimensions
        int width = image.getWidth();
        int height = image.getHeight();

        // Loop through all pixels and make non-transparent pixels black
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                // Check if the pixel is not fully transparent
                if (alpha != 0) {
                    // Set pixel color to black (RGB: 0, 0, 0)
                    image.setRGB(x, y, 0);

                }
            }
        }
        return image;
    }


}
