package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nova.noterestaurantapplication.R;

public class RecommendationActivity extends AppCompatActivity {
    //추천맛집에서 선택한 아이템의 이미지를 받는 변수
    private int mainImage;

    ImageView back, save;
    Button locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        //추천맛집 화면 아이템의 정보를 받는다
        Intent liseViewIntent = getIntent();

        //아이템에 들어갈 데이터를 참조
        ImageView foodPhoto = findViewById(R.id.recommend_food_iv);
        TextView title = findViewById(R.id.recommend_title_tv);
        TextView subTitle = findViewById(R.id.recommend_subTitle_tv);
        TextView content =findViewById(R.id.recommend_content_tv);
        final TextView phone = findViewById(R.id.recommend_phone_tv);
        final TextView address = findViewById(R.id.recommend_address_tv);

        //Integer : Wrapper클래스(객체)
        //
        mainImage = Integer.parseInt(liseViewIntent.getStringExtra("foodPhoto"));
        foodPhoto.setImageResource(mainImage);
        title.setText(liseViewIntent.getStringExtra("title"));
        subTitle.setText(liseViewIntent.getStringExtra("subTitle"));
        content.setText(liseViewIntent.getStringExtra("content"));
        phone.setText(liseViewIntent.getStringExtra("phone"));
        address.setText(liseViewIntent.getStringExtra("address"));


        Log.d("TAG", "주소 : "+ address.getText().toString());
        //뒤로가기 그림 참조
        back = findViewById(R.id.recommend_back_iv);

        //MainActivity 화면으로 이동
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecommendationActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        //장소 검색
        locationBtn = findViewById(R.id.recommend_location_btn);
        Log.d("TAG", "주소 값 : " + address +" add " );

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent locationIntent = new Intent();
                locationIntent.setAction(Intent.ACTION_VIEW);
                locationIntent.setData(Uri.parse("geo:0,0?q="+address.getText().toString()));
                startActivity(locationIntent);
            }
        });

        //저장된 맛집 전화번호
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + phone.getText().toString());
                Intent dialIntent = new Intent(Intent.ACTION_DIAL , uri);
                startActivity(dialIntent);
            }
        });



    }

}
