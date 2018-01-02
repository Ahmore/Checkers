package pl.edu.agh.szymczyk.checkers.figures;

import pl.edu.agh.szymczyk.checkers.enums.Color;

import java.io.Serializable;

/**
 * Created by Darek on 2016-12-19.
 */
public interface Figure extends Serializable {
    public int getRange();
    public Color getColor();
    public int[][] getMoveDirections();
    public int[][] getBeatDirections();
    public String getShortName();
}
