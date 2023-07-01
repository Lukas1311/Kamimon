package de.uniks.stpmon.k.views;

import de.uniks.stpmon.k.controller.chat.CreateChatController;
import de.uniks.stpmon.k.models.User;
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
            checkBox.getStyleClass().add("edit-chat-checkbox");
            checkBox.setSelected(createChatController.isSelected(item._id()));
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
