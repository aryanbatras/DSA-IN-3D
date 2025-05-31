import java.awt.image.DataBufferInt;
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
    ScheduledExecutorService Clock; ScheduledFuture<?> Schedule;

    Window() {
        setup();
        start();
        listen();
    }

    private void start() {
        int[] arr = {5, 4, 3};

        float space = 1.5f;
        float startLeft = -arr.length / 2.0f;
        for (int i = 0; i < arr.length; i++) {
            float x = (startLeft + i) * space;
            WORLD.add(new Box(new Point(x, 0, 0), 1, 1, 1,
                    new Color(0.4f, 0.7f, 1.0f), Material.TRANSLUCENT, 0));
        }

        double totalX = 0, totalY = 0, totalZ = 0;
        for (Shape s : WORLD) {
            if (s instanceof Box box) {
                Point c = box.getCenter();
                totalX += c.x;
                totalY += c.y;
                totalZ += c.z;
            }
        }

        M_X = totalX / arr.length;
        M_Y = totalY / arr.length + 1;
        M_Z = totalZ / arr.length;

        double  minX = Double.MAX_VALUE,
                maxX = -Double.MAX_VALUE;

        for (Shape s : WORLD) {
            if (s instanceof Box box) {
                double x = box.getCenter().x;
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
        }

        double worldLength = maxX - minX;
        radius = worldLength * 1.25;

        drawImage(HEIGHT, WIDTH);
    }


    private void setup() {
        SCREEN = Toolkit.getDefaultToolkit().getScreenSize(); HEIGHT = SCREEN.height; WIDTH = SCREEN.width; setSize(WIDTH,HEIGHT);
        V_YAXIS = 2; V_XAXIS = 1; V_ZAXIS = -3; M_X = 0; M_Y = 0; M_Z = 0; latestX = 0; latestY = 0;
        radius = new Point(V_XAXIS - M_X, V_YAXIS - M_Y, V_ZAXIS - M_Z).length(); yaw = 0; pitch = 0;
        Clock = Executors.newSingleThreadScheduledExecutor();
        try { ENVIRONMENT = ImageIO.read(getClass().getResourceAsStream("/Resources/lake.jpg") ); } catch(IOException e){ e.printStackTrace(); }
        setBackground(java.awt.Color.BLACK); setLocationRelativeTo(null);
        setUndecorated(true); setResizable(false); setVisible(true); setLayout(null);
        WORLD = new ArrayList<Shape>();
    }

    private void listen() {
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
                drawImage(HEIGHT / 20, WIDTH / 20);
                drawImageSchedule();
            }
        });
        addMouseListener(new MouseAdapter( ) {
            public void mousePressed(MouseEvent e)  {
                latestX = e.getX(); latestY = e.getY();
            }
            public void mouseReleased(MouseEvent e) {
                drawImageSchedule();
            }
        });
        addMouseMotionListener(new MouseAdapter( ) {
            public void mouseDragged(MouseEvent e) {
                double beingDragged_X = e.getX(), beingDragged_Y = e.getY();
                if (Math.abs(beingDragged_X - latestX) < 20 && Math.abs(beingDragged_Y - latestY) < 20) return;
                yaw += - ( beingDragged_X - latestX ) * 0.1; pitch -= ( beingDragged_Y - latestY ) * 0.1;
                drawImage(HEIGHT / 20, WIDTH / 20);
                latestX = beingDragged_X; latestY = beingDragged_Y;
            }
        });
        addMouseWheelListener(new MouseAdapter( ) {
            public void mouseWheelMoved(MouseWheelEvent e) {
                double offset_Z = e.getPreciseWheelRotation() / 10; radius += offset_Z * 0.25;
                System.out.println(radius );
                drawImage(HEIGHT / 20, WIDTH / 20);
                drawImageSchedule();
            }
        });
    }

    void drawImageSchedule(){
        if(Schedule != null && Schedule.isDone() == false) Schedule.cancel(false);
        Schedule = Clock.schedule(() -> drawImage(HEIGHT, WIDTH),200,TimeUnit.MILLISECONDS);
    }

    private void drawImage(int HEIGHT, int WIDTH) {

        long start = System.nanoTime() / 1000000;
        newCameraPosition();
        BEINGRENDERED = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(cores);
        int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster().getDataBuffer()).getData();
        ArrayList<Future<?>> tasks = new ArrayList<>();
        for (int yStart = 0; yStart < HEIGHT; yStart += HEIGHT / cores) {
            final int y0 = yStart;
            final int y1 = Math.min(yStart + (HEIGHT / cores), HEIGHT);
            tasks.add(exec.submit(() -> {
                for (int y = y0; y < y1; y++) {
                    for (int x = 0; x < WIDTH; x++) {
                        double u = (double) x / WIDTH;
                        double v = (double) (HEIGHT - y) / HEIGHT;
                        Ray ray = CAMERA.getRay(u, v);
                        Color pixelColor = new Color(Main.rayColor(WORLD, ray, ENVIRONMENT, 2));
                        pixels[y * WIDTH + x] = pixelColor.colorToInteger();
                    }
                }
            }));
        }
        for (Future<?> f : tasks) {
            try { f.get(); }
            catch (InterruptedException | ExecutionException e) { throw new RuntimeException(e); }
        }
        exec.shutdown();

        IMG = BEINGRENDERED;
        repaint();
        System.out.println("Rendered " + ( ( System.nanoTime() / 1000000 ) - start ) + "ms");
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
