package pl.edu.agh.szymczyk.checkers.controllers;

import pl.edu.agh.szymczyk.checkers.history.History;
import pl.edu.agh.szymczyk.checkers.history.HistoryList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Darek on 2016-12-26.
 */
public class HistoryListController implements Initializable {
    @FXML
    private ListView<Text> history_list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Text> items = FXCollections.observableArrayList();

        for (History h : HistoryList.getInstance()) {
            final History history = h;
            Text players = new Text(history.getSettings().getWhitePlayer().getName() + " vs " + history.getSettings().getBlackPlayer().getName() + " " + history.getStartDateFormated());

            players.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    showGame(history, me);
                }
            });
            items.add(players);
        }

        history_list.setItems(items);
    }

    private void showGame(History history, MouseEvent mouseEvent) {
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/edu/agh/szymczyk/checkers/templates/history_game.fxml"));
            loader.setController(new HistoryGameController(history));

            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void backToMenu(ActionEvent actionEvent) {
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
}
