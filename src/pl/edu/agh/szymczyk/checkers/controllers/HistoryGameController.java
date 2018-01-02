package pl.edu.agh.szymczyk.checkers.controllers;

import pl.edu.agh.szymczyk.checkers.enums.Color;
import pl.edu.agh.szymczyk.checkers.figures.Figure;
import pl.edu.agh.szymczyk.checkers.figures.Pawn;
import pl.edu.agh.szymczyk.checkers.figures.Queen;
import pl.edu.agh.szymczyk.checkers.history.History;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistoryGameController implements Initializable {
    private History history;
    int i = 0;

    @FXML
    private Pane white_player;

    @FXML
    private Label white_player_name;

    @FXML
    private Pane black_player;

    @FXML
    private Label black_player_name;

    @FXML
    private Label winner;

    @FXML
    private Pane end_pane;

    @FXML
    private AnchorPane game_pane;

    @FXML
    private GridPane board;

    @FXML
    private Button prev;

    @FXML
    private Button next;

    public HistoryGameController(History history) {
        this.history = history;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        white_player_name.setText(history.getSettings().getWhitePlayer().getName());
        black_player_name.setText(history.getSettings().getBlackPlayer().getName());

        for (int i = 0; i < history.getSettings().getBoardSize(); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0/history.getSettings().getBoardSize());
            column.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setValignment(VPos.CENTER);
            row.setPercentHeight(100.0/history.getSettings().getBoardSize());
            board.getRowConstraints().add(row);
        }

        for (int y = history.getSettings().getBoardSize() - 1; y >= 0; y--) {
            for (int x = 0; x < history.getSettings().getBoardSize(); x++) {
                Pane cell = new Pane();
                cell.setCenterShape(true);
                cell.getStyleClass().add("board-cell");

                if (((x%2)+y)%2 == 0) {
                    cell.getStyleClass().add("board-cell-light");
                }
                else {
                    cell.getStyleClass().add("board-cell-dark");
                }

                board.add(cell, x, y);
            }
        }

        updateNavigation();
        updateBoard();
        setActivePlayer();
    }

    public void prev() {
        if (!isOk(i-1)) {
            return;
        }

        this.i--;

        updateBoard();
        setActivePlayer();


        updateNavigation();
    }

    public void next() {
        if (!isOk(i+1)) {
            return;
        }

        this.i++;

        updateBoard();

        if (isOk(i+1)) {
            setActivePlayer();
        }


        updateNavigation();
    }

    private Boolean isOk(int x) {
        return (x >= 0 && x < this.history.getAll().size());
    }

    private void updateNavigation() {
        if (i == 0 && i == this.history.getAll().size()-1) {
            prev.setDisable(false);
            next.setDisable(false);
        }
        else if (i == 0) {
            prev.setDisable(true);
            next.setDisable(false);
        }
        else if (i == this.history.getAll().size()-1) {
            prev.setDisable(false);
            next.setDisable(true);
        }
        else {
            prev.setDisable(false);
            next.setDisable(false);
        }
    }

    private void setActivePlayer() {
        if (history.getAll().get(i).getPlayer().getColor() == Color.WHITE) {
            white_player.setOpacity(1);
            black_player.setOpacity(0.2);
        }
        else {
            white_player.setOpacity(0.2);
            black_player.setOpacity(1);
        }
    }

    private void updateBoard() {
        Figure[][] history_board = history.getAll().get(i).getBoard();

        for (Node node : board.getChildren()) {
            Pane pane = (Pane) node;
            pane.getChildren().clear();

            final int px = board.getColumnIndex(pane);
            final int py = board.getRowIndex(pane);

            final int x = px;
            final int y = history.getSettings().getBoardSize() -  py - 1;

            if (history_board[x][y] != null) {
                Shape figure = null;

                if (history_board[x][y] instanceof Pawn) {
                    Circle circle = new Circle(25, 25, 15);
                    circle.setFill(Paint.valueOf(history_board[x][y].getColor().toString().toLowerCase()));

                    figure = (Shape) circle;
                }
                else if (history_board[x][y] instanceof Queen) {
                    Polygon polygon = new Polygon();
                    polygon.setFill(Paint.valueOf(history_board[x][y].getColor().toString().toLowerCase()));
                    polygon.getPoints().addAll(new Double[]{
                            10.0, 15.0,
                            10.0, 35.0,
                            40.0, 35.0,
                            40.0, 15.0,
                            32.5, 25.0,
                            25.0, 15.0,
                            17.5, 25.0 });

                    figure = (Shape) polygon;
                }

                pane.getChildren().add(figure);
            }
        }
    }

    public void backToMenu(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/pl/edu/agh/szymczyk/checkers/templates/history_list.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
