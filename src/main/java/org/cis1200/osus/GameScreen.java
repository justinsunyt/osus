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
                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    for (Circle c : notes) {
                        if (c.getHittable()) {
                            if (MouseInfo.getPointerInfo().getLocation().getX() > c.getPx() - c.getWidth() / 2 && MouseInfo.getPointerInfo().getLocation().getX() < c.getPx() + 50) {
                                if (MouseInfo.getPointerInfo().getLocation().getY() > c.getPy() - 50 && MouseInfo.getPointerInfo().getLocation().getY() < c.getPy() + 50) {
                                    c.setHittable(false);
                                    c.hit();
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
            }
        });
    }

    public void loadBeatmap() {
        final Circle test1 = new Circle(20, 10, 2, 5, 8, 1, new Color(255, 0, 0, 150));
        final Circle test2 = new Circle(30, 20, 4, 5, 8, 2, new Color(0, 0, 255, 150));
        final Circle test3 = new Circle(40, 30, 6, 5, 8, 3, new Color(255, 0, 0, 150));
        final Circle test4 = new Circle(50, 40, 8, 5, 8, 4, new Color(0, 0, 255, 150));
        this.notes.add(test1);
        this.notes.add(test2);
        this.notes.add(test3);
        this.notes.add(test4);
        this.bpm = 120;
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
                boolean hittable = false;
                for (Circle note : notes) {
                    if (frame >= (note.getBeat() * 14400 / bpm - note.getAnimateDuration())
                            && frame <= (note.getBeat() * 14400 / bpm)) {
                        note.animateIn();
                        if (!hittable) {
                            note.setHittable(true);
                            hittable = true;
                        }
                    }
                    if (!note.getHit() && frame >= ((note.getBeat() + 1) * 14400 / bpm)
                            && frame <= ((note.getBeat() + 1) * 14400 / bpm + note.getAnimateDuration())) {
                        note.miss();
                    }
                    if (!note.getHit() && frame >= ((note.getBeat() + 2) * 14400 / bpm)
                            && frame <= ((note.getBeat() + 2) * 14400 / bpm + note.getAnimateDuration())) {
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