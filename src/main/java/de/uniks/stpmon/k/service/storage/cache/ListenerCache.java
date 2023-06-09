package de.uniks.stpmon.k.service.storage.cache;

import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;

import javax.inject.Inject;


public abstract class ListenerCache<T> extends SimpleCache<T> {

    @Inject
    public EventListener eventListener;

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


    public ICache<T> init() {
        super.init();
        disposables.add(eventListener.listen(Socket.WS, getEventName(), getDataClass())
                .subscribe(event -> {
                            final T user = event.data();
                            switch (event.suffix()) {
                                case "created" -> addValue(user);
                                case "updated" -> updateValue(user);
                                case "deleted" -> removeValue(user);
                            }
                        }
                ));
        return this;
    }
}
