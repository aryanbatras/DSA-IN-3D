import java.io.*;
import java.util.*;

import Shapes.Box;
import Shapes.Shape;

import Utility.*;
import Utility.Color;
import Utility.Point;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.awt.image.BufferedImage;

public class Window {

    ArrayList<Shape> WORLD;
    Camera CAMERA;
    Render RENDER;

    HashMap<Box, Integer[]> boxMap;
    int[] are; float startLeft; int frameCount;

    Window() {
        setup();
        loadScene();
        animateScene();
    }

    private void setup() {

        CAMERA = new Camera();

        RENDER = new Render(
                new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB),
                new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB),
                new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB)
        );
        RENDER.setENVIRONMENT("/Resources/sunset.jpg");

        WORLD = new ArrayList<Shape>();
    }


    private void animateScene() {
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
                    int steps = 20;
                    double delta = (finalX - initialX) / (double) steps;
                    for (int step = 0; step <= steps; step++) {
                        newBox.center.x = initialX + delta * step;
//                        CAMERA.addM_Z(0.025);
                        RENDER.drawImage(CAMERA, WORLD, boxMap);
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
                int steps = 20;
                double changer = 1.5f / (double) steps;
                double changer2 = - ( 0.75f / (double) steps);
                double delta = (finalX - initialX) / (double) steps;
                    for (int step = 0; step <= steps; step++) {
                        b.center.x = initialX + delta * step;
                        if(chains instanceof Box o){
                            o.width += changer;
                            o.center.x += changer2;
                        }
//                        CAMERA.subM_X(0.025);
                        RENDER.drawImage(CAMERA, WORLD, boxMap);
                    }
            }
        }



        // FRAME 3
        double steps = 20;
        double divider = 1.25f / (double) steps;
        for (int step = 0; step < steps; step++) {
            CAMERA.setYaw(CAMERA.getYaw() + 0.25);
//            CAMERA.subM_X(0.25);
            newBox.center.y -= divider;
            RENDER.drawImage(CAMERA, WORLD, boxMap);
        }


        String framesDir = "/Users/aryanbatra/Desktop/DSA IN 3D/src/Resources/frames";
        String outputVideoFolder = "/Users/aryanbatra/Desktop/DSA IN 3D/vid";
        try {
            Video.generateVideo(framesDir, outputVideoFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUniqueOutputFilename(String baseName, String extension) {
        int index = 1;
        String filename = baseName + "." + extension;
        while (Files.exists(Paths.get(filename))) {
            filename = baseName + "_" + index++ + "." + extension;
        }
        return filename;
    }


    private void loadScene() {

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

        CAMERA.setM_X(totalX / are.length);
        CAMERA.setM_Y(totalY / are.length + 0.25);
        CAMERA.setM_Z(totalZ / are.length);

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
//        CAMERA.setRadius(worldLength * 0.5);

        RENDER.drawImage(CAMERA, WORLD, boxMap);
    }

}
