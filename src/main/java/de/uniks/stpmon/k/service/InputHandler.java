package de.uniks.stpmon.k.service;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class InputHandler {

    private final List<Consumer<? super KeyEvent>> keyHandlers = new ArrayList<>();
    private final List<Consumer<? super KeyEvent>> keyFilters = new ArrayList<>();

    @Inject
    public InputHandler() {
    }

    public Runnable addKeyHandler(Consumer<? super KeyEvent> handler) {
        keyHandlers.add(handler);
        return () -> keyHandlers.remove(handler);
    }

    public Runnable addKeyFilter(Consumer<? super KeyEvent> handler) {
        keyFilters.add(handler);
        return () -> keyFilters.remove(handler);
    }

    public EventHandler<KeyEvent> keyPressedHandler() {
        return event -> {
            for (Consumer<? super KeyEvent> keyHandler : keyHandlers) {
                keyHandler.accept(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        };
    }

    public EventHandler<KeyEvent> keyPressedFilter() {
        return event -> {
            for (Consumer<? super KeyEvent> keyHandler : keyFilters) {
                keyHandler.accept(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        };
    }
}
