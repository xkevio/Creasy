package ovgu.creasy.ui;

import javafx.application.HostServices;
import javafx.fxml.FXML;

public class AboutWindow {

    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void openGitHub() {
        hostServices.showDocument("https://github.com/xkevio/Creasy");
    }
}

