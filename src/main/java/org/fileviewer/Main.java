package org.fileviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("file-viewer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("File viewer: " + System.getProperty("user.dir"));
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(200);
        stage.show();
    }
}