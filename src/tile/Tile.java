package tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage baseImage;
    public BufferedImage overlayImage;
    public boolean collision = false;

    public Tile(BufferedImage baseImage, BufferedImage overlayImage) {
        this.baseImage = baseImage;
        this.overlayImage = overlayImage;
    }
}
