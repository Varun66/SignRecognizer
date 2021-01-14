package ca.rationalcoding.sign_recognizer.channels;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.Random;

import android.util.Log;

public class Channel<MessageType> {
    private BlockingQueue<MessageType> queue = new LinkedBlockingQueue<>();
    private Object lock = new Object(){};

    private static final String TAG = "Channels";
    Random rand = new Random();
    private int id = rand.nextInt();

    public synchronized void send (MessageType message) {
        try {
            queue.put(message);
            Log.v(TAG, this.id + " sendRequest");
            this.notify();
            Log.v(TAG, this.id + " sendRelease");
        } catch (InterruptedException e) {}
    }

    public synchronized MessageType receive () {
        try {
            if (queue.size() == 0) {
                Log.v(TAG, this.id + " recvRequest");
                this.wait();
                Log.v(TAG, this.id + " recvRelease");
            }
            return queue.take();
        } catch (InterruptedException e) {}
        return null;
    }
}
