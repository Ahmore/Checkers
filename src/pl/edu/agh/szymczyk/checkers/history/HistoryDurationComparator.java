package pl.edu.agh.szymczyk.checkers.history;

import pl.edu.agh.szymczyk.checkers.configuration.GameDoesntEndException;

import java.util.Comparator;

/**
 * Created by Darek on 2016-12-26.
 */
public class HistoryDurationComparator implements Comparator<History> {
    @Override
    public int compare(History h1, History h2) {
        int r = 0;

        try {
            r = Long.signum(h1.getDuration() - h2.getDuration());
        } catch (GameDoesntEndException e) {
            e.printStackTrace();
        }

        return r;
    }
}
