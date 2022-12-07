package org.cis1200.osus;

import java.awt.*;

/**
 * A basic game object starting in the upper left corner of the game court. It
 * is displayed as a circle of a specified color.
 */
public class Circle extends HitObj {
    final private Color color;
    final private String number;
    final private int beat;
    final private int cs;
    final private int ar;
    private int opacity = 0;
    private int missOpacity = 0;
    private boolean hit = false;
    private boolean hittable = false;

    public Circle(int posX, int posY, int beat, int cs, int ar, int number, Color color) {
        super(ScreenSize.SCREEN_WIDTH / 100 * posX, ScreenSize.SCREEN_HEIGHT / 100 * posY,
                ScreenSize.SCREEN_WIDTH / (3 * cs), ScreenSize.SCREEN_WIDTH / (3 * cs), beat);

        this.beat = beat;
        this.cs = cs;
        this.ar = ar;
        this.color = color;
        this.number = Integer.toString(number);
    }

    public void animateIn() {
        if (this.opacity < 255) {
            this.opacity += Math.min(255 - this.opacity, this.ar);
        }
    }

    public void hit() {
        this.opacity = 0;
        this.hit = true;
    }

    public void miss() {
        this.opacity = 0;
        if (this.missOpacity < 255) {
            this.missOpacity += Math.min(255 - this.missOpacity, this.ar);
        }
    }

    public void animate100() {}

    public void animate50() {}

    public void animateMissOut() {
        if (this.missOpacity > 0) {
            this.missOpacity -= Math.min(this.missOpacity, this.ar);
        }
    }

    public int getAnimateDuration() {
        return 255 / this.ar + 1;
    }

    public int getBeat() {
        return this.beat;
    }

    public boolean getHit() {
        return this.hit;
    }

    public boolean getHittable() {
        return this.hittable;
    }

    public void setHittable(boolean hittable) {
        this.hittable = hittable;
    }

    @Override
    public int compareTo(Object o) {
        Circle other = (Circle) o;
        return this.getBeat() - other.getBeat();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(255, 255, 255, opacity));
        g.fillOval(this.getPx() - this.getWidth() / 2, this.getPy() - this.getHeight() / 2, this.getWidth(), this.getHeight());
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * opacity / 255));
        g.fillOval(this.getPx() - (int) (this.getWidth() * 0.9) / 2, this.getPy() - (int) (this.getWidth() * 0.9) / 2, (int) (this.getWidth() * 0.9), (int) (this.getHeight() * 0.9));
        g.setColor(new Color(255, 255, 255, opacity));
        Font numberFont = new Font("Roboto", Font.BOLD, this.cs * 8);
        FontMetrics metrics = g.getFontMetrics(numberFont);
        g.setFont(numberFont);
        g.drawString(this.number, this.getPx() - metrics.stringWidth(this.number) / 2, this.getPy() - metrics.getHeight() / 2 + metrics.getAscent());
        g.setColor(new Color(255, 0, 0, missOpacity));
        g.drawString("X", this.getPx() - metrics.stringWidth("X") / 2, this.getPy() - metrics.getHeight() / 2 + metrics.getAscent());
    }
}