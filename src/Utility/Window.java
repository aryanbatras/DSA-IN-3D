package Utility;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Window {
    private static JFrame frame;
    private static JLabel label;
    private static boolean initialized = false;

    public static void initializeWindow() {
        if (initialized) return;

        frame = new JFrame("Live Render Window");
        frame.setSize(Screen.getWidth(), Screen.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setAlwaysOnTop(true);
        frame.setBackground(java.awt.Color.BLACK);

        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);
        initialized = true;
    }

    public static void updateWindow(BufferedImage image){
        if (!initialized) return;
        label.setIcon(new ImageIcon(image));
    }
}
