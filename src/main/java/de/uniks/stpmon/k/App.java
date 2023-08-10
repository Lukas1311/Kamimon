package de.uniks.stpmon.k;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.LoadingScreenController;
import de.uniks.stpmon.k.di.DaggerMainComponent;
import de.uniks.stpmon.k.di.MainComponent;
import de.uniks.stpmon.k.service.AuthenticationService;
import de.uniks.stpmon.k.service.ILifecycleService;
import de.uniks.stpmon.k.service.InputHandler;
import fr.brouillard.oss.cssfx.CSSFX;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class App extends Application {

    private MainComponent component;
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
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "false");
        stage = primaryStage;
        //initial window size
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setTitle("Kamimon");

        //set scene for loading screen
        final Scene scene = new Scene(new Label("Loading"));

        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("views/css/generalStyle.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("views/css/ingameStyle.css")).toExternalForm());
        CSSFX.start(scene);

        stage.setScene(scene);

        stage.show();

        //shows in the top bar of the app window (very tiny)
        setAppIcon(stage);
        //icon in the taskbar of the os
        setTaskbarIcon();

        if (component == null) {
            return;
        }
        addInputHandler(component);

        LoadingScreenController loadingScreen = component.loadingScreenController();
        loadingScreen.setMinTime(0);
        loadingScreen.startLoading(this::onFinishedLoading);
    }

    public void setMainComponent(MainComponent component) {
        this.component = component;
    }

    public void addInputHandler(MainComponent component) {
        if (component == null) {
            return;
        }
        addInputHandler(component.inputHandler());
    }

    public void addInputHandler(InputHandler inputHandler) {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, inputHandler.keyPressedHandler());
        stage.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler.keyReleasedHandler());
        stage.addEventFilter(KeyEvent.KEY_PRESSED, inputHandler.keyPressedFilter());
        stage.addEventFilter(KeyEvent.KEY_RELEASED, inputHandler.keyReleasedFilter());
    }

    public void removeInputHandler(MainComponent component) {
        if (component == null) {
            return;
        }
        removeInputHandler(component.inputHandler());
    }

    public void removeInputHandler(InputHandler inputHandler) {
        stage.removeEventHandler(KeyEvent.KEY_PRESSED, inputHandler.keyPressedHandler());
        stage.removeEventHandler(KeyEvent.KEY_RELEASED, inputHandler.keyReleasedHandler());
        stage.removeEventFilter(KeyEvent.KEY_PRESSED, inputHandler.keyPressedFilter());
        stage.removeEventFilter(KeyEvent.KEY_RELEASED, inputHandler.keyReleasedFilter());
    }

    private void onFinishedLoading() {
        final AuthenticationService authService = component.authenticationService();
        if (authService.isRememberMe()) {
            disposables.add(authService
                    .refresh()
                    .subscribe(lr -> show(component.hybridController()),
                            err -> show(component.loginController())));
        } else {
            show(component.loginController());
        }
    }

    private URL getIconUrl() {
        //requireNonNull was not shown in Lecture, but is needed to eliminate warning
        return Objects.requireNonNull(App.class.getResource("icon_500_new.png"));
    }

    private void setAppIcon(Stage stage) {
        // Tests will all run on same stage, so we need to check if icon is already set
        if (!stage.getIcons().isEmpty()) {
            return;
        }
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
            final java.awt.Image image = ImageIO.read(getIconUrl());
            taskbar.setIconImage(image);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void stop() {
        cleanup();
        disposables.dispose();
        if (component == null) {
            return;
        }

        // remove all input handlers from stage
        removeInputHandler(component);

        // destroy all lifecycle services
        for (ILifecycleService service : component.lifecycleServices()) {
            service.destroy();
        }
    }

    public void show(Controller controller) {
        cleanup();
        this.controller = controller;
        initAndRender(controller);
    }

    private void initAndRender(Controller controller) {
        controller.init();
        //sets the first node in the scene tree
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
