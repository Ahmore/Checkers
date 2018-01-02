package pl.edu.agh.szymczyk.checkers.history;

import pl.edu.agh.szymczyk.checkers.configuration.GameDoesntEndException;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Darek on 2016-12-26.
 */
public class HistoryList implements Iterable<History> {
    private static HistoryList instance = null;
    private LinkedList<History> histories = new LinkedList<>();

    public static HistoryList getInstance() {
        if (instance == null) {
            instance = new HistoryList();
        }

        return instance;
    }

    private HistoryList() {
        // Pobiera historie z plików
        File directory = new File(".checkers/histories");
        String[] files = directory.list();

        for (String name : files) {
            try {
                ObjectInputStream file = new ObjectInputStream(new FileInputStream(".checkers/histories/" + name));
                this.histories.add((History)file.readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(History history) throws GameDoesntEndException {
        if (!history.isFinished()) {
            throw new GameDoesntEndException();
        }

        // Dodaje do listy
        this.histories.add(history);

        // Zapisuje do pliku
        try {
            ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(".checkers/histories/pl.edu.agh.szymczyk.checkers.history" + history.getStartDate().getTime() + ".txt"));
            file.writeObject(history);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public Iterable<History> byDuration() {
        LinkedList<History> copy = (LinkedList<History>) this.histories.clone();
        copy.sort(new HistoryDurationComparator());

        return (Iterable<History>) copy;
    }

    public Iterator<History> iterator() {
        return this.histories.iterator();
    }
}
