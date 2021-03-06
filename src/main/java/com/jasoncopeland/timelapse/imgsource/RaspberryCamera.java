package com.jasoncopeland.timelapse.imgsource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Jason on 3/13/2016.
 */
public class RaspberryCamera implements IImageSource {

    protected final String raspistillPath = "/opt/vc/bin/raspistill";
    protected final int imgQuality = 100;
    protected int imgWidth = 1920;
    protected int imgHeight = 1080;
    protected String imageType = "jpg";
    protected String imgCaptureParams = null;

    public RaspberryCamera(int width, int height, String imgCaptureParams) {
        imgWidth = width;
        imgHeight = height;
        this.imgCaptureParams = imgCaptureParams;
    }

    public BufferedImage getCurrentImage() {
        long startTime = System.currentTimeMillis();
        String fileName = "/tmp/tmlpse-" + System.currentTimeMillis() + ".jpg";
        try {
            StringBuilder sb = new StringBuilder(raspistillPath);

            sb.append(" -n -bm -t 1 "); // no prview or burst
            sb.append(imgCaptureParams);
            if (imgWidth > 0) {
                sb.append(" -w " + imgWidth);
            }
            if (imgHeight > 0) {
                sb.append(" -h " + imgHeight);
            }
            sb.append(" -q " + imgQuality);
            sb.append(" -e " + imageType); // jpg, png, bmp, gif
            sb.append(" -o " + fileName); // destination file path

            //System.out.println("Executing: " + sb.toString());
            System.out.print("Starting camera capture...");
            Process process = Runtime.getRuntime().exec(sb.toString());
            int exitCode = 0;
            if (process != null && (exitCode = process.waitFor()) == 0) {
                File targetFile = new File(fileName);
                BufferedImage buffImg = ImageIO.read(targetFile);
                targetFile.delete();

                return buffImg;
            } else {
                String error = readInputStreamAsString(process.getErrorStream());
                String output = readInputStreamAsString(process.getErrorStream());
                if (error.length() > 0 || output.length() > 0) {
                    System.out.println("\ncapture cmd output: \"" + output + "\" error: \"" + error + "\"");
                }
                System.out.println("Failed to create process exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.print(" in " + (System.currentTimeMillis() - startTime) + " ms");
        }
        return null;
    }

    public static String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            byte b = (byte)result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }
}
