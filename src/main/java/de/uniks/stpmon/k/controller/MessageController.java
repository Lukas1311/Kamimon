package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MessageController extends Controller {

    @FXML
    public VBox messageBox;
    @FXML
    public HBox textBox;
    @FXML
    public Text bodyText;
    @FXML
    public Text senderName;
    @FXML
    public Text sendTime;
    

    private final Message message;
    private final String username;

    public MessageController(Message msg, String senderUsername, User me) {
        this.message = msg;
        this.username = senderUsername;
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        senderName.setText(username);
        bodyText.setText(message.body());
        sendTime.setText(convertDateTimeToTime(message.createdAt()));
        // Set the alignment of the text based on the item's alignment property
        // messageBox.setAlignment(isOwnMessage() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        return parent;
    }

    // TODO: add something like "today", "yesterday" and then the date like "May 13" 
    // OR create seperators between messages when a day changed to another
    private String convertDateTimeToTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        ZoneId sourceZone = ZoneId.of("UTC"); // assuming the given time is in UTC
        ZoneId germanyZone = ZoneId.of("Europe/Berlin");

        ZonedDateTime sourceDateTime = ZonedDateTime.of(dateTime, sourceZone);
        ZonedDateTime germanyDateTime = sourceDateTime.withZoneSameInstant(germanyZone);

        LocalTime germanTime = germanyDateTime.toLocalTime();
        // strips the seconds and milliseconds from time object
        LocalTime strippedTime = germanTime.truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        return strippedTime.toString();
    }
}
