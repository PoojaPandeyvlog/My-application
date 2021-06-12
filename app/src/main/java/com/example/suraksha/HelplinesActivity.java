package com.example.suraksha;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HelplinesActivity extends AppCompatActivity implements View.OnClickListener {

    CardView ambulance, womenHelp, ncw, emergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helplines);

        ambulance = findViewById(R.id.ambulanceCardView);
        womenHelp = findViewById(R.id.womenHelpCardView);
        ncw = findViewById(R.id.ncwCardView);
        emergency = findViewById(R.id.emergencyCardView);

        ambulance.setOnClickListener(this);
        womenHelp.setOnClickListener(this);
        ncw.setOnClickListener(this);
        emergency.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.ambulanceCardView)
        {
            //Call Ambulance
            makeCall("108", "an ambulance");
        }
        else if (id == R.id.womenHelpCardView)
        {
            //Call Women Helpline
            makeCall("1091", "Women Helpline");
        }
        else if (id == R.id.ncwCardView)
        {
            //Call NCW
            makeCall("011-26943669", "NCW");
        }
        else if (id == R.id.emergencyCardView)
        {
            //Call Emergency Helpline
            makeCall("112", "Emergency Helpline");
        }

    }

    public void makeCall(String number, String name)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calling");
        builder.setMessage("Are you sure you want to call " + name + "?");

        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Call the police
                if (ContextCompat.checkSelfPermission(HelplinesActivity.this, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED)
                {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                }
                else
                {
                    ActivityCompat.requestPermissions(HelplinesActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}