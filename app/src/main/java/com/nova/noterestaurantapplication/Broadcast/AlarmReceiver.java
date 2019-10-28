package com.nova.noterestaurantapplication.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.nova.noterestaurantapplication.Service.AlarmService;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent broadIntent = new Intent(context, AlarmService.class);
        broadIntent.putExtra("state", intent.getStringExtra("state"));

        // Oreo(26) 버전 이후부터는 Background 에서 실행을 금지하기 때문에 Foreground 에서 실행해야 함
        //Service 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(broadIntent);
        }
        else {
            context.startService(broadIntent);
        }

    }
}