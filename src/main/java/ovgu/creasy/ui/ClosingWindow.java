package ovgu.creasy.ui;

import javafx.application.HostServices;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import ovgu.creasy.Main;

import java.util.Optional;

public class ClosingWindow {

    public static void open() {
        Alert closing = new Alert(Alert.AlertType.CONFIRMATION);
        closing.setTitle("Want to close Creasy?!");
        closing.setHeaderText("Are your sure yo want to close this program?");

        ImageView icon = new ImageView(Main.APPLICATION_ICON);
        icon.setFitHeight(60);
        icon.setFitWidth(60);
        closing.getDialogPane().setGraphic(icon);

        Button exitButton = (Button) closing.getDialogPane().lookupButton(ButtonType.OK);
        exitButton.setText("Exit");
        closing.initModality(Modality.APPLICATION_MODAL);
        //closing.initOwner(mainCanvas);

        Optional<ButtonType> closeResponse = closing.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            //event.consume();
        }

    }
}

