package ca.rationalcoding.sign_recognizer.experts;
import android.graphics.Bitmap;

import java.util.ArrayList;

import ca.rationalcoding.sign_recognizer.channels.Channel;
import ca.rationalcoding.sign_recognizer.dataset.Sign;

public abstract class Expert extends Thread {
    private static final int NUMBER_OF_EXPERTS = 3;

    private Channel<Bitmap> forumRecv;
    private Channel<Sign> forumSend;

    public Expert (Channel<Bitmap> recv, Channel<Sign> send) {
        this.forumRecv = recv;
        this.forumSend = send;
    }
    public void run() {
        while (true) {
            Bitmap imageData = forumRecv.receive();
            Sign result = processImage(imageData);
            forumSend.send(result);
        }
    }
    abstract Sign processImage(Bitmap imageData);

    public static int getExpertCount () {
        return NUMBER_OF_EXPERTS;
    }

    public static ArrayList<Expert> getExperts(ArrayList<Channel<Bitmap>> sendChannels, Channel receiveChannel) {
        ArrayList<Expert> experts = new ArrayList<>();
        experts.add(new ColorExpert(sendChannels.get(0), receiveChannel));
        experts.add(new ShapeExpert(sendChannels.get(1), receiveChannel));
        experts.add(new TextExpert(sendChannels.get(2), receiveChannel));
        return experts;
    }

    static int[] getRGBPixel (Bitmap bm, int x, int y) {
        int p = bm.getPixel(x, y);

        int[] color = new int[3];

        color[0] = (p >> 16) & 0xff; // Decode RGB_444 colorspace
        color[1] = (p >> 8) & 0xff;
        color[2] = p & 0xff;

        return color;
    }
}
