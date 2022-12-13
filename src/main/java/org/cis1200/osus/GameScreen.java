package org.cis1200.osus;

import org.cis1200.osus.components.*;
import org.cis1200.osus.components.Button;
import org.cis1200.osus.components.Cursor;
import org.cis1200.osus.utils.FileLineIterator;
import org.cis1200.osus.utils.Screen;
import org.cis1200.osus.utils.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * GameScreen
 *
 * This class holds the primary game logic for how different objects interact
 * with one another.
 */
public class GameScreen extends JPanel {
    private final TreeSet<Note> notes = new TreeSet<>();
    private Note currentNote;
    private final org.cis1200.osus.components.Cursor cursor = new Cursor(0, 0, 30, 30);
    private final org.cis1200.osus.components.Button startButton = new Button(
            Screen.SCREEN_WIDTH - 250, Screen.SCREEN_HEIGHT - 300, 200, 200,
            "files/images/start.png"
    );

    private boolean playing = false; // whether the game is running
    private int mouseX;
    private int mouseY;
    private String beatmap; // beatmap file location
    private String name; // song name
    private int bpm; // song bpm
    private int ar; // song approach rate
    private int cs; // song circle size
    private long offset; // beatmap offset
    private long startTime;
    private long lastTick;
    private boolean songStarted = false;
    private int score;
    private int combo;
    private int rawScore; // used to calculate accuracy
    private int totalRawScore;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 1000 / 240;

    public GameScreen() {
        // creates border around the screen
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.BLACK);

