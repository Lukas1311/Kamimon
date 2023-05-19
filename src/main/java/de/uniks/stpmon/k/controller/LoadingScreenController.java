package de.uniks.stpmon.k.controller;


import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.net.URL;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingScreenController extends Controller {

    private final Timer timer = new Timer();

    @FXML
    public ImageView imageViewKamimonLettering;
    @FXML
    public ImageView imageViewDeadBirdsSociety;
    @FXML
    public ImageView imageViewKgmLogo;
    @FXML
    public HBox hBoxCompanies;

    public Runnable onLoadingFinished;
    public int minTime = 2000;

    public void setOnLoadingFinished(Runnable onLoadingFinished) {
        this.onLoadingFinished = onLoadingFinished;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    @Inject
    public LoadingScreenController() {

    }

    @Override
    public void init() {
        super.init();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (onLoadingFinished != null) {
                    onLoadingFinished.run();
                }
            }
        }, minTime);
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        initImageAsync(imageViewKamimonLettering, "kamimonLettering.png");
        imageViewKamimonLettering.setPreserveRatio(true);
        imageViewKamimonLettering.fitWidthProperty().bind(hBoxCompanies.widthProperty().multiply(0.5));
        imageViewKamimonLettering.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );

        initImageAsync(imageViewDeadBirdsSociety, "deadBirdsSocietyLogo.png");
        imageViewDeadBirdsSociety.setPreserveRatio(true);
        imageViewDeadBirdsSociety.fitWidthProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        imageViewDeadBirdsSociety.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );

        initImageAsync(imageViewKgmLogo, "kgmLogo.png");
        imageViewKgmLogo.setPreserveRatio(true);
        imageViewKgmLogo.fitWidthProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        imageViewKgmLogo.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        return parent;
    }

    private void initImageAsync(ImageView imageView, String imagePath) {
        final Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() {
                URL url = Objects.requireNonNull(LoadingScreenController.class.getResource(imagePath));
                return new Image(url.toString());
            }
        };
        loadImageTask.setOnSucceeded(event -> {
            Image loadedImage = loadImageTask.getValue();
            imageView.setImage(loadedImage);
        });
        new Thread(loadImageTask).start();
    }
}
