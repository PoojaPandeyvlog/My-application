package com.example.suraksha;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    EditText nameEditText, culpritEditText, detailsEditText;
    String name = "", culprit = "", details = "", date = "";
    Button reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        nameEditText = findViewById(R.id.crimeName);
        culpritEditText = findViewById(R.id.crimeCulprit);
        detailsEditText = findViewById(R.id.crimeDetails);
        reportButton = findViewById(R.id.reportButton);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReport();
            }
        });
    }

    public void sendReport()
    {
        name = nameEditText.getText().toString();
        culprit = culpritEditText.getText().toString();
        details = detailsEditText.getText().toString();

        if (name.length() == 0)
        {
            nameEditText.setError("Please enter your name");
        }
        else if (culprit.length() == 0)
        {
            culpritEditText.setError("Please enter the culprit name");
        }
        else if (details.length() == 0)
        {
            detailsEditText.setError("Please enter the details");
        }
        else
        {
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-y");
            date = sdf.format(d);

            String url = getString(R.string.host) + "add_report.php";
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    try
                    {
                        JSONObject json = new JSONObject(String.valueOf(response));
                        String res = json.getString("res");

                        if (res.equals("OK"))
                        {
                            Toast.makeText(ReportsActivity.this, "Report received", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(ReportsActivity.this, "Error adding report", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ReportsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            })
            {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("culprit", culprit);
                    map.put("details", details);
                    map.put("date", date);
                    return map;
                }
            };
            queue.add(request);
        }
    }
}