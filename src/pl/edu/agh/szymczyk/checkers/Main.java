package pl.edu.agh.szymczyk.checkers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        createDirectories();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/pl/edu/agh/szymczyk/checkers/templates/home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Checkers");
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void createDirectories() {
        File dirs = new File(".checkers/histories");

        if (!dirs.exists()) {
            dirs.mkdirs();
        }
    }
}

