package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    OrdersAdapter adapter; LinearLayoutManager layoutManager;

    ArrayList<OrderData> ordersList = new ArrayList<OrderData>();
    SharedPreferences preferences; String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerview = findViewById(R.id.ordersRecyclerView);

        preferences = getSharedPreferences("user", MODE_PRIVATE);
        currentUser = preferences.getString("phone", "user");

        adapter = new OrdersAdapter();
        layoutManager = new LinearLayoutManager(this);

        loadOrders();
    }

    public void loadOrders()
    {
        String url = getString(R.string.host) + "load_orders.php";
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
                        JSONArray idArray = json.getJSONArray("oid");
                        JSONArray itemsArray = json.getJSONArray("items");
                        JSONArray amountArray = json.getJSONArray("amount");
                        JSONArray dateArray = json.getJSONArray("date");
                        JSONArray addressArray = json.getJSONArray("address");

                        for (int i = 0; i < idArray.length(); i++)
                        {
                            OrderData order = new OrderData();

                            order.setoId(idArray.getInt(i));
                            order.setoItems(itemsArray.getString(i));
                            order.setoAmount(amountArray.getInt(i));
                            order.setoDate(dateArray.getString(i));
                            order.setoAddress(addressArray.getString(i));

                            ordersList.add(order);
                        }

                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(layoutManager);

                    }
                    else
                    {
                        Toast.makeText(OrdersActivity.this, "No orders placed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OrdersActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("user", currentUser);
                return map;
            }
        };

        queue.add(request);

    }

    class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>
    {

        @NonNull
        @Override
        public OrdersAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(OrdersActivity.this);
            View view = inflater.inflate(R.layout.custom_orders, parent, false);
            return new OrdersAdapter.OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrdersAdapter.OrderViewHolder holder, int position)
        {
            //Display the information
            OrderData order = ordersList.get(position);
//            String[] itemsList = order.getoItems().split(",");
//
//            Log.d("Checking", java.util.Arrays.toString(itemsList));
//
//            String displayString = ""; int i = 1;
//
//            for (int j=0; i<itemsList.length; j++)
//            {
//                displayString += String.valueOf(i) + ". " + itemsList[j] + System.getProperty("line.separator");
//                i++;
//            }
//
//            displayString = displayString.substring(0, displayString.length() - 1);
//
//            Log.d("Checking", displayString);

            holder.displayItems.setText(order.getoItems());
            holder.displayDate.setText("Placed on: " + order.getoDate());
            holder.displayAmount.setText("₹ " + String.valueOf(order.getoAmount()) + ".00");

        }

        @Override
        public int getItemCount() {
            return ordersList.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder
        {
            TextView displayItems, displayDate, displayAmount;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);

                displayItems = itemView.findViewById(R.id.orderTitle);
                displayDate = itemView.findViewById(R.id.orderDate);
                displayAmount = itemView.findViewById(R.id.orderPrice);
            }
        }

    }

}