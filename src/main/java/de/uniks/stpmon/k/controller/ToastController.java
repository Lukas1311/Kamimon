package de.uniks.stpmon.k.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ToastController extends Controller {

    @FXML
    public Text text;
    private boolean open;

    @Inject
    public ToastController() {
    }

    private static void updatePosition(Stage mainStage, Stage toastStage) {
        toastStage.setY(mainStage.getY() + (mainStage.getHeight()) - toastStage.getHeight());
        toastStage.setX(mainStage.getX() + ((mainStage.getWidth() / 2) - (toastStage.getWidth() / 2)));
    }

    public void openToast(String message) {
        if (open) {
            return;
        }
        open = true;
        Platform.runLater(() -> {
            Stage mainStage = app.getStage();
            Stage toastStage = new Stage();
            toastStage.setTitle("Toast");
            toastStage.initOwner(app.getStage());
            mainStage.xProperty().addListener((observable, oldValue, newValue) -> updatePosition(mainStage, toastStage));
            mainStage.yProperty().addListener((observable, oldValue, newValue) -> updatePosition(mainStage, toastStage));
            mainStage.heightProperty().addListener((observable, oldValue, newValue) -> updatePosition(mainStage, toastStage));
            mainStage.widthProperty().addListener((observable, oldValue, newValue) -> updatePosition(mainStage, toastStage));
            toastStage.setResizable(false);
            toastStage.initStyle(StageStyle.TRANSPARENT);
            Parent root = render();
            text.setText(message);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            toastStage.setScene(scene);
            toastStage.setWidth(400);
            toastStage.setHeight(40);
            root.setOpacity(0);
            root.setTranslateY(20);

            toastStage.show();


            // Update position after layout is done
            Platform.runLater(() -> updatePosition(mainStage, toastStage));

            Timeline fadeIn = new Timeline();
            fadeIn.getKeyFrames().add(new KeyFrame(Duration.millis(1500),
                    new KeyValue(root.opacityProperty(), 1),
                    new KeyValue(root.translateYProperty(), 0)));
            fadeIn.setOnFinished((ae) -> new Thread(() -> {
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException ignored) {
                }
                Timeline fadeOut = new Timeline();
                fadeOut.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                        new KeyValue(root.opacityProperty(), 0),
                        new KeyValue(root.translateYProperty(), 20)
                ));
                fadeOut.setOnFinished((aeb) -> {
                    toastStage.close();
                    open = false;
                });
                fadeOut.play();
            }).start());
            fadeIn.play();
        });
    }

}
