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
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.FileWriter;

/**
 * GameScreen
 *
 * This class holds the primary game logic for how different objects interact
 * with one another.
 */
public class GameScreen extends JPanel {
    private final org.cis1200.osus.components.Cursor cursor = new Cursor(0, 0, 30, 30);
    private final org.cis1200.osus.components.Button startButton = new Button(
            Screen.SCREEN_WIDTH - 250, Screen.SCREEN_HEIGHT - 300, 200, 200,
            "files/images/start.png"
    );
    private final JTextField beatmapTextField = new JTextField(20);
    private final JButton beatmapButton = new JButton("Load Beatmap");

    private TreeSet<Note> notes = new TreeSet<>();
    private Note currentNote;
    private Sound song;
    private boolean playing = false; // whether the beatmap is playing
    private boolean paused = false; // whether the beatmap is paused
    private boolean ended = false; // whether the beatmap has ended
    private boolean error = false;
    private boolean save = true;
    private boolean saveError = false;
    private int mouseX;
    private int mouseY;
    private String beatmap; // beatmap file location
    private String name; // song name
    private int bpm; // song bpm
    private int ar; // song approach rate
    private int cs; // song circle size
    private long offset; // beatmap offset
    private long length; // song length
    private long timeDelta; // time delta between the start of the beatmap and the current time
    private long pauseDelta;
    private long startTime;
    private long pauseTime;
    private long lastTick;
    private boolean songStarted = false;
    private int score;
    private int rawScore; // used to calculate accuracy
    private int totalRawScore;
    private int combo;
    private int maxCombo;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 1000 / 240;

    public void loadBeatmap(String location) {
        try {
            FileLineIterator fileLineIterator = new FileLineIterator(
                    "files/beatmaps/" + location + ".txt"
            );
            song = new Sound("files/beatmaps/" + location + ".wav");
            beatmap = location;
            while (fileLineIterator.hasNext()) {
                String line = fileLineIterator.next();
                String[] strings = line.split(", ");
                try {
                    if (strings[0].equals("D") && strings.length == 7) {
                        name = strings[1];
                        bpm = Integer.parseInt(strings[2]);
                        ar = Integer.parseInt(strings[3]);
                        cs = Integer.parseInt(strings[4]);
                        offset = Integer.parseInt(strings[5]);
                        length = Integer.parseInt(strings[6]) * 1000L;
                    }
                    if (strings[0].equals("C") && strings.length == 9) {
                        notes.add(
                                new Circle(
                                        Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                                        Integer.parseInt(strings[3]), ar, cs,
                                        Integer.parseInt(strings[4]),
                                        new Color(
                                                Integer.parseInt(strings[5]), Integer.parseInt(strings[6]),
                                                Integer.parseInt(strings[7]), Integer.parseInt(strings[8])
                                        )
                                )
                        );
                    }
                    if (strings[0].equals("S") && strings.length == 12) {
                        notes.add(
                                new Slider(
                                        Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                                        Integer.parseInt(strings[3]), strings[4].equals("H"), Integer.parseInt(strings[5]),
                                        Integer.parseInt(strings[6]), ar, cs,
                                        Integer.parseInt(strings[7]),
                                        new Color(
                                                Integer.parseInt(strings[8]), Integer.parseInt(strings[9]),
                                                Integer.parseInt(strings[10]), Integer.parseInt(strings[11])
                                        )
                                )
                        );
                    }
                } catch (Exception ex) {
                    error = true;
                }
            }
            if (name == null || bpm == 0 || ar == 0 || cs == 0 || offset == 0 || length == 0) {
                error = true;
            } else {
                error = false;
                if (beatmapButton.getParent() != null) {
                    beatmapButton.getParent().remove(beatmapTextField);
                    beatmapButton.getParent().remove(beatmapButton);
                }
            }
        } catch (IllegalArgumentException ex) {
            error = true;
        }
    }

