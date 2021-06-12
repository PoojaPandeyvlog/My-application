package com.example.suraksha;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SirenActivity extends AppCompatActivity {

    ImageView megaphone; TextView timer;
    Button stopButton, playButton; CountDownTimer countdown;
    long timeLeft = 4000; Animation rotate;
    MediaPlayer sirenSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siren);
        megaphone = findViewById(R.id.megaphoneIcon);
        timer = findViewById(R.id.timerTextView);
        stopButton = findViewById(R.id.sirenButton);
        playButton = findViewById(R.id.playButton);

        sirenSound = MediaPlayer.create(this, R.raw.siren);

        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        megaphone.setAnimation(rotate);

        //Play siren again when the button is pressed
        playButton.setOnClickListener(view -> playSiren());

        //Stop siren when the button is pressed
        stopButton.setOnClickListener(view -> stopSiren());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        countdown = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                String timeToUpdate = String.valueOf(l/1000);
                timer.setText(timeToUpdate);
            }

            @Override
            public void onFinish() {
                playSiren();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sirenSound.stop();
    }

    public void playSiren()
    {
        sirenSound.start();
        rotate.start();
    }

    public void stopSiren()
    {
        countdown.cancel();
        rotate.cancel();
        sirenSound.pause();
    }
}