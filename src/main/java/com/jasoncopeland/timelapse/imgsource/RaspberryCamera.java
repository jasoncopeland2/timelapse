package com.jasoncopeland.timelapse.imgsource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Jason on 3/13/2016.
 */
public class RaspberryCamera implements IImageSource {

    protected final String raspistillPath = "/opt/vc/bin/raspistill";
    protected final int captureTimeoutInMS = 10000;
    protected final int imgQuality = 100;
    protected int imgWidth = 800;
    protected int imgHeight = 600;
    protected String imageType = "png";

    public BufferedImage getCurrentImage() {
        long startTime = System.currentTimeMillis();
        try {
            String fileName = "/tmp/tmlpse-" + System.currentTimeMillis() + ".png";

            StringBuilder sb = new StringBuilder(raspistillPath);

            sb.append(" -n -bm"); // no prview or burst
            sb.append(" -t " + captureTimeoutInMS); // timeout
            if (imgWidth > 0) {
                sb.append(" -w " + imgWidth);
            }
            if (imgHeight > 0) {
                sb.append(" -h " + imgHeight);
            }
            sb.append(" -q " + imgQuality);
            sb.append(" -e " + imageType); // jpg, png, bmp, gif
            sb.append(" -o " + fileName); // destination file path

            Process process = Runtime.getRuntime().exec(sb.toString());
            if (process != null && process.waitFor() == 0) {
                String error = readInputStreamAsString(process.getErrorStream());
                String output = readInputStreamAsString(process.getErrorStream());
                if (error != null || output != null) {
                    System.out.println("capture cmd output: \"" + output + "\" error: \"" + error + "\"");
                }
                return ImageIO.read(new File(fileName));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Total pi camera capture time: " + (System.currentTimeMillis() - startTime) + "ms");
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
