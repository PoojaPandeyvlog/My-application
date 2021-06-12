package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavView;

    HomeFragment homeFragment = new HomeFragment();
    ShopFragment shopFragment = new ShopFragment();
    TipsFragment tipsFragment = new TipsFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);
        fragmentManager.beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            int id = item.getItemId();

            if (id == R.id.miHome)
            {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
            }
            else if (id == R.id.miShop)
            {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, shopFragment).commit();
            }
            else if (id == R.id.miTips)
            {
                fragmentManager.beginTransaction().replace(R.id.frameLayout, tipsFragment).commit();
            }

            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.miOrders)
        {
            Intent intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.miLogout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logging out");
            builder.setMessage("Are you sure you wish to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
                    dialog.setMessage("Logging out");
                    dialog.show();

                    SharedPreferences settings = getSharedPreferences("user", Context.MODE_PRIVATE);
                    settings.edit().clear().apply();

                    Intent in = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return true;
    }
}