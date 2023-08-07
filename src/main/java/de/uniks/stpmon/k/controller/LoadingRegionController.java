package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.models.Area;
import de.uniks.stpmon.k.models.Region;
import de.uniks.stpmon.k.service.storage.RegionStorage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingRegionController extends Controller {

    @FXML
    public Label regionLabel;
    @FXML
    public Label areaLabel;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public ImageView imageViewKamimonLettering;

    public Runnable onLoadingFinished;
    public int minTime = 2000;

    @Inject
    RegionStorage regionStorage;

    @Inject
    public LoadingRegionController() {
    }

    @Override
    public Parent render() {
        Parent parent = super.render();
        Region currentRegion = regionStorage.getRegion();
        regionLabel.setText("- " + currentRegion.name() + " -");
        Area currentArea = regionStorage.getArea();
        areaLabel.setText(currentArea.name());
        progressBar.setProgress(0);
        progressBar.getStyleClass().add("progress-bar");
        loadImage(imageViewKamimonLettering, "kamimonLettering_new.png");
        imageViewKamimonLettering.setPreserveRatio(true);
        return parent;
    }

    public void startLoading(Runnable onLoadingFinished) {
        this.onLoadingFinished = onLoadingFinished;
        if (effectContext != null
                && effectContext.shouldSkipLoading()) {
            onLoadingFinished.run();
            return;
        }
        app.show(this);
        startProgress();
    }

    private void startProgress() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double progress = progressBar.getProgress();
                if (progress < 1) {
                    progress += 0.01;
                    progressBar.setProgress(progress);
                }
            }
        }, 0, minTime / 10);
        onDestroy(timer::cancel);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (onLoadingFinished != null) {
                    onLoadingFinished.run();
                }
                progressBar.setProgress(0.9);
            }
        };
        timer.schedule(task, minTime);
        onDestroy(task::cancel);
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

}
