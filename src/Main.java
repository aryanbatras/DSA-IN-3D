import Collections.JArrayList;
import Shapes.Box;
import Shapes.Shape;
import Shapes.Sphere;
import Utility.Color;
import Utility.Material;
import Utility.Point;
import Utility.Ray;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import Collections.*;

public class Main{
    public static void main(String[] args) throws InterruptedException, IOException {
//        new Window( );
        JArrayList arr = new JArrayList();
        arr.add(10);
        arr.add(20);
        arr.add(30);
        arr.add(40);
        arr.add(50);
        arr.remove(2);
        arr.generateVideo();
    }



}



