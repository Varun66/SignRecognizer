package ca.rationalcoding.sign_recognizer.experts;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import ca.rationalcoding.sign_recognizer.channels.Channel;
import ca.rationalcoding.sign_recognizer.dataset.DataReader;
import ca.rationalcoding.sign_recognizer.dataset.Sign;
import okhttp3.OkHttpClient;
import okhttp3.*;

class TextExpert extends Expert {
    public TextExpert(Channel recv, Channel send) {
        super(recv, send);
    }

    private static final String TAG = "TextExpert:";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    Sign processImage(Bitmap imageData) {
        String base64 = imageToBase64(imageData);

        OkHttpClient client = new OkHttpClient();

        String responseString = null;

        String url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDvLY59lSlkNrGPeYj0hQW5bhFSMOInW3M";
        String bodyString = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"image\": {\n" +
                "        \"content\":\"" + base64 + "\"" +
                "      },\n" +
                "      \"features\": [\n" +
                "        {\n" +
                "          \"type\": \"TEXT_DETECTION\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            responseString = response.body().string();

            //Log.v(TAG, responseString);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }

        String text = null;
        try {
            JSONObject myObj = new JSONObject(responseString.trim());
            text = myObj
                    .getJSONArray("responses")
                    .getJSONObject(0)
                    .getJSONArray("textAnnotations")
                    .getJSONObject(0)
                    .getString("description")
                    .toLowerCase()
                    .trim();

            Log.v(TAG, text);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }

        for (Sign sign : DataReader.getSigns()) {
            if (sign.text.equals(text)) {
                return sign;
            }
        }
        return DataReader.getSigns().get((int) Math.floor(Math.random() * DataReader.getSigns().size()));
    }

    private String imageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return base64String;
    }
}
