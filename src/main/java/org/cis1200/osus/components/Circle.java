package org.cis1200.osus.components;

import org.cis1200.osus.utils.Screen;

import java.awt.*;

public class Circle extends Note {
    final private Color color;
    final private String number;
    final private int cs;
    final private int ar;
    private int opacity = 0;
    private int hundredOpacity = 0;
    private int fiftyOpacity = 0;
    private int missOpacity = 0;

    public Circle(int posX, int posY, int quarterNote, int cs, int ar, int number, Color color) {
        super(
                Screen.SCREEN_WIDTH / 100 * posX, Screen.SCREEN_HEIGHT / 100 * posY,
                Screen.SCREEN_WIDTH / (3 * cs), Screen.SCREEN_WIDTH / (3 * cs), quarterNote
        );

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

    public void animateHit() {
        this.opacity = 0;
    }

    public void animate100(long timeSinceLastTick) {
        if (this.hundredOpacity < 255) {
            this.hundredOpacity += Math
                    .min(255 - this.hundredOpacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public void animate50(long timeSinceLastTick) {
        if (this.fiftyOpacity < 255) {
            this.fiftyOpacity += Math
                    .min(255 - this.fiftyOpacity, this.ar * timeSinceLastTick / 15);
        }
    }

    public void animateMiss(long timeSinceLastTick) {
        this.opacity = 0;
        if (this.missOpacity < 255) {
            this.missOpacity += Math.min(255 - this.missOpacity, this.ar * timeSinceLastTick / 15);
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

    public long getAnimateDuration() {
        return 255 / this.ar * 15 + 140;
    }

    @Override
    public void draw(Graphics g) {
        // circle
        g.setColor(new Color(255, 255, 255, opacity));
        g.fillOval(
                this.getPx() - this.getWidth() / 2, this.getPy() - this.getHeight() / 2,
                this.getWidth(), this.getHeight()
        );
        g.setColor(
                new Color(
                        color.getRed(), color.getGreen(), color.getBlue(),
                        color.getAlpha() * opacity / 255
                )
        );
        g.fillOval(
                this.getPx() - (int) (this.getWidth() * 0.9) / 2,
                this.getPy() - (int) (this.getWidth() * 0.9) / 2, (int) (this.getWidth() * 0.9),
                (int) (this.getHeight() * 0.9)
        );
        g.setColor(new Color(255, 255, 255, opacity));

        // number
        Font numberFont = new Font("Lato", Font.BOLD, this.cs * 8);
        FontMetrics metrics = g.getFontMetrics(numberFont);
        g.setFont(numberFont);
        g.drawString(
                this.number, this.getPx() - metrics.stringWidth(this.number) / 2,
                this.getPy() - metrics.getHeight() / 2 + metrics.getAscent()
        );

        // approach circle
        g.setColor(new Color(255, 255, 255, opacity));
        g.drawOval(
                this.getPx() - (this.getWidth() + 255 - opacity) / 2,
                this.getPy() - (this.getHeight() + 255 - opacity) / 2,
                this.getWidth() + 255 - opacity, this.getHeight() + 255 - opacity
        );

        // 100
        g.setColor(new Color(0, 255, 100, hundredOpacity));
        g.drawString(
                "100", this.getPx() - metrics.stringWidth("100") / 2,
                this.getPy() - metrics.getHeight() / 2 + metrics.getAscent()
        );

        // 50
        g.setColor(new Color(0, 100, 255, fiftyOpacity));
        g.drawString(
                "50", this.getPx() - metrics.stringWidth("50") / 2,
                this.getPy() - metrics.getHeight() / 2 + metrics.getAscent()
        );

        // miss
        g.setColor(new Color(255, 0, 0, missOpacity));
        g.drawString(
                "X", this.getPx() - metrics.stringWidth("X") / 2,
                this.getPy() - metrics.getHeight() / 2 + metrics.getAscent()
        );
    }
}