package tile;

import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {

    GamePanel gp;
    Tile[] tiles;
    int[][][] mapTileNum; // 3D array to store tile numbers for multiple layers

    private static final int MAX_TILES_A = 109;
    private static final int MAX_TILES_B = 16;
    private static final int MAX_TILES_C = 127; // Updated to 127
    private static final int MAX_TILES_D = 16;
    private static final int TOTAL_TILES = 150 + MAX_TILES_A + MAX_TILES_B + MAX_TILES_C + MAX_TILES_D;

    private static final int MAX_LAYERS = 3; // Adjusted for multiple layers

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[TOTAL_TILES + 1]; // +1 to account for 1-based indexing
        mapTileNum = new int[MAX_LAYERS][gp.maxWorldCol][gp.maxWorldRow]; // 3D array for layers, columns, rows

        getTileImages();
        loadMap("/maps/worldmap.txt");
    }

    public void getTileImages() {
        try {
            int index = 1; // Start from index 1 to align with tile numbering

            // Load tiles
            loadTiles(1, 150, "/tiles/l0_tile%03d.png", index);
            loadTilesWithCollision(1, MAX_TILES_A, "/tiles/l0_tileA%03d.png", index + 150);
            loadTilesWithCollision(1, MAX_TILES_B, "/tiles/l0_tileB%02d.png", index + 150 + MAX_TILES_A);
            loadTilesWithCollision(1, MAX_TILES_C, "/tiles/l0_tileC%03d.png", index + 150 + MAX_TILES_A + MAX_TILES_B); // Updated index calculation
            loadTilesWithCollision(1, MAX_TILES_D, "/tiles/l0_tileD%02d.png", index + 150 + MAX_TILES_A + MAX_TILES_B + MAX_TILES_C); // Updated index calculation

            // Load overlay tiles
            for (int i = 1; i <= TOTAL_TILES; i++) {
                String overlayPath = "/tiles/overlay_tile" + String.format("%03d", i) + ".png";
                loadTileImage(null, i, overlayPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTiles(int start, int end, String pathTemplate, int index) throws IOException {
        for (int i = start; i <= end; i++) {
            String path = String.format(pathTemplate, i);
            loadTileImage(path, index++, null);
        }
    }

    private void loadTilesWithCollision(int start, int end, String pathTemplate, int index) throws IOException {
        for (int i = start; i <= end; i++) {
            String path = String.format(pathTemplate, i);
            loadTileImageWithCollision(path, index++, null);
        }
    }

    private void loadTileImage(String path, int index, String overlayPath) throws IOException {
        InputStream stream = path != null ? getClass().getResourceAsStream(path) : null;
        InputStream overlayStream = overlayPath != null ? getClass().getResourceAsStream(overlayPath) : null;

        if (stream != null && index < tiles.length) {
            BufferedImage baseImage = ImageIO.read(stream);
            BufferedImage overlayImage = (overlayStream != null) ? ImageIO.read(overlayStream) : null;
            tiles[index] = new Tile(baseImage, overlayImage);
        }
    }

    private void loadTileImageWithCollision(String path, int index, String overlayPath) throws IOException {
        InputStream stream = path != null ? getClass().getResourceAsStream(path) : null;
        InputStream overlayStream = overlayPath != null ? getClass().getResourceAsStream(overlayPath) : null;

        if (stream != null && index < tiles.length) {
            BufferedImage baseImage = ImageIO.read(stream);
            BufferedImage overlayImage = (overlayStream != null) ? ImageIO.read(overlayStream) : null;
            Tile tile = new Tile(baseImage, overlayImage);
            tile.collision = true; // Set collision to true for B, C, and D tiles
            tiles[index] = tile;
        }
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                System.err.println("Map file not found: " + filePath);
                return; // Map file not found
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;

            String line;
            while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                String[] numbers = line.split(" ");

                for (int i = 0; i < numbers.length; i++) {
                    String[] values = numbers[i].split(",");
                    int baseTileNum = Integer.parseInt(values[0]);
                    int overlayTileNum = values.length > 1 ? Integer.parseInt(values[1]) : 0;

                    // Ensure the tile numbers are within the valid range
                    if (baseTileNum >= 1 && baseTileNum <= TOTAL_TILES) {
                        mapTileNum[0][col][row] = baseTileNum;
                    } else {
                        mapTileNum[0][col][row] = 0; // Default to 0 if the number is invalid
                    }

                    if (overlayTileNum >= 1 && overlayTileNum <= TOTAL_TILES) {
                        mapTileNum[1][col][row] = overlayTileNum; // Use layer 1 for overlay tiles
                    }

                    col++;
                    if (col >= gp.maxWorldCol) {
                        col = 0;
                        row++;
                    }
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        for (int layer = 0; layer < MAX_LAYERS; layer++) {
            int worldCol = 0;
            int worldRow = 0;

            while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
                int tileNum = mapTileNum[layer][worldCol][worldRow];
                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && 
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX && 
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY && 
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                    
                    // Check if the tile at tileNum is loaded
                    if (tileNum > 0 && tileNum < tiles.length && tiles[tileNum] != null) {
                        Tile tile = tiles[tileNum];
                        if (tile.baseImage != null) {
                            g2.drawImage(tile.baseImage, screenX, screenY, gp.tileSize, gp.tileSize, null);
                        }
                        if (tile.overlayImage != null) {
                            drawOverlayTile(g2, tile.overlayImage, screenX, screenY);
                        }
                    }
                }
                worldCol++;

                if (worldCol == gp.maxWorldCol) {
                    worldCol = 0;
                    worldRow++;
                }
            }
        }
    }

    private void drawOverlayTile(Graphics2D g2, BufferedImage overlayImage, int x, int y) {
        // Set transparency for overlay image
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2.setComposite(ac);
        g2.drawImage(overlayImage, x, y, gp.tileSize, gp.tileSize, null);
        // Reset composite to default
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
