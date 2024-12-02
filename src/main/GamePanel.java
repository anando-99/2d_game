package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    // Screen Settings
    final int originalTileSize = 32;
    final int scaleFactor = 3;
    public final int tileSize = originalTileSize * scaleFactor;
    public final int maxScreenCol = 27;
    public final int maxScreenRow = 15;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // World Settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // Full Screen Settings
    BufferedImage tempScreen;
    Graphics2D g2;

    int FPS = 60; // Frames per second

    TileManager tileM;
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public Player player;
    JFrame window; // Added JFrame field


    public GamePanel(JFrame window) {  // Modify constructor to accept JFrame
        this.window = window;  // Assign passed JFrame to the field
        // Set the size of the panel to match the calculated screen width and height
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));

        // Set the background color of the game panel to black
        this.setBackground(Color.black);

        // Enable double buffering for smoother rendering
        this.setDoubleBuffered(true);
        // Enables Key Handling for movement
        this.addKeyListener(keyH);
        // To focus game panel's receive key input
        this.setFocusable(true);
        this.requestFocusInWindow(); // Ensure the panel gains focus

        // Initialize TileManager and Player
        tileM = new TileManager(this);
        player = new Player(this, keyH);

        // Full Screen
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();


    }





    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        this.requestFocusInWindow(); // Ensure the panel gains focus
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; // 0.01666 seconds
        double nextDrawTime = System.nanoTime();
        double delta = 0;

        while (gameThread != null) {
            // Calculate delta time
            double currentTime = System.nanoTime();
            delta += (currentTime - nextDrawTime) / drawInterval;
            nextDrawTime = currentTime;

            // Update information based on delta time
            while (delta >= 1) {
                update();
                delta--;
            }

            // Draw the screen with the updated information
            drawToTempScreen();
            drawToScreen();

            // Control frame rate
            try {
                double remainingTime = (nextDrawTime + drawInterval - System.nanoTime()) / 1000000;
                if (remainingTime > 0) {
                    Thread.sleep((long) remainingTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check for fullscreen toggle
            if (keyH.toggleFullScreen) {
                toggleFullScreen();
                keyH.toggleFullScreen = false; // Reset the flag after toggling
            }
        }
    }

    public void update() {
        player.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        tileM.draw((Graphics2D) g);
        player.draw((Graphics2D) g);
    }

    public void drawToTempScreen() {
        Graphics2D g2Temp = (Graphics2D) tempScreen.getGraphics();
        // Clear the screen before drawing
        g2Temp.setBackground(Color.BLACK);
        g2Temp.clearRect(0, 0, screenWidth, screenHeight);
        paintComponent(g2Temp); // Use paintComponent to draw to the BufferedImage
        g2Temp.dispose();
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        if (g != null) {
            g.drawImage(tempScreen, 0, 0, getWidth(), getHeight(), null);
            g.dispose();
        }
    }
}
