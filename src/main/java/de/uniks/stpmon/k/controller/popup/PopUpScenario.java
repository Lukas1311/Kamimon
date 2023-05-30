package de.uniks.stpmon.k.controller.popup;

public enum PopUpScenario {
    SAVE_CHANGES("doYouWantToSaveChanges"),
    DELETE_USER("doYouWantToDeleteUser"),
    DELETION_CONFIRMATION("userGotDeleted"),
    UNSAVED_CHANGES("thereAreUnsavedChanges");


    private final String mainText;

    PopUpScenario(final String mainText) {
        this.mainText = mainText;
    }

    @Override
    public String toString() {
        return mainText;
    }
}
