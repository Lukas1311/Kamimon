package de.uniks.stpmon.k.controller;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;


@Singleton
public class LoadingScreenController extends Controller {

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

    @Inject
    public LoadingScreenController() {

    }

    @Override
    public void init() {
        super.init();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (onLoadingFinished != null) {
                    onLoadingFinished.run();
                }
            }
        }, minTime);
        onDestroy(timer::cancel);
    }

    public void startLoading(Runnable onLoadingFinished) {
        this.onLoadingFinished = onLoadingFinished;
        if (effectContext != null
                && effectContext.shouldSkipLoading()) {
            onLoadingFinished.run();
            return;
        }
        app.show(this);
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        loadImage(imageViewKamimonLettering, "kamimonLettering.png");
        imageViewKamimonLettering.setPreserveRatio(true);
        imageViewKamimonLettering.fitWidthProperty().bind(hBoxCompanies.widthProperty().multiply(0.5));
        imageViewKamimonLettering.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );

        loadImage(imageViewDeadBirdsSociety, "deadBirdsSocietyLogo.png");
        imageViewDeadBirdsSociety.setPreserveRatio(true);
        imageViewDeadBirdsSociety.fitWidthProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        imageViewDeadBirdsSociety.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        loadImage(imageViewKgmLogo, "kgmLogo.png");
        imageViewKgmLogo.setPreserveRatio(true);
        imageViewKgmLogo.fitWidthProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        imageViewKgmLogo.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        return parent;
    }
}
