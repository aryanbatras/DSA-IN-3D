package Utility;

import Rendering.Render;
import Shapes.Shape;

import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

public class Window {

    private static JFrame frame;
    private static JLabel imageLabel;
    private static boolean initialized = false;
    private static boolean initializedInteractivity = false;

    private static final AtomicBoolean stepRequested = new AtomicBoolean(false);
    private static JButton nextStepButton;
    private static JLayeredPane layeredPane;

    // --- Camera and Interaction State ---
    private static double yaw = 0.25, pitch = 0, radius = 2.5;
    private static double mx = 0, my = 0, mz = 0;
    private static double latestX = 0, latestY = 0;


    private static Renderer renderer;
    private static Camera camera;
    private static ArrayList<Shape> world;
    private static Subtitle subtitle;
    private static Render mode;

//    private final Renderer renderer;

    public static void initializeWindow() {
        if (initialized) return;

        frame = new JFrame("3D Visualizer");
        frame.setSize(Screen.getWidth(), Screen.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(java.awt.Color.BLACK);
        frame.setForeground(java.awt.Color.BLACK);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);

        layeredPane = new JLayeredPane( );
        layeredPane.setPreferredSize(new Dimension(Screen.getWidth(), Screen.getHeight()));
        layeredPane.setLayout(null);

        imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, Screen.getWidth(), Screen.getHeight());
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layeredPane.add(imageLabel, JLayeredPane.DEFAULT_LAYER);

        nextStepButton = new JButton("▶ Next");
        nextStepButton.setFocusable(false);
        nextStepButton.setVisible(false);

        nextStepButton.setSize(Screen.getWidth() / 8, Screen.getHeight() / 16);
        nextStepButton.setLocation(
                Screen.getWidth() - nextStepButton.getWidth() - Screen.getWidth() / 16,
                Screen.getHeight() - 60
        );

        nextStepButton.setBackground(new java.awt.Color(40, 40, 40, 50));
        nextStepButton.setForeground(java.awt.Color.lightGray);
        nextStepButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        nextStepButton.setBorder(BorderFactory.createEmptyBorder());
        nextStepButton.setOpaque(true);
        nextStepButton.setFocusPainted(false);
        nextStepButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextStepButton.addActionListener(e -> stepRequested.set(true));
        layeredPane.add(nextStepButton, JLayeredPane.PALETTE_LAYER);

        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setVisible(true);

//        setupInteractivity();
        initialized = true;
    }

    public static double getYaw() { return yaw; }
    public static double getPitch() { return pitch; }
    public static double getRadius() { return radius; }
    public static double getMX() { return mx; }
    public static double getMY() { return my; }
    public static double getMZ() { return mz; }

    public static void invokeReferences(Renderer renderer, Camera camera, ArrayList<Shape> world, Subtitle subtitle, Render mode){
        Window.renderer = renderer;
        Window.camera = camera;
        Window.world = world;
        Window.subtitle = subtitle;
        Window.mode = mode;
    }


    public static void setupInteractivity() {
        if (initializedInteractivity) return;

        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                double moveSpeed = 2.5;
                double yawRad = Math.toRadians(yaw), pitchRad = Math.toRadians(pitch);
                Point   forward = new Point(Math.cos(pitchRad) * Math.sin(yawRad), Math.sin(pitchRad), Math.cos(pitchRad) * Math.cos(yawRad)).normalize(),
                        right = forward.cross(new Point(0, 1, 0)).normalize(),
                        up = right.cross(forward).normalize(),
                        movement;
                switch (e.getKeyCode()) {
                    case 38: movement = forward.mul(moveSpeed);  break;
                    case 40: movement = forward.mul(-moveSpeed); break;
                    case 37: movement = right.mul(-moveSpeed);   break;
                    case 39: movement = right.mul(moveSpeed);    break;
                    case 88: movement = up.mul(moveSpeed);       break;
                    case 90: movement = up.mul(-moveSpeed);      break;
                    default: return;
                }
                mx += movement.x;
                my += movement.y;
                mz += movement.z;
                renderer.drawImage(camera, world, subtitle, mode);
            }
        });

        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                latestX = e.getX();
                latestY = e.getY();
            }
        });

        frame.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                double dx = e.getX() - latestX;
                double dy = e.getY() - latestY;
                if (Math.abs(dx) < 20 && Math.abs(dy) < 20) return;
                yaw -= dx * 0.1;
                pitch -= dy * 0.1;
                latestX = e.getX();
                latestY = e.getY();
                renderer.drawImage(camera, world, subtitle, mode);
            }
        });

        frame.addMouseWheelListener(e -> {
                radius += e.getPreciseWheelRotation() * 0.25;
                renderer.drawImage(camera, world, subtitle, mode);
    });

        initializedInteractivity = true;
    }

    public static void updateWindow(BufferedImage image) {
        if (!initialized) return;
        imageLabel.setIcon(new ImageIcon(image));
    }

    public static void enableStepWiseMode(boolean enable) {
        if (!initialized) return;
        SwingUtilities.invokeLater(() -> nextStepButton.setVisible(enable));
    }

    public static void waitUntilNextStep() {
        enableStepWiseMode(true);
        stepRequested.set(false);
        while (!stepRequested.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        enableStepWiseMode(false);
    }

}
