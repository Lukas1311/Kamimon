package de.uniks.stpmon.k.controller.popup;

import java.util.ArrayList;
import java.util.List;

public enum PopUpScenario {
    SAVE_CHANGES("doYouWantToSaveChanges"),
    DELETE_USER("doYouWantToDeleteUser"),
    DELETION_CONFIRMATION_USER("userGotDeleted"),
    UNSAVED_CHANGES("thereAreUnsavedChanges"),
    DELETE_TRAINER("doYouWantToDeleteTrainer"),
    DELETE_CONFIRMATION_TRAINER("trainerGotDeleted");


    private final String mainText;
    private List<String> parameters = new ArrayList<>();

    PopUpScenario(final String mainText) {
        this.mainText = mainText;
    }

    @Override
    public String toString() {
        return mainText;
    }

    /**
     * sets the string parameters that are used to create a translation for lang.property files.
     * Retrieve them with the getParams() call. Set parameter values in the lang.properties files.
     * Use a scheme like '{0}' for the first parameter value, '{1}' for the second, and so on...
     * E.g. 'userGotDeleted=User {0} got deleted!'
     * 
     * @param params: the parameter/s as a List of type String that hold the values to pass to translateString() method in Controller.class
     */
    public void setParams(List<String> params) {
        this.parameters = params;
    } 

    /**
     * returns the held parameters that are used to create a translation for lang.property files
     * 
     * @return parameters as a String[]
     */
    public String[] getParams() {
        return parameters.toArray(new String[0]);
    } 
}
