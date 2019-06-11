package com.example.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SongActivity extends AppCompatActivity {

    Button next_button,previous_button,pause_button;
    TextView songLabel;
    SeekBar songSeekBar;
    TextView time;

    String song_name;

    static MediaPlayer myMediaPlayer;
    int possition;
    static int  currentIndex;

    ArrayList<File> mySongs;
    static Thread updateSeekBar;


    Notification notification;
    static  NotificationManagerCompat notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        next_button = (Button) findViewById(R.id.next);
        previous_button = (Button) findViewById(R.id.previous);
        pause_button = (Button) findViewById(R.id.pause);
        songLabel = (TextView) findViewById(R.id.songLabel);
        songSeekBar = (SeekBar) findViewById(R.id.seekBar);
        time = (TextView) findViewById(R.id.time);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        updateSeekBar = new Thread() {

            @Override
            public void run(){

                int totaalDuration = myMediaPlayer.getDuration();
                int currentPossition = 0;


                while (currentPossition<totaalDuration){
                    try{
                        sleep(500);
                        currentPossition = myMediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPossition);

                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        };


        Intent i = getIntent();
        Bundle bundle = i.getExtras();


        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        song_name = mySongs.get(possition).getName().toString().replace(".mp3","");

        String songName = i.getStringExtra("songname");

        songLabel.setText(songName);
        songLabel.setSelected(true);

        possition = bundle.getInt("possition",0);

        if(myMediaPlayer!=null){
            if(possition!=currentIndex){
                myMediaPlayer.stop();
                myMediaPlayer.release();

                Uri u  = Uri.parse(mySongs.get(possition).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                myMediaPlayer.start();
                songSeekBar.setMax(myMediaPlayer.getDuration());


            }else {
                songSeekBar.setMax(myMediaPlayer.getDuration());

            }

                if(!myMediaPlayer.isPlaying()){


                    pause_button.setBackgroundResource(R.drawable.play_icon);


            }
        }
        else{

            Uri u  = Uri.parse(mySongs.get(possition).toString());
            myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

            myMediaPlayer.start();
            songSeekBar.setMax(myMediaPlayer.getDuration());


        }


        updateSeekBar.start();
        addNotification(songName);


        currentIndex=possition;


        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                set();


            }
        });






        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                songSeekBar.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying()) {

                    pause_button.setBackgroundResource(R.drawable.play_icon);
                    myMediaPlayer.pause();
                }
                else {
                    pause_button.setBackgroundResource(R.drawable.pause_icon);
                    myMediaPlayer.start();
                }

            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myMediaPlayer.stop();
                myMediaPlayer.release();
                currentIndex = ((currentIndex+1)%mySongs.size());



                Uri u = Uri.parse(mySongs.get(currentIndex).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        set();
                    }
                });

                song_name =mySongs.get(currentIndex).getName().toString().replace(".mp3","");

                songLabel.setText(song_name);
                addNotification(song_name);

                myMediaPlayer.start();
                songSeekBar.setMax(myMediaPlayer.getDuration());
            }

        });

        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myMediaPlayer.stop();
                myMediaPlayer.release();
                currentIndex = ((currentIndex-1)<0)? (mySongs.size()-1):(currentIndex-1);


                Uri u = Uri.parse(mySongs.get(currentIndex).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        set();
                    }
                });

                song_name =mySongs.get(currentIndex).getName().toString().replace(".mp3","");

                songLabel.setText(song_name);
                addNotification(song_name);

                myMediaPlayer.start();
                songSeekBar.setMax(myMediaPlayer.getDuration());
            }


        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void set(){

        myMediaPlayer.stop();
        myMediaPlayer.release();
        currentIndex = ((currentIndex+1)%mySongs.size());



        Uri u = Uri.parse(mySongs.get(currentIndex).toString());

        myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                set();
            }
        });

        song_name =mySongs.get(currentIndex).getName().toString().replace(".mp3","");

        songLabel.setText(song_name);
        addNotification(song_name);


        myMediaPlayer.start();
        songSeekBar.setMax(myMediaPlayer.getDuration());


    }

    private void addNotification(String name){


        notificationManager = NotificationManagerCompat.from(this);

        Intent activityIntent = new Intent(this,MainActivity.class);
        PendingIntent  contentIntent  = PendingIntent.getActivity(this,
                0,activityIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.note)
                .setContentTitle("MusicPlayer")
                .setContentText(name)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(1,notification);


    }


}
