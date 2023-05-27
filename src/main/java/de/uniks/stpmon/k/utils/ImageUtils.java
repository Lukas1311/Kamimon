package de.uniks.stpmon.k.utils;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageUtils {

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
