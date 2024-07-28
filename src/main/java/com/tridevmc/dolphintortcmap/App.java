package com.tridevmc.dolphintortcmap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("linker-map-to-rtc-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 620, 480);
        String css = App.class.getResource("dark-theme.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Dolphin Map to RTC VMD");
        stage.setScene(scene);

        // Check if a file was passed as an argument, if it was then tell the LinkerMapToRTCMapController to load it.
        if(!getParameters().getRaw().isEmpty()) {
            File file = new File(getParameters().getRaw().getFirst());
            LinkerMapToRTCMapController controller = fxmlLoader.getController();
            controller.loadFile(file);
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}