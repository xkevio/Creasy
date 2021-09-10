package ovgu.creasy.util;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextLogger {

    public static void logText(String event, Node logObject) {
        String timeStamp = new SimpleDateFormat("[dd.MM / HH:mm:ss]: ").format(new Date());
        ((TextArea) logObject).appendText(timeStamp + event + '\n');
    }
}
