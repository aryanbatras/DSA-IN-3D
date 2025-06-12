package Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.image.BufferedImage;
import java.io.*;

public class Encoder implements AutoCloseable {
    private static final String DEFAULT_OUTPUT_DIR =
            System.getProperty("output.dir", System.getProperty("user.home") + "/Desktop");

    private static final String DEFAULT_VIDEO_PREFIX = "DSA_IN_3D";
    private static final int DEFAULT_FPS = 60;
    private static Encoder INSTANCE;

    private final Process ffmpegProcess;
    private final OutputStream ffmpegInput;
    private final int width, height, fps;

    private static final AtomicBoolean shutdownHookRegistered = new AtomicBoolean(false);

    private static class NamedEncoderKey {
        private final String name;
        private final double scale;

        public NamedEncoderKey(String name, double scale) {
            this.name = name;
            this.scale = scale;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NamedEncoderKey that = (NamedEncoderKey) o;
            return Double.compare(that.scale, scale) == 0 &&
                   Objects.equals(name, that.name);
        }

        public int hashCode() {
            return Objects.hash(name, scale);
        }
    }

    private static final Map<Double, Encoder> sharedEncoders = new HashMap<>();
    private static final Map<NamedEncoderKey, Encoder> namedEncoders = new HashMap<>();

    public static Encoder getOrCreateSharedEncoder(double scale) {
        return sharedEncoders.computeIfAbsent(scale, s -> {
            System.out.println("Creating shared encoder for scale: " + s);
            return initializeEncoder(s);
        });
    }

    public static Encoder getOrCreateNamedEncoder(String name, double scale) {
        NamedEncoderKey key = new NamedEncoderKey(name, scale);
        return namedEncoders.computeIfAbsent(key, k -> {
            System.out.println(" Creating named encoder for: " + name + " @ scale: " + scale);
            return initializeEncoder(name, scale);
        });
    }


    public static Encoder initializeEncoder(double scale) {
        try {
                int width = (int) (Screen.getWidth() * scale);
                int height = (int) (Screen.getHeight() * scale);
                INSTANCE = new Encoder(width, height, DEFAULT_FPS, generateUniqueOutputPath());
                registerShutdownHook(INSTANCE);
            return INSTANCE;
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize encoder", e);
        }
    }


    public static Encoder initializeEncoder(String userVideoPrefix, double scale) {
        try {
                int width = (int) (Screen.getWidth() * scale);
                int height = (int) (Screen.getHeight() * scale);
                Encoder encoder = new Encoder(width, height, DEFAULT_FPS, generateUniqueOutputPath(userVideoPrefix));
                registerShutdownHook(encoder);
            return encoder;
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize encoder", e);
        }
    }

    private static String generateUniqueOutputPath(String userVideoPrefix) {
        File outputDir = new File(DEFAULT_OUTPUT_DIR);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDir.getAbsolutePath());
        }
        while (true) {
            File outputFile = new File(outputDir, String.format("%s.mp4", userVideoPrefix));
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return outputFile.getAbsolutePath();
            }
        }
    }

    private static String generateUniqueOutputPath() {
        File outputDir = new File(DEFAULT_OUTPUT_DIR);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create output directory: " + outputDir.getAbsolutePath());
        }

        int index = 1;
        while (true) {
            File outputFile = new File(outputDir, String.format("%s_%03d.mp4", DEFAULT_VIDEO_PREFIX, index++));
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return outputFile.getAbsolutePath();
            }
        }
    }

    private static void registerShutdownHook(Encoder encoder) {
        if (shutdownHookRegistered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    encoder.close();
                    System.out.println("\nVideo saved to: " + DEFAULT_OUTPUT_DIR);
                } catch (Exception e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                }
            }));
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

    private static boolean isFFmpegAvailable(String command) {
        try {
            Process process = new ProcessBuilder(command, "-version").start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static String getPlatformFFmpegExecutable() {
        if (isFFmpegAvailable("ffmpeg")) {
            System.out.println( "FFmpeg found in PATH" );
            return "ffmpeg";
        }

        String os = System.getProperty("os.name").toLowerCase();
        String ffmpegPath;

        if (os.contains("win")) {
            ffmpegPath = "src/Resources/ffmpeg.exe";
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
            System.out.println( "FFmpeg found in src/Resources" );
            ffmpegPath = "src/Resources/ffmpeg";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }

        File ffmpegFile = new File(ffmpegPath);
        if (!ffmpegFile.exists()) {
            System.out.println("Error: FFmpeg binary not found" );
            throw new IllegalStateException("Bundled FFmpeg binary not found at: " + ffmpegFile.getAbsolutePath());
        }

        if (!os.contains("win")) {
            try {
                System.out.println( "chmod +x src/Resources/ffmpeg" );
                Process chmod = new ProcessBuilder("chmod", "+x", ffmpegFile.getAbsolutePath()).start();
                chmod.waitFor();
            } catch (Exception e) {
                throw new RuntimeException("Failed to chmod +x for FFmpeg binary", e);
            }
        }

        System.out.println( "FFmpeg found at: " + ffmpegFile.getAbsolutePath() );
        return ffmpegFile.getAbsolutePath();
    }


    private Process createFFmpegProcess(String outputFile) throws IOException {

        String ffmpegPath = getPlatformFFmpegExecutable();
        System.out.println("Using FFmpeg executable: " + ffmpegPath);

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
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

    private byte[] convertToRGBBytes(BufferedImage image) {
        validateImageDimensions(image);
        int width = image.getWidth(), height = image.getHeight();
        byte[] rgbData = new byte[width * height * 3];
        int index = 0;

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                rgbData[index++] = (byte) ((pixel >> 16) & 0xFF);
                rgbData[index++] = (byte) ((pixel >> 8) & 0xFF);
                rgbData[index++] = (byte) (pixel & 0xFF);
            }

        return rgbData;
    }

    private void validateImageDimensions(BufferedImage image) {
        if (image == null) throw new IllegalArgumentException("Image cannot be null");
        if (image.getWidth() != width || image.getHeight() != height) {
            throw new IllegalArgumentException(String.format(
                    "Image dimensions (%dx%d) do not match video dimensions (%dx%d)",
                    image.getWidth(), image.getHeight(), width, height));
        }
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
            try { closeable.close(); }
            catch (IOException e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }
}
