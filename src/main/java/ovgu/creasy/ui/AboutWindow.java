package ovgu.creasy.ui;

import javafx.application.HostServices;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ovgu.creasy.Main;

public class AboutWindow {

    private static final String version = "v0.1.0";

    // TODO: add more info text
    public static void open(HostServices hostServices) {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About Creasy");
        about.setHeaderText("Creasy " + version);
        ((Stage) about.getDialogPane().getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);

        ImageView icon = new ImageView(Main.APPLICATION_ICON);
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        // about.getDialogPane().setGraphic(icon);

        Hyperlink gitHub = new Hyperlink("https://github.com/xkevio/Creasy");
        gitHub.setOnAction(e -> hostServices.showDocument("https://github.com/xkevio/Creasy"));

        TextArea license = new TextArea();
        license.setText("""
                        Creasy - a software to turn crease patterns into instructions
                        Copyright © 2021  xkevio, undertrox, Skogsbaer, RxZer
                                    
                        This program is free software: you can redistribute it and/or modify
                        it under the terms of the GNU General Public License as published by
                        the Free Software Foundation, either version 3 of the License, or
                        (at your option) any later version.
                                    
                        This program is distributed in the hope that it will be useful,
                        but WITHOUT ANY WARRANTY; without even the implied warranty of
                        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
                        GNU General Public License for more details.
                                    
                        You should have received a copy of the GNU General Public License
                        along with this program.  If not, see https://www.gnu.org/licenses.
                        """);
        license.setWrapText(true);
        license.setEditable(false);

        var iconWrapper = new VBox(icon);
        iconWrapper.setAlignment(Pos.CENTER);
        HBox.setHgrow(iconWrapper, Priority.ALWAYS);

        about.getDialogPane().setContent(new VBox(
                new HBox(
                        new TextFlow(
                                new Text("""
                                        Creasy is a software that allows you to convert
                                        crease patterns into simple step-by-step instructions.
                                                                
                                        Find us here:"""),
                                gitHub), iconWrapper), license));

        ((VBox) about.getDialogPane().getContent()).setSpacing(10);
        about.showAndWait();
    }
}

