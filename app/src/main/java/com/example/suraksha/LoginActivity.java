package com.example.suraksha;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener {

    EditText phoneEditText, passwordEditText;
    Button loginButton; TextView moveToSignUp;
    String userPhone = "", userPassword = "";
    CardView sos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        phoneEditText = findViewById(R.id.loginPhone);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        moveToSignUp = findViewById(R.id.moveToSignUp);
        sos = findViewById(R.id.sosButton);

        loginButton.setOnClickListener(loginButtonListener);
        sos.setOnClickListener(sosListener);

        moveToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    View.OnClickListener sosListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Calling the police");
            builder.setMessage("Are you sure you want to call the police?");

            builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Call the police
                    if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) ==
                            PackageManager.PERMISSION_GRANTED)
                    {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + "100"));
                        startActivity(callIntent);
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }
                }
            });

            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

    View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            userPhone = phoneEditText.getText().toString();
            userPassword = passwordEditText.getText().toString();

            //Checking validations
            if (userPhone.length() != 10)
            {
                phoneEditText.setError("Invalid phone number");
            }
            else if (userPassword.length() < 6)
            {
                passwordEditText.setError("Password should be minimum 6 characters");
            }
            else
            {
                sendData(userPhone, userPassword);
            }

        }
    };

    //Function to send data to the PHP scripts and listen to any responses
    public void sendData(final String uPhone, final String uPassword)
    {
        String url = getString(R.string.host) + "login.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, this, this)
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("phone", uPhone);
                hashMap.put("password", uPassword);
                return hashMap;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response)
    {
        try
        {
            JSONObject json = new JSONObject(String.valueOf(response));
            String res = json.getString("res");

            if (res.equals("OK"))
            {
                String name = json.getString("name");
                String email = json.getString("email");

                SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("phone", userPhone);
                editor.putString("password", userPassword);
                editor.putString("name", name);
                editor.putString("email", email);
                editor.apply();

                Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(this, HomeActivity.class);
                startActivity(in);
                finish();
            }
            else
            {
                Toast.makeText(this, "You are not a registered user", Toast.LENGTH_LONG).show();
            }

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}