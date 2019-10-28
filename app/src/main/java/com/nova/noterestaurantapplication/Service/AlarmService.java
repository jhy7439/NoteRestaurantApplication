package com.nova.noterestaurantapplication.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.nova.noterestaurantapplication.Activity.NoteActivity;
import com.nova.noterestaurantapplication.Activity.TimeSettingActivity;
import com.nova.noterestaurantapplication.Activity.WriteActivity;
import com.nova.noterestaurantapplication.R;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isRunning;
    NotificationManager manager;
    Notification notification = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Foreground 에서 실행되면 Notification 을 보여줘야 됨
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Oreo(26) 버전 이후 버전 부터는 channel 이 필요함
            String channelId = createNotificationChannel();
            //상단 알람을 클릭하면 엑티비티로 이동
            Intent notificationIntent = new Intent(this, NoteActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(getApplicationContext(), NoteActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

            //Todo : notification , setAutoCancel 적용이 안되는 문제
            notification = builder
                    .setSmallIcon(R.drawable.ic_note_black_32dp)
                    // .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentTitle("맛집 노트 알림")
                    .setContentText("맛집을 작성하려면 탭하세요.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

//            manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
//            }

            //manager.notify(1, builder.build());


             startForeground(1, notification);
        }

        String state = intent.getStringExtra("state");
        if (!this.isRunning && state.equals("on")) {
            //알람음 재생 off, 알람음 시작 상태
            this.mediaPlayer = MediaPlayer.create(this, R.raw.night);
            this.mediaPlayer.start();

            this.isRunning = true;
            Log.d("AlarmService", "Alarm Start");
        } else if (this.isRunning & state.equals("off")) {
            //알람음 재생 on, 알람음 중지 상태

            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer.release();

            this.isRunning = false;

            Log.d("AlarmService", "Alarm Stop");

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                stopForeground(true);
//            }
        }

        return START_NOT_STICKY;
    }

    //notification 오류로 channel 을 새로 만듬
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "Alarm";
        String channelName = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_NONE);

        //channel.setDescription(channelName);
        channel.setSound(null, null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelId;
    }

}
