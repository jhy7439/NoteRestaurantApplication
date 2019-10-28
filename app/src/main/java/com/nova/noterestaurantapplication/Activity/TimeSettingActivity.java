package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.airbnb.lottie.LottieAnimationView;
import com.nova.noterestaurantapplication.Broadcast.AlarmReceiver;
import com.nova.noterestaurantapplication.R;
import com.nova.noterestaurantapplication.Service.AlarmService;

public class TimeSettingActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private TimePicker timePicker;

    private PendingIntent pendingIntent;

    public static Context context;
    AlarmService alarmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);

        context = this;

        this.alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        this.timePicker = findViewById(R.id.time_picker);

        ImageView ivBackPage = findViewById(R.id.back_iv);

        findViewById(R.id.service_start_btn).setOnClickListener(clickListener);
        findViewById(R.id.service_stop_btn).setOnClickListener(clickListener);

        ivBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeSettingActivity.this , MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // lottie
        LottieAnimationView timeLottie =findViewById(R.id.time_lottie);
        timeLottie.playAnimation();


        // 앞서 설정한 값으로 보여주기
        // 없으면 디폴트 값은 현재시간
        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());
        //Calendar 객체 생성
        Calendar nextNotifyTime = new GregorianCalendar();
        //지정된 long 값에서 달력의 현재 시간을 설정
        //쉐어드에서 현재 시간을 가져와서 nextNotifyTime에 넣어준다
        nextNotifyTime.setTimeInMillis(millis);

        // 이전 설정값으로 TimePicker 초기화
        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));

        if (Build.VERSION.SDK_INT >= 23 ){
            timePicker.setHour(pre_hour);
            timePicker.setMinute(pre_minute);
        }
        else{
            timePicker.setCurrentHour(pre_hour);
            timePicker.setCurrentMinute(pre_minute);
        }
    }

    /* 알람 시작 */
    private void start(){

        //시간 설정
        Calendar calendar = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
            calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);
        }

        //지나간 시간을 지정했다면 다음날 같은 시간으로 설정
        if(calendar.before(Calendar.getInstance())){
            //다음날로 설정
            calendar.add(Calendar.DATE, 1);
        }
        // Preference에 시간 설정한 값 저장
        SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
        editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
        editor.apply();

        //Receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        //state 값이 on 이면 알람시작 , off이면 중지
        intent.putExtra("state", "on");

        this.pendingIntent = PendingIntent.getBroadcast(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //알람 설정
        this.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        //Toast 보여주기 (알람 시간 표시)
        SimpleDateFormat format = new SimpleDateFormat("a hh시 mm분", Locale.getDefault());
        Toast.makeText(this, "설정 시간 : "+ format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();

    }

    /* 알람 중지 */
    public void stop(){
        if(this.pendingIntent == null){
            return;
        }

        //알람 취소
        this.alarmManager.cancel(this.pendingIntent);

        //알람 중지 Broadcast
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("state", "off");

        sendBroadcast(intent);
        this.pendingIntent = null;

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.service_start_btn :
                    //알람 시작
                    start();

                    break;

                case R.id.service_stop_btn :
                    //알람 중지
                    stop();

                    break;
            }
        }
    };


}
