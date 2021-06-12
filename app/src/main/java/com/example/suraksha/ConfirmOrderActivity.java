package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfirmOrderActivity extends AppCompatActivity {

    EditText otpEditText; TextView totalTextView, itemsTextView; Button confirmButton;
    SharedPreferences preferences; SharedPreferences.Editor editor;

    String products="", address="", code=""; int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        otpEditText = findViewById(R.id.otpEditText);
        totalTextView = findViewById(R.id.totalAmount);
//        itemsTextView = findViewById(R.id.items);
        confirmButton = findViewById(R.id.confirmButton);

        preferences = getSharedPreferences("cart", MODE_PRIVATE);
        editor =  preferences.edit();

        Intent intent = getIntent();
        total = intent.getIntExtra("total", 0);
        products = intent.getStringExtra("items");
        address = intent.getStringExtra("address");
        code = intent.getStringExtra("code");

        totalTextView.setText("Total: ₹ " + String.valueOf(total) + ".00");
        //itemsTextView.setText("Items: " + products);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String userCode = otpEditText.getText().toString();

                if (code == null)
                {
                    Toast.makeText(ConfirmOrderActivity.this, "Please check your intenet", Toast.LENGTH_LONG).show();
                }
                else
                {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(code, userCode);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        placeOrder();
                                    }
                                    else
                                    {
                                        Toast.makeText(ConfirmOrderActivity.this, "Check OTP again",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                //placeOrder();
            }
        });
    }

    public void placeOrder()
    {
        String user = getSharedPreferences("user", MODE_PRIVATE).getString("phone", "");
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-y");
        String d = simpleDateFormat.format(date);

        String url = getString(R.string.host) + "place_order.php";
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
                        Toast.makeText(ConfirmOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                        editor.clear(); editor.apply();
                        Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(ConfirmOrderActivity.this, "Error placing order",
                                Toast.LENGTH_SHORT).show();
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
             Toast.makeText(ConfirmOrderActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap <String, String> map = new HashMap<String, String>();
                map.put("customer", user);
                map.put("items", products);
                map.put("amount", String.valueOf(total));
                map.put("date", d);
                map.put("address", address);
                return map;
            }
        };

        queue.add(request);

    }

}