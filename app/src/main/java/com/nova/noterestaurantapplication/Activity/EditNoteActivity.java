package com.nova.noterestaurantapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nova.noterestaurantapplication.Adapter.NoteRecycleViewAdapter;
import com.nova.noterestaurantapplication.Item.NoteItem;
import com.nova.noterestaurantapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nova.noterestaurantapplication.Activity.WriteActivity.rotateImage;

public class EditNoteActivity extends AppCompatActivity {

    //사용자가 직접 입력
    EditText edEditTitle, edEditAddress, edEditPhone , edEditContent;

    ImageView ivEditCompletion;
    //사진을 담는 이미지뷰
    ImageView ivSetPhoto;
    TextView tvPickPhotoBtn;
    private List<NoteItem> editList = new ArrayList<>();

    //사진 이미지 uri
    private Uri uriAlbum;
    //앨범에 담을 Bitmap 형식
    Bitmap bitmapAlbum;
    //카메라로 가져온 Bitmap 형식
    Bitmap bitmapCamera;
    //맛집 별 등급
    RatingBar ratingBar;
    //별등급 데이터를 받는 변수
    TextView tvRatingScale;
    //사용자가 입력한 별등급 확인 변수
    float ratingValue;
    //카메라로 사진 찍어 가져오기
    final static int TAKE_PICTURE = 10;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 10;
    private String TAG;

    //intent 로 전달받은 데이터를 담는 변수
    String title, photoStr, address, phone, content;
    float rating;

    //앨범에서 사진 가져오기
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    public static final int GALLERY_INTENT_CALLED = 100;
    public static final int GALLERY_KITKAT_INTENT_CALLED = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //참조
        init();
        //맛집 별등급 커스텀
        tasteScore();
        //NoteActivity 리사이클러뷰에서 받아온 데이터를 받는 메소드
        getIncomingIntent();

        //NoteActivity 리사이클러뷰 아이템 수정
        editPost();

