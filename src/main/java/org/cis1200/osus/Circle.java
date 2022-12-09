package org.cis1200.osus;

import java.awt.*;

/**
 * A basic game object starting in the upper left corner of the game court. It
 * is displayed as a circle of a specified color.
 */
public class Circle extends HitObj {
    final private Color color;
    final private String number;
    final private int quarterNote;
    final private int cs;
    final private int ar;
    private int opacity = 0;
    private int hundredOpacity = 0;
    private int fiftyOpacity = 0;
    private int missOpacity = 0;
    private boolean hit = false;
    private boolean missed = false;
    private int hitScore = 0;
    private int ifHitScore = 0; // 0 = unhittable, 50 = bad, 100 = good, 300 = perfect

    public Circle(int posX, int posY, int quarterNote, int cs, int ar, int number, Color color) {
        super(ScreenSize.SCREEN_WIDTH / 100 * posX, ScreenSize.SCREEN_HEIGHT / 100 * posY,
                ScreenSize.SCREEN_WIDTH / (3 * cs), ScreenSize.SCREEN_WIDTH / (3 * cs), quarterNote);

        this.quarterNote = quarterNote;
        this.cs = cs;
        this.ar = ar;
        this.color = color;
        this.number = Integer.toString(number);
    }

    public void animateIn(long timeSinceLastTick) {
        if (this.opacity < 255) {
            this.opacity += Math.min(255 - this.opacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public void hit() {
        this.ifHitScore = 0;
        this.opacity = 0;
        this.hit = true;
    }

    public void miss(long timeSinceLastTick) {
        this.ifHitScore = 0;
        this.opacity = 0;
        this.missed = true;
        if (this.missOpacity < 255) {
            this.missOpacity += Math.min(255 - this.missOpacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public void animate100(long timeSinceLastTick) {
        if (this.hundredOpacity < 255) {
            this.hundredOpacity += Math.min(255 - this.hundredOpacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public void animate50(long timeSinceLastTick) {
        if (this.fiftyOpacity < 255) {
            this.fiftyOpacity += Math.min(255 - this.fiftyOpacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public void animateOut(long timeSinceLastTick) {
        if (this.missOpacity > 0) {
            this.missOpacity -= Math.min(this.missOpacity, this.ar * timeSinceLastTick / 15);
        }
        if (this.hundredOpacity > 0) {
            this.hundredOpacity -= Math.min(this.hundredOpacity, this.ar * timeSinceLastTick / 15);
        }
        if (this.fiftyOpacity > 0) {
            this.fiftyOpacity -= Math.min(this.fiftyOpacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public long getAnimateDuration(long timeSinceLastTick) {
        return 255 / this.ar * 15 + 3 * timeSinceLastTick;
    }

    public int getQuarterNote() {
        return this.quarterNote;
    }

    public boolean getHit() {
        return this.hit;
    }

    public boolean getMissed() {
        return this.missed;
    }

    public int getHitScore() {
        return this.hitScore;
    }

    public void setHitScore(int hitScore) {
        this.hitScore = hitScore;
    }

    public int getIfHitScore() {
        return this.ifHitScore;
    }

    public void setIfHitScore(int ifHitScore) {
        this.ifHitScore = ifHitScore;
    }

    @Override
    public int compareTo(Object o) {
        Circle other = (Circle) o;
        return this.getQuarterNote() - other.getQuarterNote();
    }

    @Override
    public void draw(Graphics g) {
        // circle
        g.setColor(new Color(255, 255, 255, opacity));
        g.fillOval(this.getPx() - this.getWidth() / 2, this.getPy() - this.getHeight() / 2, this.getWidth(), this.getHeight());
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * opacity / 255));
        g.fillOval(this.getPx() - (int) (this.getWidth() * 0.9) / 2, this.getPy() - (int) (this.getWidth() * 0.9) / 2, (int) (this.getWidth() * 0.9), (int) (this.getHeight() * 0.9));
        g.setColor(new Color(255, 255, 255, opacity));
        Font numberFont = new Font("Roboto", Font.BOLD, this.cs * 8);
        FontMetrics metrics = g.getFontMetrics(numberFont);
        g.setFont(numberFont);
        g.drawString(this.number, this.getPx() - metrics.stringWidth(this.number) / 2, this.getPy() - metrics.getHeight() / 2 + metrics.getAscent());

        // approach circle
        g.setColor(new Color(255, 255, 255, opacity));
        g.drawOval(this.getPx() - (this.getWidth() + 255 - opacity) / 2, this.getPy() - (this.getHeight() + 255 - opacity) / 2, this.getWidth() + 255 - opacity, this.getHeight() + 255 - opacity);

        // 100
        g.setColor(new Color(0, 255, 0, hundredOpacity));
        g.drawString("100", this.getPx() - metrics.stringWidth("X") / 2, this.getPy() - metrics.getHeight() / 2 + metrics.getAscent());

        // 50
        g.setColor(new Color(0, 0, 255, fiftyOpacity));
        g.drawString("50", this.getPx() - metrics.stringWidth("X") / 2, this.getPy() - metrics.getHeight() / 2 + metrics.getAscent());

        // miss
        g.setColor(new Color(255, 0, 0, missOpacity));
        g.drawString("X", this.getPx() - metrics.stringWidth("X") / 2, this.getPy() - metrics.getHeight() / 2 + metrics.getAscent());
    }
}