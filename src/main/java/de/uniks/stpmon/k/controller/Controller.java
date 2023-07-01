package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.Main;
import io.reactivex.rxjava3.core.Scheduler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;


public abstract class Controller extends Viewable {

    public static final Scheduler FX_SCHEDULER = Viewable.FX_SCHEDULER;

    @Inject
    protected Provider<ResourceBundle> resources;

    public Parent render() {
        return load(getClass().getSimpleName().replace("Controller", ""));
    }

    protected Parent load(String view) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/" + getResourcePath() + view + ".fxml"));
        loader.setControllerFactory(c -> this);
        if (resources != null) {
            loader.setResources(resources.get());
        }
        try {
            return loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns the path relative to "resources/views/" where the view is located.
     *
     * @return Returns the path relative to "resources/views/".
     */
    public String getResourcePath() {
        return "";
    }

    /**
     * Translates a key-string from a 'lang.property-file' to the corresponding value string.
     *
     * @param word the string that represents the key in the property-file that is corresponding to a string value of the specific language.
     * @param args argument values that are passed optionally.
     *             In your property file, use a scheme like '{0}' for the first parameter value, '{1}' for the second, and so on...
     *             {0} will then contain your first parameter that you provided in 'args' and so on...
     *             E.g. 'userGotDeleted=User {0} got deleted!' You would pass it to the function like this: translateString(userGotDeleted, "Bob")
     * @return the value-string (translated key-string).
     */
    protected String translateString(String word, String... args) {
        String translation = resources.get().getString(word);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i + "}", args[i]);
        }
        return translation;
    }

}
