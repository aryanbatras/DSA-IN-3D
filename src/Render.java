import Shapes.Box;
import Shapes.Shape;
import Utility.Camera;
import Utility.Color;
import Utility.Ray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Render {

    int HEIGHT, WIDTH, FRAMES;
    BufferedImage RAYTRACER, BEINGRENDERED, ENVIRONMENT;

    public Render(BufferedImage RAYTRACER, BufferedImage BEINGRENDERED, BufferedImage ENVIRONMENT, int HEIGHT, int WIDTH) {
        this.BEINGRENDERED = BEINGRENDERED;
        this.ENVIRONMENT = ENVIRONMENT;
        this.RAYTRACER = RAYTRACER;
        this.HEIGHT = HEIGHT;
        this.WIDTH = WIDTH;
        this.FRAMES = 0;
    }

    public BufferedImage getRAYTRACER() {
        return RAYTRACER;
    }

    public void setRAYTRACER(BufferedImage RAYTRACER) {
        this.RAYTRACER = RAYTRACER;
    }

    public BufferedImage getBEINGRENDERED() {
        return BEINGRENDERED;
    }

    public void setBEINGRENDERED(BufferedImage BEINGRENDERED) {
        this.BEINGRENDERED = BEINGRENDERED;
    }

    public BufferedImage getENVIRONMENT() {
        return ENVIRONMENT;
    }

    public void setENVIRONMENT(BufferedImage ENVIRONMENT) {
        this.ENVIRONMENT = ENVIRONMENT;
    }

    public void setENVIRONMENT(String filePath) {
        try {
            ENVIRONMENT = ImageIO.read(
                    getClass().getResourceAsStream(filePath)
            );
        } catch(IOException e){
            System.out.println("Environment failed to load");
        }
    }




    public void drawImage(Camera CAMERA, ArrayList<Shape> WORLD, HashMap<Box,Integer[]> boxMap) {

        long start = System.nanoTime() / 1000000;

        CAMERA = CAMERA.setCameraPerspective(WIDTH, HEIGHT);

        int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService exec = Executors.newFixedThreadPool(cores);

        int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster().getDataBuffer()).getData();

        ArrayList<Future<?>> tasks = new ArrayList<>();

        for (int yStart = 0; yStart < HEIGHT; yStart += HEIGHT / cores) {

            final int y0 = yStart;

            final int y1 = (int) Math.min(yStart + (HEIGHT / cores), HEIGHT);

            Camera finalCAMERA = CAMERA;
            tasks.add(exec.submit(() -> {
                for (int y = y0; y < y1; y++) {
                    for (int x = 0; x < WIDTH; x++) {
                        double u = (double) x / WIDTH;
                        double v = (double) (HEIGHT - y) / HEIGHT;
                        Ray ray = finalCAMERA.getRay(u, v);
                        Color pixelColor = new Color(Main.rayColor(WORLD, ray, ENVIRONMENT, boxMap, 3));
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
        RAYTRACER = BEINGRENDERED;

        try {
            File dir = new File("src/resources/frames");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = String.format("src/resources/frames/frame_%06d.png", FRAMES++);
            File outputFile = new File(fileName);

            ImageIO.write(RAYTRACER, "png", outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Rendered " + ( ( System.nanoTime() / 1000000 ) - start ) + "ms");
    }


}
