package org.cis1200.osus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Button extends GameObj{
    private BufferedImage image;
    private boolean disabled = false;

    /**
     * Constructor
     *
     * @param px
     * @param py
     * @param width
     * @param height
     */
    public Button(int px, int py, int width, int height, String file) {
        super(px, py, width, height);
        try {
            image = ImageIO.read(new File(file));
        } catch (Exception e) {
            System.out.println("Error loading image");
        }
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
            g.drawImage(image, this.getPx(), this.getPy(), this.getWidth(), this.getHeight(), null);
        }
    }
}
