package Utility;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class BackgroundCache {
    private BufferedImage backgroundImage;
    private final Set<String> dirtyRegions;
    private final int width;
    private final int height;
    private final int tileSize;
    private final Color backgroundColor;
    
    public BackgroundCache(int width, int height, int tileSize, Color backgroundColor) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.backgroundColor = backgroundColor;
        this.dirtyRegions = new HashSet<>();
        initializeBackground();
    }
    
    private void initializeBackground() {
        backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Fill with background color
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                backgroundImage.setRGB(x, y, backgroundColor.colorToInteger());
            }
        }
        // Mark all regions as dirty initially
        markAllDirty();
    }
    
    public synchronized void markRegionDirty(int x, int y) {
        int tileX = x / tileSize;
        int tileY = y / tileSize;
        dirtyRegions.add(tileX + "_" + tileY);
    }
    
    public synchronized void markAllDirty() {
        dirtyRegions.clear();
        int xTiles = (width + tileSize - 1) / tileSize;
        int yTiles = (height + tileSize - 1) / tileSize;
        
        for (int y = 0; y < yTiles; y++) {
            for (int x = 0; x < xTiles; x++) {
                dirtyRegions.add(x + "_" + y);
            }
        }
    }
    
    public synchronized boolean isRegionDirty(int tileX, int tileY) {
        return dirtyRegions.contains(tileX + "_" + tileY);
    }
    
    public synchronized void markRegionClean(int tileX, int tileY) {
        dirtyRegions.remove(tileX + "_" + tileY);
    }
    
    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }
    
    public void updateBackground(int x, int y, Color color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            backgroundImage.setRGB(x, y, color.colorToInteger());
        }
    }
}
