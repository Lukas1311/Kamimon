package de.uniks.stpmon.k.views;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;



import de.uniks.stpmon.k.controller.MessageController;
import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserService;
import de.uniks.stpmon.k.service.UserStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MessageCell extends ListCell<Message> {

    @FXML
    public HBox textBox;
    @FXML
    public Text bodyText;
    @FXML
    public Text senderName;
    @FXML
    public Text sendTime;


    private final UserService userService;

    public MessageCell(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            
            // Text senderName = new Text(isOwnMessage(item) ? user.name() : item.sender());
            // senderName.setFont(new Font(10.0));
            // // set the time
            // Text sendTime = new Text(
            //     convertDateTimeToTime(item.createdAt())
            // );
            // sendTime.setFont(new Font(10.0));
            // Text bodyText = new Text(item.body());

            // // model the text box where the message body text is inside
            // HBox textBox = new HBox(bodyText);
            // textBox.setBorder(new Border(new BorderStroke(
            //     Color.BLACK,
            //     BorderStrokeStyle.SOLID,
            //     new CornerRadii(5),
            //     new BorderWidths(1)
            // )));
            // textBox.setPrefWidth(Region.USE_COMPUTED_SIZE); // wrap box around the text

            // VBox senderBox = new VBox(senderName);
            // senderBox.setAlignment(Pos.TOP_LEFT);
            // VBox timeBox = new VBox(sendTime);
            // timeBox.setAlignment(Pos.TOP_RIGHT);
            // HBox senderAndTimeBox = new HBox(senderBox, timeBox);
            // VBox messageBox = new VBox(textBox, senderAndTimeBox);
            
            // // Set the alignment of the text based on the item's alignment property
            // messageBox.setAlignment(isOwnMessage(item) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            // setGraphic(messageBox);

            // TODO: create everything directly here (like above) or like this as alternative

            final MessageController messageController = new MessageController(item, userService, userService.getMe());
            setGraphic(messageController.render());
        }
    }



    // private String convertDateTimeToTime(String dateTimeString) {
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //     LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

    //     ZoneId sourceZone = ZoneId.of("UTC"); // assuming the given time is in UTC
    //     ZoneId germanyZone = ZoneId.of("Europe/Berlin");

    //     ZonedDateTime sourceDateTime = ZonedDateTime.of(dateTime, sourceZone);
    //     ZonedDateTime germanyDateTime = sourceDateTime.withZoneSameInstant(germanyZone);

    //     LocalTime germanTime = germanyDateTime.toLocalTime();
    //     // strips the seconds and milliseconds from time object
    //     LocalTime strippedTime = germanTime.truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

    //     return strippedTime.toString();
    // }
}
