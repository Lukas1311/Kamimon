package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.App;
import de.uniks.stpmon.k.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Inject;
import java.io.IOException;

public abstract class Controller {
    @Inject
    protected App app;
    public void init(){

    }

    public void destroy(){

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
