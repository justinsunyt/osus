package org.cis1200.osus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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


    private boolean playing = false; // whether the game is running
    private int frame = 0;
    private int beat = 0;
    private int bpm;
    private int mouseX;
    private int mouseY;
    private boolean hittableNote = false;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 1000 / 240;

    public GameScreen() {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.BLACK);

        // The timer is an object which triggers an action periodically with the
        // given INTERVAL. We register an ActionListener with this timer, whose
        // actionPerformed() method is called each time the timer triggers. We
        // define a helper method called tick() that actually does everything
        // that should be done in a single time step.
        Timer timer = new Timer(INTERVAL, e -> tick());
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
                                }
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
        // loop to create circles spaced 2 apart every 2nd quarternote
        for (int i = 0; i < 10; i++) {
            final Circle c = new Circle(i * 2, i * 2, i * 4, 5, 8, i, new Color(255, 0, 0, 150));
            this.notes.add(c);
        }
        this.bpm = 60;
    }


    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {


        playing = true;

        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    void tick() {
        if (playing) {

            // update the display
            if (notes != null) {
                for (Circle note : notes) {
                    if (!note.getHit() && frame >= (note.getQuarterNote() * 14400 / (bpm * 4) - note.getAnimateDuration())
                            && frame <= (note.getQuarterNote() * 14400 / (bpm * 4))) {
                        if (!hittableNote) {
                            note.setHittable(true);
                            hittableNote = true;
                        }
                        note.animateIn();
                    }
                    if (!note.getHit() && frame >= note.getQuarterNote() * 14400 / (bpm * 4) + note.getAnimateDuration()
                            && frame <= note.getQuarterNote() * 14400 / (bpm * 4) + note.getAnimateDuration() * 2) {
                        note.miss();
                        hittableNote = false;
                    }
                    if (!note.getHit() && frame >= ((note.getQuarterNote() + 2) * 14400 / (bpm * 4))
                            && frame <= ((note.getQuarterNote() + 2) * 14400 / (bpm * 4) + note.getAnimateDuration())) {
                        note.animateMissOut();
                    }
                }
            }



            frame++;
            if (frame % (14400 / bpm) == 0) {
                beat++;
                System.out.println("Beat: " + beat);
            }
//            System.out.println("Frame: " + frame);
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Circle note : notes) {
            note.draw(g);
        }
        cursor.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ScreenSize.SCREEN_WIDTH, ScreenSize.SCREEN_HEIGHT);
    }
}