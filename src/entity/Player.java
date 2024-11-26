package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {

    GamePanel gp; 
    KeyHandler keyH; 
    
    public final int screenX;
    
    public final int screenY;

    // Array for holding player images (12 total for each direction)
    BufferedImage[] upImages = new BufferedImage[12];
    BufferedImage[] downImages = new BufferedImage[12];
    BufferedImage[] leftImages = new BufferedImage[12];
    BufferedImage[] rightImages = new BufferedImage[12];
    boolean justStoppedMoving = false;
    
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize/2);
        
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 5;
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            // Load idle images (1-4)
            for (int i = 0; i < 4; i++) {
                upImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/up" + (i + 1) + ".png"));
                downImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/down" + (i + 1) + ".png"));
                leftImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/left" + (i + 1) + ".png"));
                rightImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/right" + (i + 1) + ".png"));
            }

            // Load movement images (5-12)
            for (int i = 4; i < 12; i++) {
                upImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/up" + (i + 1) + ".png"));
                downImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/down" + (i + 1) + ".png"));
                leftImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/left" + (i + 1) + ".png"));
                rightImages[i] = ImageIO.read(getClass().getResourceAsStream("/player/right" + (i + 1) + ".png"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void update() {
        boolean isMoving = false;

        // Handle movement logic
        if (keyH.upPressed) {
            direction = "up";
            worldY -= speed; 
            isMoving = true;
        } 
        else if (keyH.downPressed) {
            direction = "down";
            worldY += speed; 
            isMoving = true;
        } 
        else if (keyH.leftPressed) {
            direction = "left";
            worldX -= speed; 
            isMoving = true;
        } 
        else if (keyH.rightPressed) {
            direction = "right";
            worldX += speed; 
            isMoving = true;
        }

        // Movement animation logic
        if (isMoving) {
            // Reset to movement animation if player starts moving
            if (spriteNum < 5 || spriteNum > 12) {
                spriteNum = 5; // Start movement animation
            }
            spriteCounter++;
            justStoppedMoving = true;  // Mark that the player was moving

            if (spriteCounter > 3) { // Adjust this for movement animation speed
                spriteNum++;
                if (spriteNum > 12) { // Loop back to the first movement sprite
                    spriteNum = 5;
                }
                spriteCounter = 0;
            }
        } else {
            // If the player just stopped moving, immediately set to the first idle frame
            if (justStoppedMoving) {
                spriteNum = 1; // First idle frame
                justStoppedMoving = false; // Reset flag
                spriteCounter = 0; // Reset counter so the normal idle animation starts fresh
            } else {
                // Slow down the rest of the idle animation
                spriteCounter++;
                if (spriteCounter > 32) { // Adjust for normal idle speed
                    spriteNum++;
                    if (spriteNum > 4) {
                        spriteNum = 1; // Loop back to the first idle sprite
                    }
                    spriteCounter = 0; 
                }
            }
        }
    }




    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = upImages[spriteNum - 1];
                break;
            case "down":
                image = downImages[spriteNum - 1];
                break;
            case "left":
                image = leftImages[spriteNum - 1];
                break;
            case "right":
                image = rightImages[spriteNum - 1];
                break;
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    } 
}

