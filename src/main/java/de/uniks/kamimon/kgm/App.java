package de.uniks.kamimon.kgm;

import de.uniks.kamimon.kgm.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {
    private Stage stage;
    private Controller controller;
    public App(){

    }
    public App(Controller controller){
        this.controller = controller;
    }
    public Stage getStage(){
        return stage;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        //initial window size
        stage.setWidth(640);
        stage.setHeight(480);
        stage.setTitle("Kamimon");

        //set scene for loading screen
        final Scene scene = new Scene(new Label("Loading..."));
        stage.setScene(scene);
        stage.show();
    }
}
