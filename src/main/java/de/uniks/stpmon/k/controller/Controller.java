package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.Main;
import io.reactivex.rxjava3.core.Scheduler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;


public abstract class Controller extends Viewable {

    @Inject
    protected Provider<ResourceBundle> resources;

    public static final Scheduler FX_SCHEDULER = Viewable.FX_SCHEDULER;


    public Parent render() {
        return load(getClass().getSimpleName().replace("Controller", ""));
    }

    protected Parent load(String view) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/" + view + ".fxml"));
        loader.setControllerFactory(c -> this);
        if (resources != null) {
            loader.setResources(resources.get());
        }
        try {
            return loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected String translateString(String word) {
        return resources.get().getString(word);
    }
}
