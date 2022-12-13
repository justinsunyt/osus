package org.cis1200.osus;

import org.cis1200.osus.components.Circle;
import org.cis1200.osus.components.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
    private GameScreen gameModel;
    private TreeSet<Note> notes;

    @BeforeEach
    public void init() {
        gameModel = new GameScreen();
        notes = new TreeSet<>();
        notes.add(new Circle(50, 50, 1, 5, 5, 1, Color.RED));
        notes.add(new Circle(50, 50, 2, 5, 5, 1, Color.RED));
        notes.add(new Circle(50, 50, 3, 5, 5, 1, Color.RED));
        notes.add(new Circle(50, 50, 4, 5, 5, 1, Color.RED));
        notes.add(new Circle(50, 50, 5, 5, 5, 1, Color.RED));
        notes.add(new Circle(50, 50, 6, 5, 5, 1, Color.RED));
        gameModel.setNotes(notes);
    }

    @Test
    public void testFullCombo300() {
        for (Note note : gameModel.getNotes()) {
            note.setHitScore(300);
            gameModel.addScore(note);
        }
        int expectedScore = 0;
        int expectedCombo = 0;
        for (Note note : gameModel.getNotes()) {
            expectedCombo++;
            expectedScore += 300 * expectedCombo;
        }
        assertEquals(expectedScore, gameModel.getScore());
        assertEquals(expectedCombo, gameModel.getCombo());
    }

    @Test
    public void testFullCombo100() {
        for (Note note : gameModel.getNotes()) {
            note.setHitScore(100);
            gameModel.addScore(note);
        }
        int expectedScore = 0;
        int expectedCombo = 0;
        for (Note note : gameModel.getNotes()) {
            expectedCombo++;
            expectedScore += 100 * expectedCombo;
        }
        assertEquals(expectedScore, gameModel.getScore());
        assertEquals(expectedCombo, gameModel.getCombo());
    }

    @Test
    public void testFullCombo50() {
        for (Note note : gameModel.getNotes()) {
            note.setHitScore(50);
            gameModel.addScore(note);
        }
        int expectedScore = 0;
        int expectedCombo = 0;
        for (Note note : gameModel.getNotes()) {
            expectedCombo++;
            expectedScore += 50 * expectedCombo;
        }
        assertEquals(expectedScore, gameModel.getScore());
        assertEquals(expectedCombo, gameModel.getCombo());
    }

    @Test
    public void testRandomCombo() {
        int expectedScore = 0;
        int expectedCombo = 0;
        for (Note note : gameModel.getNotes()) {
            if (note.getQuarterNote() % 2 == 0) {
                note.setHitScore(50);

            } else if (note.getQuarterNote() % 3 == 0) {
                note.setHitScore(100);
            } else {
                note.setHitScore(300);
            }
            gameModel.addScore(note);
        }
        for (Note note : gameModel.getNotes()) {
            expectedCombo++;
            if (note.getQuarterNote() % 2 == 0) {
                expectedScore += 50 * expectedCombo;
            } else if (note.getQuarterNote() % 3 == 0) {
                expectedScore += 100 * expectedCombo;
            } else {
                expectedScore += 300 * expectedCombo;
            }
        }
        assertEquals(expectedScore, gameModel.getScore());
        assertEquals(expectedCombo, gameModel.getCombo());
    }

    @Test
    public void testAllMiss() {
        for (Note note : gameModel.getNotes()) {
            note.miss();
            gameModel.addScore(note);
        }
        int expectedScore = 0;
        int expectedCombo = 0;
        assertEquals(expectedScore, gameModel.getScore());
        assertEquals(expectedCombo, gameModel.getCombo());
    }

    @Test
    public void testComboBreak() {
        int expectedScore = 0;
        for (Note note : gameModel.getNotes()) {
            if (note.getQuarterNote() == 4) {
                note.miss();
            } else {
                note.setHitScore(300);
            }
            gameModel.addScore(note);
        }
        int expectedCombo = 0;
        int expectedMaxCombo = 3;
        for (Note note : gameModel.getNotes()) {
            expectedCombo++;
            if (note.getQuarterNote() == 4) {
                expectedCombo = 0;
            } else {
                expectedScore += 300 * expectedCombo;
            }
        }
        assertEquals(expectedScore, gameModel.getScore());
        assertEquals(expectedCombo, gameModel.getCombo());
        assertEquals(expectedMaxCombo, gameModel.getMaxCombo());
    }
}
