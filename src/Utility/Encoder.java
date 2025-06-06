package Utility;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Encoder implements AutoCloseable {

    private static final String DEFAULT_OUTPUT_DIR = "output";
    private static final String DEFAULT_VIDEO_PREFIX = "video";
    private static final int DEFAULT_FPS = 25;

    private final Process ffmpegProcess;
    private final OutputStream ffmpegInput;
    private final int width;
    private final int height;

    private final int fps;

    private static final AtomicBoolean encoderInitialized = new AtomicBoolean(false);
    private static final Object INIT_LOCK = new Object();
    
    public static void initializeEncoder() {
        if (!encoderInitialized.get()) {
            synchronized (INIT_LOCK) {
                if (!encoderInitialized.get()) {
                    try {
                        setupPreviewEncoder();
                        registerShutdownHook();
                        encoderInitialized.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to initialize video encoder", e);
                    }
                }
            }
        }
    }
    
    private static void setupPreviewEncoder() throws IOException {
        int width = (int) Screen.getWidth();
        int height = (int) Screen.getHeight();
        Render.PREVIEW_FFMPEG = createPreviewEncoder(width, height, DEFAULT_FPS);
        System.out.println("Video will be saved to: " + DEFAULT_OUTPUT_DIR);
    }
    
    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closePreview();
                System.out.println("\nVideo saved to: " + new File(DEFAULT_OUTPUT_DIR).getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));
    }
    public static Encoder createPreviewEncoder(int width, int height, int fps) throws IOException {
        validateDimensions(width, height);
        validateFps(fps);
        
        String outputPath = generateUniqueOutputPath();
        return new Encoder(width, height, fps, outputPath);
    }
    
    private static void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive values");
        }
    }
    
    private static void validateFps(int fps) {
        if (fps <= 0) {
            throw new IllegalArgumentException("FPS must be a positive value");
        }
    }
    
    private static String generateUniqueOutputPath() {
        File outputDir = ensureOutputDirectory();
        return findAvailableFilename(outputDir).getAbsolutePath();
    }
    
    private static File ensureOutputDirectory() {
        File outputDir = new File(DEFAULT_OUTPUT_DIR);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDir.getAbsolutePath());
        }
        return outputDir;
    }
    
    private static File findAvailableFilename(File directory) {
        int index = 1;
        while (true) {
            File outputFile = new File(directory, 
                String.format("%s_%03d.mp4", DEFAULT_VIDEO_PREFIX, index++));
            if (!outputFile.exists()) {
                return outputFile;
            }
        }
    }

    public static void closePreview() {
        if (Render.PREVIEW_FFMPEG != null) {
            try {
                Render.PREVIEW_FFMPEG.close();
            } catch (Exception e) {
                System.err.println("Error closing preview encoder: " + e.getMessage());
            } finally {
                Render.PREVIEW_FFMPEG = null;
            }
        }
    }

    private Encoder(int width, int height, int fps, String outputFile) throws IOException {
        this.width = width % 2 == 0 ? width : width - 1;
        this.height = height % 2 == 0 ? height : height - 1;

        this.fps = fps;
        ensureParentDirectoryExists(outputFile);
        this.ffmpegProcess = createFFmpegProcess(outputFile);
        this.ffmpegInput = new BufferedOutputStream(ffmpegProcess.getOutputStream());
    }
    
    private void ensureParentDirectoryExists(String filePath) {
        File outputFile = new File(filePath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new RuntimeException("Failed to create parent directory: " + parentDir.getAbsolutePath());
        }
    }




    private Process createFFmpegProcess(String outputFile) throws IOException {
//        ProcessBuilder pb = new ProcessBuilder(
//            "ffmpeg",
//            "-f", "rawvideo",
//            "-pixel_format", "rgb24",
//            "-video_size", String.format("%dx%d", width, height),
//            "-framerate", String.valueOf(fps),
//            "-i", "-",
//            "-c:v", "libx264",
//            "-preset", "ultrafast",
//            "-pix_fmt", "yuv420p",
//            "-y",
//            outputFile
//        );

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-f", "rawvideo",
                "-pixel_format", "rgb24",
                "-video_size", String.format("%dx%d", width, height),
                "-framerate", String.valueOf(fps),
                "-i", "-",
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-pix_fmt", "yuv420p",
                "-y",
                outputFile
        );


        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        return pb.start();
    }

    public void writeFrame(BufferedImage image) throws IOException {
        if (ffmpegInput == null) {
            throw new IllegalStateException("Encoder is closed");
        }
        ffmpegInput.write(convertToRGBBytes(image));
    }


    public void close() throws IOException {
        try {
            closeQuietly(ffmpegInput);
            if (ffmpegProcess != null) {
                int exitCode = ffmpegProcess.waitFor();
                if (exitCode != 0) {
                    throw new IOException("FFmpeg exited with error code " + exitCode);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Encoder close was interrupted", e);
        }
    }
    
    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }

    private byte[] convertToRGBBytes(BufferedImage image) {
        validateImageDimensions(image);
        
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] rgbData = new byte[width * height * 3];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                rgbData[index++] = (byte) ((pixel >> 16) & 0xFF); // R
                rgbData[index++] = (byte) ((pixel >> 8) & 0xFF);  // G
                rgbData[index++] = (byte) (pixel & 0xFF);         // B
            }
        }
        return rgbData;
    }
    
    private void validateImageDimensions(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (image.getWidth() != width || image.getHeight() != height) {
            throw new IllegalArgumentException(String.format(
                "Image dimensions (%dx%d) do not match video dimensions (%dx%d)",
                image.getWidth(), image.getHeight(), width, height));
        }
    }
}
