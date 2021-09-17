package ovgu.creasy.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ovgu.creasy.Main;

public class CreasePatternHelpWindow {

    public static void open() {
        Alert alertCP = new Alert(Alert.AlertType.INFORMATION);
        alertCP.setTitle("What are Crease Patterns?");
        alertCP.setHeaderText(null);
        ((Stage) alertCP.getDialogPane().getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);

        Text black = buildTextWithColor("Black \n", Color.BLACK);
        Text blue = buildTextWithColor("Blue \n", Color.BLUE);
        Text red = buildTextWithColor("Red \n", Color.RED);

        TextFlow colorWords = new TextFlow(black, blue, red);
        colorWords.setTextAlignment(TextAlignment.JUSTIFY);

        Text blackText = buildTextWithColor("= Edge Folds\n", Color.BLACK);
        Text blueText = buildTextWithColor("= Valley Folds\n", Color.BLUE);
        Text redText = buildTextWithColor("= Mountain Folds\n", Color.RED);

        TextFlow colorTexts = new TextFlow(blackText, blueText, redText);
        colorTexts.setTextAlignment(TextAlignment.JUSTIFY);

        TextFlow mainText = new TextFlow(
                new Text("""
                        Crease Patterns are a two-dimensional
                        representation of an origami structure.
                                                    
                        Creasy converts those representations
                        step-by-step into DIY instructions.
                                                    
                        The representation is a square image
                        consisting of three types of lines:
                          """));

        HBox wrapper = new HBox(new TextFlow(colorWords, colorTexts));

        VBox container = new VBox();
        container.getChildren().addAll(mainText, wrapper);
        container.setPadding(new Insets(20, 20, 0, 10));

        alertCP.getDialogPane().setContent(container);
        alertCP.getDialogPane().getStylesheets().add(Main.STYLESHEET);
        alertCP.showAndWait();
    }

    private static Text buildTextWithColor(String text, Color color) {
        Text coloredText = new Text(text);
        coloredText.setFill(color);
        coloredText.setStroke(color);
        return coloredText;
    }
}

