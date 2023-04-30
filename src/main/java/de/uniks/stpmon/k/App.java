package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

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

        //shows in the top bar of the app window (very tiny)
        setAppIcon(stage);
        //icon in the taskbar of the os
        setTaskbarIcon();

        if(controller != null){
            initAndRender(controller);
            return;
        }
        final MainComponent component = DaggerMainComponent.builder().mainApp(this).build();
        //code after loginController is implemented
        controller = component.loginController();
        initAndRender(controller);
    }

    private void setAppIcon(Stage stage){
        //requireNonNull was not shown in Lecture, but is needed to eliminate warning
        final Image image = new Image(Objects.requireNonNull(App.class.getResource("icon.png")).toString());
        stage.getIcons().add(image);
    }

    private void setTaskbarIcon(){
        if(GraphicsEnvironment.isHeadless()){
            //No Taskbar Icon, if headless -> is important for tests
            return;
        }
        try{
            final Taskbar taskbar = Taskbar.getTaskbar();
            //requireNonNull was not shown in Lecture, but is needed to eliminate warning
            final java.awt.Image image = ImageIO.read(Objects.requireNonNull(Main.class.getResource("icon.png")));
            taskbar.setIconImage(image);
        }catch (Exception ignored){

        }
    }
    @Override
    public void stop() throws Exception{
        cleanup();
    }
    public void show(Controller controller){
        cleanup();
        this.controller = controller;
        initAndRender(controller);
    }

    private void initAndRender(Controller controller) {
        controller.init();
        //sets the firs knot in the scene tree
        stage.getScene().setRoot(controller.render());
    }


    /**
     * This method destroys the current controller
     * Is called, when the app is closed
     */
    private void cleanup() {

        if(controller != null){
            controller.destroy();
            controller = null;
        }
    }
}
