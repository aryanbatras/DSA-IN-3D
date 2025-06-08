package Utility;

import java.awt.*;
import java.awt.Color;
import java.util.LinkedHashMap;

public class Variable {

    private static final LinkedHashMap<String, String> variableMap = new LinkedHashMap<>();

    private static final Font FONT = new Font("Monospaced", Font.PLAIN, Math.min(18, (int)(10 / Screen.getScale())));
    private static final Color BG = new Color(30, 30, 40, 220);
    private static final Color TEXT = new Color(240, 240, 250);
    private static final int PADDING = 10;

    private static final int MAX_VISIBLE_VARS = 3;
    private static final java.util.LinkedList<String> recentKeys = new java.util.LinkedList<>();


    public static void update(String varName, Object value) {
        if (varName == null) return;
        String refName = extractReferenceName();
        if (refName != null) {
                varName = refName + "." + varName;
        }
        variableMap.put(varName, String.valueOf(value));

        recentKeys.remove(varName);
        recentKeys.addLast(varName);
        if (recentKeys.size() > MAX_VISIBLE_VARS) {
            recentKeys.removeFirst();
        }
    }

    public static void update(String varName, Object index, Object value) {
        if (varName == null) return;
        String refName = extractReferenceName();
        if (refName != null) {
            varName = refName + "." + varName + "[" + String.valueOf(index) + "]";
        }
        variableMap.put(varName, String.valueOf(value));

        recentKeys.remove(varName);
        recentKeys.addLast(varName);
        if (recentKeys.size() > MAX_VISIBLE_VARS) {
            recentKeys.removeFirst();
        }
    }

    public static void render(Graphics2D g2d, double scale) {
        if (variableMap.isEmpty() || recentKeys.isEmpty()) return;

        int baseFontSize = 18;
        int fontSize = Math.max(10, (int) (baseFontSize * scale));
        Font font = new Font("Monospaced", Font.PLAIN, fontSize);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight() + 2;

        int baseWidth = 360;
        int width = (int)(baseWidth * scale);
        int padding = (int)(10 * scale);
        int visibleLines = Math.min(MAX_VISIBLE_VARS, recentKeys.size());
        int height = visibleLines * lineHeight + 2 * padding;

        int x = padding;
        int y = padding;

        g2d.setColor(BG);
        g2d.fillRoundRect(x, y, width, height, 16, 16);

        g2d.setColor(TEXT);
        int currentY = y + padding + fm.getAscent();

        for (int i = 0; i < visibleLines; i++) {
            String key = recentKeys.get(recentKeys.size() - visibleLines + i);
            String line = key + " = " + variableMap.get(key);
            g2d.drawString(line, x + padding, currentY);
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
