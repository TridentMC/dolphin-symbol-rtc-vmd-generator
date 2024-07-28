module com.tridevmc.dolphintortcmap {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.tridevmc.dolphintortcmap to javafx.fxml;
    exports com.tridevmc.dolphintortcmap;
}