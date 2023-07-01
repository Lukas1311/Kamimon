package de.uniks.stpmon.k.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

public class ResponseUtils {

    public static Observable<BufferedImage> readImage(Observable<ResponseBody> bodyObservable) {
        return bodyObservable
                .observeOn(Schedulers.io())
                .map((body) -> {
                    try (body) {
                        return ImageIO.read(new BufferedInputStream(body.byteStream()));
                    }
                });
    }

    public static <T> Observable<T> readJson(Observable<ResponseBody> bodyObservable, ObjectMapper mapper, Class<T> valueType) {
        return bodyObservable
                .observeOn(Schedulers.io())
                .map((body) -> {
                    try (body) {
                        return mapper.readValue(new BufferedInputStream(body.byteStream()), valueType);
                    }
                });
    }

}
