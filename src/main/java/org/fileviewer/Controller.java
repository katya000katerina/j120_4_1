package org.fileviewer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final Text text = new Text();
    private final TreeItem<String> rootItem = new TreeItem<>("");
    private final TreeItem<String> parentDirectory = new TreeItem<>("..");
    private final String separator = File.separator;
    @FXML
    private HBox hbox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TreeView<String> tree;
    private MultipleSelectionModel<TreeItem<String>> selectionModel;
    private File currentDirectory;
    private boolean areDoubleDotsHidden;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tree.setRoot(rootItem);
        tree.setShowRoot(false);
        rootItem.getChildren().add(parentDirectory);
        parentDirectory.setGraphic(getIcon("folder-icon.png"));

        selectionModel = tree.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        scrollPane.setContent(text);
        text.wrappingWidthProperty().bind(scrollPane.widthProperty());
        showDefaultText();

        currentDirectory = new File(System.getProperty("user.dir"));
        setCurrentDirectoryContent();
    }

    @FXML
    private void processItemSelection(InputEvent e) {
        if (selectionModel.getSelectedItem() == null) {
            return;
        }
        if ((e instanceof MouseEvent && (((MouseEvent) e).getClickCount() == 2))
                || (e instanceof KeyEvent && (((KeyEvent) e).getCode().equals(KeyCode.ENTER)))) {
            try {
                selectItem();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void selectItem() throws IOException {
        String selectedItem = selectionModel.getSelectedItem().getValue();
        String currentDirectoryPath = currentDirectory.getAbsolutePath();
        if (selectedItem.equals("..")) {
            File directory = new File(currentDirectoryPath.substring(0, currentDirectoryPath.lastIndexOf(separator) + 1));
            setNewDirectory(directory);
            if (directory.getParentFile() == null) {
                hideDoubleDots();
            }
        } else {
            File file = new File(currentDirectoryPath + separator + selectedItem);
            if (file.isDirectory()) {
                setNewDirectory(file);
            } else if (file.canRead()) {
                displayText(file);
            }
        }
    }

    private void hideDoubleDots() {
        rootItem.getChildren().remove(0);
        areDoubleDotsHidden = true;
    }

    private void showDoubleDots() {
        rootItem.getChildren().add(0, parentDirectory);
        areDoubleDotsHidden = false;
    }

    private void setNewDirectory(File newDirectory) {
        currentDirectory = newDirectory;
        setCurrentDirectoryContent();
        updateStageTitle();
        showDefaultText();
    }

    private void showDefaultText() {
        text.setText("Choose a file to view as a text");
        text.setTextAlignment(TextAlignment.CENTER);
    }

    private void updateStageTitle() {
        Stage stage = (Stage) hbox.getScene().getWindow();
        stage.setTitle("File viewer: " + currentDirectory.getAbsolutePath());
    }

    private void setCurrentDirectoryContent() {
        removePreviousDirectoryContent();
        if (areDoubleDotsHidden) {
            showDoubleDots();
        }
        for (File file : currentDirectory.listFiles()) {
            TreeItem<String> newItem = new TreeItem<>(file.getName());
            rootItem.getChildren().add(newItem);
            if (file.isDirectory()) {
                newItem.setGraphic(getIcon("folder-icon.png"));
            } else {
                newItem.setGraphic(getIcon("file-icon.png"));
            }
        }
        sortCurrenDirectoryContent();
    }

    private void removePreviousDirectoryContent() {
        while (rootItem.getChildren().size() > 1) {
            rootItem.getChildren().remove(1);
        }
    }

    private void sortCurrenDirectoryContent() {
        ObservableList<TreeItem<String>> items = rootItem.getChildren();
        items.sort((i1, i2) -> {
            if (i1.getValue().equals("..")) {
                return -1;
            } else if (i2.getValue().equals("..")) {
                return 1;
            } else if (i1.getGraphic().getId().equals("folder-icon.png") && i2.getGraphic().getId().equals("file-icon.png")) {
                return -1;
            } else if (i1.getGraphic().getId().equals("file-icon.png") && i2.getGraphic().getId().equals("folder-icon.png")) {
                return 1;
            } else {
                return i1.getValue().compareTo(i2.getValue());
            }
        });
    }

    private void displayText(File file) throws IOException {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }
            text.setText(builder.toString());
            text.setTextAlignment(TextAlignment.LEFT);
        } catch (MalformedInputException ex) {
            text.setText(file.getName() + " can not be viewed as a text");
            text.setTextAlignment(TextAlignment.CENTER);
        }
    }

    private ImageView getIcon(String fileName) {
        ImageView iv = new ImageView(new Image(Controller.class.getResource(fileName).toString()));
        iv.setFitWidth(15);
        iv.setFitHeight(15);
        iv.setId(fileName);
        return iv;
    }
}
