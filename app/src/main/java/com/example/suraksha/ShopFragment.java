package com.example.suraksha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ShopFragment extends Fragment {

    ArrayList<ProductData> productList = new ArrayList<ProductData>();
    ArrayList<String> addedToCart = new ArrayList<String>();

    RecyclerView recyclerview; FloatingActionButton cartButton;
    RecyclerAdapter shopAdapter; LinearLayoutManager layoutManager;

    SharedPreferences preferences; SharedPreferences.Editor editor;

    public ShopFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        recyclerview = view.findViewById(R.id.shopListView);
        cartButton = view.findViewById(R.id.cartFab);

        preferences = getActivity().getSharedPreferences("cart", MODE_PRIVATE);
        editor = preferences.edit();

        checkCart();

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                //intent.putStringArrayListExtra("items", addedToCart);
                startActivity(intent);
            }
        });

        productList.clear();
        loadProducts();
        shopAdapter = new RecyclerAdapter();
        layoutManager = new LinearLayoutManager(getContext());

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        shopAdapter.notifyDataSetChanged();
        checkCart();
    }

    public void checkCart()
    {
        addedToCart.clear();

        Map<String, ?> map = preferences.getAll();

        if (!map.isEmpty())
        {
            for (Map.Entry<String, ?> entry : map.entrySet())
            {
                Log.d("Checking cart", entry.getValue().toString());
                addedToCart.add(entry.getValue().toString());
            }
        }

        if (addedToCart.size() == 0)
        {
            cartButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            cartButton.setVisibility(View.VISIBLE);
        }
    }

    public void loadProducts()
    {
        String url = getString(R.string.host) + "load_products.php";
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                        JSONArray infoArray = json.getJSONArray("pinfo");
                        JSONArray priceArray = json.getJSONArray("pprice");

                        for (int i=0; i<idArray.length(); i++)
                        {
                            ProductData product = new ProductData();

                            product.setpId(idArray.getInt(i));
                            product.setpName(nameArray.getString(i));
                            product.setpInfo(infoArray.getString(i));
                            product.setpPrice(priceArray.getInt(i));
                            product.setProductIcon(i);

                            String itemInCart = preferences.getString(String.valueOf(i), "no");
                            if (!itemInCart.equals("no"))
                            {
                                editor.putString(String.valueOf(idArray.getInt(i)), String.valueOf(idArray.getInt(i)));
                                //product.setItemInCart(true);
                            }

                            productList.add(product);
                        }

                        recyclerview.setAdapter(shopAdapter);
                        recyclerview.setLayoutManager(layoutManager);

                    }
                    else
                    {
                        Toast.makeText(getContext(), "No products found", Toast.LENGTH_LONG).show();
                        cartButton.setVisibility(View.INVISIBLE);
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
                Toast.makeText(getContext(), "Error fetching products", Toast.LENGTH_LONG).show();
                cartButton.setVisibility(View.INVISIBLE);
            }
        });

        queue.add(request);

    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ShopViewHolder>
    {

        @NonNull
        @Override
        public RecyclerAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.custom_shop, parent, false);
            return new RecyclerAdapter.ShopViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.ShopViewHolder holder, int position)
        {
            checkCart();

            ProductData productData = productList.get(position);
            String productPrice = "₹ " + String.valueOf(productData.getpPrice()) + ".00";

            holder.displayIcon.setImageResource(productData.getProductIcon());
            holder.displayName.setText(productData.getpName());
            holder.displayInfo.setText(productData.getpInfo());
            holder.displayPrice.setText(productPrice);

            String itemInCart = preferences.getString(String.valueOf(position), "no");

            //if (productData.itemInCart)
            if (!itemInCart.equals("no"))
            {
                holder.displayAdd.setVisibility(View.INVISIBLE);
                holder.displayRemove.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.displayAdd.setVisibility(View.VISIBLE);
                holder.displayRemove.setVisibility(View.INVISIBLE);
            }

            holder.displayAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addedToCart.add(String.valueOf(position));
                    holder.displayAdd.setVisibility(View.INVISIBLE);
                    holder.displayRemove.setVisibility(View.VISIBLE);
                    productData.setItemInCart(true);
                    editor.putString(String.valueOf(position), String.valueOf(position));
                    editor.apply();
                    shopAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                    checkCart();
                }
            });

            holder.displayRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addedToCart.remove(String.valueOf(position));
                    holder.displayAdd.setVisibility(View.VISIBLE);
                    holder.displayRemove.setVisibility(View.INVISIBLE);
                    productData.setItemInCart(false);
                    editor.remove(String.valueOf(position));
                    editor.apply();
                    shopAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Removed from cart", Toast.LENGTH_SHORT).show();
                    checkCart();
                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ShopViewHolder extends RecyclerView.ViewHolder
        {
            ImageView displayIcon;
            TextView displayName, displayInfo, displayPrice;
            ImageButton displayAdd, displayRemove;

            public ShopViewHolder(@NonNull View itemView)
            {
                super(itemView);

                displayIcon = itemView.findViewById(R.id.shopIcon);
                displayName = itemView.findViewById(R.id.shopTitle);
                displayInfo = itemView.findViewById(R.id.shopInfo);
                displayPrice = itemView.findViewById(R.id.shopPrice);
                displayAdd = itemView.findViewById(R.id.shopAdd);
                displayRemove = itemView.findViewById(R.id.shopRemove);
            }
        }

    }
}