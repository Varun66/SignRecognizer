package ca.rationalcoding.sign_recognizer.experts;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;

import ca.rationalcoding.sign_recognizer.channels.Channel;
import ca.rationalcoding.sign_recognizer.dataset.DataReader;
import ca.rationalcoding.sign_recognizer.dataset.Sign;

class ColorExpert extends Expert {
    private static final String TAG = "ColorExpert";

    public ColorExpert (Channel recv, Channel send) {
        super(recv, send);
    }
    Sign processImage (Bitmap imageData) {
        // Average color
        int[] color = new int[]{0, 0, 0};

        for (int x = 0; x < imageData.getWidth(); x++) {
            for (int y = 0; y < imageData.getHeight(); y++) {
                int[] p = getRGBPixel(imageData, x, y);

                for (int i=0; i<3; i++) {
                    color[i] += p[i];
                }
            }
        }

        int numPixels = imageData.getWidth() * imageData.getHeight();
        for (int i=0; i<3; i++) {
            color[i] = color[i] / numPixels;
        }

        Sign result = null;
        int minDiff = Integer.MAX_VALUE;
        for (Sign sign : DataReader.getSigns()) {
            for (int i=0; i < 3; i++) {
                int diff = Math.abs(color[i] - sign.color[i]);
                if (diff < minDiff) {
                    minDiff = diff;
                    result = sign;
                }
            }
        }

        if (result == null) {
            return DataReader.getSigns().get((int) Math.floor(Math.random() * DataReader.getSigns().size()));
        }

        return result;
    }
}
