package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {

    ArrayList <String> contactsList = new ArrayList<String>();
    ArrayList <String> contactNumber = new ArrayList<String>();
    ListView listView; String userLocation = "";
    SharedPreferences preferences;

    public static final String TAG = "Test";
    LocationManager mLocationManager; LocationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        listView = findViewById(R.id.contactsListView);

        preferences = getSharedPreferences("friends", MODE_PRIVATE);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mListener = (Location location) ->
        {
            userLocation = location.toString();
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        else
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    mListener);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    3);
        }
        else
        {
            loadFriends();
        }

        ContactsAdapter adapter = new ContactsAdapter();
        listView.setAdapter(adapter);

    }

    public void loadFriends()
    {
        Map<String, ?> map = preferences.getAll();

        if (!map.isEmpty())
        {
            for (Map.Entry<String, ?> entry : map.entrySet())
            {
                Log.d(TAG, "Preferences " + entry.getValue().toString());
                contactNumber.add(entry.getKey());
                contactsList.add(entry.getValue().toString());
            }
        }

        getContacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 3)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                loadFriends();
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, mListener);
            }
        }

    }

    public void getContacts()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null);

            while (cursor.moveToNext())
            {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (!contactNumber.contains(number))
                {
                    Log.d(TAG, "Contacts " + name);
                    contactsList.add(name);
                    contactNumber.add(number);
                }
            }
        }
    }

    class ContactsAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return contactsList.size();
        }

        @Override
        public Object getItem(int i) {
            return contactsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null)
            {
                view = getLayoutInflater().inflate(R.layout.custom_contact, viewGroup, false);
            }

            TextView contactName = view.findViewById(R.id.contactName);
            TextView contactNum = view.findViewById(R.id.contactNumber);
            Button sendLocation = view.findViewById(R.id.sendLocationButton);
            ImageView starImageView = view.findViewById(R.id.contactStar);

           contactName.setText(contactsList.get(i));
           contactNum.setText(contactNumber.get(i));

           //Make star invisible if not in emergency contacts
            String check = preferences.getString(contactNumber.get(i), "no");
            Log.d("Test", contactsList.get(i) + " " + check);

            if (check.equals("no"))
            {
                starImageView.setVisibility(View.INVISIBLE);
            }
            else
            {
                starImageView.setVisibility(View.VISIBLE);
            }

            sendLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ContactsActivity.this, LocationActivity.class);
                    intent.putExtra("number", contactNumber.get(i));
                    intent.putExtra("location", userLocation);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}