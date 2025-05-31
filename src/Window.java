import java.io.*;
import java.awt.*;
import java.util.*;

import Shapes.Box;
import Shapes.Shape;
import javax.swing.*;

import Shapes.Sphere;
import Utility.*;
import Utility.Color;
import Utility.Point;

import java.awt.event.*;
import javax.imageio.ImageIO;
import java.util.concurrent.*;
import java.awt.image.BufferedImage;

public class Window extends JFrame {
    int HEIGHT, WIDTH;
    double V_YAXIS, V_XAXIS, V_ZAXIS, M_X, M_Y, M_Z, latestX, latestY, yaw, pitch, radius;
    BufferedImage IMG, BEINGRENDERED, ENVIRONMENT;
    Dimension SCREEN; Camera CAMERA; ArrayList<Shape> WORLD;
    ScheduledExecutorService mouseClock, keyClock;
    ScheduledFuture<?> mouseSchedule, keySchedule;

    Window() {
        setup();
        listener();
    }

    private void setup() {
        SCREEN = Toolkit.getDefaultToolkit().getScreenSize(); HEIGHT = SCREEN.height / 2; WIDTH = SCREEN.width / 2; setSize(WIDTH,HEIGHT);
        V_YAXIS = 2; V_XAXIS = 1; V_ZAXIS = -3; M_X = 0; M_Y = 0; M_Z = 0; latestX = 0; latestY = 0;
        Point startpos = new Point(M_X - V_XAXIS, M_Y - V_YAXIS, M_Z - V_ZAXIS).normalize();
        yaw = Math.toDegrees(Math.atan2(startpos.x, startpos.z)); pitch = Math.toDegrees(Math.asin(startpos.y));
        radius = new Point(V_XAXIS - M_X, V_YAXIS - M_Y, V_ZAXIS - M_Z).length();
        keyClock = Executors.newSingleThreadScheduledExecutor(); mouseClock = Executors.newSingleThreadScheduledExecutor();
        String[] sceneNames = { "PureSky", "Room", "MirrorHall", "ModernHall", "ChristmasHall",  "Lounge", "Garden", "Backyard", "Lake", "Pool" };
        String selectedScene = sceneNames[new Random().nextInt(sceneNames.length)];
        try { ENVIRONMENT = ImageIO.read(getClass().getResourceAsStream("/Resources/" + selectedScene.toLowerCase() + ".jpg") ); } catch(IOException e){ e.printStackTrace(); }
        WORLD = new ArrayList<Shape>();
        WORLD.add(new Sphere(new Point(0.5,0.5,0.5), 0.5, new Color(0.8f,0.8f,0.8f), Material.METAL, 0.01 ));
        WORLD.add(new Box(new Point(-1, 0.5, -1), 0.5, 0.5, 0.5,new Color(0.8f,0.8f,0.8f), Material.METAL, 0.01 ));
        drawImage(HEIGHT, WIDTH); setBackground(java.awt.Color.BLACK); setLocationRelativeTo(null);
        setUndecorated(true); setResizable(false); setVisible(true); setLayout(null);
    }

    private void listener() {
        addKeyListener(new KeyAdapter( ) {
            public void keyPressed(KeyEvent e) {
                double  moveSpeed = 0.5,
                        yawRad = Math.toRadians(yaw),
                        pitchRad = Math.toRadians(pitch);
                Point forward = new Point(Math.cos(pitchRad) * Math.sin(yawRad), Math.sin(pitchRad),Math.cos(pitchRad) * Math.cos(yawRad)).normalize(),
                        right = forward.cross(new Point(0, 1, 0)).normalize(); Point up = right.cross(forward).normalize(),
                        movement = new Point(0, 0, 0);
                switch (e.getKeyCode()) {
                    case 38: movement = forward.mul(moveSpeed);  break;
                    case 40: movement = forward.mul(-moveSpeed); break;
                    case 37: movement = right.mul(-moveSpeed);   break;
                    case 39: movement = right.mul(moveSpeed);    break;
                    case 88: movement = up.mul(moveSpeed);       break;
                    case 90: movement = up.mul(-moveSpeed);      break;
                    default: return;
                }
                M_X += movement.x; M_Y += movement.y; M_Z += movement.z;
                drawImage(HEIGHT / 8, WIDTH / 8);
                if(keySchedule != null && keySchedule.isDone() == false) keySchedule.cancel(true);
                keySchedule = keyClock.schedule(() -> drawImage(HEIGHT, WIDTH),250,TimeUnit.MILLISECONDS);
            }
        });
        addMouseListener(new MouseAdapter( ) {
            public void mousePressed(MouseEvent e)  {
                latestX = e.getX(); latestY = e.getY();
            }
            public void mouseReleased(MouseEvent e) {
                drawImage(HEIGHT, WIDTH);
            }
        });
        addMouseMotionListener(new MouseAdapter( ) {
            public void mouseDragged(MouseEvent e) {
                double beingDragged_X = e.getX(), beingDragged_Y = e.getY();
                yaw += - ( beingDragged_X - latestX ) * 0.1; pitch -= ( beingDragged_Y - latestY ) * 0.1;
                if (Math.abs(beingDragged_X - latestX) < 20 && Math.abs(beingDragged_Y - latestY) < 20) return;
                drawImage(HEIGHT / 8, WIDTH / 8);
                latestX = beingDragged_X; latestY = beingDragged_Y;
            }
        });
        addMouseWheelListener(new MouseAdapter( ) {
            public void mouseWheelMoved(MouseWheelEvent e) {
                double offset_Z = e.getPreciseWheelRotation() / 10; radius += offset_Z * 0.25;
                drawImage(HEIGHT / 8, WIDTH / 8);
                if(mouseSchedule != null && mouseSchedule.isDone() == false) mouseSchedule.cancel(true);
                mouseSchedule = mouseClock.schedule(() -> drawImage(HEIGHT, WIDTH),250,TimeUnit.MILLISECONDS);
            }
        });

    }

    private void drawImage(int HEIGHT, int WIDTH) {
        newCameraPosition();
        BEINGRENDERED = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                double u = (double) x / WIDTH;
                double v = (double) (HEIGHT - y) / HEIGHT;
                Ray ray = CAMERA.getRay(u, v);
                Color pixelColor = new Color(Main.rayColor(WORLD, ray, ENVIRONMENT, 10));
                    BEINGRENDERED.setRGB(x, y, pixelColor.colorToInteger());
            }
        }
        IMG = BEINGRENDERED;
        repaint();
    }

    private void newCameraPosition() {
        double  yawRad = Math.toRadians(yaw),
                pitchRad = Math.toRadians(pitch),
                dirX = Math.cos(pitchRad) * Math.sin(yawRad),
                dirY = Math.sin(pitchRad),
                dirZ = Math.cos(pitchRad) * Math.cos(yawRad);
        Point direction = new Point(dirX, dirY, dirZ).normalize(),
                lookFrom = new Point(M_X, M_Y, M_Z).add(direction.mul(-radius)),
                lookAt = new Point(M_X, M_Y, M_Z); Point vup = new Point(0, 1, 0);
        CAMERA = new Camera(lookFrom, lookAt, vup, 90, (double) WIDTH / HEIGHT);
    }

    public void paint(Graphics g) {
        if (IMG == null) return;
        g.drawImage(
                IMG,
                (getWidth( ) / 2) - (WIDTH / 2),
                (getHeight( ) / 2) - (HEIGHT / 2),
                WIDTH,
                HEIGHT,
                this
        );
    }

}
