import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Video {

    public static File extractFFmpegBinary() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String ffmpegPath = "/";

        InputStream in = Video.class.getResourceAsStream("/resources/ffmpeg");

        if (in == null) throw new FileNotFoundException("FFmpeg binary not found in resources: " + ffmpegPath);

        File tempFile = File.createTempFile("resources/ffmpeg", null);
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }

        tempFile.setExecutable(true);

        return tempFile;
    }

    public static void deleteFrameImages(String folderPath) {
        File dir = new File(folderPath);
        File[] files = dir.listFiles((d, name) -> name.matches("frame_\\d{6}\\.png"));

        if (files != null) {
            for (File file : files) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    System.err.println("Failed to delete: " + file.getName());
                }
            }
        }
    }

    public static String getUniqueOutputFilename(String baseName, String extension) {
        int index = 1;
        String filename = baseName + "." + extension;
        while (Files.exists(Paths.get(filename))) {
            filename = baseName + "_" + index++ + "." + extension;
        }
        return filename;
    }


    public static void generateVideo(String framesDir, String outputPath) throws IOException, InterruptedException {
        File ffmpeg = extractFFmpegBinary();

        String outputFile = getUniqueOutputFilename("output", "mp4");

        String finalOutput = outputPath + outputFile;

        ProcessBuilder pb = new ProcessBuilder(
                ffmpeg.getAbsolutePath(),
                "-framerate", "30",
                "-i", "frame_%06d.png",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-y", finalOutput
        );

        pb.directory(new File(framesDir));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            Video.deleteFrameImages("/Users/aryanbatra/Desktop/DSA IN 3D/src/resources/frames");
            System.out.println("Video created successfully: " + outputFile);
        } else {
            System.err.println("FFmpeg failed with exit code: " + exitCode);
        }
    }
}
