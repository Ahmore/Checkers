package pl.edu.agh.szymczyk.checkers.configuration;

import pl.edu.agh.szymczyk.checkers.enums.Color;

import java.io.Serializable;

/**
 * Created by Darek on 2016-12-19.
 */
public class Player implements Serializable {
    private String name;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }
}
