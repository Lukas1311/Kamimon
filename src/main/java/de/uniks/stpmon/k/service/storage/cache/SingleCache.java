package de.uniks.stpmon.k.service.storage.cache;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Optional;

public class SingleCache<V> {

    private final BehaviorSubject<Optional<V>> value = BehaviorSubject.createDefault(Optional.empty());

    public void setValue(V value) {
        this.value.onNext(Optional.ofNullable(value));
    }

    public V asNullable() {
        return asOptional().orElse(null);
    }

    public Optional<V> asOptional() {
        return value.getValue();
    }

    public Observable<Optional<V>> onValue() {
        return value;
    }

    public boolean isEmpty() {
        return asOptional().isEmpty();
    }

    public void reset() {
        value.onNext(Optional.empty());
    }
}
