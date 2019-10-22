package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nova.noterestaurantapplication.Item.NoteItem;
import com.nova.noterestaurantapplication.R;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//광고 스레드 (Handler 사용) : NoteActivity 리사이클러뷰 아이템의 정보를 가져와서 보여준다
public class MyPageActivity extends AppCompatActivity implements View.OnClickListener {


    //tvTitle: 작업 스레드의 제목을 출력할 텍스트뷰
    TextView tvTitle , tvTotalWriting, tvBookmark;
    //작업 스레드의 이미지를 출력할 이미지뷰
    ImageView ivSetImage;
    //작업 스레드의 별등급을 출력할 RatingBar
    RatingBar ratingBar;

    //하단 버튼
    TextView tvHomeButton, tvNoteButton;

    //메인 스레드로 작업을 전달할 핸들러
    Handler handler;
    //스레드 내부의 while문을 제어할 flag
    boolean flag;

    //
    int index;
    private List<NoteItem> recyclerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        init();
        tvHomeButton.setOnClickListener(this);
        tvNoteButton.setOnClickListener(this);


        //총 작업 개수
        SharedPreferences sharedDataPost = getSharedPreferences("noteList" , MODE_PRIVATE);
        String jsonItemStr =  sharedDataPost.getString("jsonDataStr", "");

         try {
                JSONArray getJsonArray = new JSONArray(jsonItemStr);

            for (int i =0 ; i < getJsonArray.length(); i++){
            JSONObject getObjectItem = getJsonArray.getJSONObject(i);

            //jsonString에 담긴 값을 get("키")에 따라 String 변수에 넣어준다
            String title = getObjectItem.get("titleStr").toString();
            String rating = getObjectItem.get("ratingFlo").toString();
            String photo = getObjectItem.get("photoStr").toString();
            String address = getObjectItem.get("addressStr").toString();
            String phone = getObjectItem.get("phoneStr").toString();
            String content = getObjectItem.get("contentStr").toString();

            //NoteItem 클래스 생성자 2번째에 들어갈 변수타입 : float
            float ratingFlo = Float.valueOf(rating);
            //NoteItem 클래스 생성자 순서에 맞게 변수를 넣어야한다
            NoteItem noteItemList = new NoteItem(title, ratingFlo , photo, address , phone ,content );

            //noteItemList 객체에 담긴 데이터를 받아서 리사이클러뷰 어댑터에 넘겨준다
            recyclerList.add(noteItemList);
            }
        }
        catch (JSONException e) {
        e.printStackTrace();
        }
        //쉐어드에 저장 된 데이터를 리스트로 담아 리스트의 사이즈를 구한다
       int listLength = recyclerList.size();
       String writingAmount = Integer.toString(listLength);
       //사용자가 작성한 게시물 갯수
       tvTotalWriting.setText(writingAmount);

        //메인 스레드로 작업을 전달할 핸들러
        handler = new Handler();

        flag = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //작성된 맛집 게시물을 스레드로 보여준다
                while (flag){
                    try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //게시물의 갯수보다 index 숫자가 크거나 같으면
                        if (index >= recyclerList.size()) {
                            index = 0;
                            ivSetImage.setImageURI(Uri.parse(recyclerList.get(index).getPhoto()));
                            tvTitle.setText(recyclerList.get(index).getTitle());
                            ratingBar.setRating(recyclerList.get(index).getRating());


                        }else {
                            ivSetImage.setImageURI(Uri.parse(recyclerList.get(index).getPhoto()));
                            tvTitle.setText(recyclerList.get(index).getTitle());
                            ratingBar.setRating(recyclerList.get(index).getRating());
                            ++index;
                        }

                        }
                    });
                    Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void init(){
        tvTitle = findViewById(R.id.async_title_tv);
        ratingBar = findViewById(R.id.rate_ratingBar);
        ivSetImage = findViewById(R.id.async_iv);
        tvTotalWriting = findViewById(R.id.total_writing_tv);
        tvBookmark = findViewById(R.id.bookmark_writing_tv);
        tvHomeButton = findViewById(R.id.recommend_button_tv);
        tvNoteButton = findViewById(R.id.note_button_tv);

    }

    //버튼을 클릭 이벤트
    @Override
    public  void onClick(View view){
        switch (view.getId()){
            //노트 엑티비티로 이동
            case R.id.note_button_tv:
                Intent goNotePage = new Intent(MyPageActivity.this , NoteActivity.class);
                goNotePage.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goNotePage);
                finish();
                break;
            //맛집 추천 엑티비티로 이동
            case R.id.recommend_button_tv:
                Intent goRecommend = new Intent(MyPageActivity.this , MainActivity.class);
                startActivity(goRecommend);
                finish();
                break;
        }
    }


}
