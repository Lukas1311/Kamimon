package de.uniks.stpmon.k.service;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Responsible for handling input events by calling registered handlers and filters.
 * The events are received from the {@link javafx.scene.Scene} and are passed to the focused
 * node if no filter consumes the event.
 * If the focused node does not consume the event, the handlers will be called.
 * <p>
 * The handlers and filters are called in the order they were registered.
 * If a filter or handler consumes any other handler or filter will not be called.
 */
@Singleton
public class InputHandler {

    private final List<Consumer<? super KeyEvent>> keyPressedHandlers = new ArrayList<>();
    private final List<Consumer<? super KeyEvent>> keyPressedFilters = new ArrayList<>();
    private final List<Consumer<? super KeyEvent>> keyReleasedFilters = new ArrayList<>();

    @Inject
    public InputHandler() {
    }

    /**
     * Registers a new handler for the "key pressed" event.
     * The handler will be called if the focused node does not consume the event by itself.
     * If any handler consumes the event, the remaining handlers will not be called.
     * <p>
     * The returned runnable can be used to remove the handler from the list of registered handlers.
     *
     * @param handler A handler that will be called when a key is pressed.
     * @return A runnable that can be used to remove the handler from the list of registered handlers.
     */
    public Runnable addPressedKeyHandler(Consumer<? super KeyEvent> handler) {
        keyPressedHandlers.add(handler);
        return () -> keyPressedHandlers.remove(handler);
    }

    /**
     * Registers a new filter for the "key pressed" event.
     * The filter will be called before the focused node receives the event.
     * If any filter consumes the event, the focused node will not receive the event.
     * If any handler consumes the event, the remaining handlers will not be called.
     * <p>
     * The returned runnable can be used to remove the handler from the list of registered handlers.
     *
     * @param handler A handler that will be called when a key is pressed.
     * @return A runnable that can be used to remove the handler from the list of registered handlers.
     */
    public Runnable addPressedKeyFilter(Consumer<? super KeyEvent> handler) {
        keyPressedFilters.add(handler);
        return () -> keyPressedFilters.remove(handler);
    }


    /**
     * Registers a new handler for the "key released" event.
     * The handler will be called if the focused node does not consume the event by itself.
     * If any handler consumes the event, the remaining handlers will not be called.
     * <p>
     * The returned runnable can be used to remove the handler from the list of registered handlers.
     *
     * @param handler A handler that will be called when a key is pressed.
     * @return A runnable that can be used to remove the handler from the list of registered handlers.
     */
    public Runnable addReleasedKeyFilter(Consumer<? super KeyEvent> handler) {
        keyReleasedFilters.add(handler);
        return () -> keyReleasedFilters.remove(handler);
    }

    /**
     * Creates an event handler that will call all registered handlers for the "key pressed" event.
     * If a handler consumes the event, the remaining handlers will not be called.
     */
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

    /**
     * Creates an event filter that will call all registered handlers for the "key pressed" event.
     * <p>
     * If a handler consumes the event, the remaining handlers will not be called.
     */
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

    /**
     * Creates an event filter that will call all registered handlers for the "key released" event.
     * <p>
     * If a handler consumes the event, the remaining handlers will not be called.
     */
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
