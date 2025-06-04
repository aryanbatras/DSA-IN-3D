package Utility;

import java.awt.*;

public class Screen {
    private static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public static double getWidth(){
        return WIDTH;
    }
    public static double getHeight(){
        return HEIGHT;
    }
}
