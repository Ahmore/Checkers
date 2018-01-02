package pl.edu.agh.szymczyk.checkers.history;

import pl.edu.agh.szymczyk.checkers.configuration.Player;
import pl.edu.agh.szymczyk.checkers.figures.Figure;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Darek on 2016-12-26.
 */
public class HistoryState implements Serializable {
    private Figure[][] board;
    private Player player;

    public HistoryState(Figure[][] board, Player current_player) {
        this.board = deepCopy(board);
        this.player = current_player;
    }

    public Figure[][] getBoard() {
        return this.board;
    }

    public Player getPlayer() {
        return player;
    }

    private Figure[][] deepCopy(Figure[][] original) {
        if (original == null) {
            return null;
        }

        final Figure[][] result = new Figure[original.length][];

        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return result;
    }
}
