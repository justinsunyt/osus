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
        // Enable OpenGL hardware acceleration
        System.setProperty("sun.java2d.opengl", "true");

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("osus!");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        // Main playing area
        final GameScreen screen = new GameScreen();
        frame.add(screen, BorderLayout.CENTER);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Hide default cursor
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor"
        );
        frame.getContentPane().setCursor(blankCursor);

        // Start game
        screen.reset();
    }
}