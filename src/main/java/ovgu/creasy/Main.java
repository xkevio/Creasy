package ovgu.creasy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {
        Scene initialScene = new Scene(new StackPane(), 640, 480);
        stage.setScene(initialScene);
        stage.setTitle("Creasy");
        stage.show();
    }
}
