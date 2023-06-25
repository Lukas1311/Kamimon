package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.service.storage.WorldRepository;

import java.awt.image.BufferedImage;
import java.util.List;

public class WorldDummy {
    public static void addWorldDummy(WorldRepository repository) {
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        repository.regionMap().setValue(dummyImage);
        repository.minimapImage().setValue(dummyImage);
        repository.floorImage().setValue(dummyImage);
        repository.props().setValue(List.of());
    }
}
