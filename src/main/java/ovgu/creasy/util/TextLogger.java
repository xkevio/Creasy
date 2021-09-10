package ovgu.creasy.util;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextLogger {

    /**
     * Class TextLogger provides a function to log program events and show them in a TextArea
     * @param event is the event string which get displayed
     * @param logObject is the object to which the event is written to
     */
    public static void logText(String event, Node logObject) {
        String timeStamp = new SimpleDateFormat("[HH:mm:ss]: ").format(new Date());
        ((TextArea) logObject).appendText(timeStamp + event + '\n');
    }
}
