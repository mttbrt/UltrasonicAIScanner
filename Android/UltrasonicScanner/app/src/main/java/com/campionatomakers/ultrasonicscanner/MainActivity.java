package com.campionatomakers.ultrasonicscanner;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final String URL = "http://www.mysite.com/";
    private RequestQueue queue;
    private TextView text;
    private Button update;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.tw_text);
        update = findViewById(R.id.btn_update);
        queue = Volley.newRequestQueue(this);

        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeGetRequest();
                speak("This is a bottle");
            }
        });

        // Speaker setup
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int lang = tts.setLanguage(Locale.ENGLISH);

                    if(lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED)
                        Toast.makeText(getApplicationContext(), "Speech language not supported.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "Speech initialization failed.", Toast.LENGTH_LONG).show();
            }
        });

        makePostRequest();
    }

    // Store data in server sending a post request
    private void makePostRequest() {
        final JSONObject postParams = new JSONObject();
        try {
            postParams.put("data1", "Hello");
            postParams.put("data2", "World");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URL + "push-app", postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        text.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text.setText("Error getting data from server after push request.");
            }
        }) {
            public byte[] getBody() {
                try {
                    String stringifyParams = postParams.toString();
                    return stringifyParams == null ? null : stringifyParams.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        queue.add(postRequest);
    }

    // Get data from server sending a get request
    private void makeGetRequest() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL + "get-app", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        text.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text.setText("Error getting data from server after get request.");
            }
        });
        queue.add(getRequest);
    }

    // Read a text
    private void speak(String txt) {
        tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
    }
}
