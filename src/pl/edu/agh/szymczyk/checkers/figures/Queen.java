package pl.edu.agh.szymczyk.checkers.figures;

import pl.edu.agh.szymczyk.checkers.enums.Color;

/**
 * Created by Darek on 2016-12-19.
 */
public class Queen implements Figure {
    private int range = 8;
    private int [][] move_directions = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
    private int [][] beat_directions = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
    private Color color;
    private String short_name = "Q";

    public Queen(Color color) {
        this.color = color;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public int[][] getMoveDirections() {
        return this.move_directions;
    }

    @Override
    public int[][] getBeatDirections() {
        return this.beat_directions;
    }

    @Override
    public String getShortName() {
        return this.color.toString().charAt(0) + "" + this.short_name;
    }
}
