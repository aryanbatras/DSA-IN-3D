package Utility;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Variable {

    private static final LinkedHashMap<String, String> variableMap = new LinkedHashMap<>();
    private static final LinkedList<String> recentKeys = new LinkedList<>();

    private static final int MAX_VISIBLE_VARS = 3;

    private static final java.awt.Color BACKGROUND_COLOR = new java.awt.Color(30, 30, 40, 220);
    private static final java.awt.Color TEXT_COLOR = new java.awt.Color(240, 240, 250);
    private static final Font BASE_FONT = new Font("Monospaced", Font.PLAIN, 18);

    public static void update(String varName, Object value) {
        if (varName == null) return;
        String ref = extractReferenceName();
        String fullName = (ref != null) ? ref + "." + varName : varName;

        variableMap.put(fullName, String.valueOf(value));
        updateRecentKeys(fullName);
    }

    public static void update(String varName, Object index, Object value) {
        if (varName == null) return;
        String ref = extractReferenceName();
        String fullName = (ref != null)
                ? ref + "." + varName + "[" + index + "]"
                : varName + "[" + index + "]";

        variableMap.put(fullName, String.valueOf(value));
        updateRecentKeys(fullName);
    }

    private static void updateRecentKeys(String key) {
        recentKeys.remove(key);
        recentKeys.addLast(key);
        if (recentKeys.size() > MAX_VISIBLE_VARS) {
            recentKeys.removeFirst();
        }
    }

    public static void render(Graphics2D g2d, double scale) {
        if (variableMap.isEmpty() || recentKeys.isEmpty()) return;

        int fontSize = Math.max(10, (int) (BASE_FONT.getSize() * scale));
        Font scaledFont = BASE_FONT.deriveFont((float) fontSize);
        g2d.setFont(scaledFont);
        FontMetrics metrics = g2d.getFontMetrics();

        int lineHeight = metrics.getHeight() + 2;
        int padding = (int) (10 * scale);
        int width = (int) (360 * scale);
        int height = Math.min(MAX_VISIBLE_VARS, recentKeys.size()) * lineHeight + 2 * padding;

        int x = padding;
        int y = padding;

        // Draw background box
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(x, y, width, height, 16, 16);

        // Draw text lines
        g2d.setColor(TEXT_COLOR);
        int currentY = y + padding + metrics.getAscent();

        for (int i = recentKeys.size() - Math.min(MAX_VISIBLE_VARS, recentKeys.size()); i < recentKeys.size(); i++) {
            String key = recentKeys.get(i);
            String line = key + " = " + variableMap.get(key);
            g2d.drawString(line, x + padding, currentY);
            currentY += lineHeight;
        }
    }

    private static String extractReferenceName() {
        try {
            for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
                if ("Main.java".equals(frame.getFileName())) {
                    int lineNumber = frame.getLineNumber();
                    String path = System.getProperty("user.dir") + "/src/Main.java";
                    List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(path));
                    if (lineNumber - 1 < lines.size()) {
                        String codeLine = lines.get(lineNumber - 1).strip();
                        int dotIndex = codeLine.indexOf(".");
                        int parenIndex = codeLine.indexOf("(");
                        if (dotIndex > 0 && parenIndex > dotIndex) {
                            return codeLine.substring(0, dotIndex).trim();
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
