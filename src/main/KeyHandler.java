package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // Movement flags
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean toggleFullScreen; // Flag for toggling fullscreen

    @Override
    public void keyTyped(KeyEvent e) {
        // No implementation needed for keyTyped in this case
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Check which key was pressed and set the corresponding flag
        if (keyCode == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (keyCode == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (keyCode == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (keyCode == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (keyCode == KeyEvent.VK_F11) {
            toggleFullScreen = true;
        }
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Check which key was released and reset the corresponding flag
        if (keyCode == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (keyCode == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (keyCode == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (keyCode == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (keyCode == KeyEvent.VK_F11) {
            toggleFullScreen = false;
        }
    }
}
