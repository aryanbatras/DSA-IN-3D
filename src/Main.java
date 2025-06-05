import Collections.JArrayList;
import Shapes.Box;
import Shapes.Shape;
import Shapes.Sphere;
import Utility.*;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import Collections.*;

public class Main{

    public static void main(String[] args) throws InterruptedException, IOException {

        Render.initFFmpegPreview((int) Screen.getWidth(), (int) Screen.getHeight(), 25, "video001.mp4");

        JArrayList arr = new JArrayList();
        arr.add(10);
        arr.add(20);
//        arr.add(30);
//        arr.add(40);
//        arr.add(50);
//        arr.add(60);
//        arr.add(70);
//        arr.add(80);
//        arr.add(90);
//        arr.add(100);
        Render.closeFFmpegPreview();
    }

    public static void writeVideo(ArrayList<BufferedImage> frames, int width, int height, int fps, String outputFilename) throws IOException, InterruptedException {
        // Start the FFmpeg process
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-f", "rawvideo",
                "-pixel_format", "rgb24",
                "-video_size", width + "x" + height,
                "-framerate", String.valueOf(fps),
                "-i", "-",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-preset", "ultrafast",
                "-y", outputFilename
        );

        Process ffmpeg = pb.start();
        OutputStream ffmpegIn = new BufferedOutputStream(ffmpeg.getOutputStream());

        // Send all frames as raw RGB data
        for (BufferedImage frame : frames) {
            byte[] rawBytes = getRGBBytes(frame);
            ffmpegIn.write(rawBytes);
        }

        ffmpegIn.close();
        int exitCode = ffmpeg.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
        }
    }

    public static void streamFramesToFFmpeg(ArrayList<BufferedImage> frames, int width, int height) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-f", "rawvideo",
                "-pixel_format", "rgb24",
                "-video_size", width + "x" + height,
                "-framerate", "25",
                "-i", "-",
                "-y", // Overwrite output
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-crf", "18", // Low CRF = high quality
                "output.mp4"
        );

        Process ffmpeg = pb.start();
        OutputStream ffmpegIn = ffmpeg.getOutputStream();

        for (BufferedImage frame : frames) {
            byte[] rgb = getRGBBytes(frame);
            ffmpegIn.write(rgb);
        }

        ffmpegIn.close();
    }

    public static byte[] getRGBBytes(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] pixels = new byte[width * height * 3]; // RGB
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                pixels[index++] = (byte) ((rgb >> 16) & 0xFF); // R
                pixels[index++] = (byte) ((rgb >> 8) & 0xFF);  // G
                pixels[index++] = (byte) (rgb & 0xFF);         // B
            }
        }

        return pixels;
    }



}



