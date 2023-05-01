package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.Main;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import javax.inject.Inject;

public abstract class Controller {

    @Inject
    protected App app;

    protected final CompositeDisposable disposables = new CompositeDisposable();

    public void init(){

    }

    public void destroy(){
        disposables.dispose();
    }

    public void onDestroy(Runnable action) {
        disposables.add(Disposable.fromRunnable(action));
    }

    public Parent render(){
        return load(getClass().getSimpleName().replace("Controller", ""));
    }

    protected Parent load(String view){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/" + view + ".fxml"));
        loader.setControllerFactory(c -> this);
        try{
            return loader.load();
        }catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }
}
