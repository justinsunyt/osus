package org.cis1200.osus;

// imports necessary libraries for Java swing

import javax.swing.*;
import java.awt.*;
import java.awt.Cursor;
import java.awt.image.BufferedImage;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class RunOsus implements Runnable {
    public void run() {
        // NOTE : recall that the 'final' keyword notes immutability even for
        // local variables.

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("sus");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        // Main playing area
        final GameScreen screen = new GameScreen();
        frame.add(screen, BorderLayout.CENTER);


        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> screen.reset());

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        frame.getContentPane().setCursor(blankCursor);

        // Start game
        screen.loadBeatmap();
        screen.reset();
    }
}