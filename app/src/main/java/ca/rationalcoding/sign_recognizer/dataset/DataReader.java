package ca.rationalcoding.sign_recognizer.dataset;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

import ca.rationalcoding.sign_recognizer.MainActivity;

public class DataReader {
    private static ArrayList<Sign> signs = new ArrayList<Sign>();
    private static boolean isFileRead = false;
    public static Context mContext;

    private static final String TAG = "DataReader:";

    public static synchronized ArrayList<Sign> getSigns() {
        if (!DataReader.isFileRead) {
            readFile();
        }
        return signs;
    }

    private static void readFile() {
        String fileName = "signs.json";

        String content = "";
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            content = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.v(TAG, "IOException");
        }

        //Log.v(TAG, content);

        try {
            JSONObject jObj = new JSONObject(content.trim());

            JSONArray signArray = jObj.getJSONArray("signs");
            for (int i = 0; i < signArray.length(); i++) {
                JSONObject signObj = signArray.getJSONObject(i);
                JSONArray colorArr = signObj.getJSONArray("color");
                int[] colorArrInt = new int[3];
                for (int i2 = 0; i2 < colorArr.length(); i2++) {
                    colorArrInt[i2] = colorArr.getInt(i2);
                }
                signs.add(new Sign(
                        signObj.getString("name"),
                        colorArrInt,
                        signObj.getInt("shape"),
                        signObj.getString("text")
                ));
                Log.v(TAG, "Added sign " + signObj.getString("name"));
            }

        } catch (Exception e) {
            Log.v(TAG, "JSON Parse Failure: " + e.toString());
        }
    }
}
