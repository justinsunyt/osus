package org.cis1200.osus;

import java.awt.*;

public class Button extends GameObj{
    private String text;
    private boolean disabled = false;

    /**
     * Constructor
     *
     * @param px
     * @param py
     * @param width
     * @param height
     */
    public Button(int px, int py, int width, int height, String text) {
        super(px, py, width, height);
        this.text = text;
    }

    public void setDisabled() {
        this.disabled = true;
    }

    public boolean getDisabled() {
        return this.disabled;
    }

    @Override
    public void draw(Graphics g) {
        if (!disabled) {
            g.setColor(Color.WHITE);
            g.fillRect(this.getPx(), this.getPy(), this.getWidth(), this.getHeight());
            g.setColor(Color.BLACK);
            Font numberFont = new Font("Roboto", Font.BOLD, 24);
            FontMetrics metrics = g.getFontMetrics(numberFont);
            g.setFont(numberFont);
            g.drawString(this.text, this.getPx() + this.getWidth() / 2 - metrics.stringWidth(this.text) / 2, this.getPy() + this.getHeight() / 2 + metrics.getHeight() / 2);
        }
    }
}
