package ca.rationalcoding.sign_recognizer;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

public class LoadingScreen extends AppCompatActivity {

    AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        ImageView loading = (ImageView) findViewById(R.id.imageView);
        animation = (AnimationDrawable) loading.getDrawable();


        //animation.start();
    }


    public void startLoad(View v){
        animation.start();
    }

    public void stopLoad(View v){
        animation.stop();
    }
}
