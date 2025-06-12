package Utility;

import java.awt.*;

public class Screen {
    private static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    private static double SCALE = 1;

    public static int getWidth() {
        return (int)(WIDTH * SCALE) & ~1;   // Ensures even number
    }

    public static int getHeight() {
       return (int)(HEIGHT * SCALE) & ~1;  // Ensures even number
    }

  public static double getScale(){
        return SCALE;
  }

}