        // The timer is an object which triggers an action periodically with the
        // given INTERVAL. We register an ActionListener with this timer, whose
        // actionPerformed() method is called each time the timer triggers. We
        // define a helper method called tick() that actually does everything
        // that should be done in a single time step.
        Timer timer = new Timer(INTERVAL, this::tick);
        timer.start();

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_X) {
                    if (currentNote != null && currentNote.getIfHitScore() > 0) {
                        if (mouseX >= currentNote.getPx() - currentNote.getWidth() / 2
                                && mouseX <= currentNote.getPx() + currentNote.getWidth() / 2) {
                            if (mouseY >= currentNote.getPy() - currentNote.getHeight() / 2
                                    && mouseY <= currentNote.getPy()
                                            + currentNote.getHeight() / 2) {
                                if (currentNote.getIfHitScore() == 50) {
                                    currentNote.setHitScore(50);
                                }
                                if (currentNote.getIfHitScore() == 100) {
                                    currentNote.setHitScore(100);
                                }
                                if (currentNote.getIfHitScore() == 300) {
                                    currentNote.setHitScore(300);
                                }
                                currentNote.hit();
                                currentNote.animateHit();
                                if (currentNote.getClass() == Circle.class) {
                                    combo += 1;
                                    score += combo * currentNote.getHitScore();
                                    rawScore += currentNote.getHitScore();
                                    totalRawScore += 300;
                                }
                            }
                        }
                    }
                    if (!startButton.getDisabled()) {
                        if (mouseX >= startButton.getPx()
                                && mouseX <= startButton.getPx() + startButton.getWidth()) {
                            if (mouseY >= startButton.getPy()
                                    && mouseY <= startButton.getPy() + startButton.getHeight()) {
                                playing = true;
                                startTime = System.currentTimeMillis();
                                startButton.setDisabled();
                                Sound.playSound("files/sounds/start.wav");
                            }
                        }
                    }

                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_X) {
                    if (currentNote != null && currentNote.getClass() == Slider.class
                            && currentNote.getHit() && !((Slider) currentNote).getReleased()) {
                        Slider currentSlider = (Slider) currentNote;
                        if (currentSlider.getHorizontal()) {
                            if (mouseX >= currentNote.getPx()
                                    + currentSlider.getApproachCircleLocation()
                                    - currentNote.getWidth() / 2
                                    && mouseX <= currentNote.getPx()
                                            + currentSlider.getApproachCircleLocation()
                                            + currentNote.getWidth() / 2) {
                                if (mouseY >= currentNote.getPy() - currentNote.getHeight() / 2
                                        && mouseY <= currentNote.getPy()
                                                + currentSlider.getApproachCircleLocation()
                                                + currentNote.getHeight() / 2) {
                                    if (currentNote.getIfHitScore() == 50) {
                                        currentNote.setHitScore(50);
                                    }
                                    if (currentNote.getIfHitScore() == 100) {
                                        currentNote.setHitScore(100);
                                    }
                                    if (currentNote.getIfHitScore() == 300) {
                                        currentNote.setHitScore(300);
                                    }
                                } else {
                                    currentNote.setHitScore(100);
                                }
                            } else {
                                currentNote.setHitScore(100);
                            }
                        } else {
                            if (mouseX >= currentNote.getPx() - currentNote.getWidth() / 2
                                    && mouseX <= currentNote.getPx() + currentNote.getWidth() / 2) {
                                if (mouseY >= currentNote.getPy()
                                        + currentSlider.getApproachCircleLocation()
                                        - currentNote.getHeight() / 2
                                        && mouseY <= currentNote.getPy()
                                                + currentNote.getHeight() / 2) {
                                    if (currentNote.getIfHitScore() == 50) {
                                        currentNote.setHitScore(50);
                                    }
                                    if (currentNote.getIfHitScore() == 100) {
                                        currentNote.setHitScore(100);
                                    }
                                    if (currentNote.getIfHitScore() == 300) {
                                        currentNote.setHitScore(300);
                                    }
                                } else {
                                    currentNote.setHitScore(100);
                                }
                            } else {
                                currentNote.setHitScore(100);
                            }
                        }
                        currentSlider.release();
                        combo += 1;
                        score += combo * currentNote.getHitScore();
                        rawScore += currentNote.getHitScore();
                        totalRawScore += 300;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                cursor.setPx(e.getX() - cursor.getWidth() / 2);
                cursor.setPy(e.getY() - cursor.getHeight() / 2);
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
    }

    public void loadBeatmap() {
        this.beatmap = "HarumachiClover";
        FileLineIterator fileLineIterator = new FileLineIterator(
                "files/beatmaps/" + beatmap + ".txt"
        );
        while (fileLineIterator.hasNext()) {
            String line = fileLineIterator.next();
            String[] strings = line.split(", ");
            if (strings[0].equals("D")) {
                this.name = strings[1];
                this.bpm = Integer.parseInt(strings[2]);
                this.ar = Integer.parseInt(strings[3]);
                this.cs = Integer.parseInt(strings[4]);
                this.offset = Integer.parseInt(strings[5]);
            }
            if (strings[0].equals("C")) {
                this.notes.add(
                        new Circle(
                                Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                                Integer.parseInt(strings[3]), this.ar, this.cs,
                                Integer.parseInt(strings[4]),
                                new Color(
                                        Integer.parseInt(strings[5]), Integer.parseInt(strings[6]),
                                        Integer.parseInt(strings[7]), Integer.parseInt(strings[8])
                                )
                        )
                );
            }
            if (strings[0].equals("S")) {
                this.notes.add(
                        new Slider(
                                Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                                Integer.parseInt(strings[3]), strings[4].equals("H"), Integer.parseInt(strings[5]),
                                Integer.parseInt(strings[6]), this.ar, this.cs,
                                Integer.parseInt(strings[7]),
                                new Color(
                                        Integer.parseInt(strings[8]), Integer.parseInt(strings[9]),
                                        Integer.parseInt(strings[10]), Integer.parseInt(strings[11])
                                )
                        )
                );
            }
        }
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    void tick(ActionEvent e) {
        long timeDelta = e.getWhen() - startTime;
        long timeSinceLastTick = e.getWhen() - lastTick;
        lastTick = e.getWhen();

        if (playing) {

            // play song 1 second after clicking play
            if (timeDelta >= 1000 && !songStarted) {
                Sound.playSound("files/beatmaps/" + beatmap + ".wav");
                songStarted = true;
            }

            // set currentNote
            if (currentNote == null) {
                currentNote = notes.first();
            } else {
                if (currentNote.getClass() == Circle.class) {
                    if (currentNote.getHit() || currentNote.getMiss()) {
                        currentNote = notes.higher(currentNote);
                    }
                } else {
                    Slider currentSlider = (Slider) currentNote;
                    if (currentSlider.getHit() && currentSlider.getReleased()
                            || currentSlider.getMiss()) {
                        currentNote = notes.higher(currentNote);
                    }
                }
            }

            // note timing logic!
            if (notes != null) {
                for (Note note : notes) {
                    if (!note.getHit()
                            && timeDelta >= (note.getQuarterNote() * 15000L / bpm
                                    - note.getAnimateDuration() + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + offset)) {
                        note.animateIn(timeSinceLastTick);
                    }
                    if (!note.getHit()
                            && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200
                                    + note.getAnimateDuration() + offset)) {
                        if (!note.getMiss()) {
                            combo = 0;
                            totalRawScore += 300;
                        }
                        note.miss();
                        note.animateMiss(timeSinceLastTick);
                    }
                    if (note.getClass() == Circle.class) {
                        if (!note.getHit()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 200
                                        + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm - 125
                                        + offset)) {
                            note.setIfHitScore(50);
                        }
                        if (!note.getHit()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 100
                                        + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm - 50
                                        + offset)) {
                            note.setIfHitScore(100);
                        }
                        if (!note.getHit()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 50 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 50
                                        + offset)) {
                            note.setIfHitScore(300);
                        }
                        if (!note.getHit()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 50 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 125
                                        + offset)) {
                            note.setIfHitScore(100);
                        }
                        if (!note.getHit()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 125
                                        + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200
                                        + offset)) {
                            note.setIfHitScore(50);
                        }
                        if (timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200
                                        + note.getAnimateDuration() + offset)) {
                            if (note.getHitScore() == 50) {
                                note.animate50(timeSinceLastTick);
                            }
                            if (note.getHitScore() == 100) {
                                note.animate100(timeSinceLastTick);
                            }
                        }
                        if (timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200
                                + note.getAnimateDuration() + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200
                                        + 2L * note.getAnimateDuration() + offset)) {
                            note.animateOut(timeSinceLastTick);
                        }
                    }
                    if (note.getClass() == Slider.class) {
                        Slider slider = (Slider) note;
                        if (!note.getHit()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 200
                                        + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200
                                        + offset)) {
                            note.setIfHitScore(50);
                        }
                        if (note.getHit() && !slider.getReleased()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm
                                        + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm + offset)) {
                            slider.animateApproachCircle(bpm, timeSinceLastTick);
                        }
                        if (!slider.getReleased()
                                && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200
                                        + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm - 200 + offset)) {
                            note.setIfHitScore(100);
                        }
                        if (!slider.getReleased()
                                && timeDelta >= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm - 200 + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm + offset)) {
                            note.setIfHitScore(300);
                        }
                        if (slider.getReleased()
                                && timeDelta >= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm + note.getAnimateDuration() + offset)) {
                            if (note.getHitScore() == 50) {
                                note.animate50(timeSinceLastTick);
                            }
                            if (note.getHitScore() == 100) {
                                note.animate100(timeSinceLastTick);
                            }
                        }
                        if (!slider.getMiss() && !slider.getReleased()
                                && timeDelta >= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm + offset)) {
                            slider.release();
                            if (slider.getHorizontal()) {
                                if (mouseX >= slider.getPx() + slider.getApproachCircleLocation()
                                        - slider.getWidth() / 2
                                        && mouseX <= slider.getPx()
                                                + slider.getApproachCircleLocation()
                                                + slider.getWidth() / 2) {
                                    if (mouseY >= slider.getPy() - slider.getHeight() / 2
                                            && mouseY <= slider.getPy()
                                                    + slider.getApproachCircleLocation()
                                                    + slider.getHeight() / 2) {
                                        note.setHitScore(300);
                                    } else {
                                        note.setHitScore(100);
                                    }
                                } else {
                                    note.setHitScore(100);
                                }
                            } else {
                                if (mouseX >= slider.getPx() - slider.getWidth() / 2
                                        && mouseX <= slider.getPx()
                                                + slider.getApproachCircleLocation()
                                                + slider.getWidth() / 2) {
                                    if (mouseY >= slider.getPy()
                                            + slider.getApproachCircleLocation()
                                            - slider.getHeight() / 2
                                            && mouseY <= slider.getPy()
                                                    + slider.getApproachCircleLocation()
                                                    + slider.getHeight() / 2) {
                                        note.setHitScore(300);
                                    } else {
                                        note.setHitScore(100);
                                    }
                                } else {
                                    note.setHitScore(100);
                                }
                            }
                            combo += 1;
                            score += combo * currentNote.getHitScore();
                            rawScore += currentNote.getHitScore();
                            totalRawScore += 300;
                        }
                        if (timeDelta >= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L
                                / bpm + note.getAnimateDuration() + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength())
                                        * 15000L / bpm + 2L * note.getAnimateDuration() + offset)) {
                            note.animateOut(timeSinceLastTick);
                        }
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // paint 31 notes at a time to reduce lag
        TreeSet<Note> drawNotes = new TreeSet<>();
        ArrayList<Note> noteList = new ArrayList<>(notes);
        for (Note note : notes) {
            if (currentNote != null) {
                if (noteList.indexOf(note) >= noteList.indexOf(currentNote) - 15
                        && noteList.indexOf(note) <= noteList.indexOf(currentNote) + 15) {
                    drawNotes.add(note);
                }
            }
        }
        for (Note note : drawNotes.descendingSet()) {
            note.draw(g);
        }
        startButton.draw(g);

        // draw score, combo, and accuracy
        if (playing) {
            g.setColor(Color.WHITE);
            Font numberFont = new Font("Lato", Font.BOLD, 50);
            FontMetrics metrics = g.getFontMetrics(numberFont);
            g.setFont(numberFont);
            g.drawString(
                    String.valueOf(score),
                    Screen.SCREEN_WIDTH - metrics.stringWidth(String.valueOf(score)) - 30, 70
            );
            g.drawString(
                    combo + "X", Screen.SCREEN_WIDTH - metrics.stringWidth(combo + "X") - 30,
                    Screen.SCREEN_HEIGHT - metrics.getHeight() - 70
            );
            numberFont = new Font("Lato", Font.BOLD, 30);
            metrics = g.getFontMetrics(numberFont);
            g.setFont(numberFont);
            if (totalRawScore == 0) {
                g.drawString("0%", Screen.SCREEN_WIDTH - metrics.stringWidth("0%") - 30, 120);
            } else {
                String accuracy = Math.round((rawScore * 100.0 / totalRawScore) * 100.0)
                        / 100.0 + "%";
                g.drawString(
                        accuracy, Screen.SCREEN_WIDTH - metrics.stringWidth(accuracy) - 30, 130
                );
            }
        } else { // draw instructions
            g.setColor(Color.WHITE);
            Font titleFont = new Font("Lato", Font.BOLD, 50);
            g.setFont(titleFont);
            g.drawString("osus!", 50, 100);
            Font instructionsFont = new Font("Lato", Font.BOLD, 20);
            FontMetrics metrics = g.getFontMetrics(instructionsFont);
            g.setFont(instructionsFont);
            g.drawString(
                    "Click the circles to the beat of the song! " +
                            "Drag the cursor over the circles, " +
                            "and press Z or X to click or hold for sliders.",
                    50, 150
            );
            g.drawString(
                    "When you hit multiple notes in a row, you'll gain a combo. " +
                            "The bigger your combo, the greater your score.",
                    50, 180
            );
            g.drawString(
                    "If you hit the note on time, you get a 300. " +
                            "Otherwise, you may get a 100 or 50 depending on accuracy.",
                    50, 210
            );
            g.drawString(
                    "When you're ready, drag the cursor over the sus! " +
                            "circle and hit Z or X to begin!",
                    50, 240
            );
        }

        // draw cursor
        cursor.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Screen.SCREEN_WIDTH, Screen.SCREEN_HEIGHT);
    }
}