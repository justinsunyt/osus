package org.cis1200.osus.components;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Button extends GameObj {
    private BufferedImage image;
    private boolean enabled = true;

    public Button(int px, int py, int width, int height, String file) {
        super(px, py, width, height);
        try {
            image = ImageIO.read(new File(file));
        } catch (Exception e) {
            System.out.println("Error loading image");
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void draw(Graphics g) {
        if (enabled) {
            g.drawImage(image, this.getPx(), this.getPy(), this.getWidth(), this.getHeight(), null);
        }
    }
}
