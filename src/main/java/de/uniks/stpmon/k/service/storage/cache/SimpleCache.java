package de.uniks.stpmon.k.service.storage.cache;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
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
        disposables.add(observable.subscribe(values -> {
            values.forEach(value -> valuesById.put(getId(value), value));
            subject.onNext(new ArrayList<>(valuesById.values()));
        }));
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
        values.forEach(value -> valuesById.put(getId(value), value));
        subject.onNext(new ArrayList<>(valuesById.values()));
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
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    /**
     * Update a value in the cache.
     *
     * @param value The value to update.
     */
    public void updateValue(T value) {
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    /**
     * Remove a value from the cache.
     *
     * @param value The value to remove.
     */
    public void removeValue(T value) {
        valuesById.remove(getId(value));
        subject.onNext(new ArrayList<>(valuesById.values()));
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
