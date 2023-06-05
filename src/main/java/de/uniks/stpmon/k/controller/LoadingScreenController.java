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
    private boolean skipLoading = false;

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

    public void startLoading(Runnable onLoadingFinished) {
        this.onLoadingFinished = onLoadingFinished;
        if (skipLoading) {
            onLoadingFinished.run();
            return;
        }
        app.show(this);
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public void setSkipLoading(boolean skipLoading) {
        this.skipLoading = skipLoading;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();
        setVectorImage(imageViewKamimonLettering, "kamimonLettering.svg");
        imageViewKamimonLettering.setPreserveRatio(true);
        imageViewKamimonLettering.fitWidthProperty().bind(hBoxCompanies.widthProperty().multiply(0.5));
        imageViewKamimonLettering.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );

        setVectorImage(imageViewDeadBirdsSociety, "deadBirdsSocietyLogo.svg");
        imageViewDeadBirdsSociety.setPreserveRatio(true);
        imageViewDeadBirdsSociety.fitWidthProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        imageViewDeadBirdsSociety.fitHeightProperty().bind(
                hBoxCompanies.heightProperty().multiply(0.5)
        );
        setVectorImage(imageViewKgmLogo, "kgmLogo.svg");
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
