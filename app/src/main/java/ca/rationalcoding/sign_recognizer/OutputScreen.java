package ca.rationalcoding.sign_recognizer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OutputScreen extends AppCompatActivity {

    ImageButton imgMain;
    ImageButton alt1;
    ImageButton alt2;
    TextView signDesc;

    String expert1;
    String expert2;
    String expert3;

    TextView mainName;
    TextView alt1Name;
    TextView alt2Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_screen);


        // receive the strings from the main activity

        Bundle bundle = getIntent().getExtras();
        expert1 = bundle.getString("expert1_name");
        expert2 = bundle.getString("expert2_name");
        expert3 = bundle.getString("expert3_name");

        imgMain = (ImageButton)findViewById(R.id.mainOption);
        //signDesc = (TextView)findViewById(R.id.signDesc);

        mainName = (TextView)findViewById(R.id.sign1Name);
        //alt1Name = (TextView)findViewById(R.id.sign2Name);
        //alt2Name = (TextView)findViewById(R.id.sign3Name);

        //alt1 = (ImageButton)findViewById(R.id.alternative1);
        //alt2 = (ImageButton) findViewById(R.id.alternative2);

        mainName.setText(expert1);
        //alt1Name.setText(expert2);
        //alt2Name.setText(expert3);

        decideImage(mainName, imgMain);
       // decideImage(alt1Name, alt1);
        //decideImage(alt2Name, alt2);
    }


    public void returnCorrect(View v){
        Intent i = new Intent(OutputScreen.this, MainActivity.class);
        OutputScreen.this.startActivity(i);
    }

    public void returnIncorrect(View v){
        Intent i = new Intent(OutputScreen.this, MainActivity.class);
        OutputScreen.this.startActivity(i);
    }

/*
    public void openAlternative1(View v){

        //change image spots

        Drawable temp = imgMain.getBackground();
        imgMain.setBackground(alt1.getBackground());
        alt1.setBackground(temp);

        //change text
        String tempName = mainName.getText().toString();
        mainName.setText(alt1Name.getText().toString());
        alt1Name.setText(tempName);

    }
    */
    /*

    public void openAlternative2(View v) {


        Drawable temp = imgMain.getBackground();
        imgMain.setBackground(alt2.getBackground());
        alt2.setBackground(temp);

        //change text
        String tempName = mainName.getText().toString();
        mainName.setText(alt2Name.getText().toString());
        alt2Name.setText(tempName);
    }

    */

    public void decideImage(TextView t, ImageButton i){

        if (t.getText().toString().equals("stop")){
            i.setBackground(getResources().getDrawable(R.drawable.stop));
        }

        if (t.getText().toString().equals("yield")){
            i.setBackground(getResources().getDrawable(R.drawable.yield));
        }

        if (t.getText().toString().equals("no-standing")){
            i.setBackground(getResources().getDrawable(R.drawable.no_standing));
        }

        if (t.getText().toString().equals("speed")){
            i.setBackground(getResources().getDrawable(R.drawable.speed));
        }

        if (t.getText().toString().equals("school-zone")){
            i.setBackground(getResources().getDrawable(R.drawable.school_zone));
        }

        if (t.getText().toString().equals("do-not-enter")){
            i.setBackground(getResources().getDrawable(R.drawable.do_not_enter));
        }

    }
}
