package ovgu.creasy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ovgu.creasy.ui.MainWindow;

import java.util.Objects;

public class Main extends Application {

    public static final String APPLICATION_TITLE = "Creasy";

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("main_window.fxml")));
        Parent rootFXML = loader.load();

        ((MainWindow) loader.getController()).setHostServices(getHostServices());

        Scene initialScene = new Scene(rootFXML);
        // TODO insert Icon / Logo:
        // stage.getIcons().add(new Image(start.class.getResourceAsStream("image.png")));

        stage.setScene(initialScene);
        stage.sizeToScene();
        stage.setTitle(APPLICATION_TITLE);
        stage.show();
    }
}
