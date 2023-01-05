package org.dsc.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MetaDataProgram extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MetaDataProgram.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 700, 800);
        stage.setTitle("Tika Meta Data Analyser");
        stage.getIcons().add(new Image(Objects.requireNonNull(MetaDataProgram.class.getResource("tika.png")).openStream()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}