package de.uniks.stpmon.k.service.storage.cache;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class SimpleCache<T> implements ICache<T> {
    protected final BehaviorSubject<List<T>> subject = BehaviorSubject.createDefault(List.of());
    protected final Map<String, T> valuesById = new LinkedHashMap<>();
    protected final Map<String, ObservableEmitter<Optional<T>>> listenersById = new LinkedHashMap<>();
    protected final CompositeDisposable disposables = new CompositeDisposable();
    /**
     * A completable that completes when the cache is initialized.
     */
    protected Completable initialized = Completable.never();
    protected Status status = Status.UNINITIALIZED;

    /**
     * Retrieve the initial values from the server.
     *
     * @return The initial values.
     */
    protected abstract Observable<List<T>> getInitialValues();

    /**
     * Retrieve the id of the value.
     *
     * @param value The value to retrieve the id from.
     * @return The id of the value.
     */
    public abstract String getId(T value);

    public void destroy() {
        subject.onComplete();
        disposables.dispose();
        status = Status.DESTROYED;
    }

    public ICache<T> init() {
        if (status != Status.UNINITIALIZED) {
            throw new IllegalStateException("Cache already destroyed or was already initialized");
        }
        Observable<List<T>> observable = getInitialValues().cache();
        initialized = Completable.fromObservable(observable);
        disposables.add(observable.subscribe(this::addValues));
        status = Status.INITIALIZED;
        return this;
    }

    /**
     * Add a runnable that will be executed when the cache is destroyed.
     *
     * @param onDestroy The runnable to execute.
     */
    public void addOnDestroy(Runnable onDestroy) {
        if (status != Status.UNINITIALIZED) {
            throw new IllegalStateException("Cache already destroyed or was already initialized");
        }
        disposables.add(Disposable.fromRunnable(onDestroy));
    }

    public Completable onInitialized() {
        if (status == Status.DESTROYED) {
            return Completable.error(new IllegalStateException("Cache already destroyed"));
        }
        return initialized;
    }

    protected void addValues(List<T> values) {
        values.stream().filter(this::isCacheable)
                .forEach(value -> valuesById.put(getId(value), value));
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    /**
     * Checks if the value should be cached.
     *
     * @param value The value to check.
     * @return True if the value should be cached, false otherwise.
     */
    protected boolean isCacheable(T value) {
        return true;
    }

    @Override
    public boolean hasValue(String id) {
        return valuesById.containsKey(id);
    }

    /**
     * Add a value to the cache.
     *
     * @param value The value to add.
     */
    public void addValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (!isCacheable(value)) {
            return;
        }
        String id = getId(value);
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
        Optional.ofNullable(listenersById.get(id))
                .ifPresent(emitter -> emitter.onNext(Optional.of(value)));
    }

    /**
     * Update a value in the cache.
     *
     * @param value The value to update.
     */
    public void updateValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (!isCacheable(value)) {
            return;
        }
        String id = getId(value);
        valuesById.put(id, value);
        subject.onNext(new ArrayList<>(valuesById.values()));
        Optional.ofNullable(listenersById.get(id))
                .ifPresent(emitter -> emitter.onNext(Optional.of(value)));
    }

    /**
     * Remove a value from the cache.
     *
     * @param value The value to remove.
     */
    public void removeValue(T value) {
        if (!isCacheable(value)) {
            return;
        }
        String id = getId(value);
        valuesById.remove(id);
        subject.onNext(new ArrayList<>(valuesById.values()));
        Optional.ofNullable(listenersById.get(id))
                .ifPresent(emitter -> {
                    emitter.onNext(Optional.empty());
                });
    }

    @Override
    public Observable<Optional<T>> listenValue(String id) {
        return Observable.create((emitter -> {
            listenersById.put(id, emitter);
            emitter.onNext(getValue(id));
            disposables.add(Disposable.fromRunnable(emitter::onComplete));
            emitter.setCancellable(() -> listenersById.remove(id));
        }));
    }

    public Optional<T> getValue(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (status == Status.DESTROYED) {
            throw new IllegalStateException("Cache already destroyed");
        }
        return Optional.ofNullable(valuesById.get(id));
    }

    public Observable<List<T>> getValues() {
        if (status == Status.DESTROYED) {
            return Observable.error(new IllegalStateException("Cache already destroyed"));
        }
        return subject;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
