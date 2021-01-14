package ca.rationalcoding.sign_recognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ca.rationalcoding.sign_recognizer.channels.Channel;
import ca.rationalcoding.sign_recognizer.dataset.DataReader;
import ca.rationalcoding.sign_recognizer.dataset.Sign;

public class MainActivity extends AppCompatActivity {
    private Channel<Bitmap> forumSend;
    private Channel<Sign> forumRecv;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String TAG = "Channels";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataReader.mContext = this;

        forumSend = new Channel<Bitmap>();
        forumRecv = new Channel<Sign>();;

        Forum forum = new Forum(forumSend, forumRecv);
        forum.start();

        dispatchTakePictureIntent();
    }

    private void sendImageData (Bitmap imageData) {
        forumSend.send(imageData);

        // nariman - open loading screen
        //Intent intentLoading = new Intent(MainActivity.this, LoadingScreen.class);
        //MainActivity.this.startActivity(intentLoading);

        Sign result = forumRecv.receive();

        // nariman - open results page once loading done
        Intent intentOutput = new Intent(MainActivity.this, OutputScreen.class);
        intentOutput.putExtra("expert1_name", result.name);
        MainActivity.this.startActivity(intentOutput);

        Log.v(TAG, "Processing complete!");

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = this.findViewById(R.id.imageView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap rawBitmap = (Bitmap) extras.get("data");
            Bitmap imageData = rawBitmap.copy(Bitmap.Config.ARGB_4444, false); // Change to RGB color space
            imageView.setImageBitmap(imageData);
            sendImageData(imageData);
        }
    }


    // nariman - home screen, user can choose either camera or gallery

    public void onCameraOpen(View v){
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        MainActivity.this.startActivity(i);
    }

    public void onGalleryOpen(View v){

    }

}
