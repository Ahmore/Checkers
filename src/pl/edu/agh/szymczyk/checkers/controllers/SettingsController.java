package pl.edu.agh.szymczyk.checkers.controllers;

import pl.edu.agh.szymczyk.checkers.configuration.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Darek on 2016-12-25.
 */
public class SettingsController implements Initializable {
    @FXML
    private TextField white_player;

    @FXML
    private TextField black_player;

    public void save(ActionEvent actionEvent) {
        Settings.getInstance().setWhitePlayer(white_player.getText());
        Settings.getInstance().setBlackPlayer(black_player.getText());
        Settings.getInstance().save();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/pl/edu/agh/szymczyk/checkers/templates/home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        white_player.setText(Settings.getInstance().getWhitePlayer().getName());
        black_player.setText(Settings.getInstance().getBlackPlayer().getName());
    }
}
