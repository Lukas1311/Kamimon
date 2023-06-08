package de.uniks.stpmon.k.service.cache;

import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public abstract class CacheStorage<T> implements ICache<T> {
    protected final BehaviorSubject<List<T>> subject = BehaviorSubject.createDefault(List.of());
    protected final Map<String, T> valuesById = new LinkedHashMap<>();
    protected final CompositeDisposable disposables = new CompositeDisposable();
    /**
     * A completable that completes when the cache is initialized.
     */
    protected Completable initialized = Completable.never();
    protected boolean destroyed = false;

    @Inject
    public EventListener eventListener;

    /**
     * Retrieve the initial values from the server.
     *
     * @return The initial values.
     */
    protected abstract Observable<List<T>> getInitialValues();

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

    /**
     * Retrieve the id of the value.
     *
     * @param value The value to retrieve the id from.
     * @return The id of the value.
     */
    protected abstract String getId(T value);

    public void destroy() {
        subject.onComplete();
        disposables.dispose();
        destroyed = true;
    }

    public void init(Runnable onDestroy) {
        if (destroyed) {
            throw new IllegalStateException("Cache already destroyed");
        }
        disposables.add(Disposable.fromRunnable(onDestroy));
        Observable<List<T>> observable = getInitialValues();
        initialized = Completable.fromObservable(observable);
        disposables.add(observable.subscribe(values -> {
            values.forEach(value -> valuesById.put(getId(value), value));
            subject.onNext(new ArrayList<>(valuesById.values()));
        }));
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
    }

    public Completable onInitialized() {
        if (destroyed) {
            return Completable.error(new IllegalStateException("Cache already destroyed"));
        }
        return initialized;
    }

    /**
     * Add a value to the cache.
     *
     * @param value The value to add.
     */
    private void addValue(T value) {
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    /**
     * Update a value in the cache.
     *
     * @param value The value to update.
     */
    private void updateValue(T value) {
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    /**
     * Remove a value from the cache.
     *
     * @param value The value to remove.
     */
    private void removeValue(T value) {
        valuesById.remove(getId(value));
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    public Optional<T> getValue(String id) {
        if (destroyed) {
            throw new IllegalStateException("Cache already destroyed");
        }
        return Optional.ofNullable(valuesById.get(id));
    }


    public Observable<List<T>> getValues() {
        if (destroyed) {
            return Observable.error(new IllegalStateException("Cache already destroyed"));
        }
        return subject;
    }
}
