package Utility;

import Rendering.Render;
import Shapes.Core.Shape;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
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

    private static double yaw = 0.25, pitch = 0, radius = 2.5;
    private static double mx = 0, my = 0, mz = 0;
    private static double latestX = 0, latestY = 0;

    private static Renderer renderer;
    private static Camera camera;
    private static ArrayList<Shape> world;
    private static Subtitle subtitle;
    private static Render mode;
    private static double scale;

    public static void initializeWindow() {
        if (initialized) return;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("3D Visualizer");
        frame.setSize(Screen.getWidth(), Screen.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(java.awt.Color.black);
        frame.setForeground(java.awt.Color.black);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);

        layeredPane = new JLayeredPane( );
        layeredPane.setPreferredSize(new Dimension(Screen.getWidth(), Screen.getHeight()));
        layeredPane.setLayout(null);

        JPanel vignette = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                RadialGradientPaint gradient = new RadialGradientPaint(
                        new Point2D.Double((double) width /2, (double) height /2),
                        Math.max(width, height) * 0.7f,
                        new float[]{0.0f, 1.0f},
                        new java.awt.Color[]{new java.awt.Color(0, 0, 0, 0), new java.awt.Color(0, 0, 0, 120)}
                );

                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
            }
        };
        vignette.setBounds(0, 0, Screen.getWidth(), Screen.getHeight());
        vignette.setOpaque(false);


        imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, Screen.getWidth(), Screen.getHeight());
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layeredPane.add(imageLabel, JLayeredPane.DEFAULT_LAYER);

        nextStepButton = new JButton("▶ Next");
        nextStepButton.setFocusable(false);
        nextStepButton.setVisible(false);

        nextStepButton.setSize(Screen.getWidth() / 3, Screen.getHeight() / 4 ) ;
        nextStepButton.setLocation(
                ( Screen.getWidth() / 2 ) - ( nextStepButton.getWidth() / 2 ),
                Screen.getHeight() / 3
        );

        nextStepButton.setBackground(new java.awt.Color(40, 40, 40, 50));
        nextStepButton.setForeground(java.awt.Color.lightGray);
        nextStepButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        nextStepButton.setBorder(BorderFactory.createEmptyBorder());
        nextStepButton.setOpaque(true);
        nextStepButton.setFocusPainted(false);
        nextStepButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextStepButton.addActionListener(e -> stepRequested.set(true));

//        layeredPane.add(vignette, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(nextStepButton, JLayeredPane.PALETTE_LAYER);

        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setVisible(true);

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

    public static void setScale(double scale) {
        frame.setSize((int) (Screen.getWidth() * scale), (int) (Screen.getHeight() * scale));
        imageLabel.setBounds(0, 0, (int) (Screen.getWidth() * scale), (int) (Screen.getHeight() * scale));
        layeredPane.setPreferredSize(new Dimension((int) (Screen.getWidth() * scale), (int) (Screen.getHeight() * scale)));
        nextStepButton.setSize((int) (( Screen.getWidth() * scale ) / 8), (int) ((Screen.getHeight() * scale ) / 16));
        nextStepButton.setLocation(
                (int) ((Screen.getWidth() * scale) - nextStepButton.getWidth() - (Screen.getWidth() * scale) / 16),
                (int) ((Screen.getHeight() * scale) - ( (Screen.getHeight() / 10.0 ) ) )
        );

        frame.setLocationRelativeTo(null);

        if (renderer != null && camera != null && world != null && subtitle != null) {
            BufferedImage newImage = renderer.getActualFrame();
            updateWindow(newImage);
            frame.revalidate();
            frame.repaint();
        }

        Window.scale = scale;
    }

    public static double getScale() {
        return scale;
    }


    public static void setupInteractivity() {
        if (initializedInteractivity) return;

        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                double moveSpeed = 1;
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
