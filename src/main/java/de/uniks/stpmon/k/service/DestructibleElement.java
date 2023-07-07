package de.uniks.stpmon.k.service;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class DestructibleElement implements ILifecycleService {

    protected CompositeDisposable disposables = new CompositeDisposable();

    public void onDestroy(Runnable action) {
        disposables.add(Disposable.fromRunnable(action));
    }

    public void onDestroy(Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    public void destroy() {
        disposables.dispose();
    }

}
