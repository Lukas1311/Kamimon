package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.constants.DummyConstants;
import de.uniks.stpmon.k.service.storage.WorldRepository;

import java.util.List;

public class WorldDummy {

    public static void addWorldDummy(WorldRepository repository) {
        repository.regionMap().setValue(DummyConstants.EMPTY_IMAGE);
        repository.minimapImage().setValue(DummyConstants.EMPTY_IMAGE);
        repository.floorImage().setValue(DummyConstants.EMPTY_IMAGE);
        repository.props().setValue(List.of());
    }

}
