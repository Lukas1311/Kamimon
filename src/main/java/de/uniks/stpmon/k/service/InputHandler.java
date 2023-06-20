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

    private final List<Consumer<? super KeyEvent>> keyPressedHandlers = new ArrayList<>();
    private final List<Consumer<? super KeyEvent>> keyPressedFilters = new ArrayList<>();
    private final List<Consumer<? super KeyEvent>> keyReleasedFilters = new ArrayList<>();

    @Inject
    public InputHandler() {
    }

    public Runnable addPressedKeyHandler(Consumer<? super KeyEvent> handler) {
        keyPressedHandlers.add(handler);
        return () -> keyPressedHandlers.remove(handler);
    }

    public Runnable addPressedKeyFilter(Consumer<? super KeyEvent> handler) {
        keyPressedFilters.add(handler);
        return () -> keyPressedFilters.remove(handler);
    }


    public Runnable addReleasedKeyFilter(Consumer<? super KeyEvent> handler) {
        keyReleasedFilters.add(handler);
        return () -> keyReleasedFilters.remove(handler);
    }

    public EventHandler<KeyEvent> keyPressedHandler() {
        return event -> {
            for (Consumer<? super KeyEvent> keyHandler : keyPressedHandlers) {
                keyHandler.accept(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        };
    }

    public EventHandler<KeyEvent> keyPressedFilter() {
        return event -> {
            for (Consumer<? super KeyEvent> keyHandler : keyPressedFilters) {
                keyHandler.accept(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        };
    }

    public EventHandler<KeyEvent> keyReleasedFilter() {
        return event -> {
            for (Consumer<? super KeyEvent> keyHandler : keyReleasedFilters) {
                keyHandler.accept(event);
                if (event.isConsumed()) {
                    return;
                }
            }
        };
    }
}
