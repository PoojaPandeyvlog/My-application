package com.example.suraksha;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlertActivity extends AppCompatActivity {

    ImageView imageView; TextView timerTextView;
    Button sendAlertButton, stopAlertButton;
    CountDownTimer countdown; String userLocation = "", currentUser = "";
    long timeLeft = 6000; Animation rotate;
    SharedPreferences preferences; Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        userLocation = getIntent().getStringExtra("location");
        imageView = findViewById(R.id.alertIcon);
        timerTextView = findViewById(R.id.alertTextView);
        sendAlertButton = findViewById(R.id.sendAlertButton);
        stopAlertButton = findViewById(R.id.stopAlertButton);

        preferences = getSharedPreferences("user", MODE_PRIVATE);
        currentUser = preferences.getString("phone", "user");

        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        imageView.setAnimation(rotate);
        rotate.start();

        stopAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countdown.cancel();
                Toast.makeText(AlertActivity.this, "Alert Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        sendAlertButton.setOnClickListener(view -> sendAlert());

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        countdown = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                String timeToUpdate = String.valueOf(l/1000);
                timerTextView.setText(timeToUpdate);
            }

            @Override
            public void onFinish() {
                sendAlert();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countdown.cancel();
    }

    public void sendAlert()
    {
        String[] locationList = userLocation.split(" ");

        String coordinates = locationList[1];
        String[] latLong = coordinates.split(",");
        double lat = Double.parseDouble(latLong[0]);
        double lon = Double.parseDouble(latLong[1]);

        String url = getString(R.string.host) + "add_location_alert.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject json = new JSONObject(String.valueOf(response));
                    String res = json.getString("res");
                    Log.d("Response", res);

                    if (res.equals("OK"))
                    {
                        Toast.makeText(AlertActivity.this, "Alert Sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(AlertActivity.this, "Alert Not Sent", Toast.LENGTH_LONG).show();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AlertActivity.this, "Error Sending Alert", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("number", currentUser);
                map.put("location", String.valueOf(lat) + ", " + String.valueOf(lon));
                map.put("date", date.toString());

                return map;
            }
        };

        queue.add(request);

    }
}