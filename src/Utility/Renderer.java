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

    public Renderer(String environmentImagePath) {
        int w = (int) (Screen.getWidth( ));
        int h = (int) (Screen.getHeight( ));
        this.BEINGRENDERED = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ACTUALFRAME = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT(environmentImagePath);
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
//        long startTime = System.nanoTime( );
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

//        long renderTime = System.nanoTime( ) - startTime;

        Graphics2D g2d = ACTUALFRAME.createGraphics();
        g2d.drawImage(BEINGRENDERED, 0, 0, null);

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(new java.awt.Color(0, 0, 0, 80));
        g2d.fillRoundRect(20, height - 60, width / 2, 40, 32, 32);
        g2d.setColor(java.awt.Color.lightGray);
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, Math.min(16, (int)(16 / Screen.getScale()))));
        g2d.drawString(subtitle.getSubtitle(), ( width / 4 ) - Math.min(16, (int)(16 / Screen.getScale())) - 20, height - 35);

        Code.render(g2d, Screen.getWidth());
        VariableTracker.render(g2d);

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
//        System.out.printf("Rendered in %.2f ms%n", renderTime / 1_000_000.0);

    }

    public BufferedImage getActualFrame() {
        return ACTUALFRAME;
    }


}