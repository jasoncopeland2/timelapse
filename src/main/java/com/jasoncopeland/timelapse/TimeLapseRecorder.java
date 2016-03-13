package com.jasoncopeland.timelapse;

import com.jasoncopeland.timelapse.imgsource.IImageSource;
import com.jasoncopeland.timelapse.imgsource.URLImageSource;

import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;

public class TimeLapseRecorder {

    public static void main(String[] args) {
        List<ImageTimelapse> timeLapses = new ArrayList<ImageTimelapse>();
        // new file on X frame, location, name
        timeLapses.add(new ImageTimelapse(450, 1440, args[0], "1440s"));
        timeLapses.add(new ImageTimelapse(300, 300, args[0], "300s"));
//        timeLapses.add(new ImageMotion(300, 1, args[0], "motion"));

        IImageSource imageSource = new URLImageSource(args[1], args[2], args[3]);

        while (true) {
            try {
                BufferedImage img = null;
                for (ImageTimelapse tl : timeLapses) {
                    // We found one that needs a fresh image!
                    if (tl.needNewImage()) {
                        // If one hasn't been captured for this segment, capture one
                        if (img == null) {
                            img = imageSource.getCurrentImage();
                        }
                        // write it to the stream
                        tl.processImage(img);
                    }
                }
                if (img != null) {
                    img.flush();
                }
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}