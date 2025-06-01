import java.io.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import Shapes.Box;
import Shapes.Shape;
import javax.swing.*;

import Utility.*;
import Utility.Color;
import Utility.Point;

import java.awt.event.*;
import javax.imageio.ImageIO;
import java.util.concurrent.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

// KISS: KEEP IT SUPER SIMPLE
public class Window extends JFrame {
    int HEIGHT, WIDTH;
    double V_YAXIS, V_XAXIS, V_ZAXIS, M_X, M_Y, M_Z, latestX, latestY, yaw, pitch, radius;
    BufferedImage IMG, BEINGRENDERED, ENVIRONMENT;
    Dimension SCREEN; Camera CAMERA; ArrayList<Shape> WORLD;
    ScheduledExecutorService Clock; ScheduledFuture<?> Schedule;
    Map<Box, Integer[]> boxMap;
    int[] are; float startLeft; int frameCount;

    Window() throws InterruptedException, IOException {
        setup();
        start();
        listen();
        animate();
    }

    private void animate() throws InterruptedException, IOException {
        int insertVal = 50;
        int insertAtPos = 2;
        int size = WORLD.size();

        Box newBox = new Box(new Point(startLeft, 1.25, 0), 1, 1, 0.1,
                new Color(0.4f, 0.7f, 1.0f), Material.CHROME, 0, insertVal);

        String str = String.valueOf(insertVal);
        Integer[] digits = new Integer[str.length()];
        for (int j = 0; j < str.length(); j++) {
            digits[j] = Character.getNumericValue(str.charAt(j));
        }
        boxMap.put(newBox, digits);

        WORLD.add(newBox);

        Shape s;

        // FRAME 1
        for(int i = 0; i < size - 1; i++){
            s = WORLD.get(i);
            if(s instanceof Box b){
                if(insertAtPos == i){
                    double initialX = (double) (startLeft) * 1.5f;
                    double finalX = (double) (startLeft + i) * 1.5f;
                    int steps = 60;
                    double delta = (finalX - initialX) / (double) steps;
                    for (int step = 0; step <= steps; step++) {
                        newBox.center.x = initialX + delta * step;
                        M_Z += 0.025;
                        drawImage(HEIGHT, WIDTH);
                    }

                }
            }
        }

        // FRAME 2
        Shape chains = WORLD.get(size - 1);
        for (int i = 0; i < size - 1; i++) {
            s = WORLD.get(i);
            if (s instanceof Box b && insertAtPos >= i) {
                double initialX = (double) b.center.x;
                double finalX = initialX - 1.5f;
                int steps = 60;
                double changer = 1.5f / (double) steps;
                double changer2 = - ( 0.75f / (double) steps);
                double delta = (finalX - initialX) / (double) steps;
                    for (int step = 0; step <= steps; step++) {
                        b.center.x = initialX + delta * step;
                        if(chains instanceof Box o){
                            o.width += changer;
                            o.center.x += changer2;
                        }
                        M_X -= 0.025;
                        drawImage(HEIGHT, WIDTH);
                    }
            }
        }



        // FRAME 3
        double steps = 60;
        double divider = 1.25f / (double) steps;
        for (int step = 0; step < steps; step++) {
            yaw += 0.25;
            M_X += 0.02;
            newBox.center.y -= divider;
            drawImage(HEIGHT, WIDTH);
        }


        String framesDir = "/Users/aryanbatra/Desktop/DSA IN 3D/src/resources/frames";
        String outputVideoFolder = "/Users/aryanbatra/Desktop/DSA IN 3D/";
        Video.generateVideo(framesDir, outputVideoFolder);
    }

    public static String getUniqueOutputFilename(String baseName, String extension) {
        int index = 1;
        String filename = baseName + "." + extension;
        while (Files.exists(Paths.get(filename))) {
            filename = baseName + "_" + index++ + "." + extension;
        }
        return filename;
    }


