package org.cis1200.osus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private final TreeSet<Circle> notes = new TreeSet<Circle>();
    private Circle currentCircle;
    private final Cursor cursor = new Cursor(0, 0, 30, 30);
    private final Button startButton = new Button(ScreenSize.SCREEN_WIDTH / 2 - 100, ScreenSize.SCREEN_HEIGHT / 2 - 100, 200, 200, "files/images/start.png");

    private boolean playing = false; // whether the game is running
    private int bpm;
    private int mouseX;
    private int mouseY;
    private String beatmap;
    private long offset;
    private long startTime;
    private long lastTick;
    private boolean songStarted = false;

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
                    if (currentCircle != null && currentCircle.getIfHitScore() > 0) {
                        if (mouseX >= currentCircle.getPx() - currentCircle.getWidth() / 2 && mouseX <= currentCircle.getPx() + currentCircle.getWidth() / 2) {
                            if (mouseY >= currentCircle.getPy() - currentCircle.getHeight() / 2 && mouseY <= currentCircle.getPy() + currentCircle.getHeight() / 2) {
                                if (currentCircle.getIfHitScore() == 50) {
                                    currentCircle.setHitScore(50);
                                }
                                if (currentCircle.getIfHitScore() == 100) {
                                    currentCircle.setHitScore(100);
                                }
                                currentCircle.hit();
                                currentCircle.animateHit();
                                Sound.playSound("files/sounds/hit.wav");
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
//        for (int i = 1; i < 20; i++) {
//            final Circle c = new Circle(i * 2 + 10, i * 2 + 10, i * 2 + 16, 5, 8, i, new Color(255, 0, 0, 150));
//            this.notes.add(c);
//        }
        // triplet circle
        final Circle c1 = new Circle(40, 40, 12, 5, 8, 1, new Color(255, 0, 0, 150));
        final Circle c2 = new Circle(41, 41, 13, 5, 8, 2, new Color(255, 0, 0, 150));
        final Circle c3 = new Circle(42, 42, 14, 5, 8, 3, new Color(255, 0, 0, 150));

        this.notes.add(c1);
        this.notes.add(c2);
        this.notes.add(c3);
        for (int i = 1; i < 20; i++) {
            final Circle c = new Circle(i * 2 + 20, i * 2 + 20, i * 4 + 14, 5, 8, i, new Color(255, 0, 255, 150));
            this.notes.add(c);
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

            if (currentCircle == null) {
                currentCircle = notes.first();
            } else {
                if (currentCircle.getHit() || currentCircle.getMiss()) {
                    currentCircle = notes.higher(currentCircle);
                }
            }

            // update the display
            if (notes != null) {
                for (Circle note : notes) {
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - note.getAnimateDuration(timeSinceLastTick) + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + offset)) {
                        note.animateIn(timeSinceLastTick);
                    }
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
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration(timeSinceLastTick) + offset)) {
                        if (note.getHitScore() == 50) {
                            note.animate50(timeSinceLastTick);
                        }
                        if (note.getHitScore() == 100) {
                            note.animate100(timeSinceLastTick);
                        }
                    }
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration(timeSinceLastTick) + offset)) {
                        note.miss();
                        note.animateMiss(timeSinceLastTick);
                    }
                    if (timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration(timeSinceLastTick) + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + 2L * note.getAnimateDuration(timeSinceLastTick) + offset)) {
                        note.animateOut(timeSinceLastTick);
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Circle note : notes.descendingSet()) {
            note.draw(g);
        }
        startButton.draw(g);
        cursor.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT);
    }
}