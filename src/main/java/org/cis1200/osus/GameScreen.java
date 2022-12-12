package org.cis1200.osus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * GameCourt
 *
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 */
public class GameScreen extends JPanel {

    // the state of the game logic
    private final TreeSet<Note> notes = new TreeSet<>();
    private Note currentNote;
    private final Cursor cursor = new Cursor(0, 0, 30, 30);
    private final Button startButton = new Button(ScreenSize.SCREEN_WIDTH - 250, ScreenSize.SCREEN_HEIGHT - 300, 200, 200, "files/images/start.png");

    private boolean playing = false; // whether the game is running
    private int bpm;
    private int mouseX;
    private int mouseY;
    private String beatmap;
    private long offset;
    private long startTime;
    private long lastTick;
    private boolean songStarted = false;
    private int score;
    private int combo;
    private int rawScore;
    private int totalRawScore;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 1000/240;

    public GameScreen() {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.BLACK);

        // The timer is an object which triggers an action periodically with the
        // given INTERVAL. We register an ActionListener with this timer, whose
        // actionPerformed() method is called each time the timer triggers. We
        // define a helper method called tick() that actually does everything
        // that should be done in a single time step.
        Timer timer = new Timer(INTERVAL, this::tick);
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // This key listener allows the square to move as long as an arrow key
        // is pressed, by changing the square's velocity accordingly. (The tick
        // method below actually moves the square.)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_X) {
                    if (currentNote != null && currentNote.getIfHitScore() > 0) {
                        if (mouseX >= currentNote.getPx() - currentNote.getWidth() / 2 && mouseX <= currentNote.getPx() + currentNote.getWidth() / 2) {
                            if (mouseY >= currentNote.getPy() - currentNote.getHeight() / 2 && mouseY <= currentNote.getPy() + currentNote.getHeight() / 2) {
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
                        if (mouseX >= startButton.getPx() && mouseX <= startButton.getPx() + startButton.getWidth()) {
                            if (mouseY >= startButton.getPy() && mouseY <= startButton.getPy() + startButton.getHeight()) {
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
                    if (currentNote != null && currentNote.getClass() == Slider.class && currentNote.getHit() && !((Slider) currentNote).getReleased()) {
                        Slider currentSlider = (Slider) currentNote;
                        if (currentSlider.getHorizontal()) {
                            if (mouseX >= currentNote.getPx() + currentSlider.getApproachCircleLocation() - currentNote.getWidth() / 2 && mouseX <= currentNote.getPx() + currentSlider.getApproachCircleLocation() + currentNote.getWidth() / 2) {
                                if (mouseY >= currentNote.getPy() - currentNote.getHeight() / 2 && mouseY <= currentNote.getPy() + currentSlider.getApproachCircleLocation() + currentNote.getHeight() / 2) {
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
                            if (mouseX >= currentNote.getPx() - currentNote.getWidth() / 2 && mouseX <= currentNote.getPx() + currentNote.getWidth() / 2) {
                                if (mouseY >= currentNote.getPy() + currentSlider.getApproachCircleLocation() - currentNote.getHeight() / 2 && mouseY <= currentNote.getPy() + currentNote.getHeight() / 2) {
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
        // triplet circle
        final Circle c1 = new Circle(40, 40, 12, 5, 8, 1, new Color(255, 0, 0, 150));
        final Circle c2 = new Circle(41, 41, 13, 5, 8, 2, new Color(255, 0, 0, 150));
        final Circle c3 = new Circle(42, 42, 14, 5, 8, 3, new Color(255, 0, 0, 150));

        this.notes.add(c1);
        this.notes.add(c2);
        this.notes.add(c3);

//        this.notes.add(new Slider(40, 40, 30, false, 18, 16, 5, 8, 4, new Color(255, 0, 255, 150)));
        // for loop that adds one two jumps
        for (int i = 1; i < 500; i++) {
            this.notes.add(new Circle(40, 40, 10 + 8 * i, 5, 8, 5, new Color(255, 0, 0, 150)));
            this.notes.add(new Circle(60, 60, 14 + 8 * i, 5, 8, 6, new Color(255, 0, 0, 150)));
        }

        this.bpm = 180;
        this.beatmap = "files/beatmaps/sunglow.wav";
        this.offset = -40;
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
            if (timeDelta >= 1000 && !songStarted) {
                Sound.playSound(beatmap);
                songStarted = true;
            }

            if (currentNote == null) {
                currentNote = notes.first();
            } else {
                if (currentNote.getClass() == Circle.class) {
                    if (currentNote.getHit() || currentNote.getMiss()) {
                        currentNote = notes.higher(currentNote);
                    }
                } else {
                    Slider currentSlider = (Slider) currentNote;
                    if (currentSlider.getHit() && currentSlider.getReleased() || currentSlider.getMiss()) {
                        currentNote = notes.higher(currentNote);
                    }
                }

            }

            // update the display
            if (notes != null) {
                for (Note note : notes) {
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - note.getAnimateDuration() + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + offset)) {
                        note.animateIn(timeSinceLastTick);
                    }
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration() + offset)) {
                        note.miss();
                        note.animateMiss(timeSinceLastTick);
                        combo = 0;
                    }
                    if (note.getClass() == Circle.class) {
                        if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 200 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm - 125 + offset)) {
                            note.setIfHitScore(50);
                        }
                        if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 100 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm - 50 + offset)) {
                            note.setIfHitScore(100);
                        }
                        if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 50 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 50 + offset)) {
                            note.setIfHitScore(300);
                        }
                        if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 50 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 125 + offset)) {
                            note.setIfHitScore(100);
                        }
                        if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 125 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + offset)) {
                            note.setIfHitScore(50);
                        }
                        if (timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration() + offset)) {
                            if (note.getHitScore() == 50) {
                                note.animate50(timeSinceLastTick);
                            }
                            if (note.getHitScore() == 100) {
                                note.animate100(timeSinceLastTick);
                            }
                        }
                        if (timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration() + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + 2L * note.getAnimateDuration() + offset)) {
                            note.animateOut(timeSinceLastTick);
                        }
                    }
                    if (note.getClass() == Slider.class) {
                        Slider slider = (Slider) note;
                        if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - 200 + offset)
                                && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + offset)) {
                            note.setIfHitScore(50);
                        }
                        if (note.getHit() && !slider.getReleased() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + 200 + offset)) {
                            slider.animateApproachCircle(bpm, timeSinceLastTick);
                        }
                        if (!slider.getReleased() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm - 200 + offset)) {
                            note.setIfHitScore(100);
                        }
                        if (!slider.getReleased() && timeDelta >= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm - 200 + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + offset)) {
                            note.setIfHitScore(300);
                        }
                        if (slider.getReleased() && timeDelta >= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + note.getAnimateDuration() + offset)) {
                            if (note.getHitScore() == 50) {
                                note.animate50(timeSinceLastTick);
                            }
                            if (note.getHitScore() == 100) {
                                note.animate100(timeSinceLastTick);
                            }
                        }
                        if (!slider.getMiss() && !slider.getReleased() && timeDelta >= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + offset)) {
                            slider.release();
                            if (slider.getHorizontal()) {
                                if (mouseX >= slider.getPx() + slider.getApproachCircleLocation() - slider.getWidth() / 2 && mouseX <= slider.getPx() + slider.getApproachCircleLocation() + slider.getWidth() / 2) {
                                    if (mouseY >= slider.getPy() - slider.getHeight() / 2 && mouseY <= slider.getPy() + slider.getApproachCircleLocation() + slider.getHeight() / 2) {
                                        note.setHitScore(300);
                                    } else {
                                        note.setHitScore(100);
                                    }
                                } else {
                                    note.setHitScore(100);
                                }
                            } else {
                                if (mouseX >= slider.getPx() - slider.getWidth() / 2 && mouseX <= slider.getPx() + slider.getApproachCircleLocation() + slider.getWidth() / 2) {
                                    if (mouseY >= slider.getPy() + slider.getApproachCircleLocation() - slider.getHeight() / 2 && mouseY <= slider.getPy() + slider.getApproachCircleLocation() + slider.getHeight() / 2) {
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
                        if (timeDelta >= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + note.getAnimateDuration() + offset)
                                && timeDelta <= ((note.getQuarterNote() + slider.getNoteLength()) * 15000L / bpm + 2L * note.getAnimateDuration() + offset)) {
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
        TreeSet<Note> drawNotes = new TreeSet<>();
        ArrayList<Note> noteList = new ArrayList<>(notes);
        for (Note note : notes) {
            if (currentNote != null) {
                if (noteList.indexOf(note) >= noteList.indexOf(currentNote) - 20 && noteList.indexOf(note) <= noteList.indexOf(currentNote) + 20) {
                    drawNotes.add(note);
                }
            }
        }
        for (Note note : drawNotes.descendingSet()) {
            note.draw(g);
        }
        startButton.draw(g);
        if (playing) {
            g.setColor(Color.WHITE);
            Font numberFont = new Font("Lato", Font.BOLD, 50);
            FontMetrics metrics = g.getFontMetrics(numberFont);
            g.setFont(numberFont);
            g.drawString(String.valueOf(score), ScreenSize.SCREEN_WIDTH - metrics.stringWidth(String.valueOf(score)) - 30, 70);
            g.drawString(combo + "X", ScreenSize.SCREEN_WIDTH - metrics.stringWidth(combo + "X") - 30, ScreenSize.SCREEN_HEIGHT - metrics.getHeight() - 70);
            numberFont = new Font("Lato", Font.BOLD, 30);
            metrics = g.getFontMetrics(numberFont);
            g.setFont(numberFont);
            if (totalRawScore == 0) {
                g.drawString("0%", ScreenSize.SCREEN_WIDTH - metrics.stringWidth("0%") - 30, 120);
            } else {
                String accuracy = Math.round((float) (rawScore * 100 / totalRawScore) * 100.0) / 100.0  + "%";
                g.drawString(accuracy, ScreenSize.SCREEN_WIDTH - metrics.stringWidth(accuracy) - 30, 130);
            }
        } else {
            g.setColor(Color.WHITE);
            Font titleFont = new Font("Lato", Font.BOLD, 50);
            g.setFont(titleFont);
            g.drawString("osus!", 50, 100);
            Font instructionsFont = new Font("Lato", Font.BOLD, 20);
            FontMetrics metrics = g.getFontMetrics(instructionsFont);
            g.setFont(instructionsFont);
            g.drawString("Click the circles to the beat of the song! Drag the cursor over the circles, and press Z or X to click or hold for sliders.", 50, 150);
            g.drawString("When you hit multiple notes in a row, you'll gain a combo. The bigger your combo, the greater your score.", 50, 180);
            g.drawString("If you hit the note on time, you get a 300. Otherwise, you may get a 100 or 50 depending on accuracy.", 50, 210);
            g.drawString("When you're ready, drag the cursor over the sus! circle and hit Z or X to begin!", 50, 240);
        }
        cursor.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT);
    }
}