package ovgu.creasy.origami.oripa.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import oripa.domain.fold.FoldedModel;
import ovgu.creasy.Main;

import java.io.IOException;
import java.util.Objects;

public class EstimationResultLauncher {

    private final Stage stage;
    private final FXMLLoader oripaWindow;

    public EstimationResultLauncher() throws IOException {
        oripaWindow = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("oripa_window.fxml")));

        stage = new Stage();
        Scene scene = new Scene(oripaWindow.load());
        scene.getStylesheets().add(Main.STYLESHEET);
        // stage.setResizable(false);
        stage.getIcons().add(Main.APPLICATION_ICON);

        stage.setTitle("Folded Origami - " + Main.APPLICATION_TITLE);
        stage.setScene(scene);
        stage.sizeToScene();

        stage.show();
    }

    public void setModel(FoldedModel foldedModel) {
        ((EstimationResultFrame) oripaWindow.getController()).setModel(foldedModel);
    }

    public void show() {
        stage.show();
    }
}