    public GameScreen() {
        // creates border around the screen
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.BLACK);
        beatmapTextField.setFont(new Font("Lato", Font.PLAIN, 20));
        beatmapButton.setFont(new Font("Lato", Font.PLAIN, 20));
        beatmapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBeatmap(beatmapTextField.getText());
            }
        });

        this.add(beatmapTextField);
        this.add(beatmapButton);

        try {
            FileLineIterator fileLineIterator = new FileLineIterator(
                    "files/save.txt"
            );
            if (fileLineIterator.hasNext()) {
                String line = fileLineIterator.next();
                String[] strings = line.split(", ");
                try {
                    if (strings[0].equals("P") && strings.length == 12) {
                        loadBeatmap(strings[1]);
                        currentNote = new ArrayList<Note>(notes).get(Integer.parseInt(strings[2]));
                        timeDelta = Long.parseLong(strings[3]);
                        pauseDelta = Long.parseLong(strings[4]);
                        startTime = Long.parseLong(strings[5]);
                        pauseTime = Long.parseLong(strings[6]);
                        score = Integer.parseInt(strings[7]);
                        rawScore = Integer.parseInt(strings[8]);
                        totalRawScore = Integer.parseInt(strings[9]);
                        combo = Integer.parseInt(strings[10]);
                        maxCombo = Integer.parseInt(strings[11]);
                        playing = true;
                        paused = true;
                        songStarted = true;
                        startButton.setEnabled(false);
                    } else {
                        save = false;
                    }
                } catch (Exception ex) {
                    saveError = true;
                    ex.printStackTrace();
                }
            } else {
                save = false;
            }
        } catch (IllegalArgumentException ex) {
            saveError = true;
        }

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
                    if (!paused && currentNote != null && currentNote.getIfHitScore() > 0) {
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
                                    combo ++;
                                    if (combo > maxCombo) {
                                        maxCombo = combo;
                                    }
                                    score += combo * currentNote.getHitScore();
                                    rawScore += currentNote.getHitScore();
                                    totalRawScore += 300;
                                }
                            }
                        }
                    }
                    if (startButton.getEnabled()) {
                        if (mouseX >= startButton.getPx()
                                && mouseX <= startButton.getPx() + startButton.getWidth()) {
                            if (mouseY >= startButton.getPy()
                                    && mouseY <= startButton.getPy() + startButton.getHeight()) {
                                if (beatmap != null) {
                                    playing = true;
                                    startTime = System.currentTimeMillis();
                                    startButton.setEnabled(false);
                                    new Sound("files/sounds/start.wav").play();
                                }
                            }
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (ended) {
                        notes = new TreeSet<>();
                        currentNote = null;
                        beatmap = null;
                        name = null;
                        bpm = 0;
                        ar = 0;
                        cs = 0;
                        offset = 0;
                        length = 0;
                        startTime = 0;
                        lastTick = 0;
                        songStarted = false;
                        score = 0;
                        rawScore = 0;
                        totalRawScore = 0;
                        combo = 0;
                        maxCombo = 0;
                        ended = false;
                        add(beatmapTextField);
                        add(beatmapButton);
                    }
                    if (playing) {
                        if (!paused) {
                            paused = true;
                            pauseTime = System.currentTimeMillis();
                            song.pause();
                            try {
                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("files/save.txt"));
                                int currentNoteIndex = new ArrayList<Note>(notes).indexOf(currentNote);
                                bufferedWriter.write("P, " + beatmap + ", " + currentNoteIndex + ", " + timeDelta + ", " + pauseDelta + ", " + startTime + ", " + pauseTime + ", " + score + ", " + rawScore + ", " + totalRawScore + ", " + combo + ", " + maxCombo);
                                bufferedWriter.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            paused = false;
                            pauseDelta += System.currentTimeMillis() - pauseTime;
                            song.playFrom(System.currentTimeMillis() - startTime - pauseDelta - 1000);
                        }
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_X) {
                    if (!paused && currentNote != null && currentNote.getClass() == Slider.class
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
                        combo ++;
                        if (combo > maxCombo) {
                            maxCombo = combo;
                        }
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
        if (playing) {
            long timeSinceLastTick = e.getWhen() - lastTick;
            lastTick = e.getWhen();
            if (!paused) {
                timeDelta = e.getWhen() - startTime - pauseDelta;

                // play song 1 second after clicking play
                if (timeDelta >= 1000 && !songStarted) {
                    song.play();
                    songStarted = true;
                }

                if (timeDelta >= 1000 + length && !ended) {
                    song.pause();
                    ended = true;
                    playing = false;
                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("files/save.txt"));
                        bufferedWriter.newLine();
                        bufferedWriter.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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
                                combo++;
                                if (combo > maxCombo) {
                                    maxCombo = combo;
                                }
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
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (playing) {
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

            // draw score, combo, and accuracy
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

            if (paused) {
                g.setColor(Color.WHITE);
                numberFont = new Font("Lato", Font.BOLD, 50);
                metrics = g.getFontMetrics(numberFont);
                g.setFont(numberFont);
                g.drawString(
                        "Press Escape to resume", Screen.SCREEN_WIDTH / 2 - metrics.stringWidth("Press Escape to resume") / 2,
                        Screen.SCREEN_HEIGHT / 2
                );
            }

        } else if (ended) {
            g.setColor(Color.WHITE);
            Font numberFont = new Font("Lato", Font.BOLD, 50);
            FontMetrics metrics = g.getFontMetrics(numberFont);
            g.setFont(numberFont);
            g.drawString(
                    name, Screen.SCREEN_WIDTH / 2 - metrics.stringWidth(name) / 2, Screen.SCREEN_HEIGHT / 2 - metrics.getHeight() * 2
            );
            g.drawString(
                    String.valueOf(score),
                    Screen.SCREEN_WIDTH / 2 - metrics.stringWidth(String.valueOf(score)) / 2, Screen.SCREEN_HEIGHT / 2 - metrics.getHeight()
            );
            g.drawString(
                    "Max Combo: " + maxCombo + "X", Screen.SCREEN_WIDTH / 2 - metrics.stringWidth("Max Combo: " + maxCombo + "X") / 2,
                    Screen.SCREEN_HEIGHT / 2
            );
            if (totalRawScore == 0) {
                g.drawString("0%", Screen.SCREEN_WIDTH / 2 - metrics.stringWidth("0%") / 2, Screen.SCREEN_HEIGHT / 2 + metrics.getHeight());
            } else {
                String accuracy = Math.round((rawScore * 100.0 / totalRawScore) * 100.0)
                        / 100.0 + "%";
                g.drawString(
                        accuracy, Screen.SCREEN_WIDTH / 2 - metrics.stringWidth(accuracy) / 2, Screen.SCREEN_HEIGHT / 2 + metrics.getHeight()
                );
            }
            g.drawString(
                    "Press Escape to return to the main menu", Screen.SCREEN_WIDTH / 2 - metrics.stringWidth("Press Escape to return to the main menu") / 2,
                    Screen.SCREEN_HEIGHT / 2 + metrics.getHeight() * 2
            );
        } else {
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
                    "At any point in the game, you can press Escape to pause and save the game.",
                    50, 240
            );
            g.drawString(
                    "When you're ready, enter a beatmap name (try HarumachiClover) and load the beatmap",
                    50, 270
            );
            g.drawString(
                    "Once loaded, hover over the sus! logo and press Z or X to start! Good luck!",
                    50, 300
            );
            if (beatmap != null && !error) {
                g.setColor(Color.CYAN);
                g.drawString("Loaded: " + name, 50, 330);
            } else if (error) {
                g.setColor(Color.RED);
                g.drawString("Error loading beatmap", 50, 330);
            } else if (!save) {
                g.setColor(Color.CYAN);
                g.drawString("No available save, please play and pause the game to save", 50, 330);
            } else if (saveError) {
                g.setColor(Color.RED);
                g.drawString("Error loading save", 50, 330);
            }
            startButton.setEnabled(true);
        }

        // draw buttons
        startButton.draw(g);

        // draw cursor
        cursor.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Screen.SCREEN_WIDTH, Screen.SCREEN_HEIGHT);
    }
}