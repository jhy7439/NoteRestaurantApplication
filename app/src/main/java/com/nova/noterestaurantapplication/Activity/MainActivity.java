package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nova.noterestaurantapplication.Adapter.MainAdapter;
import com.nova.noterestaurantapplication.Item.MainItem;
import com.nova.noterestaurantapplication.R;

import java.util.ArrayList;

//추천 맛집을 목록으로 확인가능
//원하는 맛집 검색 기능
//노트, 맛집작성, 마이페이지 이동
public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //노트, 맛집작성, 마이페이지로 이동
    TextView tvNoteClick, tvMyClick;
    //MainActivity안 리스트뷰 아이템에 들어갈 Arraylist
    private ArrayList<MainItem> mainList = null;

    //오늘 뭐먹지 페이지로 이동
    TextView tvWhatEatToday;

//    String pastaTitle, pastaSubTitle, pastaContent, pastaphone, pastaAddress,
//            susiTitle, susiSubTitle, susiContent, susiPhone, susiAddress,
//    gogiTitle, gogiSubTitle, gogiContent, gogiPhone, gogiAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("생명주기", "MainActivity" + ": onCreate");


        //하단 화면 이동 버튼 참조
        tvNoteClick = findViewById(R.id.main_noteList_tv);
        tvMyClick = findViewById(R.id.main_my_tv);

        //오늘 뭐먹지 결정해주는 버튼
        tvWhatEatToday = findViewById(R.id.whatEatToday_tv);
        tvWhatEatToday.setOnClickListener(this);

        //노트 엑티비티로 이동
        tvNoteClick.setOnClickListener(this);
        tvMyClick.setOnClickListener(this);

        //리스트 객체 선언
        mainList = new ArrayList<>();

        //리스트뷰 참조
        ListView listView = findViewById(R.id.main_listview);

        //아이템 객체에 데이터를 넣어준다
        MainItem pastaItem = new MainItem(R.drawable.pasta , getString(R.string.pasta_title) ,getString(R.string.pasta_subTitle)
        , getString( R.string.pasta_content),getString( R.string.pasta_phone) ,getString( R.string.pasta_address));


        MainItem susiItem = new MainItem(R.drawable.susi , getString(R.string.susi_title),getString(R.string.susi_subTitle) ,
               getString(R.string.susi_content), getString(R.string.susi_phone), getString(R.string.susi_address));

        MainItem gogiItem = new MainItem(R.drawable.gogi , getString(R.string.gogi_title), getString(R.string.gogi_subTitle),
               getString(R.string.gogi_content), getString(R.string.gogi_phone), getString(R.string.gogi_address));

        //아이템을 리스트에 추가
        mainList.add(pastaItem);
        mainList.add(susiItem);
        mainList.add(gogiItem);

        //리스트 속의 아이템 연결
        MainAdapter mainAdapter = new MainAdapter(this , R.layout.item_main , mainList);
        listView.setAdapter(mainAdapter);

        //아이템 클릭시 이벤트
        listView.setOnItemClickListener(this);

      }

    //리스트 아이템 클릭 이벤트
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent listViewIntent = new Intent(getApplicationContext() , RecommendationActivity.class);

        listViewIntent.putExtra("foodPhoto" , Integer.toString(mainList.get(position).getFoodPhoto()));
        listViewIntent.putExtra("title" , mainList.get(position).getTitle());
        listViewIntent.putExtra("subTitle" , mainList.get(position).getSubTitle());
        listViewIntent.putExtra("content", mainList.get(position).getContent());
        listViewIntent.putExtra("phone", mainList.get(position).getPhone());
        listViewIntent.putExtra("address", mainList.get(position).getAddress());
        Log.d("주소",mainList.get(position).getAddress());
        startActivity(listViewIntent);

    }

    //버튼을 클릭 이벤트
    @Override
    public  void onClick(View view){
        switch (view.getId()){
            //노트 엑티비티로 가라
            case R.id.main_noteList_tv:
                Intent goNotePage = new Intent(MainActivity.this , NoteActivity.class);
                goNotePage.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goNotePage);
                finish();
                break;
            case R.id.whatEatToday_tv:
                Intent goWhatEatToday = new Intent(MainActivity.this , WhatEatTodayActivity.class);
                startActivity(goWhatEatToday);
                break;
            case R.id.main_my_tv:
                Intent goMyPage = new Intent(MainActivity.this , MyPageActivity.class);
                startActivity(goMyPage);
                finish();
                break;

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("생명주기", "MainActivity" + ": onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("생명주기", "MainActivity" + ": onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("생명주기", "MainActivity" + ": onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("생명주기", "MainActivity" + ": onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("생명주기", "MainActivity" + ": onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("생명주기", "MainActivity" + ": onRestart");
    }
}
