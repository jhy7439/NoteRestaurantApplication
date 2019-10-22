package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nova.noterestaurantapplication.R;

public class WhatEatTodayActivity extends AppCompatActivity implements View.OnClickListener {

    //초를 정해줌
    EditText edInputGap;
    //메인으로 돌아가는 버튼
    Button btnFirsttime;

    //이미지가 바뀜
    ImageView ivSet;
    //시작 하고 걸리는 시간
    TextView tvTimeSet;

    int gap, index, time, temptime;
    int[] images = {R.drawable.fork, R.drawable.susi, R.drawable.pasta,
            R.drawable.pizza, R.drawable.ramen, R.drawable.steak};

    String[] names = {"돼지고기", "초밥", "파스타", "피자", "라멘", "스테이크"};
    boolean started = false;
    //textTask textAsyncTask;
    imageTask imageAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_eat_today);
        //이미지가 바뀐다
        ivSet = findViewById(R.id.set_iv);
        //시간 간격
        tvTimeSet = findViewById(R.id.time_set_tv);
        edInputGap = findViewById(R.id.input_gap_et);


        btnFirsttime = findViewById(R.id.first_time_btn);

        ivSet.setOnClickListener(this);
        btnFirsttime.setOnClickListener(this);

    }


    class imageTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            index = 0;
            ivSet.setImageResource(images[0]);
        }

        @Override
        protected Void doInBackground(Integer... params) {

            while (isCancelled() == false) {

                try {
                    Thread.sleep(1000);
                    if (index >= 5) {
                        index = -1;
                    }
                    publishProgress(++index);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //진행상황을 UI에 업데이트
        //publishProgress(value)의 value를 값으로 받는다.values는 배열이라 여러개 받기가능
        //이미지가 변할 때 마다 UI에 보여줌
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ivSet.setImageResource(images[values[0]]);
        }

        //수행되던 작업이 종료되었을 때 호출되는 메소드
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        //작업이 cancel 되면 호출되는 메소드
        //사용자가 스레드 작동 중 이미지를 터치하면 스레드는 종료되고
        //마지막에 보이는 이미지와 이미지의 이름을 보여준다
        @Override
        protected void onCancelled() {
            super.onCancelled();
            ivSet.setImageResource(images[index]);
            tvTimeSet.setText(names[index] + " 선택 ");
        }

    }

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.set_iv:
                if (started == true) {

                    imageAsyncTask.cancel(true);
                    started = false;
                } else {
                    imageAsyncTask = new imageTask();
                    imageAsyncTask.execute();
                    started = true;

                }
            case R.id.first_time_btn:
                ivSet.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                tvTimeSet.setVisibility(View.INVISIBLE);
                started = false;
                edInputGap.setText(null);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(imageAsyncTask == null){
            imageTask imageAsyncTask = new imageTask();
            imageAsyncTask.execute();
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
       if((imageAsyncTask != null) && (isFinishing())) {
           imageAsyncTask.cancel(false);
       }
    }
}



