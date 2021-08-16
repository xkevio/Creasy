package ovgu.creasy.ui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class AboutWindow {

    @FXML
    private ImageView GitHubLogo;
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void openGitHub() {
        hostServices.showDocument("https://github.com/xkevio/Creasy");
    }

    @FXML
    public void changeCursor(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            GitHubLogo.setCursor(Cursor.HAND);
        } else {
            GitHubLogo.setCursor(Cursor.DEFAULT);
        }
    }
}

