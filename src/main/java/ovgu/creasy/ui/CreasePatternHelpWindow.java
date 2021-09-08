package ovgu.creasy.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class CreasePatternHelpWindow {

    public static void open() {
        Alert alertCP = new Alert(Alert.AlertType.INFORMATION);
        alertCP.setTitle("What are Crease Patterns?");
        alertCP.setHeaderText(null);

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
        // wrapper.setAlignment(Pos.CENTER);

        VBox container = new VBox();
        container.getChildren().addAll(mainText, wrapper);
        container.setPadding(new Insets(20, 20, 0, 10));

        alertCP.getDialogPane().setContent(container);
        alertCP.showAndWait();
    }

    private static Text buildTextWithColor(String text, Color strokeColor) {
        Text coloredText = new Text(text);
        coloredText.setStroke(strokeColor);
        return coloredText;
    }
}

