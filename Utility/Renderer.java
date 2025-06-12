package Utility;

import Rendering.Render;
import Shapes.Core.Shape;
import Shapes.JBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

import static Utility.RayTracer.rayColor;

public class Renderer {

    private static final int TILE_SIZE = 32;
    private static final int MAX_RECURSION_DEPTH = 3;
    private static final int MAX_THREADS = Math.max(1, Runtime.getRuntime( ).availableProcessors( ) - 1);

    private Encoder encoder;
    BufferedImage BEINGRENDERED, ENVIRONMENT, ACTUALFRAME;
    private double scale;
    private double antiAliasing;
    private int lastInteractiveCaller;
    private Camera savedCameraState;


    private static final Random RAND = new Random();

    public Renderer(String environmentImagePath) {

        this.scale = 0.5;
        this.antiAliasing = 1.0;
        int w = ((int)(Screen.getWidth() * scale)) & ~1;
        int h = ((int)(Screen.getHeight() * scale)) & ~1;
        this.BEINGRENDERED = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ACTUALFRAME = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT(environmentImagePath);
        this.lastInteractiveCaller = -1;
        this.savedCameraState = null;
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

    public void setAntialiasing(double antiAliasing) {
        this.antiAliasing = antiAliasing;
    }

    public void setBackground(String background) {
        setENVIRONMENT(background);
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

//    public void setENVIRONMENT(String filePath) {
//        try {
//            ENVIRONMENT = ImageIO.read(getClass().getResourceAsStream(filePath));
//        } catch (IOException e) {
//            System.out.println("Environment failed to load");
//        }
//    }

    public void setENVIRONMENT(String filePath) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (in == null) {
                throw new IllegalStateException("Could not find resource: " + filePath);
            }
            ENVIRONMENT = ImageIO.read(in);
        } catch (IOException e) {
            System.out.println("Environment failed to load: " + filePath);
            e.printStackTrace();
        }
    }

    public void interactiveDrawImage(Camera camera, ArrayList<Shape> world, Subtitle subtitle, Render mode, int interactive){

        if (mode == Render.STEP_WISE_INTERACTIVE) {
            camera.setRadius(Window.getRadius());
            camera.setYaw(Window.getYaw());
            camera.setPitch(Window.getPitch());
            camera.setM_X(Window.getMX());
            camera.setM_Y(Window.getMY());
            camera.setM_Z(Window.getMZ());
        }

        drawImage(camera, world, subtitle, mode, 1);
    }

    public void drawImage(Camera camera, ArrayList<Shape> world, Subtitle subtitle, Render mode, int interactiveCaller) {

        final int width = BEINGRENDERED.getWidth( );
        final int height = BEINGRENDERED.getHeight( );
        final int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster( ).getDataBuffer( )).getData( );
        final Camera finalCamera;


        // Track transition
        boolean transitionedFrom1to0 = (lastInteractiveCaller == 1 && interactiveCaller == 0);
        boolean transitionedFrom0to1 = (lastInteractiveCaller == 0 && interactiveCaller == 1);

        if (mode == Render.STEP_WISE_INTERACTIVE && transitionedFrom0to1) {
            savedCameraState = camera.clone();
        }

        // 🔁 Camera restore logic (1 ➝ 0 transition)
        if (mode == Render.STEP_WISE_INTERACTIVE && transitionedFrom1to0 && savedCameraState != null) {
            camera.copyFrom(savedCameraState);
        }

        finalCamera = camera.setCameraPerspective(width, height);

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
                            final int rowOffset = y * width;
                            for (int x = tx; x < tx + tileW; x++) {

                                float r = 0, g = 0, b = 0;
                                int samples = (int) antiAliasing;
                                int sqrtSamples = (int) Math.sqrt(samples);

                                for (int sy = 0; sy < sqrtSamples; sy++) {
                                    for (int sx = 0; sx < sqrtSamples; sx++) {
                                        double u = (x + (sx + 0.5) / sqrtSamples) / (width - 1);
                                        double v = (height - y - 1 + (sy + 0.5) / sqrtSamples) / (height - 1);
                                        ray = finalCamera.getRay(u, v);
                                        Color color = rayColor(mode, finalCamera, world, ray, ENVIRONMENT, MAX_RECURSION_DEPTH);
                                        r += color.r;
                                        g += color.g;
                                        b += color.b;
                                    }
                                }

                                float scale = 1.0f / (samples);
                                Color averaged = new Color(r * scale, g * scale, b * scale);
                                pixels[rowOffset + x] = averaged.colorToInteger();
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

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawSubtitles(width, height, g2d, subtitle);

        for (Shape s : world) {
            if (s instanceof JBox box && box.val != null) {
                Point screen = box.getProjected2D(camera, width, height);
                if (screen != null) {
                    drawSubtitleLikeBox(g2d, screen, String.valueOf(box.val));
                }
            }
        }

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

        lastInteractiveCaller = interactiveCaller;

    }

    private void drawSubtitleLikeBox(Graphics2D g2d, Point screen, String text) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int fontSize = 18;
        Font font = new Font("SansSerif", Font.BOLD, fontSize);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int padding = 6;
        int boxWidth = textWidth + padding * 2;
        int boxHeight = textHeight + padding * 2;

        int boxX = (int) screen.x - boxWidth / 2;
        int boxY = (int) screen.y - (  boxHeight / 2);

        g2d.setColor(new java.awt.Color(0, 0, 0, 100));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 24, 24);

        g2d.setColor(java.awt.Color.WHITE);
        g2d.drawString(text, boxX + padding, boxY + padding + fm.getAscent() - 2);
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