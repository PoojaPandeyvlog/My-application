package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerview; Button placeOrderButton;
    TextView totalTextView; int totalAmount = 0;
    EditText addressEditText;

    SharedPreferences preferences; SharedPreferences.Editor editor;
    CartAdapter adapter; LinearLayoutManager layoutManager;

    ArrayList <CartData> productList = new ArrayList<CartData>();
    ArrayList <String> productsInCart = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerview = findViewById(R.id.cartRecyclerView);
        totalTextView = findViewById(R.id.cartTotal);
        addressEditText = findViewById(R.id.addressEditText);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        preferences = getSharedPreferences("cart", MODE_PRIVATE);
        editor =  preferences.edit();
        placeOrderButton.setOnClickListener(view -> placeOrder());

        updateCart();

//        productList.clear();
//        productsInCart.clear();
//
//        Map<String, ?> map = preferences.getAll();
//
//        if (!map.isEmpty())
//        {
//            for (Map.Entry<String, ?> entry : map.entrySet())
//            {
//                productsInCart.add(entry.getValue().toString());
//            }
//        }

        totalTextView.setText("₹ " + String.valueOf(totalAmount) + ".00");

        adapter = new CartAdapter();
        layoutManager = new LinearLayoutManager(this);
    }

    public void updateCart()
    {
        productList.clear();
        productsInCart.clear();

        Map<String, ?> map = preferences.getAll();

        if (!map.isEmpty())
        {
            Log.d("Checking", "Preferences are not empty");
            for (Map.Entry<String, ?> entry : map.entrySet())
            {
                productsInCart.add(entry.getValue().toString());
            }

            loadProducts();
        }

//        else
//        {
//            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
//            finish();
//        }
    }

    public void loadProducts()
    {
        String url = getString(R.string.host) + "load_products.php";
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
                        //Store the products
                        JSONArray idArray = json.getJSONArray("pid");
                        JSONArray nameArray = json.getJSONArray("pname");
                        JSONArray priceArray = json.getJSONArray("pprice");

                        for (int i=0; i<idArray.length(); i++)
                        {
                            String currentId = String.valueOf(idArray.getInt(i));
                            if (productsInCart.contains(currentId))
                            {
                                CartData product = new CartData();

                                product.setpId(idArray.getInt(i));
                                product.setpName(nameArray.getString(i));
                                product.setpPrice(priceArray.getInt(i));
                                product.setpAmount(priceArray.getInt(i));
                                product.setpQty(1);
                                product.setProductIcon();

                                productList.add(product);
                                totalAmount += product.getpAmount();
                            }
                        }

                        totalTextView.setText("₹ " + String.valueOf(totalAmount) + ".00");
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(layoutManager);

                    }
                    else
                    {
                        Toast.makeText(CartActivity.this, "No products found", Toast.LENGTH_LONG).show();
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
                Toast.makeText(CartActivity.this, "Error fetching products", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);

    }

    public void placeOrder()
    {
        String address = addressEditText.getText().toString();

        if (productList.size() == 0)
        {
            Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
            placeOrderButton.setEnabled(false);
        }
        else if (address.length() == 0)
        {
            addressEditText.setError("Enter a delivery address here");
        }
        else
        {
            SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
            String phone = pref.getString("phone", "");
            //OTP Authentication
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + phone, 60, TimeUnit.SECONDS, this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            String products = "";

                            for (int i = 0; i < productList.size(); i++)
                            {
                                products += productList.get(i).getpName() + ", ";
                            }

                            products =  products.substring(0, products.length()-2);

                            Log.d("Checking", "Code: " + s);

                            placeOrderButton.setEnabled(true);
                            Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                            intent.putExtra("total", totalAmount);
                            intent.putExtra("code", s);
                            intent.putExtra("address", address);
                            intent.putExtra("items", products);
                            startActivity(intent);
                        }
                    });

        }
    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>
    {
        @NonNull
        @Override
        public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(CartActivity.this);
            View view = inflater.inflate(R.layout.custom_cart, parent, false);
            return new CartAdapter.CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position)
        {
            CartData productData = productList.get(position);
            String productAmount = "₹ " + String.valueOf(productData.getpAmount()) + ".00";

            holder.cartIcon.setImageResource(productData.getProductIcon());
            holder.cartName.setText(productData.getpName());
            holder.cartQty.setText(String.valueOf(productData.getpQty()));
            holder.cartPrice.setText(productAmount);

            holder.addItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    totalAmount -= productData.getpAmount();
                    productData.setpQty(productData.getpQty() + 1);
                    productData.setpAmount(productData.getpPrice() * productData.getpQty());
                    holder.cartPrice.setText(String.valueOf(productData.getpAmount()));
                    totalAmount += productData.getpAmount();
                    totalTextView.setText("₹ " + String.valueOf(totalAmount) + ".00");
                    adapter.notifyDataSetChanged();
                }
            });

            holder.removeItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (productData.getpQty() == 1)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Remove item");
                        builder.setMessage("Are you sure you want to remove this product from the cart?");

                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (productList.size() == 1)
                                {
                                    Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                                    editor.clear().apply();
                                    finish();
                                }
                                else
                                {
                                    totalAmount -= productData.getpAmount();
                                    productsInCart.remove(String.valueOf(position));
                                    productList.remove(position);
                                    editor.remove(String.valueOf(position)).apply();
                                    //updateCart();
                                    totalTextView.setText("₹ " + String.valueOf(totalAmount) + ".00");
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    else
                    {
                        totalAmount -= productData.getpAmount();
                        productData.setpQty(productData.getpQty() - 1);
                        productData.setpAmount(productData.getpPrice() * productData.getpQty());
                        totalAmount += productData.getpAmount();
                        holder.cartQty.setText(String.valueOf(productData.getpQty()));
                        holder.cartPrice.setText("₹ " + String.valueOf(productData.getpAmount()) + ".00");
                    }

                    totalTextView.setText("₹ " + String.valueOf(totalAmount) + ".00");
                    adapter.notifyDataSetChanged();

                }
            });
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class CartViewHolder extends RecyclerView.ViewHolder
        {
            ImageView cartIcon, addItemButton, removeItemButton;
            TextView cartName, cartQty, cartPrice;

            public CartViewHolder(@NonNull View itemView)
            {
                super(itemView);

                cartIcon = itemView.findViewById(R.id.cartIcon);
                cartName = itemView.findViewById(R.id.cartTitle);
                cartQty = itemView.findViewById(R.id.cartQty);
                cartPrice = itemView.findViewById(R.id.cartPrice);
                addItemButton = itemView.findViewById(R.id.addItemButton);
                removeItemButton = itemView.findViewById(R.id.removeItemButton);

            }
        }

    }
}