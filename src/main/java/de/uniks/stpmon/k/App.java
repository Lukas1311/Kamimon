package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoadingScreenController;
import de.uniks.stpmon.k.service.AuthenticationService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class App extends Application {
    private final MainComponent component;
    protected final CompositeDisposable disposables = new CompositeDisposable();

    private Controller controller;
    private Stage stage;

    public App() {
        component = DaggerMainComponent.builder().mainApp(this).build();
    }

    public App(MainComponent component) {
        this.component = component;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        //initial window size
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setMinWidth(512);
        stage.setMinHeight(400);
        stage.setTitle("Kamimon");

        //set scene for loading screen
        final Scene scene = new Scene(new Label("Loading"));
        stage.setScene(scene);
        stage.show();

        //shows in the top bar of the app window (very tiny)
        setAppIcon(stage);
        //icon in the taskbar of the os
        setTaskbarIcon();

        if (component == null) {
            return;
        }
        LoadingScreenController loadingScreen = component.loadingScreenController();
        loadingScreen.setOnLoadingFinished(this::onFinishedLoading);
        show(loadingScreen);

        disposables.add(Disposable.fromAction(() -> component.friendCache().reset()));
    }

    private void onFinishedLoading() {
        final AuthenticationService authService = component.authenticationService();
        if (authService.isRememberMe()) {
            disposables.add(authService
                    .refresh()
                    .subscribe(lr -> show(component.hybridController()), err -> show(component.loginController())));
        } else {
            show(component.loginController());
        }
    }

    private URL getIconUrl() {
        return Objects.requireNonNull(App.class.getResource("icon_256.png"));
    }

    private void setAppIcon(Stage stage) {
        //requireNonNull was not shown in Lecture, but is needed to eliminate warning
        final Image image = new Image(getIconUrl().toString());
        stage.getIcons().add(image);
    }

    private void setTaskbarIcon() {
        if (GraphicsEnvironment.isHeadless()) {
            //No Taskbar Icon, if headless -> is important for tests
            return;
        }
        try {
            final Taskbar taskbar = Taskbar.getTaskbar();
            //requireNonNull was not shown in Lecture, but is needed to eliminate warning
            final java.awt.Image image = ImageIO.read(getIconUrl());
            taskbar.setIconImage(image);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void stop() throws Exception {
        cleanup();
        disposables.dispose();
    }

    public void show(Controller controller) {
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

        if (controller != null) {
            controller.destroy();
            controller = null;
        }
    }
}
