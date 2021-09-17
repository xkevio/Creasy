package ovgu.creasy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ovgu.creasy.ui.MainWindow;

import java.util.Objects;

public class Main extends Application {

    public static final String APPLICATION_TITLE = "Creasy";
    public static final Image APPLICATION_ICON = new Image(
            Objects.requireNonNull(
                    Main.class.getResourceAsStream("creasy_logo8.png")
            )
    );
    public static final String VERSION = "v0.1.0";
    public static final String STYLESHEET = String.valueOf(Main.class.getResource("theme.css"));

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("main_window.fxml")));
        Parent rootFXML = loader.load();

        ((MainWindow) loader.getController()).setHostServices(getHostServices());

        Scene initialScene = new Scene(rootFXML);
        stage.getIcons().add(APPLICATION_ICON);

        initialScene.getStylesheets().add(STYLESHEET);

        stage.setScene(initialScene);
        stage.sizeToScene();
        stage.setTitle(APPLICATION_TITLE);
        stage.show();
    }
}
