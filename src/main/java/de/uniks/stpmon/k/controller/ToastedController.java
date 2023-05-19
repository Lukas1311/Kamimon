package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.controller.Controller;
import de.uniks.stpmon.k.controller.ToastController;
import retrofit2.HttpException;

import javax.inject.Inject;

public class ToastedController extends Controller {
    @Inject
    ToastController toastController;

    protected void handleError(Throwable error) {
        if(!(error instanceof HttpException http)){
            return;
        }
        if (http.code() == 429) {
            toastController.openToast(translateString("too.many.requests.try.again"));
        }
    }
}
