package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nova.noterestaurantapplication.R;

//맛집노트 어플임을 사용자에게 알려주는 엑티비티
public class IntroActivity extends AppCompatActivity {

    //로딩화면 이미지 뷰
    ImageView setImageResource;

    //로딩화면 이미지
    int introImage = R.drawable.intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        //로딩 이미지 참조
        setImageResource = findViewById(R.id.intro);

        //스레드 생성
        new introTask().execute();
    }
    //맛집 인트로 Async Task
    class introTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setImageResource.setImageResource(introImage);

        }

        //스레드 작업 메소드
        @Override
        protected Void doInBackground(Integer... params) {

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            return null;
        }

        //진행상황을 UI에 업데이트
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            setImageResource.setImageResource(introImage);
        }

        //Async Task 종료되면 호출되는 메소드
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //맛집 작성 할 수 있는 NoteActivity로 이동
            Intent intent = new Intent(IntroActivity.this , NoteActivity.class);
            startActivity(intent);
            finish();

        }

    }

    //백버튼 제한
    @Override
    public void onBackPressed() {

    }

}











//사용자에게 엑티비티화면을 보여주는 시간
//    private final int SPLASH_DISPLAY_TIME = 2000;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_intro);
//
//        Handler introHandler = new Handler();
//        //2초 뒤에 MainActivity로 이동
//        introHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(getApplication(), MainActivity.class));
//                IntroActivity.this.finish();
//            }
//        }, SPLASH_DISPLAY_TIME);
//    }
//    @Override
//    public void onBackPressed() {
//        /* 스플래시 화면에서 뒤로가기 기능 제거. */
//    }