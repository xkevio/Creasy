package ovgu.creasy.ui;

import javafx.application.HostServices;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import ovgu.creasy.Main;

import java.util.Objects;

public class AboutWindow {

    // TODO: add more info text
    public static void open(HostServices hostServices) {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About Creasy");
        about.setHeaderText("Creasy v0.1.0");

        ImageView icon = new ImageView("ovgu/creasy/dickere_Linien_Logo.jpg");
        icon.setFitHeight(60);
        icon.setFitWidth(60);
        about.getDialogPane().setGraphic(icon);
        about.show();

        Hyperlink gitHub = new Hyperlink("https://github.com/xkevio/Creasy");
        gitHub.setOnAction(e -> hostServices.showDocument("https://github.com/xkevio/Creasy"));

        about.getDialogPane().setContent(new TextFlow(
                new Text("""
                Creasy is a software that allows you to convert
                crease patterns into simple step-by-step instructions.
                
                Find us here:\040"""),
                gitHub));

        about.showAndWait();

    }
}

