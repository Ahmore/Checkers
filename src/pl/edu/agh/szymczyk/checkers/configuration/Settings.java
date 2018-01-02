package pl.edu.agh.szymczyk.checkers.configuration;

import pl.edu.agh.szymczyk.checkers.enums.Color;

import java.io.*;

/**
 * Created by Darek on 2016-12-19.
 */
public class Settings implements Serializable {
    private static Settings instance = null;

    private Player white;
    private Player black;
    private int board_size = 8;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    private Settings() {
        Player wp = null;
        Player bp = null;

        try {
            ObjectInputStream file = new ObjectInputStream(new FileInputStream(".checkers/settings.txt"));
            wp = (Player)file.readObject();
            bp = (Player)file.readObject();

            this.white = wp;
            this.black = bp;
        } catch (IOException e) {
            // Jeżeli nie może odczytać z pliku inicjuje domyślnie
            this.white = new Player("Player1", Color.WHITE);
            this.black = new Player("Player2", Color.BLACK);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setWhitePlayer(String name) {
        this.white.setName(name);
    }

    public void setBlackPlayer(String name) {
        this.black.setName(name);
    }

    public Player getWhitePlayer() {
        return this.white;
    }

    public Player getBlackPlayer() {
        return this.black;
    }

    public void setBoardSize(int size) {
        this.board_size = size;
    }

    public int getBoardSize() {
        return this.board_size;
    }

    public void save() {
        try {
            ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(".checkers/settings.txt"));
            file.writeObject(this.white);
            file.writeObject(this.black);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
