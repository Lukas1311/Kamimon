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

    @Inject
    public InputHandler() {
    }

    public Runnable addKeyHandler(Consumer<? super KeyEvent> handler) {
        keyHandlers.add(handler);
        return () -> keyHandlers.remove(handler);
    }

    public EventHandler<KeyEvent> keyPressedHandler() {
        return event -> {
            for (Consumer<? super KeyEvent> keyHandler : keyHandlers) {
                keyHandler.accept(event);
            }
        };
    }
}
