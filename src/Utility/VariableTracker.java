package Utility;

import java.awt.*;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class VariableTracker {

    private static final LinkedHashMap<String, String> variableMap = new LinkedHashMap<>();

    private static final Font FONT = new Font("Monospaced", Font.PLAIN, Math.min(18, (int)(10 / Screen.getScale())));
    private static final Color BG = new Color(30, 30, 40, 220);
    private static final Color TEXT = new Color(240, 240, 250);
    private static final int PADDING = 10;

    public static void update(String varName, Object value) {
        if (varName == null) return;
        String refName = extractReferenceName();
        if (refName != null) {
                varName = refName + "." + varName;
        }
        variableMap.put(varName, String.valueOf(value));
    }

    public static void update(String varName, Object index, Object value) {
        if (varName == null) return;
        String refName = extractReferenceName();
        if (refName != null) {
            varName = refName + "." + varName + "[" + String.valueOf(index) + "]";
        }
        variableMap.put(varName, String.valueOf(value));
    }


    public static void render(Graphics2D g2d) {
        if (variableMap.isEmpty()) return;

        g2d.setFont(FONT);
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight() + 2;

        int width = 220;
        int height = variableMap.size() * lineHeight + 2 * PADDING;

        int x = PADDING;
        int y = PADDING;

        g2d.setColor(BG);
        g2d.fillRoundRect(x, y, width, height, 16, 16);

        g2d.setColor(TEXT);
        int currentY = y + PADDING + fm.getAscent();
        for (Map.Entry<String, String> entry : variableMap.entrySet()) {
            String line = entry.getKey() + " = " + entry.getValue();
            g2d.drawString(line, x + PADDING, currentY);
            currentY += lineHeight;
        }
    }

    private static String extractReferenceName() {
        try {
            for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
                if ("Main.java".equals(el.getFileName())) {
                    int line = el.getLineNumber();
                    String path = System.getProperty("user.dir") + "/src/Main.java";
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(path));
                    if (line - 1 < lines.size()) {
                        String codeLine = lines.get(line - 1).strip();
                        int dotIndex = codeLine.indexOf(".");
                        int parenIndex = codeLine.indexOf("(");
                        if (dotIndex > 0 && parenIndex > dotIndex) {
                            return codeLine.substring(0, dotIndex).trim();
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return null;
    }

}
