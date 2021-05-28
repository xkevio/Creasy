package ovgu.creasy.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import ovgu.creasy.origami.OrigamiModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class MainWindow extends Component {
    public Canvas mainCanvas;
    private OrigamiModel model;
    private ActionEvent actionEvent;
    private final JFileChooser openFileChooser;
    private String filePath = "";
    private String lastPath = "";

    public MainWindow() {
        openFileChooser = new JFileChooser(lastPath);
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Crease Pattern","cp"));
    }

    @FXML
    public void onMenuImportAction(ActionEvent actionEvent) {
        int returnValue = openFileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION){
            try {
                filePath = openFileChooser.getSelectedFile().getPath();
                lastPath = filePath;
                System.out.println("Import completed! Filename: " + openFileChooser.getSelectedFile().getName());
                // TODO: Crease Pattern via Oripa einfügen (Klassenaufruf + loadCP(filePath))
            }catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this, e.toString(), "Error_FileLoadFailed",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.out.println("Error to import your file!");
            }
        }
        else {
            System.out.println("No file selected!");
        }
    }

    @FXML
    private void initialize() {

    }
}
