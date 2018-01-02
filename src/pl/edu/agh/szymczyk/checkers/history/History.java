package pl.edu.agh.szymczyk.checkers.history;

import pl.edu.agh.szymczyk.checkers.configuration.GameDoesntEndException;
import pl.edu.agh.szymczyk.checkers.configuration.GameEndedException;
import pl.edu.agh.szymczyk.checkers.configuration.Settings;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Darek on 2016-12-26.
 */
public class History implements Serializable {
    private Settings settings;
    private Date start = null;
    private Date end = null;
    private DateFormat date_format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    LinkedList<HistoryState> boards = new LinkedList<HistoryState>();

    public History(Settings settings, HistoryState state) {
        this.settings = settings;
        start = new Date();

        boards.add(state);
    }

    public void add(HistoryState state) throws GameEndedException {
        if (end != null) {
            throw new GameEndedException();
        }

        boards.add(state);
    }

    public HistoryState getLast() {
        return boards.getLast();
    }

    public LinkedList<HistoryState> getAll() {
        return this.boards;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Date getStartDate() {
        return this.start;
    }

    public String getStartDateFormated() {
        return date_format.format(this.start);
    }

    public long getDuration() throws GameDoesntEndException {
        if (end == null) {
            throw new GameDoesntEndException();
        }

        return Duration.ofMillis(end.getTime() - start.getTime()).toMillis()/1000;
    }

    public void end() {
        end = new Date();
    }

    public Date getEndDate() {
        return this.end;
    }

    public String getEndDateFormated() throws GameDoesntEndException {
        if (end == null) {
            throw new GameDoesntEndException();
        }

        return date_format.format(this.end);
    }

    public Boolean isFinished() {
        return this.end != null;
    }
}