        //카메라 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //권한 설정 완료
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "권한 설정 완료");
                //권한 설정 요청
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 90);
            }
        }


        tvPickPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items = {"카메라", "앨범", "취소하기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
                // 여기서 부터는 알림창의 속성 설정
                // 제목 설정
                builder.setTitle("사진 추가")

                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정

                            public void onClick(DialogInterface dialog, int index){
                                switch (index){
                                    case 0:
                                        dispatchTakePictureIntent();
                                        break;
                                    case 1:
                                        if (Build.VERSION.SDK_INT <19){
                                            Intent setImageIntent = new Intent();
                                            setImageIntent.setType("*/*");
                                            setImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(setImageIntent,GALLERY_INTENT_CALLED );
                                        }
                                        else{
                                            Intent setImageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                            setImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                            setImageIntent.setType("*/*");
                                            startActivityForResult(Intent.createChooser(setImageIntent,"select_photo"),GALLERY_KITKAT_INTENT_CALLED);
                                        }
                                        break;
                                    case 2:
                                        dialog.cancel();
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성

                dialog.show();    // 알림창 띄우기
            }
        });

    }

    //참조
     private void init() {
        //사용자가 맛집작성하는 부분
        edEditTitle = findViewById(R.id.edit_title_et);
        edEditAddress = findViewById(R.id.edit_address_et);
        edEditPhone = findViewById(R.id.edit_phoneNumber_et);
        edEditContent = findViewById(R.id.edit_content_et);

        //맛집 별 등급
        ratingBar = findViewById(R.id.edit_ratingBar);

        //맛집 별 등급 텍스트
        tvRatingScale = findViewById(R.id.edit_rating_tv);

        //사진 이미지가 놓여질 이미지뷰
        ivSetPhoto =findViewById(R.id.edit_photo_iv);

        //앨범에서 사진을 가져오는 텍스트뷰
         tvPickPhotoBtn = findViewById(R.id.pick_photo_tv);

        //사용자가 수정한 내용을 저장하는 버튼
        ivEditCompletion = findViewById(R.id.edit_editPost_iv);
    }

    //카메라 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    //이미지 뷰 안에 사진을 결과로 받는다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //카메라로 사진을 받는다
        if(requestCode ==TAKE_PICTURE) {
            bitmapCamera = null;
            if (resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                try {
                    bitmapCamera = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmapCamera != null) {
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(mCurrentPhotoPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmapCamera, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmapCamera, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmapCamera, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmapCamera;
                    }

                    ivSetPhoto.setImageBitmap(rotatedBitmap);
                }
            }
        }
        //앨범에서 사진을 받는다
        else if (resultCode == RESULT_OK) {
            uriAlbum = null;
            if (Build.VERSION.SDK_INT < 19) {
                uriAlbum = data.getData();
            } else {
                uriAlbum = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    getApplication().getContentResolver().takePersistableUriPermission(uriAlbum, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            try {
                bitmapAlbum = BitmapFactory.decodeStream(getApplication().getContentResolver().openInputStream(uriAlbum));
                ivSetPhoto.setImageBitmap(bitmapAlbum);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    //이미지를 돌려주는 함수
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    //카메라 인텐트를 실행하는 부분
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.nova.noterestaurantapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //bitmap을 uri로 바꿔주는 메소드
    //카메라에서 받아온 비트맵을 uri로 변환하여 쉐어드 저장
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        //uriAlbum : 카메라와 앨범에서 공유하는 uri
        //리턴값을 전역변수에 넣어주면 원하는 곳에서 변수 사용가능
        return  uriAlbum = Uri.parse(path);
    }

       //맛집 작성: 사용자가 별 등급 선택
    private void tasteScore(){
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                tvRatingScale.setText(String.valueOf(rating));
                switch ((int) ratingBar.getRating()){
                    case 1:
                        tvRatingScale.setText("가지마세요!");
                        ratingValue = 1;
                        break;
                    case 2:
                        tvRatingScale.setText("별로에요.");
                        ratingValue = 2;
                        break;
                    case 3:
                        tvRatingScale.setText("보통이에요.");
                        ratingValue = 3;
                        break;
                    case 4:
                        tvRatingScale.setText("추천해요!");
                        ratingValue = 4;
                        break;
                    case 5:
                        tvRatingScale.setText("최고에요!");
                        ratingValue = 5;
                        break;
                }
            }
        });
    }

    //NoteActivity 리사이클러뷰 아이템에서 전달한 데이터를 받는다
    private void getIncomingIntent(){
        Intent getItemIntent = getIntent();
        if(getItemIntent != null){
            title = getIntent().getStringExtra("edit_title");
            edEditTitle.setText(title);

            rating = getIntent().getFloatExtra("edit_rating",0);
            ratingBar.setRating(rating);

            //스트링을 > uri : uri.parse
            //uri 가 짱
            //Uri < -- > String 이게 짱
            photoStr = getIntent().getStringExtra("edit_photo");
            uriAlbum = Uri.parse(photoStr);
            ivSetPhoto.setImageURI(Uri.parse(photoStr));

            address = getIntent().getStringExtra("edit_address");
            edEditAddress.setText(address);

            phone = getIntent().getStringExtra("edit_phone");
            edEditPhone.setText(phone);

            content = getIntent().getStringExtra("edit_content");
            edEditContent.setText(content);
        }
    }

    //오른쪽 상단 저장버튼:맛집작성을 마치고 버튼을 누르면 NoteActivity 리사이클러뷰 아이템이 변경되고 EditNoteActivity 종료
    //쉐어드에 변경된 데이터가 저장
    private void editPost() {
        ivEditCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedDataPost = getSharedPreferences("noteList" , MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedDataPost.edit();

                //쉐어드에 저장된 데이터 불러오기
                String jsonItemStr =  sharedDataPost.getString("jsonDataStr", "");
                Log.d("쉐어드", "EditNoteActivity" + ": editPost" + " : 불러오기 "  + "jsonItemStr(스트링 값) : " + jsonItemStr);

                    try {
                        JSONArray getJsonArray = new JSONArray(jsonItemStr);

                        Log.d("쉐어드", "EditNoteActivity" + ": editPost" + " : 불러오기 " + "getJsonArray(제이슨 어레이) : " + getJsonArray);
                        for (int i = 0; i < getJsonArray.length(); i++) {
                            JSONObject getObjectItem = getJsonArray.getJSONObject(i);

                            Log.d("쉐어드", "EditNoteActivity" + ": editPost" + " : 불러오기 " + "getObjectItem(오브젝트 아이템) : " + "(for문) :" + getObjectItem);
                            //jsonString에 담긴 값을 get("키")에 따라 String 변수에 넣어준다
                            String title = getObjectItem.get("titleStr").toString();
                            String rating = getObjectItem.get("ratingFlo").toString();
                            String photo = getObjectItem.get("photoStr").toString();
                            String address = getObjectItem.get("addressStr").toString();
                            String phone = getObjectItem.get("phoneStr").toString();
                            String content = getObjectItem.get("contentStr").toString();
                            Log.d("쉐어드", "EditNoteActivity" + ": editPost" + " : 불러오기 " + "getObjectItem(오브젝트 아이템) : " + "스트링(title) :" + title);
                            //NoteItem 클래스 생성자 2번째에 들어갈 변수타입 : float
                            float ratingFlo = Float.valueOf(rating);
                            //NoteItem 클래스 생성자 순서에 맞게 변수를 넣어야한다
                            NoteItem noteItemList = new NoteItem(title, ratingFlo, photo, address, phone, content);
                            Log.d("쉐어드", "EditNoteActivity" + ": editPost" + " : 불러오기 " + "getObjectItem(오브젝트 아이템) : " + "아이템 클래스 :" + noteItemList);
                            //noteItemList 객체에 담긴 데이터를 받아서 리사이클러뷰 어댑터에 넘겨준다
                            editList.add(noteItemList);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //아이템 데이터 위치
                    Intent getItemIntent = getIntent();
                    int position = getItemIntent.getIntExtra("position", 0);

                    title = edEditTitle.getText().toString();
                    rating = ratingBar.getRating();
                    photoStr = uriAlbum.toString();
                    phone = edEditPhone.getText().toString();
                    address = edEditAddress.getText().toString();
                    content = edEditContent.getText().toString();

                Log.d("사진", "EditNoteActivity" + ": photoStr : " + photoStr + " : uriAlbum : " + uriAlbum);

                    editList.get(position).setTitle(title);
                    editList.get(position).setRating(rating);
                    editList.get(position).setPhoto(photoStr);
                    editList.get(position).setPhone(phone);
                    editList.get(position).setAddress(address);
                    editList.get(position).setContent(content);

                    //변경한 데이터 저장하기
                try {
                    JSONArray adapterJsonArray = new JSONArray();//배열
                    for (int i = 0; i < editList.size(); i++) {
                        JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                        sObject.put("titleStr", editList.get(i).getTitle());
                        sObject.put("ratingFlo", editList.get(i).getRating());
                        sObject.put("photoStr", editList.get(i).getPhoto());
                        sObject.put("addressStr", editList.get(i).getAddress());
                        sObject.put("phoneStr", editList.get(i).getPhone());
                        sObject.put("contentStr", editList.get(i).getContent());

                        adapterJsonArray.put(sObject);
                    }
                    editor.putString("jsonDataStr", adapterJsonArray.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
                Intent intent = new Intent(EditNoteActivity.this , NoteActivity.class);
                startActivity(intent);

            }
        });
    }
    //백버튼 누를 때 종료 또는
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert_ex = new AlertDialog.Builder(this);
        alert_ex.setMessage("정말로 종료하시겠습니까?");

        alert_ex.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert_ex.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(EditNoteActivity.this , NoteActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alert_ex.setTitle("맛집작성 중 입니다");
        AlertDialog alert = alert_ex.create();
        alert.show();

    }


}
//전체 데이터 리스트를 받아와서
//EditNoteActivity 에서 리사이클러뷰 아이템 데이터를 가져와서 수정하면
//수정한 값을 전체 리스트에 넣어주어
//JSON 형식으로 바꾸어 쉐어드에 저장
//NoteActivity 리사이클러뷰 아이템 포지션 값
// startactivityforresult 사용시 setResult 해주는 메소드
//    Intent getItemIntent = getIntent();
//    int position = getItemIntent.getIntExtra("position", 0);
//
//    Intent editIntent = new Intent();
//
//                if(title != null || content != null || phone != null || uriAlbum.toString() != null || address != null ){
//                        editIntent.putExtra("edit_title", edEditTitle.getText().toString());
//                        editIntent.putExtra("edit_rating", ratingBar.getRating());
//                        editIntent.putExtra("edit_photo", uriAlbum.toString());
//                        editIntent.putExtra("edit_address" , edEditAddress.getText().toString());
//                        editIntent.putExtra("edit_phone" , edEditPhone.getText().toString());
//                        editIntent.putExtra("edit_content" , edEditContent.getText().toString());
//
//                        editIntent.putExtra("position", position);
//                        setResult(Activity.RESULT_OK, editIntent);
//                        finish();
//                        }



// null 검사
//                if(edEditTitle.getText().toString() == null){
//                        editIntent.putExtra("edit_title", title);
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 No :  "  + "title : "+title);
//                        }
//                        else {
//                        editIntent.putExtra("edit_title", edEditTitle.getText().toString());
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 OK :  "  + "title : "+edEditTitle.getText().toString());
//                        }
//                        //별점
//                        if (edEditTitle.getText().toString() == null){
//                        editIntent.putExtra("edit_rating", rating);
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 No :  "  + "rating : "+rating);
//                        }
//                        else {
//                        editIntent.putExtra("edit_rating", ratingBar.getRating());
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 OK :  "  + "rating : "+ratingBar.getRating());
//                        }
//                        //사진
//                        if(uriAlbum != null){
//                        editIntent.putExtra("edit_photo", uriAlbum.toString());
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 OK :  "  + "uriAlbum : "+uriAlbum.toString());
//                        }
//                        else {
//                        editIntent.putExtra("edit_photo" , photoStr);
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 No :  "  + "uriAlbum : "+photoStr);
//                        }
//                        //주소
//                        if( edEditAddress.getText().toString() == null){
//                        editIntent.putExtra("edit_address" , address);
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 No :  "  + "address : "+address);
//                        }
//                        else {
//                        editIntent.putExtra("edit_address" , edEditAddress.getText().toString());
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 OK :  "  + "address : "+edEditAddress.getText().toString());
//                        }
//                        //연락처
//                        if(edEditPhone.getText().toString() == null){
//                        editIntent.putExtra("edit_phone" , phone);
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 No :  "  + "phone : "+phone);
//                        }
//                        else {
//                        editIntent.putExtra("edit_phone" , edEditPhone.getText().toString());
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 OK :  "  + "phone : "+edEditPhone.getText().toString());
//                        }
//                        //내용
//                        if( edEditContent.getText().toString() == null){
//                        editIntent.putExtra("edit_content" , content);
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 No :  "  + "content : "+content);
//                        }
//                        else {
//                        editIntent.putExtra("edit_content" , edEditContent.getText().toString());
//                        Log.d("리사이클러뷰", "EditNoteActivity" + ": editPost" + " : 수정 OK :  "  + "content : "+edEditContent.getText().toString());
//                        }