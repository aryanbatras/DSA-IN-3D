package Utility;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Code {

    public static List<String> sourceLines;
    public static boolean enabled = false;

    private static double scale;
    private static final int PADDING = 4;
    private static final Font CODE_FONT = new Font("Monospaced", Font.PLAIN, Math.min(18, (int)(10 / Screen.getScale())));

    private static final Color BG = new Color(20, 20, 30, 230);
    private static final Color TEXT = new Color(230, 230, 240);
    private static final Color CURRENT_LINE_BG = new Color(80, 130, 255, 80);
    private static final Color LINE_NO = new Color(120, 120, 150);

    private static final AtomicInteger currentLine = new AtomicInteger(-1);

    static {
        try {
            String mainFile = findMainFile();
            if (mainFile != null) {
                Utility.Code.sourceLines = Files.readAllLines(Paths.get(mainFile));
                Utility.Code.enabled = true;
            }
        } catch (IOException e) {
            System.err.println("CodeOverlay: Auto-load failed: " + e.getMessage());
        }
    }

    private static List<String> mainSource = null;
    private static final java.util.Map<String, List<String>> enumSources = new java.util.HashMap<>();

    private static String findFileNamed(String filename) {
        Path root = Paths.get(System.getProperty("user.dir"));
        try (Stream<Path> files = Files.walk(root, 5)) {
            return files
                    .filter(p -> p.getFileName().toString().equals(filename))
                    .findFirst()
                    .map(Path::toString)
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    public static String findMainFile() {
        return findFileNamed("Main.java");
    }

    static {
        try {
            String mainFile = findMainFile();
            if (mainFile != null && Files.exists(Paths.get(mainFile))) {
                mainSource = Files.readAllLines(Paths.get(mainFile));
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load Main.java — " + e.getMessage());
        }

        String[] enumFiles = {"Array.java", "Stack.java", "Queue.java", "LinkedList.java", "Graph.java", "Trees.java", "AVLTrees.java", "MinHeap.java", "MaxHeap.java"};
        for (String filename : enumFiles) {
            try {
                String foundPath = findFileNamed(filename);
                if (foundPath != null) {
                    String className = "Algorithms." + filename.replace(".java", "");
                    enumSources.put(className, Files.readAllLines(Paths.get(foundPath)));
                } else {

                }
            } catch (IOException e) { }
        }
    }




    public static void markCurrentLine() {
        if (!enabled) return;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement el = stackTrace[i];
            String className = el.getClassName();
            String baseClassName = className.split("\\$")[0];

            if (baseClassName.startsWith("Algorithms.") && enumSources.containsKey(baseClassName)) {
                sourceLines = enumSources.get(baseClassName);
                currentLine.set(el.getLineNumber() - 1);
                return;
            }
        }

        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement el = stackTrace[i];
            if ("Main.java".equals(el.getFileName()) && mainSource != null) {
                sourceLines = mainSource;
                currentLine.set(el.getLineNumber() - 1);
                return;
            }
        }
    }




    private static String conditionResult = " ";

    public static void setConditionResult(String result) {
        conditionResult = result;
    }

    public static String getConditionResult() {
        return conditionResult;
    }





    private static int getMaxVisibleLines() {
        double scale = Code.scale;
        int maxLines;
        if (scale <= 0.25) {
            maxLines = 3;
        } else if (scale <= 0.5) {
            maxLines = 5;
        } else if (scale <= 0.75) {
            maxLines = 10;
        } else {
            maxLines = 15;
        }
        return maxLines;
    }

    private static List<String> wrapText(FontMetrics fm, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String test = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (fm.stringWidth(test) <= maxWidth) {
                currentLine = new StringBuilder(test);
            } else {
                if (!currentLine.isEmpty()) lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (!currentLine.isEmpty()) lines.add(currentLine.toString());
        return lines;
    }

    public static void render(Graphics2D g2d, int canvasWidth, double scale)
    {
        canvasWidth = (int) (canvasWidth * scale);
        Code.scale = scale;

        if (!enabled || sourceLines == null || currentLine.get() < 0) return;

        int lineIndex = currentLine.get();
        if (lineIndex >= sourceLines.size()) return;

        g2d.setFont(CODE_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();

        int overlayWidth = (int) Math.max(50, Math.min(600, (double) canvasWidth / 2));
        int contentWidth = overlayWidth - 2 * PADDING - 30;
        int availableHeight = Math.max(100, Screen.getHeight() / 4);
        int maxVisibleLines = getMaxVisibleLines();

        List<Integer> visibleLineIndices = new ArrayList<>();
        List<List<String>> wrappedLines = new ArrayList<>();
        int totalLines = 0;
        int startLine = Math.max(0, lineIndex - maxVisibleLines / 2);

        for (int i = startLine; i < sourceLines.size() && visibleLineIndices.size() < getMaxVisibleLines(); i++) {
            List<String> wrapped = wrapText(fm, sourceLines.get(i).strip(), contentWidth);
            visibleLineIndices.add(i);
            wrappedLines.add(wrapped);
            totalLines += wrapped.size();
        }

        int overlayHeight = Math.min((totalLines * lineHeight) + 2 * PADDING, availableHeight);
        int x = Math.max(PADDING, canvasWidth - overlayWidth - PADDING);
        int y = PADDING;
        int currentY = y + PADDING + fm.getAscent();

        g2d.setColor(BG);
        g2d.fillRect(x, y, overlayWidth, overlayHeight);

        for (int i = 0; i < visibleLineIndices.size(); i++) {
            int lineNum = visibleLineIndices.get(i);
            List<String> wrapped = wrappedLines.get(i);
            boolean isCurrentLine = (lineNum == lineIndex);

            for (String wrappedLine : wrapped) {
                if (wrappedLine.equals(wrapped.get(0))) {
                    g2d.setColor(LINE_NO);
                    g2d.drawString(String.valueOf(lineNum + 1), x + 6, currentY);
                }

                if (isCurrentLine) {
                    g2d.setColor(CURRENT_LINE_BG);
                    g2d.fillRect(x + 30, currentY - fm.getAscent(), contentWidth + 10, lineHeight);
                }

                g2d.setColor(TEXT);

                g2d.drawString(wrappedLine, x + 30, currentY);

                if (!getConditionResult().isBlank()) {
                    int footerHeight = lineHeight + 4;
                    int footerY = y + overlayHeight - footerHeight + lineHeight + 5;

                    g2d.setColor(new Color(30, 30, 40, 25));
                    g2d.fillRect(x, footerY, overlayWidth, footerHeight);

                    Color resultColor = getConditionResult().contains("true")
                            ? new Color(120, 255, 120, 45)
                            : new Color(255, 120, 120, 45);


                    g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                    g2d.setColor(resultColor);

                    String label = "Condition Result: " + getConditionResult();
                    g2d.drawString(label, x + 16, footerY + fm.getAscent());
                }


                currentY += lineHeight;
            }
        }
    }
}