    private void start() {

        boxMap = new HashMap<Box, Integer[]>();

        are = new int[]{102, 62, 25, 2, 1};

        float space = 1.5f;
        startLeft = -are.length / 2.0f;

        for (int i = 0; i < are.length; i++) {

            float x = (startLeft + i) * space;

            Box box = new Box(new Point(x, 0, 0), 1, 1, 0.1,
                    new Color(0.4f, 0.7f, 1.0f), Material.CHROME, 0, are[i]);

            WORLD.add(box);

            String str = String.valueOf(are[i]);
            Integer[] digits = new Integer[str.length()];
            for (int j = 0; j < str.length(); j++) {
                digits[j] = Character.getNumericValue(str.charAt(j));
            }

            boxMap.put(box, digits);
        }

        float totalWidth = (are.length) * space - 0.5f;

        Point barCenter = new Point(0 - 0.75f, 0, 0.01f); // Z-forward

        Box chain = new Box(barCenter, totalWidth, 0.5f, 0.1f,
                new Color(0.85f, 0.85f, 0.85f), Material.CHROME, 0, 0);

        WORLD.add(chain); // add chain at end

        double totalX = 0, totalY = 0, totalZ = 0;

        for (Shape s : WORLD) {
            if (s instanceof Box box) {
                Point c = box.getCenter();
                totalX += c.x;
                totalY += c.y;
                totalZ += c.z;
            }
        }

        M_X = totalX / are.length;
        M_Y = totalY / are.length + 0.25;
        M_Z = totalZ / are.length;

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
        radius = worldLength * 0.5;

        drawImage(HEIGHT, WIDTH);
    }


    private void setup() {
        SCREEN = Toolkit.getDefaultToolkit().getScreenSize(); HEIGHT = SCREEN.height; WIDTH = SCREEN.width; setSize(WIDTH,HEIGHT);
        V_YAXIS = 2; V_XAXIS = 1; V_ZAXIS = -3; M_X = 0; M_Y = 0; M_Z = 0; latestX = 0; latestY = 0;
        radius = new Point(V_XAXIS - M_X, V_YAXIS - M_Y, V_ZAXIS - M_Z).length(); yaw = 0.25; pitch = 0;
        Clock = Executors.newSingleThreadScheduledExecutor();
        try { ENVIRONMENT = ImageIO.read(getClass().getResourceAsStream("/resources/lake.jpg") ); } catch(IOException e){ e.printStackTrace(); }
        setBackground(java.awt.Color.BLACK); setLocationRelativeTo(null);
        setUndecorated(true); setResizable(false); setVisible(true); setLayout(null);
        WORLD = new ArrayList<Shape>();
        frameCount = 0;
    }

    private void listen() {
        addKeyListener(new KeyAdapter( ) {
            public void keyPressed(KeyEvent e) {
                double  moveSpeed = 0.5,
                        yawRad = Math.toRadians(yaw), pitchRad = Math.toRadians(pitch);
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
                drawImage(HEIGHT, WIDTH);
//                drawImageSchedule();
            }
        });
        addMouseListener(new MouseAdapter( ) {
            public void mousePressed(MouseEvent e)  {
                latestX = e.getX(); latestY = e.getY();
            }
            public void mouseReleased(MouseEvent e) {
//                drawImageSchedule();
            }
        });
        addMouseMotionListener(new MouseAdapter( ) {
            public void mouseDragged(MouseEvent e) {
                double beingDragged_X = e.getX(), beingDragged_Y = e.getY();
                if (Math.abs(beingDragged_X - latestX) < 20 && Math.abs(beingDragged_Y - latestY) < 20) return;
                yaw += - ( beingDragged_X - latestX ) * 0.1; pitch -= ( beingDragged_Y - latestY ) * 0.1;
                drawImage(HEIGHT, WIDTH);
                latestX = beingDragged_X; latestY = beingDragged_Y;
            }
        });
        addMouseWheelListener(new MouseAdapter( ) {
            public void mouseWheelMoved(MouseWheelEvent e) {
                double offset_Z = e.getPreciseWheelRotation() / 10; radius += offset_Z * 0.25;
                System.out.println(radius );
                drawImage(HEIGHT, WIDTH);
//                drawImageSchedule();
            }
        });
    }
//
//    void drawImageSchedule(){
//        if(Schedule != null && Schedule.isDone() == false) Schedule.cancel(false);
//        Schedule = Clock.schedule(() -> drawImage(HEIGHT, WIDTH),200,TimeUnit.MILLISECONDS);
//    }

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
                        Color pixelColor = new Color(Main.rayColor(WORLD, ray, ENVIRONMENT, boxMap, 50));
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

        try {
            File dir = new File("src/resources/frames");
            if (!dir.exists()) {
                dir.mkdirs(); // Create the folders if they don't exist
            }

            String fileName = String.format("src/resources/frames/frame_%06d.jpg", frameCount++);
            File outputFile = new File(fileName);

            ImageIO.write(IMG, "jpg", outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }


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
