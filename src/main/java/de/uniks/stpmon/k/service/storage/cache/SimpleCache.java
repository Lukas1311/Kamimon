package de.uniks.stpmon.k.service.storage.cache;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.*;

public abstract class SimpleCache<T, K> implements ICache<T, K> {

    protected final BehaviorSubject<List<T>> subject = BehaviorSubject.createDefault(List.of());
    protected final Map<K, T> valuesById = Collections.synchronizedMap(new LinkedHashMap<>());
    protected final Map<K, BehaviorSubject<Optional<T>>> listenersById = new LinkedHashMap<>();
    protected final PublishSubject<T> onCreate = PublishSubject.create();
    protected final PublishSubject<T> onUpdate = PublishSubject.create();
    protected final PublishSubject<T> onRemove = PublishSubject.create();
    protected final CompositeDisposable disposables = new CompositeDisposable();
    protected final Set<SimpleCache<T, ?>> childCaches = new LinkedHashSet<>();
    protected boolean sendEvents = true;
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
    public abstract K getId(T value);

    public void destroy() {
        subject.onComplete();
        disposables.dispose();
        status = Status.DESTROYED;
    }

    public ICache<T, K> init() {
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

    protected void addValues(Collection<T> values) {
        values.stream().filter(this::isCacheable)
                .forEach(value -> valuesById.put(getId(value), value));
        if (sendEvents) {
            subject.onNext(new ArrayList<>(valuesById.values()));
        }
        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.addValues(Objects.requireNonNull(subject.getValue()));
        }

        // Update listeners
        for (T value : values) {
            K id = getId(value);
            Optional.ofNullable(listenersById.get(id))
                    .ifPresent(emitter -> emitter.onNext(Optional.of(value)));
        }
    }

    /**
     * Disable subject events to prevent unnecessary events.
     * This allows to add or remove multiple values (batch update) without sending events.
     */
    protected void disableEvents() {
        sendEvents = false;
    }


    /**
     * Enable subject events.
     */
    protected void enableEvents() {
        sendEvents = true;
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
    public boolean hasValue(K id) {
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
        K id = getId(value);
        if (id == null) {
            return;
        }

        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.beforeAdd(value);
        }

        valuesById.put(id, value);
        if (sendEvents) {
            subject.onNext(new ArrayList<>(valuesById.values()));
        }
        Optional.ofNullable(listenersById.get(id))
                .ifPresent(emitter -> emitter.onNext(Optional.of(value)));

        onCreate.onNext(value);
        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.addValue(value);
        }
    }

    /**
     * Update a value in the cache.
     *
     * @param value The value to update.
     */
    public void updateValue(T value) {
        if (isDestroyed()) {
            return;
        }
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        K id = getId(value);
        if (id == null) {
            return;
        }
        // Add if value is not already cached
        if (!hasValue(id)) {
            addValue(value);
            return;
        }
        // If new value is not cacheable, remove the old value from the cache
        if (!isCacheable(value)) {
            removeValue(value);
            return;
        }

        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.beforeUpdate(value);
        }

        valuesById.put(id, value);
        if (sendEvents) {
            subject.onNext(new ArrayList<>(valuesById.values()));
        }
        Optional.ofNullable(listenersById.get(id))
                .ifPresent(emitter -> emitter.onNext(Optional.of(value)));
        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.updateValue(value);
        }
    }

    /**
     * Remove a value from the cache.
     *
     * @param value The value to remove.
     */
    public void removeValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        K id = getId(value);
        if (!hasValue(id) || id == null) {
            return;
        }

        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.beforeRemove(value);
        }

        valuesById.remove(id);

        if (sendEvents) {
            subject.onNext(new ArrayList<>(valuesById.values()));
        }
        Optional.ofNullable(listenersById.get(id))
                .ifPresent(emitter -> emitter.onNext(Optional.empty()));

        onRemove.onNext(value);

        for (SimpleCache<T, ?> childCache : childCaches) {
            childCache.removeValue(value);
        }
    }

    @Override
    public Observable<Optional<T>> listenValue(K id) {
        return listenersById.computeIfAbsent(id, (k) -> BehaviorSubject
                .createDefault(getValue(id)));
    }

    @Override
    public Observable<T> onCreation() {
        return onCreate;
    }

    @SuppressWarnings("unused")
    public Observable<T> onUpdate() {
        return onUpdate;
    }

    @Override
    public Observable<T> onDeletion() {
        return onRemove;
    }

    public Optional<T> getValue(K id) {
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

    public List<T> getCurrentValues() {
        if (status == Status.DESTROYED) {
            throw new IllegalStateException("Cache already destroyed");
        }
        return subject.getValue();
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    @Override
    public Collection<K> getIds() {
        return valuesById.keySet();
    }
}
