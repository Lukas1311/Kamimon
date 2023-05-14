package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.CreateChatController;
import de.uniks.stpmon.k.dto.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

public class GroupMemberCell extends ListCell<User> {

    private final CreateChatController createChatController;

    public GroupMemberCell(CreateChatController createChatController) {
        this.createChatController = createChatController;
    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            final CheckBox checkBox = new CheckBox();
            checkBox.setText(item.name());
            checkBox.setId(item.name());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (checkBox.isSelected()) {
                    createChatController.handleGroup(item);
                } else {
                    createChatController.handleGroup(item);
                }
            });
            setGraphic(checkBox);
        }
    }
}
