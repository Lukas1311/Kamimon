package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;

import javax.inject.Inject;


public abstract class ListenerCache<T, K> extends SimpleCache<T, K> {

    @Inject
    protected EventListener listener;

    /**
     * Retrieve the class of the data type.
     *
     * @return The class of the data type.
     */
    protected abstract Class<? extends T> getDataClass();

    /**
     * Retrieve the event name to listen to.
     *
     * @return The event name to listen to.
     */
    protected abstract String getEventName();

    public ICache<T, K> init() {
        super.init();
        disposables.add(listener.listen(Socket.WS, getEventName(), getDataClass())
                .subscribe(event -> {
                            final T value = event.data();
                    if (!isCacheable(value) || status == Status.DESTROYED) {
                                return;
                            }
                            switch (event.suffix()) {
                                case "created" -> addValue(value);
                                case "updated" -> updateValueFromSocket(value);
                                case "deleted" -> removeValue(value);
                            }
                        }
                ));
        return this;
    }

    protected void updateValueFromSocket(T value) {
        updateValue(value);
    }

}
