package Utility;

import Rendering.Render;
import Shapes.Shape;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

import static Utility.Tracer.rayColor;

public class Renderer {

    private static final int TILE_SIZE = 32;
    private static final int MAX_RECURSION_DEPTH = 3;
    private static final int MAX_THREADS = Math.max(1, Runtime.getRuntime( ).availableProcessors( ) - 1);

    private Encoder encoder;
    BufferedImage BEINGRENDERED, ENVIRONMENT, ACTUALFRAME;
    private double scale;

    public Renderer(String environmentImagePath) {
        this.scale = 0.5;
        int w = ((int)(Screen.getWidth() * scale)) & ~1;
        int h = ((int)(Screen.getHeight() * scale)) & ~1;
        this.BEINGRENDERED = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ACTUALFRAME = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT(environmentImagePath);
    }

    public void setScale(String environmentImagePath, double scale) {
        int w = ((int)(Screen.getWidth() * scale)) & ~1;
        int h = ((int)(Screen.getHeight() * scale)) & ~1;
        this.BEINGRENDERED = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ACTUALFRAME = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT(environmentImagePath);
        this.scale = scale;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void setENVIRONMENT(String filePath) {
        try {
            ENVIRONMENT = ImageIO.read(getClass( ).getResourceAsStream(filePath));
        } catch (IOException e) {
            System.out.println("Environment failed to load");
        }
    }

    public void drawImage(Camera camera, ArrayList<Shape> world, Subtitle subtitle, Render mode) {

        final int width = BEINGRENDERED.getWidth( );
        final int height = BEINGRENDERED.getHeight( );
        final int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster( ).getDataBuffer( )).getData( );
        final Camera finalCamera;

        if (mode == Render.STEP_WISE_INTERACTIVE) {
            finalCamera = new Camera(
                    Window.getRadius(),
                    Window.getYaw(),
                    Window.getPitch(),
                    (int) Window.getMX(),
                    (int) Window.getMY(),
                    (int) Window.getMZ()
            ).setCameraPerspective(width, height);
        } else {
            finalCamera = camera.setCameraPerspective(width, height);
        }

        final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        final CountDownLatch latch = new CountDownLatch((width / TILE_SIZE + 1) * (height / TILE_SIZE + 1));
        for (int tileY = 0; tileY < height; tileY += TILE_SIZE) {
            for (int tileX = 0; tileX < width; tileX += TILE_SIZE) {
                final int tx = tileX;
                final int ty = tileY;
                final int tileW = Math.min(TILE_SIZE, width - tx);
                final int tileH = Math.min(TILE_SIZE, height - ty);
                executor.submit(() -> {
                    try {
                        Ray ray = new Ray( );
                        for (int y = ty; y < ty + tileH; y++) {
                            final double v = (double) (height - y) / (height - 1);
                            final int rowOffset = y * width;
                            for (int x = tx; x < tx + tileW; x++) {
                                final double u = (double) x / (width - 1);
                                ray = finalCamera.getRay(u, v);
                                Color pixelColor = rayColor(world, ray, ENVIRONMENT, MAX_RECURSION_DEPTH);
                                pixels[rowOffset + x] = pixelColor.colorToInteger( );
                            }
                        }
                    } finally {
                        latch.countDown( );
                    }
                });
            }
        }
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                System.err.println("Warning: Rendering took too long, some tiles may be incomplete");
            }
        } catch (InterruptedException e) {
            Thread.currentThread( ).interrupt( );
            throw new RuntimeException("Rendering interrupted", e);
        } finally {
            executor.shutdownNow( );
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow( );
                }
            } catch (InterruptedException e) {
                Thread.currentThread( ).interrupt( );
            }
        }

        Graphics2D g2d = ACTUALFRAME.createGraphics();
        g2d.drawImage(BEINGRENDERED, 0, 0, null);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawSubtitles(width, height, g2d, subtitle);
        Code.render(g2d, Screen.getWidth(), scale);
        Variable.render(g2d, scale);

        g2d.dispose();

        if (mode == Render.VIDEO) {
            try {
                encoder.writeFrame(ACTUALFRAME);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (mode == Render.LIVE) {
            Window.updateWindow(ACTUALFRAME);
        } else if (mode == Render.STEP_WISE) {
            Window.updateWindow(ACTUALFRAME);
        } else if (mode == Render.STEP_WISE_INTERACTIVE) {
            Window.updateWindow(ACTUALFRAME);
        }

    }

    private void drawSubtitles(int width, int height, Graphics2D g2d, Subtitle subtitle) {

        String subtitleText = subtitle.getSubtitle();
        if (subtitleText != null && !subtitleText.isEmpty()) {

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int baseFontSize = 18;
            int fontSize = Math.max(10, (int)(baseFontSize * scale));
            Font font = new Font("SansSerif", Font.BOLD, fontSize);
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(subtitleText);
            int textHeight = fm.getHeight();

            int paddingX = (int)(20 * scale);
            int paddingY = (int)(12 * scale);

            int boxWidth = textWidth + 2 * paddingX;
            int boxHeight = textHeight + 2 * paddingY;

            int boxX = Math.max(20, (width - boxWidth) / 2);

            int bottomMargin = 30;
            int boxY = height - boxHeight - bottomMargin;

            g2d.setColor(new java.awt.Color(0, 0, 0, 100));
            g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 24, 24);

            g2d.setColor(java.awt.Color.LIGHT_GRAY);
            int textX = boxX + paddingX;
            int textY = boxY + paddingY + fm.getAscent() - 2;
            g2d.drawString(subtitleText, textX, textY);
        }
    }


    public BufferedImage getActualFrame() {
        return ACTUALFRAME;
    }

}