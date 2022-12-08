package org.cis1200.osus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.temporal.Temporal;
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
    private final Cursor cursor = new Cursor(0, 0, 30, 30);
    private final Button startButton = new Button(ScreenSize.SCREEN_WIDTH / 2 - 100, ScreenSize.SCREEN_HEIGHT / 2 - 50, 200, 100, "Start");

    private boolean playing = false; // whether the game is running
    private int bpm;
    private int mouseX;
    private int mouseY;
    private boolean hittableNote = false;
    private String beatmap;
    private long offset;
    private long startTime;

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
                    for (Circle c : notes) {
                        if (c.getHittable()) {
                            if (mouseX >= c.getPx() - c.getWidth() / 2 && mouseX <= c.getPx() + c.getWidth() / 2) {
                                if (mouseY >= c.getPy() - c.getHeight() / 2 && mouseY <= c.getPy() + c.getHeight() / 2) {
                                    c.hit();
                                    hittableNote = false;
                                    System.out.println("hit");
                                    Sound.playSound("files/sounds/hit.wav");
                                }
                            }
                        }
                    }
                    if (!startButton.getDisabled()) {
                        if (mouseX >= startButton.getPx() && mouseX <= startButton.getPx() + startButton.getWidth()) {
                            if (mouseY >= startButton.getPy() && mouseY <= startButton.getPy() + startButton.getHeight()) {
                                playing = true;
                                startTime = System.currentTimeMillis();
                                Sound.playSound(beatmap);
                                startButton.setDisabled();
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
        for (int i = 1; i < 20; i++) {
            final Circle c = new Circle(i * 2 + 10, i * 2 + 10, i * 4 + 16, 5, 8, i, new Color(255, 0, 0, 150));
            this.notes.add(c);
        }

        this.bpm = 180;
        this.beatmap = "files/beatmaps/sunglow.wav";
        this.offset = -120;
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
        if (playing) {
            // update the display
            if (notes != null) {
                for (Circle note : notes) {
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm - note.getAnimateDuration() + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + offset)) {
                        if (!hittableNote) {
                            note.setHittable(true);
                            hittableNote = true;
                        }
                        note.animateIn();
                    }
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration() + offset)) {
                        note.miss();
                        hittableNote = false;
                    }
                    if (!note.getHit() && timeDelta >= (note.getQuarterNote() * 15000L / bpm + 200 + note.getAnimateDuration() + offset)
                            && timeDelta <= (note.getQuarterNote() * 15000L / bpm + 200 + 2L * note.getAnimateDuration() + offset)) {
                        note.animateMissOut();
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
        cursor.draw(g);
        startButton.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT);
    }
}