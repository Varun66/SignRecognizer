package ca.rationalcoding.sign_recognizer;

import android.graphics.Bitmap;

import ca.rationalcoding.sign_recognizer.channels.Channel;
import ca.rationalcoding.sign_recognizer.dataset.Sign;
import ca.rationalcoding.sign_recognizer.experts.Expert;
import java.util.ArrayList;
import java.util.HashMap;

class Forum extends Thread {
    private ArrayList<Expert> experts;
    private ArrayList<Channel<Bitmap>> expertSendChannels = new ArrayList<>();
    private Channel<Bitmap> uiRecv;
    private Channel<Sign> uiSend;
    private Channel<Sign> expertReceiveChannel;

    public Forum (Channel<Bitmap> recv, Channel<Sign> send) { // The "send" channel is the "recv" channel for the parent
        this.uiRecv = recv;
        this.uiSend = send;
        this.expertReceiveChannel = new Channel<Sign>(); // All experts write to this shared channel
    }

    public void run () {
        createExperts();

        while (true) {
            Bitmap imageData = uiRecv.receive();
            sendToAllExperts(imageData);

            // Forum resolves conflict
            ArrayList<Sign> results = receiveFromAllExperts();
            HashMap<String, Integer> votes = new HashMap<String, Integer>();
            Sign maxSign = null;
            int maxVoteCount = -1;

            for (Sign sign: results) {
                if (!votes.containsKey(sign.name)) {
                    votes.put(sign.name, 0);
                }
                int voteCount = votes.get(sign.name) + 1;
                votes.put(sign.name, voteCount);
                if (voteCount > maxVoteCount) {
                    maxSign = sign;
                }
            }

            uiSend.send(maxSign);
        }
    }

    // Send a message to all experts
    private void sendToAllExperts (Bitmap data) {
        for (Channel<Bitmap> send : expertSendChannels) {
            send.send(data);
        }
    }

    // Receive one message from each expert and return the aggregated result
    private ArrayList<Sign> receiveFromAllExperts () {
        ArrayList<Sign> results = new ArrayList<Sign>();
        for (int i=0; i < experts.size(); i++) {
            results.add(expertReceiveChannel.receive());
        }
        return results;
    }

    private void createExperts () {
        int expertCount = Expert.getExpertCount();
        for (int i=0; i<expertCount; i++) {
            expertSendChannels.add(new Channel<Bitmap>());
        }

        experts = Expert.getExperts(expertSendChannels, expertReceiveChannel);

        for (Expert expert: experts ){
            expert.start();
        }
    }
}
