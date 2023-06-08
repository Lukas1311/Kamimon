package de.uniks.stpmon.k.constants;

import de.uniks.stpmon.k.map.CharacterSet;
import de.uniks.stpmon.k.map.WorldSet;
import de.uniks.stpmon.k.service.map.PropInspectionTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DummyLazy {
    public static final DummyLazy INSTANCE = new DummyLazy();
    private WorldSet worldSet;

    public WorldSet getWorldSet() {
        if (worldSet == null) {
            BufferedImage images = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            //--------------------
            BufferedImage charImage;
            try {
                charImage = ImageIO.read(Objects.requireNonNull(PropInspectionTest.class.getResource("trainer_0.png")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            worldSet = new WorldSet(images, images, new ArrayList<>(),
                    Map.of("trainer_0", new CharacterSet("trainer_0", charImage)));
        }
        return worldSet;
    }
}
