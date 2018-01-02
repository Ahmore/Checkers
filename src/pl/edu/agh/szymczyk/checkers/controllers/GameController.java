package pl.edu.agh.szymczyk.checkers.controllers;

import pl.edu.agh.szymczyk.checkers.configuration.FinishedGameException;
import pl.edu.agh.szymczyk.checkers.configuration.Settings;
import pl.edu.agh.szymczyk.checkers.configuration.WrongMoveException;
import pl.edu.agh.szymczyk.checkers.enums.Color;
import pl.edu.agh.szymczyk.checkers.figures.Pawn;
import pl.edu.agh.szymczyk.checkers.figures.Queen;
import pl.edu.agh.szymczyk.checkers.history.HistoryList;
import pl.edu.agh.szymczyk.checkers.logic.Game;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private Game game;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new Game(Settings.getInstance(), HistoryList.getInstance());

        white_player_name.setText(game.getSettings().getWhitePlayer().getName());
        black_player_name.setText(game.getSettings().getBlackPlayer().getName());

        for (int i = 0; i < Settings.getInstance().getBoardSize(); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0/Settings.getInstance().getBoardSize());
            column.setHalignment(HPos.CENTER);
            board.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setValignment(VPos.CENTER);
            row.setPercentHeight(100.0/Settings.getInstance().getBoardSize());
            board.getRowConstraints().add(row);
        }

        for (int y = Settings.getInstance().getBoardSize() - 1; y >= 0; y--) {
            for (int x = 0; x < Settings.getInstance().getBoardSize(); x++) {
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

        if (game.isFinished()) {
            finishGame();
        }

        setActivePlayer();
        updateBoard();
    }

    private void setActivePlayer() {
        if (game.getCurrentPlayer().getColor() == Color.WHITE) {
            white_player.setOpacity(1);
            black_player.setOpacity(0.2);
        }
        else {
            white_player.setOpacity(0.2);
            black_player.setOpacity(1);
        }
    }

    private void setWinner() {
        try {
            if (game.getWinner().getColor() == Color.WHITE) {
                white_player.setOpacity(1);
                black_player.setOpacity(0.2);
            }
            else {
                white_player.setOpacity(0.2);
                black_player.setOpacity(1);
            }
        } catch (FinishedGameException e) {
            e.printStackTrace();
        }
    }

    private void updateBoard() {
        for (Node node : board.getChildren()) {
            Pane pane = (Pane) node;
            pane.getChildren().clear();

            final int px = board.getColumnIndex(pane);
            final int py = board.getRowIndex(pane);

            final int x = px;
            final int y = game.getSettings().getBoardSize() -  py - 1;

            if (game.getBoard()[x][y] != null) {
                Shape figure = null;

                if (game.getBoard()[x][y] instanceof Pawn) {
                    Circle circle = new Circle(25, 25, 15);
                    circle.setFill(Paint.valueOf(game.getBoard()[x][y].getColor().toString().toLowerCase()));

                    figure = (Shape) circle;
                }
                else if (game.getBoard()[x][y] instanceof Queen) {
                    Polygon polygon = new Polygon();
                    polygon.setFill(Paint.valueOf(game.getBoard()[x][y].getColor().toString().toLowerCase()));
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



                if (!game.hasPossibitiesWith(x, y)) {
                    figure.getStyleClass().add("game-figure-impossible");
                }
                else {
                    figure.getStyleClass().remove("game-figure-impossible");
                }

                final Shape final_figure = figure;

                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent me) {
                        if (game.hasPossibitiesWith(x, y)) {
                            showSelect(final_figure);
                            showPossibilities(x, y);
                        }
                    }
                });
                pane.getChildren().add(figure);
            }
        }
    }

    private void showSelect(Shape figure) {
        hideSelect();
        figure.getStyleClass().add("board-figure-current");
    }

    private void hideSelect() {
        for (Node node : board.getChildren()) {
            Pane pane = (Pane) node;
            if (!pane.getChildren().isEmpty()) {
                pane.getChildren().get(0).getStyleClass().remove("board-figure-current");
            }
        }
    }

    private void showPossibilities(int x, int y) {
        LinkedList<int[]> cells = game.getPossibilitiesWith(x, y);

        hideAllPossibilities();

        for (int[] p : cells) {
            final int x2 = p[0];
            final int y2 = p[1];

            Pane cell = getGridPane(x2, game.getSettings().getBoardSize() - y2 - 1);

            cell.getStyleClass().add("board-cell-possible");

            cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    try {
                        game.move(x, y, x2, y2);

                        hideSelect();
                        hideAllPossibilities();
                        unbindAll();

                        updateBoard();

                        if (game.isFinished()) {
                            setWinner();
                            finishGame();
                        }
                        else {
                            setActivePlayer();
                        }
                    } catch (WrongMoveException e) {
                        System.out.println("Wrong move!");
                    } catch (FinishedGameException e) {
                        System.out.println("Game over!");
                    }
                }
            });
        }
    }

    private void hideAllPossibilities() {
        for (Node p : board.getChildren()) {
            ((Pane) p).getStyleClass().remove("board-cell-possible");
        }
    }

    private void finishGame() {
        try {
            winner.setText("Wygrał/a " + game.getWinner().getName());
        } catch (FinishedGameException e) {
            e.printStackTrace();
        }
        end_pane.setVisible(true);
    }

    private void unbindAll() {
        for (Node p : board.getChildren()) {
            ((Pane) p).setOnMouseClicked(null);
        }
    }

    private Pane getGridPane(int x, int y) {
        for (Node p : board.getChildren()) {
            Node pc = p;
            if (board.getColumnIndex(p) == x && board.getRowIndex(p) == y) {
                return (Pane) p;
            }
        }

        return null;
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
