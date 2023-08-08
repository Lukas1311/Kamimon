package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.models.builder.TileLayerBuilder;
import de.uniks.stpmon.k.models.map.DecorationLayer;
import de.uniks.stpmon.k.models.map.TileProp;
import de.uniks.stpmon.k.models.map.layerdata.TileLayerData;
import de.uniks.stpmon.k.world.rules.BasicRules;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static de.uniks.stpmon.k.utils.AssertImage.assertEqualsImages;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PropInspectionTest {

    private static BufferedImage sourceImage;
    private static BufferedImage resultImage;

    protected PropInspector propInspector;

    @BeforeAll
    static void setUp() throws IOException {
        BufferedImage oldImage = ImageIO.read(Objects.requireNonNull(
                PropInspectionTest.class.getResourceAsStream("layer.png")));
        sourceImage = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = sourceImage.getGraphics();
        graphics.drawImage(oldImage, 0, 0, null);
        graphics.dispose();

        resultImage = ImageIO.read(Objects.requireNonNull(
                PropInspectionTest.class.getResourceAsStream("prop.png")));
    }

    @AfterAll
    static void tearDown() {
        sourceImage.flush();
        resultImage.flush();
        sourceImage = null;
        resultImage = null;
    }

    @Test
    public void test() {
        propInspector = new PropInspector(2, 3, 1, BasicRules.registerRules());
        assertEquals(32, sourceImage.getWidth());
        assertEquals(48, sourceImage.getHeight());

        TileLayerData layer = TileLayerBuilder.builderTiles()
                .setName(TileLayerData.GROUND_TYPE)
                .setData(List.of(1L, 1L, 1L, 1L))
                .setWidth(2).setHeight(2)
                .create();
        List<TileProp> props = propInspector.work(new DecorationLayer(layer, 0, sourceImage), DummyConstants.AREA_MAP_DATA).props();
        assertEquals(1, props.size());
        TileProp prop = props.get(0);
        assertEquals(0, prop.x());
        assertEquals(1, prop.y());
        assertEquals(2, prop.width());
        assertEquals(1, prop.height());
        assertEqualsImages(resultImage, prop.image());
    }

}
