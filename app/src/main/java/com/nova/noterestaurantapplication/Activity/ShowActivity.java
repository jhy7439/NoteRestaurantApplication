package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nova.noterestaurantapplication.R;

//NoteActivity 리사이클러뷰 아이템 클릭 -> 아이템의 정보를 보여주는 엑티비티
public class ShowActivity extends AppCompatActivity {

    ImageView ivBackButton, ivSetImage;
    TextView tvTitle, tvAddress ,tvPhone;
    RatingBar getRating;
    TextView tvContent;

    String title, photoStr, address, phone, content;
    Uri uriAlbum;
    float rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        //참조
        init();
        //NoteActivity 리사이클러뷰 아이템의 정보를 보여주는 메소드
        getShowIntent();
    }
    //참조
    private void init(){
        //백버튼
        ivBackButton = findViewById(R.id.show_back_iv);
        //이미지
        ivSetImage = findViewById(R.id.show_iv);
        //타이틀
        tvTitle = findViewById(R.id.show_title_tv);
        //별 등급
        getRating = findViewById(R.id.show_ratingBar);
        //글 내용
        tvContent = findViewById(R.id.show_content_tv);
        //주소
        tvAddress = findViewById(R.id.show_address_tv);
        //연락처
        tvPhone = findViewById(R.id.show_phone_tv);

    }
    //NoteActivity 리사이클러뷰 아이템의 데이터를 받아서 엑티비티에 전달
    private void getShowIntent(){
        Intent getShowIntent = getIntent();
        if(getShowIntent != null){
            title = getIntent().getStringExtra("edit_title");
            tvTitle.setText(title);

            rating = getIntent().getFloatExtra("edit_rating",0);
            getRating.setRating(rating);

            //스트링을 > uri : uri.parse
            //uri 가 짱
            //Uri < -- > String 이게 짱
            photoStr = getIntent().getStringExtra("edit_photo");
            uriAlbum = Uri.parse(photoStr);
            ivSetImage.setImageURI(Uri.parse(photoStr));

            address = getIntent().getStringExtra("edit_address");
            tvAddress.setText(address);

            phone = getIntent().getStringExtra("edit_phone");
            tvPhone.setText(phone);

            content = getIntent().getStringExtra("edit_content");
            tvContent.setText(content);
        }
    }


}
