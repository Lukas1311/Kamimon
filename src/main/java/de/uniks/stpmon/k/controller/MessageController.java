package de.uniks.stpmon.k.controller;

import de.uniks.stpmon.k.dto.Message;
import de.uniks.stpmon.k.dto.User;
import de.uniks.stpmon.k.service.UserService;

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
    

    private Message message;
    private UserService userService;
    private User me;
    private String username;

    public MessageController(Message msg, UserService userService, User me) {
        this.message = msg;
        this.userService = userService;
        this.me = me;
    }

    @Override
    public void init() {
        // gets the current user data of the message like user and username
        disposables.add(userService
        .getUserById(message.sender())
        .observeOn(FX_SCHEDULER)
        .subscribe(usr -> {
            this.username = usr.name();
        }, error -> {
            System.out.println("Look here for the error: " + error);
            error.printStackTrace();
        })
    );
    }

    @Override
    public Parent render() {
        final Parent parent = super.render();

        // bodyText.setText("Testnachricht");

        senderName.setText(username);
        bodyText.setText(message.body());
        sendTime.setText(convertDateTimeToTime(message.createdAt()));
        // Set the alignment of the text based on the item's alignment property
        messageBox.setAlignment(isOwnMessage() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        return parent;
    }

    private boolean isOwnMessage() {
        return this.message.sender().equals(me._id());
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
