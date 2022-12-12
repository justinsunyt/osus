package org.cis1200.osus.components;

import java.awt.*;

public class Cursor extends GameObj {

    /**
     * Constructor
     *
     * @param px
     * @param py
     * @param width
     * @param height
     */
    public Cursor(int px, int py, int width, int height) {
        super(px, py, width, height);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillOval(this.getPx(), this.getPy(), this.getWidth(), this.getHeight());
    }
}
