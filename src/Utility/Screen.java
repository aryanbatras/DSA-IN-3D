package Utility;

import java.awt.*;

public class Screen {
    private static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    // flip width and height and you get the mobile screen

    private static final double SCALE = 0.38;  // or whatever scale you prefer

    public static int getWidth() {
        return (int)(WIDTH * SCALE) & ~1;  // Ensures even number
    }

    public static int getHeight() {
        return (int)(HEIGHT * SCALE) & ~1;  // Ensures even number
    }

    // issues with odd even match of width and height

}
