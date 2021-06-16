package ovgu.creasy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {
        Parent rootFXML = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main_window.fxml")));

        Scene initialScene = new Scene(rootFXML);
        // TODO insert Icon / Logo:
        // stage.getIcons().add(new Image(start.class.getResourceAsStream("image.png")));

        stage.setScene(initialScene);
        stage.sizeToScene();
        stage.setTitle("Creasy");
        stage.show();
    }
}
