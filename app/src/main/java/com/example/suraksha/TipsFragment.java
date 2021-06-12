package com.example.suraksha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class TipsFragment extends Fragment {

    int[] videosList = {R.raw.one, R.raw.two, R.raw.three, R.raw.four, R.raw.five};
    String[] videoTitle = {"Back Throw", "Groin Kick", "Elbow to Chin", "Elbow Attack", "Knee Hit"};

    GridView gridView;

    public TipsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);
        gridView = view.findViewById(R.id.tipsGridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int videoClicked = videosList[i];
                Intent intent = new Intent(getContext(), TipsActivity.class);
                intent.putExtra("video", videoClicked);
                startActivity(intent);
            }
        });

        VideosAdapter adapter = new VideosAdapter();
        gridView.setAdapter(adapter);

        return view;
    }

    class VideosAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return videosList.length;
        }

        @Override
        public Object getItem(int i) {
            return videosList[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null)
            {
                view = getLayoutInflater().inflate(R.layout.custom_videos, viewGroup, false);
            }

            ImageView imageView = view.findViewById(R.id.videoThumbnailView);
            TextView textView = view.findViewById(R.id.videoTitleView);

            Uri uri = Uri.parse("android.resource://com.example.suraksha/" + videosList[i]);
            Glide.with(TipsFragment.this).load(uri).into(imageView);

            textView.setText(videoTitle[i]);

            return view;
        }
    }

}