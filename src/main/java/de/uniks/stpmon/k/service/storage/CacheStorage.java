package de.uniks.stpmon.k.service.storage;

import de.uniks.stpmon.k.net.EventListener;
import de.uniks.stpmon.k.net.Socket;
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

public abstract class CacheStorage<T> {
    protected final BehaviorSubject<List<T>> subject = BehaviorSubject.createDefault(List.of());
    protected final Map<String, T> valuesById = new LinkedHashMap<>();
    protected boolean initialized = false;
    protected final CompositeDisposable disposables = new CompositeDisposable();
    private Disposable disposable;

    @Inject
    public EventListener eventListener;

    protected abstract Observable<List<T>> getInitialValues();

    protected abstract Class<? extends T> getDataClass();

    protected abstract String getEventName();

    protected abstract String getId(T value);

    /**
     * Destroy the cache and all its values.
     * <p>
     * Also removes it from its parent.
     */
    public void destroy() {
        subject.onComplete();
        disposables.dispose();
    }

    public void init(Runnable onDestroy) {
        disposables.add(Disposable.fromRunnable(onDestroy));
        disposables.add(getInitialValues().subscribe(values -> {
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

    private void addValue(T value) {
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    private void updateValue(T value) {
        valuesById.put(getId(value), value);
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    private void removeValue(T value) {
        valuesById.remove(getId(value));
        subject.onNext(new ArrayList<>(valuesById.values()));
    }

    public Optional<T> getValue(String id) {
        return Optional.ofNullable(valuesById.get(id));
    }

    public Observable<List<T>> getValues() {
        return subject;
    }
}
