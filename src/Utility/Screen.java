package Utility;

import java.awt.*;
import Rendering.Quality;

public class Screen {
    private static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    // flip width and height and you get the mobile screen

    private static double SCALE = 0.5;  // or whatever scale you prefer

    public static int getWidth() {
//        double scale = Math.max(0.25, (Math.min(1, SCALE)));
        return (int)(WIDTH * SCALE) & ~1;  // Ensures even number
    }

    public static int getHeight() {
//        double scale = Math.max(0.25, (Math.min(1, SCALE)));
       return (int)(HEIGHT * SCALE) & ~1;  // Ensures even number
    }

    public static void setQuality(Quality quality) {
        switch (quality) {
            case BEST -> SCALE = 1.0;
            case GOOD -> SCALE = 0.75;
            case BALANCE -> SCALE = 0.5;
            case FASTEST -> SCALE = 0.25;
        }
    }

  public static double getScale(){
        return SCALE;
  }

    // issues with odd even match of width and height

}






