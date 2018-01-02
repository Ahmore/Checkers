package pl.edu.agh.szymczyk.checkers.logic;

import pl.edu.agh.szymczyk.checkers.configuration.*;
import pl.edu.agh.szymczyk.checkers.enums.Color;
import pl.edu.agh.szymczyk.checkers.figures.Figure;
import pl.edu.agh.szymczyk.checkers.figures.Pawn;
import pl.edu.agh.szymczyk.checkers.figures.Queen;
import pl.edu.agh.szymczyk.checkers.history.History;
import pl.edu.agh.szymczyk.checkers.history.HistoryList;
import pl.edu.agh.szymczyk.checkers.history.HistoryState;

import java.util.LinkedList;

/**
 * Created by Darek on 2016-12-19.
 */
public class Game {
    private Figure[][] board;
    private Player current;
    private Settings settings;
    private History history;
    private HistoryList history_list;
    LinkedList<int[]>[][] possibilities;

    private int[] lf = {-1, -1}; // Ostatni ruch w kolejce -1, -1 domyślnie
    private Boolean lfwb = false; // Czy ostatni ruch w kolejce był biciem


    public Game(Settings settings, HistoryList history_list) {
        this.settings = settings;
        this.history_list = history_list;
        this.current = settings.getWhitePlayer();
        this.board = new Figure[this.settings.getBoardSize()][this.settings.getBoardSize()];

        for (int y = 0; y < this.settings.getBoardSize(); y++) {
            for (int x = y%2; x < this.settings.getBoardSize(); x += 2) {
                if (y < 3) {
                    this.board[x][y] = new Pawn(Color.WHITE);
                }
                else if (y < this.settings.getBoardSize() && y > this.settings.getBoardSize() - 4) {
                    this.board[x][y] = new Pawn(Color.BLACK);
                }
            }
        }

//        this.board[0][0] = new Queen(Color.WHITE);
//        this.board[3][3] = new Pawn(Color.BLACK);
//        this.board[2][6] = new Pawn(Color.BLACK);


        this.history = new History(this.settings, new HistoryState(this.board, this.getCurrentPlayer()));
        this.generatePossibilities();
    }

    public Figure[][] getBoard() {
        return this.board;
    }

    public Settings getSettings() { return this.settings; }

    public History getHistory() {
        return this.history;
    }

    public Player getCurrentPlayer() {
        return this.current;
    }

    public boolean isFinished() {
        return !this.hasMoreMoves();
    }

    public Player getWinner() throws FinishedGameException {
        if (!this.isFinished()) {
            throw new FinishedGameException();
        }

        if (this.current.getColor() == Color.WHITE) {
            return this.settings.getBlackPlayer();
        }
        else {
            return this.settings.getWhitePlayer();
        }
    }

    public void move(int x1, int y1, int x2, int y2) throws WrongMoveException, FinishedGameException {
        if (this.isFinished()) {
            throw new FinishedGameException();
        }

        // Sprawdza czy ruch jest możliwy
        if (!this.isPossible(x1, y1, x2, y2)) {
            throw new WrongMoveException();
        }

        // Porusza pionkiem
        this.board[x2][y2] = this.board[x1][y1];
        this.board[x1][y1] = null;

        // Ustawia ostatni pionek w kolejce
        this.lf = new int[]{x2, y2};
        this.lfwb = this.isBeat(x1, y1, x2, y2);

        // Usuwa wszystkie pionki po drodze
        this.deleteOnTheWay(x1, y1, x2, y2);

        // Jeśli to konieczne zamienia pionka na damkę
        if (this.changeIfItIsNecessary(x2, y2)) {
            this.nextPlayer();
        }
        else {
            this.generatePossibilities();

            // Jeśli nie ma więcej ruchów zmienia kolejkę
            if (!this.hasMoreMoves()) {
                this.nextPlayer();
            }
        }

        // Aktualizuje historię
        try {
            this.history.add(new HistoryState(this.board, this.getCurrentPlayer()));
        } catch (GameEndedException e) {
            e.printStackTrace();
        }

        // Jeśli gra się skończyła
        if (isFinished()) {
            this.history.end();

            try {
                this.history_list.add(history);
            } catch (GameDoesntEndException e) {
                e.printStackTrace();
            }
        }
    }

