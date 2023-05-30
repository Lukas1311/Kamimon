package de.uniks.stpmon.k.controller.popup;

public enum PopUpScenario {
    SAVECHANGES("doYouWantToSaveChanges"),
    DELETEUSER("here text for delete");

    private final String mainText;

    PopUpScenario(final String mainText) {
        this.mainText = mainText;
    }

    @Override
    public String toString() {
        return mainText;
    }
}
