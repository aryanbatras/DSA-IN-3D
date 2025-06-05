package Utility;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FFmpegStream {
    private Process ffmpegProcess;
    private OutputStream ffmpegInput;

    public FFmpegStream(int width, int height, int fps, String outputFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-f", "rawvideo",
                "-pixel_format", "rgb24",
                "-video_size", width + "x" + height,
                "-framerate", String.valueOf(fps),
                "-i", "-",
                "-c:v", "libx264",
                "-preset", "ultrafast",
                "-pix_fmt", "yuv420p",
                "-y", outputFile
        );
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        ffmpegProcess = pb.start();
        ffmpegInput = new BufferedOutputStream(ffmpegProcess.getOutputStream());
    }

    public void writeFrame(BufferedImage image) throws IOException {
        byte[] rgb = toRGBBytes(image);
        ffmpegInput.write(rgb);
    }


    public void close() throws IOException, InterruptedException {
        ffmpegInput.close();
        int code = ffmpegProcess.waitFor();
        if (code != 0) {
            throw new RuntimeException("FFmpeg exited with error code " + code);
        }
    }

    private byte[] toRGBBytes(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        byte[] bytes = new byte[width * height * 3];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);
                bytes[index++] = (byte) ((pixel >> 16) & 0xFF); // R
                bytes[index++] = (byte) ((pixel >> 8) & 0xFF);  // G
                bytes[index++] = (byte) (pixel & 0xFF);         // B
            }
        }
        return bytes;
    }
}
