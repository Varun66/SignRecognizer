package ca.rationalcoding.sign_recognizer.experts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import ca.rationalcoding.sign_recognizer.channels.Channel;
import ca.rationalcoding.sign_recognizer.dataset.DataReader;
import ca.rationalcoding.sign_recognizer.dataset.Sign;

class ShapeExpert extends Expert {
    private static final int FLOOD_COUNT = 10;
    private static final int FLOOD_THRESHOLD = 40;
    private static final float PERCENT_SEGMENT_AREA = (float) 0.1;
    private static final String TAG = "ShapeExpert:";

    public ShapeExpert(Channel recv, Channel send) {
        super(recv, send);
    }

    Sign processImage(Bitmap imageData) {
        Bitmap dsBitmap = downscaleBitmap(imageData, (float) 0.1);
        boolean[][] maxSegment = getMaxSegmentByFloodFill(dsBitmap);
        int vCount = countVertexes(maxSegment);

        int minDiff = Integer.MAX_VALUE;
        Sign minSign = DataReader.getSigns().get(0);
        for (Sign sign : DataReader.getSigns()) {
            int diff = Math.abs(sign.shape - vCount);
            if (diff < minDiff) {
                minDiff = diff;
                minSign = sign;
            }
        }
        return minSign;
    }

    private boolean isNearTrackingPoint (int x, int y, int scanRadius, ArrayList<int[]> trackingPoints) {
        for (int[] t : trackingPoints) {
            if (Math.abs(x - t[0]) < scanRadius || Math.abs(y - t[1]) < scanRadius) {
                return true;
            }
        }
        return false;
    }

    private boolean isCollidingPoint (int x, int y, int scanRadius, boolean[][] pixels, ArrayList<int[]> trackingPoints) {
        for (int x2 = x - scanRadius; x2 < x + scanRadius; x2++) {
            if (x2 < 0 ||  x2 >= pixels.length) continue;
            for (int y2 = y - scanRadius; y2 < y + scanRadius; y2++) {
                if (y2 < 0 || y2 >= pixels[0].length) continue;

                if (pixels[x2][y2]) {
                    return true;
                }
            }
        }

        return false;
    }

    private int countVertexes(boolean[][] pixels) {
        // Detect vertexes in segment using shrinking circle method

        ArrayList<int[]> trackingPoints = new ArrayList<int[]>();
        int vertexCount = 0;

        int r = (int) Math.sqrt(Math.pow(pixels.length, 2) + Math.pow(pixels[0].length, 2));
        int radiusStep = 10;
        int scanRadius = 10;
        double angleStep = 2 * Math.PI / 100;

        while (r > 0) {
            r -= radiusStep;
            for (double angle = 0; angle < 2 * Math.PI; angle += angleStep) {
                int x = (int) Math.cos(angle) * r;
                int y = (int) Math.sin(angle) * r;

                if (isCollidingPoint(x, y, scanRadius, pixels, trackingPoints)) {
                    if (!isNearTrackingPoint(x, y, scanRadius, trackingPoints)) {
                        vertexCount++;
                    }
                    trackingPoints.add(new int[]{x, y});
                }
            }
        }
        return vertexCount;
    }

    private Bitmap downscaleBitmap(Bitmap bm, float scale) {
        return Bitmap.createScaledBitmap(bm, bm.getWidth() / 10, bm.getHeight() / 10, false);
    }

    private int colorDifference(int[] color1, int[] color2) {
        int diff = 0;
        for (int i = 0; i < 3; i++) {
            diff += Math.abs(color1[i] - color2[i]);
        }
        return diff;
    }

    private boolean[][] getMaxSegmentByFloodFill(Bitmap imageData) {
        boolean[][][] segments = new boolean[FLOOD_COUNT][imageData.getWidth()][imageData.getHeight()];

        int maxSegment = 0;
        int maxCount = -1;

        for (int i = 0; i < FLOOD_COUNT; i++) {
            int count = 0;

            int[] startPixel = new int[]{
                    (int) Math.floor(imageData.getWidth() * Math.random()),
                    (int) Math.floor(imageData.getHeight() * Math.random())
            };
            LinkedBlockingQueue<int[]> edgePixels = new LinkedBlockingQueue<int[]>();
            edgePixels.add(startPixel);

            while (!edgePixels.isEmpty()) {
                int[] pixel = edgePixels.remove();
                int[] color = getRGBPixel(imageData, pixel[0], pixel[1]);

                segments[i][pixel[0]][pixel[1]] = true;

                for (int x2 = pixel[0] - 1; x2 <= pixel[0] + 1; x2++) {
                    if (x2 < 0 || x2 >= imageData.getWidth()) continue;;
                    for (int y2 = pixel[1] - 1; y2 <= pixel[1] + 1; y2++) {
                         if (y2 < 0  || y2 >= imageData.getHeight()) continue;
                        if (segments[i][x2][y2]) continue;

                        int[] color2 = getRGBPixel(imageData, x2, y2);
                        if (colorDifference(color, color2) < 20) {
                            edgePixels.add(new int[]{x2, y2});
                            count++;
                        }
                    }
                }
            }

            if (count > maxCount) {
                maxSegment = i;
                maxCount = count;
            }
        }

        return segments[maxSegment];
    }
}
