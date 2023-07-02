package de.uniks.stpmon.k.controller;

import retrofit2.HttpException;

import javax.inject.Inject;

public class ToastedController extends Controller {

    @Inject
    protected ToastController toastController;

    protected void handleError(Throwable error) {
        if (!(error instanceof HttpException http)) {
            return;
        }
        if (http.code() == 429) {
            toastController.openToast(translateString("too.many.requests.try.again"));
        }
    }

}
