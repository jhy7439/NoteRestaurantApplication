package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nova.noterestaurantapplication.Adapter.NoteRecycleViewAdapter;
import com.nova.noterestaurantapplication.Item.NoteItem;
import com.nova.noterestaurantapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

//추천맛집, 맛집작성 화면으로 이동
public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    //화면 하단 추천 맛집, 마이페이지 화면 이동 변수
    TextView tv_mainClick, tv_myClick;
    //화면 상단 버튼(포스트 추가, 검색)
    ImageView iv_addPost, iv_search;

    private BackPressCloseHandler backPressCloseHandler;

    //NoteItem 클래스의 데이터 리스트를 받는 리스트
    private List<NoteItem> noteList;
    private RecyclerView noteRecyclerView;
    private NoteRecycleViewAdapter noteRecycleViewAdapter;


    int index = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteList = new ArrayList<>();
        Log.d("생명주기", "NoteActivity" + ": onCreate");

        //참조
        init();

        //엑티비티 전환 버튼 클릭을 감지하는 메소드
        //추천 맛집(MainActivity) , 맛집 작성(WriteActivity)
        tv_mainClick.setOnClickListener(this);
        iv_addPost.setOnClickListener(this);
        tv_myClick.setOnClickListener(this);

        //백버튼 두번 누르면 종료
        backPressCloseHandler =new BackPressCloseHandler(this);

        //쉐어드 저장된 리사이클러뷰 아이템 데이터 불러오기
        //NoteActivity가 처음 화면에 보일때 저장한 맛집작성 데이터가 리싸이클러뷰에 뿌려진다
        //SharedPreferences 선언 -> String 변수에 저장된 데이터 담기 -> String to JSONArray -> JSONArray에 담긴 오브젝트를 꺼낸 후 오브젝트 안에 데이터를 사용한다
        SharedPreferences sharedDataPost = getSharedPreferences("noteList" , MODE_PRIVATE);
        //"noteList" 쉐어드에 저장된 모든 값을 jsonNoteList 넣는다
//        SharedPreferences.Editor editor = sharedDataPost.edit();
//        editor.clear();
//        editor.commit();

        Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "sharedDataPost : " + sharedDataPost);
        String jsonItemStr =  sharedDataPost.getString("jsonDataStr", "");
        Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "jsonItemStr(스트링 값) : " + jsonItemStr);
        if(jsonItemStr == null || jsonItemStr == ""){
            Toast.makeText(NoteActivity.this,"맛집을 작성해 보세요!", Toast.LENGTH_LONG).show();
        }else {
            Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "jsonItemStr(스트링 값) : " +"(else)"+ jsonItemStr);

            try {
                JSONArray getJsonArray = new JSONArray(jsonItemStr);

                Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "getJsonArray(제이슨 어레이) : " + getJsonArray);
                for (int i =0 ; i < getJsonArray.length(); i++){
                    JSONObject getObjectItem = getJsonArray.getJSONObject(i);

                    Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "getObjectItem(오브젝트 아이템) : "+ "(for문) :" + getObjectItem);
                    //jsonString에 담긴 값을 get("키")에 따라 String 변수에 넣어준다
                    String title = getObjectItem.get("titleStr").toString();
                    String rating = getObjectItem.get("ratingFlo").toString();
                    String photo = getObjectItem.get("photoStr").toString();
                    String address = getObjectItem.get("addressStr").toString();
                    String phone = getObjectItem.get("phoneStr").toString();
                    String content = getObjectItem.get("contentStr").toString();
                    Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "getObjectItem(오브젝트 아이템) : "+ "스트링(title) :" + title);
                    //NoteItem 클래스 생성자 2번째에 들어갈 변수타입 : float
                    float ratingFlo = Float.valueOf(rating);
                    //NoteItem 클래스 생성자 순서에 맞게 변수를 넣어야한다
                    NoteItem noteItemList = new NoteItem(title, ratingFlo , photo, address , phone ,content );
                    Log.d("쉐어드", "NoteActivity" + ": onCreate" + " : 불러오기 "  + "getObjectItem(오브젝트 아이템) : "+ "아이템 클래스 :" + noteItemList);
                    //noteItemList 객체에 담긴 데이터를 받아서 리사이클러뷰 어댑터에 넘겨준다
                    noteList.add(noteItemList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //리사이클러뷰 형태 잡아 줌
        LinearLayoutManager noteLinearManager = new LinearLayoutManager(getApplicationContext());
        noteRecyclerView.setLayoutManager(noteLinearManager);

        //리사이클러뷰 어댑터를 생성하고 context , 레이아웃 , 데이터리스트를 넣어준다
        //어댑터 생성자 인자 값으로 this는 Activity를 상속
        noteRecycleViewAdapter = new NoteRecycleViewAdapter(/*getApplicationContext()*/ this,
                R.layout.item_note , noteList);
        //어댑터로 activity를 어떻게 넘길것인지
        //        //어플리케이션 공부

        //리사이클럴뷰에 어댑터를 연결한다
        noteRecyclerView.setAdapter(noteRecycleViewAdapter);

    }

    //참조
    private void init(){
        //엑티비티 전환 버튼 참조
        tv_mainClick = findViewById(R.id.note_home_tv);
        tv_myClick = findViewById(R.id.note_my_tv);
        //맛집 작성 포스트 추가 버튼 참조
        iv_addPost = findViewById(R.id.note_addPost_iv);
        //노트 화면 리사이클러뷰 아이템 검색 버튼 참조
        iv_search = findViewById(R.id.note_search_iv);
        noteRecyclerView = findViewById(R.id.note_recyclerview);

    }

    //리사이클러뷰 세팅
