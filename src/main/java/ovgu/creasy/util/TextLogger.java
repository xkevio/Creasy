package ovgu.creasy.util;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextLogger {
    /**
     * Class TextLogger provides possibilty to log program events and show them in a textarea in the UI
     * @param event is a String which get displayed
     * @param logObject ???
     */
    public static void logText(String event, Node logObject) {
        String timeStamp = new SimpleDateFormat("[HH:mm:ss]: ").format(new Date());
        ((TextArea) logObject).appendText(timeStamp + event + '\n');
    }
}
