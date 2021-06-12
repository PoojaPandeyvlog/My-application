package com.example.suraksha;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class SignUpActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener {

    EditText nameEditText, emailEditText, phoneEditText, passwordEditText;
    Button signUpButton;
    TextView loginTextView;
    String userName="", userEmail="", userPassword="", userPhone="";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        nameEditText = findViewById(R.id.signUpName);
        emailEditText = findViewById(R.id.signUpEmail);
        phoneEditText = findViewById(R.id.signUpPhone);
        passwordEditText = findViewById(R.id.signUpPassword);
        signUpButton = findViewById(R.id.signUpButton);
        loginTextView = findViewById(R.id.moveToLogin);

        signUpButton.setOnClickListener(signUpListener);

        //Open the login page when the text is clicked
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    //Called when the button is clicked
    View.OnClickListener signUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            userName = nameEditText.getText().toString();
            userEmail = emailEditText.getText().toString();
            userPassword = passwordEditText.getText().toString();
            userPhone = phoneEditText.getText().toString();

            //Validations
            if (userName.length() == 0)
            {
                nameEditText.setError("Please enter your name");
            }
            else if (userEmail.length() == 0)
            {
                emailEditText.setError("Please enter an email");
            }
            else if (userPassword.length() < 6)
            {
                passwordEditText.setError("Password should be minimum 6 characters");
            }
            else if (userPhone.length() != 10)
            {
                phoneEditText.setError("Please enter a valid phone number");
            }
            else
            {
                //Starting up the script
                sendData(userName, userEmail, userPassword, userPhone);
            }
        }
    };

    public void sendData(final String uName, final String uEmail, final String uPassword, final String uPhone)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up");
        String url = getString(R.string.host) + "signup.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, this, this)
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", uName);
                map.put("email", uEmail);
                map.put("password", uPassword);
                map.put("phone", uPhone);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        progressDialog.dismiss();
        Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Object response)
    {
        progressDialog.dismiss();
        try
        {
            JSONObject jsonObject = new JSONObject(String.valueOf(response));
            Log.d("Checking", String.valueOf(response));
            String res = jsonObject.getString("res");

            if (res.equals("OK"))
            {
                SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", userName);
                editor.putString("email", userEmail);
                editor.putString("password", userPassword);
                editor.putString("phone", userPhone);
                editor.apply();

                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Successfully signed up", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Error signing up", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