//    private void setRecyclerView(){
//
//        LinearLayoutManager noteLinearManager = new LinearLayoutManager(getApplicationContext());
//        noteRecyclerView.setLayoutManager(noteLinearManager);
//
//        noteRecycleViewAdapter = new NoteRecycleViewAdapter(getApplicationContext(),
//                R.layout.item_note , noteList);
//        noteRecyclerView.setAdapter(noteRecycleViewAdapter);
//    }

    //버튼을 클릭 이벤트
    @Override
    public  void onClick(View view){
        switch (view.getId()){
            //추천 맛집 엑티비티로 가라
            case R.id.note_home_tv:
                Intent goMainPage = new Intent(NoteActivity.this , MainActivity.class);
                goMainPage.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goMainPage);
                break;
            //맛집 작성 엑티비티로
            //맛집 작성에서 작성한 데이터는 Note엑티비티 리사이클러뷰 아이템으로 전달
            case R.id.note_addPost_iv:
                    Intent postIntent = new Intent(NoteActivity.this , WriteActivity.class);
                    startActivityForResult(postIntent , 3000);
                break;
            case R.id.note_my_tv:
                Intent goMyPage = new Intent(NoteActivity.this , MyPageActivity.class);
                goMyPage.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goMyPage);
                break;


        }
    }



    //WriteActivity에서 보낸 데어터를 받는다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3000) {

            if (resultCode == RESULT_OK && data != null) {

                //NoteItem 객체를 생성하여 WriteActivity에서 보낸 데이터를 담고
                NoteItem receiveNoteItem = (NoteItem) data.getSerializableExtra("data");

                //NoteActivity(해당 엑티비티)의 리스트로 받는다
                noteList.add(receiveNoteItem);
                noteRecycleViewAdapter.notifyDataSetChanged();

                //쉐어드에 JSON형식으로 데이터 저장: 사용자 입력값 -> JSONObject -> JSONArray -> String으로 변환 -> 쉐어드 저장
                // WriteActivity에서 보낸 값은 receiveNoteItem 객체에 담긴다
                SharedPreferences sharedDataPost = getSharedPreferences("noteList" , MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedDataPost.edit();
                Log.d("쉐어드", "NoteActivity" + ": onActivityResult" + " : 저장하기 "  + "noteList(길이) : "+noteList.size() + " receiveNoteItem 객체(타이틀) :" + receiveNoteItem.getTitle());

                try {
                    //json 을 사용하여 리사이클러뷰 아이템의 값을 저장
                    //사용자가 입력한 값을 JSON오브젝트로 감싸고, 배열로 나열한다
                    JSONArray jsonArrayItem = new JSONArray();

                    for(int i =0; i<noteList.size() ; i++) {
                        //for문 안에 오브젝트를 생성해야 사용자가 입력한 값이 중복되지 않고 저장된다
                        JSONObject jsonObjectItem = new JSONObject();

                        //i번 배열 안에 i번 데이터가 담긴다
                        jsonObjectItem.put("titleStr", noteList.get(i).getTitle());
                        jsonObjectItem.put("ratingFlo", noteList.get(i).getRating());
                        jsonObjectItem.put("photoStr", noteList.get(i).getPhoto());
                        jsonObjectItem.put("addressStr", noteList.get(i).getAddress());
                        jsonObjectItem.put("phoneStr", noteList.get(i).getPhone());
                        jsonObjectItem.put("contentStr", noteList.get(i).getContent());

                        jsonArrayItem.put(jsonObjectItem);
                        Log.d("쉐어드", "NoteActivity" + ": onActivityResult" + " : 저장하기 "  + "jsonArrayItem : "+jsonArrayItem );
                    }
                    editor.putString("jsonDataStr", jsonArrayItem.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
           }
        }//requestCode == 3000

//        else if(requestCode == 3001){
//            if(resultCode == RESULT_OK){
//                Log.d("리사이클러뷰", "NoteActivity" + ": onActivityResult" + " : 결과값 :  "  + "noteList : "+noteList +": 인텐트 data : " + data.getStringExtra("edit_title"));
//                //EditNoteActivity 부터 데이터를 받아온다
//                //리사이클러뷰의 아이템 포지션을 int 형 pos 변수에 받는다
//                int pos = data.getIntExtra("position", 0);
//                noteList.get(pos).setTitle(data.getStringExtra("edit_title"));
//                noteList.get(pos).setRating(data.getFloatExtra("edit_rating", 0));
//                noteList.get(pos).setPhoto(data.getStringExtra("edit_photo"));
//                noteList.get(pos).setContent(data.getStringExtra("edit_content"));
//                noteList.get(pos).setPhone(data.getStringExtra("edit_phone"));
//                noteList.get(pos).setAddress(data.getStringExtra("edit_address"));
//                Log.d("리사이클러뷰", "NoteActivity" + ": onActivityResult" + " : 수정 후 리스트 값 :  "  +": 인텐트 data : " + noteList.get(pos).getTitle());
//
//                //변경된 값을 어댑터에 적용한다
//                noteRecycleViewAdapter.notifyDataSetChanged();
//            }
//        }


    }


    @Override
    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("생명주기", "NoteActivity" + ": onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("생명주기", "NoteActivity" + ": onStop");
        Log.d("쉐어드", "NoteActivity" + ": onStop :" + "데이터 리스트 : " + noteList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("생명주기", "NoteActivity" + ": onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("생명주기", "NoteActivity" + ": onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("생명주기", "NoteActivity" + ": onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("생명주기", "NoteActivity" + ": onRestart");
    }

}

    //쉐어드에 저장된 데이터를 noteList에 넣어준다
//    SharedPreferences jsonWrapShred = getSharedPreferences("jsonItemShared" , MODE_PRIVATE);
//
//    String jsonSharedStr = jsonWrapShred.getString("jsonKey", "");
//        if( jsonSharedStr.equals("")){
//                Log.d("제이슨", "NoteActivity" + ": onCreate" + ": 쉐어드 : "+ jsonSharedStr);
//                }else {
//                try {
//                JSONArray unPackJsonArray = new JSONArray(jsonSharedStr);
//
//                for (int number = 0; number < unPackJsonArray.length(); number++) {
//        JSONObject unPackJsonObject = unPackJsonArray.getJSONObject(number);
//
//        String title = unPackJsonObject.getString("jsonTitle");
//        String rating = unPackJsonObject.getString("jsonRating");
//        String address = unPackJsonObject.getString("jsonAddress");
//        String phone = unPackJsonObject.getString("jsonPhone");
//        String photo = unPackJsonObject.getString("jsonUriAlbum");
//
//        float floatRating = Float.valueOf(rating);
//
//        jsonItemList.setTitle(title);
//        jsonItemList.setRating(floatRating);
//        jsonItemList.setPhoto(photo);
//        jsonItemList.setAddress(address);
//        jsonItemList.setPhone(phone);
//        noteList.add(jsonItemList);
//        }
//        noteList.notify();
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//        }


//    //WriteActivity에서 작성한 데이터를 받는 메소드
//    //데이터를 리사이클러뷰 아이템에 넣는다
//    public void receiveWriteData(){
//
//        //rating 값이 0이면 디폴트 값이 출력되기 때문에 조건문 사용
//        if(rating != 0){
//
//            Intent intent = getIntent();
//            Log.d("생명주기", "NoteActivity" + "인텐트 : " + "레이팅 : " + intent.getStringExtra("title"));
//            Log.d("생명주기", "NoteActivity" + "인텐트 : " + "레이팅 : " + intent.getFloatExtra("rating", 0));
//            String title = intent.getStringExtra("title");
//            float rating = intent.getFloatExtra("rating", 0);
//            String address = intent.getStringExtra("address");
//            String phone = intent.getStringExtra("phone");
//
//            Log.d("생명주기", "NoteActivity" + "겟인텐트 : " + "레이팅 : " + rating);
//
//            NoteItem noteItem = new NoteItem(title, rating, address, phone);
//
//            Log.d("생명주기", "NoteActivity" + "겟인텐트 : " + "noteItem : " + noteItem);
//            noteList.add(noteItem);
//
//
//            noteRecycleViewAdapter = new NoteRecycleViewAdapter(getApplicationContext(), R.layout.item_note, noteList);
//
//            noteRecyclerView.setAdapter(noteRecycleViewAdapter);
//            Log.d("생명주기", "NoteActivity" + " 어레이 리스트 : " + noteList.set(0, noteItem));
//
//            noteRecycleViewAdapter.notifyDataSetChanged();
//            Log.d("생명주기", "NoteActivity" + "노티파이 : " + "레이팅 : " + rating);
//            //각 아이템이 보여지는 것을 일정하게
//            noteRecyclerView.setHasFixedSize(true);
//        }
//    }

//쉐어드 불러오기
//        Map<String,?> jsonNoteList = sharedDataPost.getAll();
//        for(Map.Entry<String,?> entry : jsonNoteList.entrySet()){
//            String jsonString = entry.getValue().toString();
//            try {
//                //jsonString에 담긴 값을 get("키")에 따라 String 변수에 넣어준다
//                JSONObject jsonObject =  new JSONObject(jsonString);
//                String title = jsonObject.get("titleStr").toString();
//                String rating = jsonObject.get("ratingFlo").toString();
//                String photo = jsonObject.get("photoStr").toString();
//                String address = jsonObject.get("addressStr").toString();
//                String phone = jsonObject.get("phoneStr").toString();
//                String content = jsonObject.get("contentStr").toString();
//                String noteSeq = jsonObject.get("noteSeq").toString();
//
//                //NoteItem 클래스 생성자 2번째에 들어갈 변수타입 : float
//                float ratingFlo = Float.valueOf(rating);
//
//                //NoteItem 클래스 생성자 순서에 맞게 변수를 넣어야한다
//                NoteItem noteItemList = new NoteItem(title, ratingFlo , photo, address, phone ,content );
//                noteItemList.setNoteSeq(Long.parseLong(noteSeq));
//                //noteItemList 객체에 담긴 데이터를 받아서 리사이클러뷰 어댑터에 넘겨준다
//                noteList.add(noteItemList);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }


//제이슨으로 쉐어드에 값 저장
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("titleStr",receiveNoteItem.getTitle());
//                    jsonObject.put("ratingFlo",receiveNoteItem.getRating());
//                    jsonObject.put("photoStr",receiveNoteItem.getPhoto());
//                    jsonObject.put("addressStr",receiveNoteItem.getAddress());
//                    jsonObject.put("phoneStr",receiveNoteItem.getPhone());
//                    jsonObject.put("contentStr", receiveNoteItem.getContent());
//                    jsonObject.put("noteSeq",receiveNoteItem.getNoteSeq());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                //저장되는 데이터 마다 위치값을 준다 : getNoteSeq
//                editor.putString(receiveNoteItem.getNoteSeq()+"", jsonObject.toString());
//                editor.apply();