    public LinkedList<int[]>[][] getPossibilities() {
        return this.possibilities;
    }

    public Boolean hasPossibitiesWith(int x, int y) {
        return (!this.getPossibilities()[x][y].isEmpty());
    }

    public LinkedList<int[]> getPossibilitiesWith(int x, int y) {
        return this.getPossibilities()[x][y];
    }

    public void printBoard() {
        for (int y = (this.settings.getBoardSize() - 1); y >= 0; y--) {
            for (int x = 0; x < this.settings.getBoardSize(); x++) {
                if (board[x][y] != null) {
                    System.out.print(this.board[x][y].getShortName());
                }
                else {
                    System.out.print("[]");
                }
            }
            System.out.print("\n");
        }
    }

    public void printPossibilities() {
        for (int y = 0; y < this.settings.getBoardSize(); y++) {
            for (int x = y%2; x < this.settings.getBoardSize(); x += 2) {
                for (int[] p : this.possibilities[x][y]) {
                    System.out.println("(" + x + ", " + y + ")" + " -> " + "(" + p[0] + ", " + p[1] + ")");
                }
            }
        }
    }

    public void printPossibilitiesWith(int x, int y) {
        for (int[] p : this.possibilities[x][y]) {
            System.out.println("(" + x + ", " + y + ")" + " -> " + "(" + p[0] + ", " + p[1] + ")");
        }
    }

    private void nextPlayer() {
        this.lf = new int[]{-1, -1};
        this.lfwb = false;


        if (this.current.getColor() == Color.WHITE) {
            this.current = this.settings.getBlackPlayer();
        }
        else {
            this.current = this.settings.getWhitePlayer();
        }

        this.generatePossibilities();
    }

    private Boolean isPossible(int x1, int y1, int x2, int y2) {
        for (int[] p : this.possibilities[x1][y1]) {
            if (x2 == p[0] && y2 == p[1]) {
                return true;
            }
        }

        return false;
    }

