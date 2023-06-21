package de.uniks.stpmon.k.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class InteractionService {
    private final Map<Integer, List<Integer>>  trainersByPosition = Collections.synchronizedMap(new HashMap<>());
    @Inject
    InteractionService interactionService;

    @Inject
    public InteractionService() {
    }


}
