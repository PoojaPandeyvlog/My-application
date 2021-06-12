package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CloseFriendsActivity extends AppCompatActivity {

    ArrayList <String> contactName = new ArrayList<String>();
    ArrayList<String> contactNumber = new ArrayList<String>();

    SharedPreferences preferences; SharedPreferences.Editor editor;
    RecyclerView recyclerview; CloseFriendsAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_friends);
        recyclerview = findViewById(R.id.closeFriendsList);

        preferences = getSharedPreferences("friends", MODE_PRIVATE);
        editor = preferences.edit();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    3);
        }
        else
        {
            getContacts();
        }

        adapter = new CloseFriendsAdapter();
        recyclerview.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 3)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
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

                contactName.add(name);
                contactNumber.add(number);
            }

            if (contactName.size() == 0)
            {
                Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    class CloseFriendsAdapter extends RecyclerView.Adapter<CloseFriendsAdapter.MyViewHolder>
    {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(CloseFriendsActivity.this);
            View view = inflater.inflate(R.layout.custom_friend, parent, false);
            return new CloseFriendsActivity.CloseFriendsAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.nameTextView.setText(contactName.get(position));
            holder.numberTextView.setText(contactNumber.get(position));

            //Check if the contact is already in the emergency contacts
            String check = preferences.getString(contactNumber.get(position), "empty");
            Log.d("Checking", String.valueOf(position) + ". " + check);

            if (check.equals(contactName.get(position)))
            {
                holder.starImageView.setImageResource(R.drawable.ic_selected_star);
            }
            else
            {
                holder.starImageView.setImageResource(R.drawable.ic_unselected_star);
            }

            holder.starImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (check.equals(contactName.get(position)))
                    {
                        editor.remove(contactNumber.get(position)).apply();
                        Toast.makeText(CloseFriendsActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        editor.putString(contactNumber.get(position), contactName.get(position)).apply();
                        Toast.makeText(CloseFriendsActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return contactName.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView nameTextView, numberTextView;
            ImageView starImageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView= itemView.findViewById(R.id.friendName);
                numberTextView= itemView.findViewById(R.id.friendNumber);
                starImageView = itemView.findViewById(R.id.friendStar);
            }
        }
    }

//    class CloseFriendsAdapter extends BaseAdapter
//    {
//        @Override
//        public int getCount() {
//            return contactName.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return contactName.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//
//            if (view == null)
//            {
//                view = getLayoutInflater().inflate(R.layout.custom_friend, viewGroup, false);
//            }
//
//            TextView nameTextView= view.findViewById(R.id.friendName);
//            TextView numberTextView= view.findViewById(R.id.friendNumber);
//            ImageView starImageView = view.findViewById(R.id.friendStar);
//
//            nameTextView.setText(contactName.get(i));
//            numberTextView.setText(contactNumber.get(i));
//
//            //Check if contact is already in favourites
//            String check = preferences.getString(contactNumber.get(i), "none");
//            Log.d("Checking", check);
//
//            if (check.equals("none"))
//            {
//                starImageView.setImageResource(R.drawable.ic_selected_star);
//            }
//
//            starImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (check.equals("none"))
//                    {
//                        editor.putString(contactNumber.get(i), contactName.get(i));
//                        starImageView.setImageResource(R.drawable.ic_selected_star);
//                        Toast.makeText(CloseFriendsActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show();
//                    }
//
//                    else if (check.equals(contactName.get(i)))
//                    {
//                        editor.remove(contactNumber.get(i)).apply();
//                        starImageView.setImageResource(R.drawable.ic_unselected_star);
//                        Toast.makeText(CloseFriendsActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//
//            return view;
//        }
//    }
}