    private Boolean hasMoreMoves() {
        for (int y = 0; y < this.settings.getBoardSize(); y++) {
            for (int x = y%2; x < this.settings.getBoardSize(); x += 2) {
                if (!this.possibilities[x][y].isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void generatePossibilities() {
        Boolean beat = false;
        this.possibilities = this.generatePossibilitiesStructure();

        if (this.lf[0] == -1 || (this.lf[0] != -1 && this.lfwb == true)) {
            // Wyszukuje możliwości bicia
            for (int y = 0; y < this.settings.getBoardSize(); y++) {
                for (int x = y % 2; x < this.settings.getBoardSize(); x += 2) {
                    Figure f = this.board[x][y];

                    // Jeśli pole jest puste lub jeśli pionek nie należy do gracza aktualnie wykonującego ruch nie sprawdza ich
                    if (f == null || f.getColor() != this.current.getColor()) {
                        continue;
                    }

                    // Jeśli to nie pierwszy ruch w kolejce i to nie ta figura to idzie dalej
                    if (this.lf[0] != -1 && (x != this.lf[0] || y != this.lf[1])) {
                        continue;
                    }

                    // Pobiera zasięg figury
                    int range = f.getRange();

                    // Sprawdza możliwości bicia
                    int[][] directions = f.getBeatDirections();

                    // Ustawia kierunek poruszania się figur
                    int d = 1;
                    if (f.getColor() == Color.BLACK) {
                        d = -1;
                    }

                    for (int i = 0; i < directions.length; i++) {
                        int dx = directions[i][0];
                        int dy = directions[i][1]*1;

                        for (int j = 1; j <= range; j++) {
                            int vx = dx * j;
                            int vy = dy * j;

                            // Jeśli ruch wyrzuca poza planszę
                            if (!this.inBoard(x + vx, y + vy)) {
                                break;
                            }

                            if (this.board[x + vx][y + vy] != null) {
                                // Jeśli pole jest koloru aktualnie wykonującego ruch gracza
                                if (this.board[x + vx][y + vy].getColor() == this.current.getColor()) {
                                    break;
                                }

                                // Jeśli można za nim stanąć tzn że można go zbić
                                if (inBoard(x + vx + dx, y + vy + dy) && this.board[x + vx + dx][y + vy + dy] == null) {
                                    beat = true;
                                    this.possibilities[x][y].add(new int[]{x + vx + dx, y + vy + dy});
                                }

                                break;
                            }
                        }
                    }
                }
            }

            if (!beat && this.lf[0] == -1) {
                // Wyszukuje możliwości zwykłych ruchów pod warunkiem że nie ma bić
                for (int y = 0; y < this.settings.getBoardSize(); y++) {
                    for (int x = y % 2; x < this.settings.getBoardSize(); x += 2) {
                        Figure f = this.board[x][y];

                        // Jeśli pole jest puste lub jeśli pionek nie należy do gracza aktualnie wykonującego ruch nie sprawdza ich
                        if (f == null || f.getColor() != this.current.getColor()) {
                            continue;
                        }

                        // Pobiera zasięg figury
                        int range = f.getRange();

                        // Sprawdza możliwości ruchu
                        int[][] directions = f.getMoveDirections();

                        // Ustawia kierunek poruszania się figur
                        int d = 1;
                        if (f.getColor() == Color.BLACK) {
                            d = -1;
                        }

                        for (int i = 0; i < directions.length; i++) {
                            int dx = directions[i][0];
                            int dy = directions[i][1]*d;

                            for (int j = 1; j <= range; j++) {
                                int vx = dx * j;
                                int vy = dy * j;

                                // Jeśli ruch wyrzuca poza planszę
                                if (!this.inBoard(x + vx, y + vy)) {
                                    break;
                                }

                                // Jeśli pole nie jest puste to kończy
                                if (this.board[x + vx][y + vy] != null) {
                                    break;
                                }

                                this.possibilities[x][y].add(new int[]{x + vx, y + vy});
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean inBoard(int x, int y) {
        return ((x >= 0) && (x < this.settings.getBoardSize()) && (y >= 0) && (y < this.settings.getBoardSize()));
    }

    private LinkedList<int[]>[][] generatePossibilitiesStructure() {
        LinkedList<int[]>[][] result = new LinkedList[this.settings.getBoardSize()][this.settings.getBoardSize()];

        for (int y = 0; y < this.settings.getBoardSize(); y++) {
            for (int x = y%2; x < this.settings.getBoardSize(); x += 2) {
                result[x][y] = new LinkedList<>();
            }
        }

        return result;
    }

    private void deleteOnTheWay(int x1, int y1, int x2, int y2) {
        int dx = Integer.signum(x2 - x1);
        int dy = Integer.signum(y2 - y1);
        int cx = x1 + dx;
        int cy = y1 + dy;


        while (cx != x2) {
            this.board[cx][cy] = null;

            cx += dx;
            cy += dy;
        }
    }

    private Boolean changeIfItIsNecessary(int x, int y) {
        Figure f = this.board[x][y];

        // Jeśli to damki to nic nie robi
        if (f instanceof Queen) {
            return false;
        }

        if (f.getColor() == Color.BLACK && y == 0) {
            this.board[x][y] = new Queen(Color.BLACK);

            return true;
        }
        else if (f.getColor() == Color.WHITE && y == this.settings.getBoardSize()-1) {
            this.board[x][y] = new Queen(Color.WHITE);

            return true;
        }

        return false;
    }

    private Boolean isBeat(int x1, int y1, int x2, int y2) {
        int dx = Integer.signum(x2 - x1);
        int dy = Integer.signum(y2 - y1);
        int cx = x1 + dx;
        int cy = y1 + dy;


        while (cx != x2) {
            if (this.board[cx][cy] != null) {
                return true;
            }

            cx += dx;
            cy += dy;
        }

        return false;
    }
}