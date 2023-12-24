module org.fileviewer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.fileviewer to javafx.fxml;
    exports org.fileviewer;
}