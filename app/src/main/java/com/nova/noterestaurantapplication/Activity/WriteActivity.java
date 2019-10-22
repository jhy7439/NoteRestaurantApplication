package com.nova.noterestaurantapplication.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nova.noterestaurantapplication.Item.NoteItem;
import com.nova.noterestaurantapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {


    //iv_save : 작성한 화면 저장 버튼
    //iv_set_photo : 앨범, 카메라에서 받아온 이미지 담는 변수
    //ivRecall:작성했던 데이터 불러오기 버튼
    ImageView ivSave, ivSetPhoto;
    //카메라, 앨범 버튼 변수
    TextView tvPickPhoto;
    TextView tvRecall;
    //맛집 별 등급
    RatingBar ratingBar;
    //별등급 데이터를 받는 변수
    TextView tvRatingScale;
    //사용자가 입력한 별등급 확인 변수
    float ratingValue;

    //카메라, 앨범의 uri
    private Uri uriAlbum;

    //쉐어드 여러 변수를 담을 변수
    String valuePack;

    //앨범에 담을 Bitmap 형식
    Bitmap bitmapAlbum;
    //카메라로 가져온 Bitmap 형식
    Bitmap bitmapCamera;
    //맛집 작성 쉐어드프리퍼런스
    private static final String PREFS_POST = "postWritingPrefs";

    //쉐어드
    SharedPreferences sharedDataPost;

    //사용자가 입력한 데이터 받는 변수
    //제목, 내용, 주소, 연락처
    private EditText edTitle, edContent, edAddress, edPhoneNumber;


    //카메라로 사진 찍어 가져오기
    final static int TAKE_PICTURE = 10;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 10;

    //앨범에서 사진 가져오기
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    public static final int GALLERY_INTENT_CALLED = 100;
    public static final int GALLERY_KITKAT_INTENT_CALLED = 101;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
          Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 1");

//          SharedPreferences sharedPreferences1 = getSharedPreferences("array" , MODE_PRIVATE);
//          SharedPreferences.Editor jsonEditor1 = sharedPreferences1.edit();
//          jsonEditor1.clear();
//          jsonEditor1.commit();
          //참조
          init();
          //맛집 작성 데이터 인텐트로 전달
          savePost();
          //ratingBar 별 등급 조절
          tasteScore();
        Log.d("생명주기", "WriteActivity" + ": onCreate");


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
        //사진 가져오기
        tvPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items = {"카메라", "앨범", "취소하기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);

                builder.setTitle("사진 추가")

                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정

                            public void onClick(DialogInterface dialog, int index){
                                switch (index){
                                    //카메라 선택
                                    //카메라 인텐트를 실행하는 부분
                                    case 0:
                                        dispatchTakePictureIntent();
                                        break;
                                    //앨범에서 사진 가져오기
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
                                    //나가기
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

        //리콜버튼을 누르면 불러오기 기능
          //onCreat에서 불러오기
          //쉐어드에 저장한 값이 있으면 불러오고 없으면 토스트메세지로 불러올 값이 없다고 띄운다
        tvRecall.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  sharedDataPost = getSharedPreferences(PREFS_POST , MODE_PRIVATE);
                  if(sharedDataPost == null){
                      Toast.makeText(WriteActivity.this, "저장된 내용이 없습니다", Toast.LENGTH_LONG).show();
                      Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 2");
                  }
                  else{
                      Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 객체 존재");
                      AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                      builder.setTitle("이전에 작성한 내용을 불러오겠습니까?");
                      builder.setCancelable(true);
                      builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int which) {

                              //쉐어드에 저장한 String 값을 result 변수로 받는다
                              String result = sharedDataPost.getString("postWrite", "");
                              //쉐어드로 기존에 데이터 이어쓰기
                              Log.d("쉐어드", "WriteActivity" + ": onCreate" + result + " : 쉐어드 객체 " + sharedDataPost);
                              String[] splitStr = result.split("#");

                              //result에 저장한 값이 존재하면 데이터를 화면에 보여준다
                              if (result != null && result != "") {
                                  Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 불러오기");
                                  String getTitle = splitStr[0];
                                  String getRating = splitStr[1];
                                  String getContent = splitStr[2];
                                  String getAddress = splitStr[3];
                                  String getPhone = splitStr[4];
                                  String getPhoto = splitStr[5];
                                  Log.d("쉐어드", "WriteActivity" + ": onCreate : " + result + " : 별등급 :" + getRating + "/ 사진 uri(getPhoto) : " + getPhoto);
                                  float resultRating = Float.valueOf(getRating);

                                  edTitle.setText(getTitle);
                                  ratingBar.setRating(resultRating);
                                  edContent.setText(getContent);
                                  ivSetPhoto.setImageURI(Uri.parse(getPhoto));
                                  edAddress.setText(getAddress);
                                  edPhoneNumber.setText(getPhone);
                              }
                              else if(result.equals("#0.0####null")){
                                  Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 불러오기 실패" + " : #0.0####null");

                                  Toast.makeText(WriteActivity.this, "저장된 내용이 없습니다", Toast.LENGTH_LONG).show();
                                  ratingValue = 0;
                              }
                              else {
                                  Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 불러오기 실패" );
                                  Toast.makeText(WriteActivity.this, "저장된 내용이 없습니다", Toast.LENGTH_LONG).show();
                                  ratingValue = 0;
                              }

                          }
                      });
                      builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int which) {
                              dialogInterface.cancel();
                          }
                      });
                      AlertDialog alertDialog = builder.create();
                      alertDialog.show();
                  }
              }
          });
          Log.d("쉐어드", "WriteActivity"+ ": onCreate" + ": 메소드 흐름 6");

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
                finish();
            }
        });
        alert_ex.setTitle("맛집작성 중 입니다");
        AlertDialog alert = alert_ex.create();
        alert.show();

    }

        //사진을 받아서 이미지 뷰에 넣는다
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

    //카메라로 찍은 사진 원본
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
    //이미지를 돌려주는 함수
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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

    //참조
    private void init(){
        //세이브 버튼
        ivSave = findViewById(R.id.write_postSave_iv);
        //작성내용 불러오기 버튼
        tvRecall = findViewById(R.id.write_recall_tv);
        //사용자가 입력할 EditText와 변수 참조
        edTitle =findViewById(R.id.write_title_et);
        edContent =findViewById(R.id.write_content_et);
        edAddress = findViewById(R.id.write_address_et);
        edPhoneNumber = findViewById(R.id.write_phoneNumber_et);

        //앨범에서 가져온 사진을 담는다
        ivSetPhoto = findViewById(R.id.write_photo_iv);

        //별 등급, 등급에 따른 텍스트 참조
        ratingBar = findViewById(R.id.write_ratingBar);
        tvRatingScale = findViewById(R.id.write_rating_tv);


        //카메라 버튼, 앨범 버튼 레이아웃과 변수 연결
        tvPickPhoto = findViewById(R.id.pick_photo_tv);
    }


    //오른쪽 상단 저장버튼:맛집작성을 마치고 버튼을 누르면 NoteActivity로 데이터 전달
    private void savePost(){
        ivSave.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  //포스트 내용 미작성시 토스트
                  if(edTitle.getText().toString().isEmpty()){
                      Toast.makeText(WriteActivity.this, "제목을 작성해주세요", Toast.LENGTH_LONG).show();
                  }
                  else if(ratingValue == 0 || ratingValue >= 6){
                      Toast.makeText(WriteActivity.this, "점수를 매겨주세요", Toast.LENGTH_LONG).show();
                  }
                  else if(edContent.getText().toString().isEmpty()){
                      Toast.makeText(WriteActivity.this, "내용을 작성해주세요", Toast.LENGTH_LONG).show();
                  }
                  //uriAlbum == null : 앨범의 사진을 선택하지 않은경우 null, 앨범의 사진이 uri 주소로 받아지기 때문에 이미지를
                  //선택하지 않은경우 null처리한다
//                  else if(bitmapCamera == null || bitmapAlbum == null){
//                      Toast.makeText(WriteActivity.this, "이미지를 채워주세요", Toast.LENGTH_LONG).show();
//                  }
                  else if(edAddress.getText().toString().isEmpty()){
                      Toast.makeText(WriteActivity.this, "주소를 작성해주세요", Toast.LENGTH_LONG).show();
                  }
                  else if(edPhoneNumber.getText().toString().isEmpty()){
                      Toast.makeText(WriteActivity.this, "연락처를 작성해주세요", Toast.LENGTH_LONG).show();
                  }

                  //포스트 작성완료 데이터 전달
                  else {
                      if(bitmapCamera != null) {
                          getImageUri(getApplicationContext(), bitmapCamera);
                      }
                          //사용자가 입력한 데이터를 NoteItem 객체에 담는다
                          NoteItem noteItem = new NoteItem(edTitle.getText().toString(), ratingValue , uriAlbum.toString(),
                                  edAddress.getText().toString(), edPhoneNumber.getText().toString(), edContent.getText().toString());
                          //NoteActivity 로 데이터 전달
                          Intent postIntent = new Intent();
                          postIntent.putExtra("data", noteItem);
                          setResult(RESULT_OK, postIntent);
                          //엑티비티 종료
                          finish();

                          //정상적으로 포스트 작성이 완료되면 쉐어드에 저장값을 삭제한다
//                          sharedDataPost = getSharedPreferences(PREFS_POST, MODE_PRIVATE);
//                          SharedPreferences.Editor editor = sharedDataPost.edit();
//                          editor.clear();
//
//                          editor.commit();

                          Log.d("쉐어드", "WriteActivity" + ": 작성완료 :" + ": 쉐어드 객체 : " + sharedDataPost);

                          Toast.makeText(getApplicationContext(), "성공적으로 작성하셨습니다.", Toast.LENGTH_SHORT).show();

//                      Intent intent = new Intent(WriteActivity.this , NoteActivity.class);
//                      intent.putExtra("title", edTitle.getText().toString());
//                      intent.putExtra("rating", ratingValue);
//                      intent.putExtra("address" , edAddress.getText().toString());
//                      intent.putExtra("phone" , edPhoneNumber.getText().toString());
//                      Log.d("생명주기", "WriteActivity" + "인텐트 : " + "레이팅 : "+ ratingValue);
//
//                      intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                      startActivity(intent);
//                      Log.d("생명주기", "WriteActivity" + "인텐트 : " + "타이틀 : "+ intent.getStringExtra("title"));
//                      Log.d("생명주기", "WriteActivity" + "인텐트 : " + "레이팅 : "+ intent.getFloatExtra("rating",0));
//                      finish();
                  }
              }
          });
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



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("생명주기", "WriteActivity" + ": onStart");
        Log.d("쉐어드", "WriteActivity" + ": onStart" +"사진 uri(uriAlbum) :  " + uriAlbum );
       }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("생명주기", "WriteActivity" + ": onResume");

        //쉐어드에 저장한 데이터를 WriteActivity가 새로 만들어질 때 마다 불러오기 위해서 조건문 사용
//        SharedPreferences sharedDataPost = getSharedPreferences(PREFS_POST , MODE_PRIVATE);
//        Log.d("쉐어드", "WriteActivity" + ": onResume" + ": 쉐어드 객체 : " + sharedDataPost);
//        if(sharedDataPost != null && sharedDataPost.contains("postWrite")) {
//            //쉐어드에 저장한 String 값을 result 변수로 받는다
//            String result = sharedDataPost.getString("postWrite", "");
//            //쉐어드로 기존에 데이터 이어쓰기
//            Log.d("쉐어드", "WriteActivity" + ": onResume" + result + " : 쉐어드 객체 " + sharedDataPost);
//            String[] splitStr = result.split("#");
//
//            //result에 저장한 값이 존재하면 데이터를 화면에 보여준다
//            if (result != null && result != "" && uriAlbum == null) {
//
//                String getTitle = splitStr[0];
//                String getRating = splitStr[1];
//                String getContent = splitStr[2];
//                String getAddress = splitStr[3];
//                String getPhone = splitStr[4];
//                String getPhoto = splitStr[5];
//                Log.d("쉐어드", "WriteActivity" + ": onResume" + result + " : 별등급 :" + getRating + "/ 사진 uri(getPhoto) : " + getPhoto);
//                float resultRating = Float.valueOf(getRating);
//
//                edTitle.setText(getTitle);
//                ratingBar.setRating(resultRating);
//                edContent.setText(getContent);
//                ivSetPhoto.setImageURI(Uri.parse(getPhoto));
//                edAddress.setText(getAddress);
//                edPhoneNumber.setText(getPhone);
//            } else {
//
//            }
//        }
        Log.d("쉐어드", "WriteActivity" + ": onResume" +"사진 uri(uriAlbum) :  " + uriAlbum );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("생명주기", "WriteActivity" + ": onPause");

        //앨범에서 받아온 uri값을 쉐어드에 저장하려한다
        //현재 엑티비티에서 앨범 앱으로 전환되었다가 다시 현재 엑티비티가 실행되어야 하기에
        //생명주기는 onPause - > onStop -> onRestart -> onStart -> onResume 변화한다
        //앨범의 uri 값은 onRestart 주기에서 받아온다

        //맛집 작성 Activity가 화면에서 가려지면 작성된 데이터를 쉐어드(postPrefs.xml)에 저장한다
        //MODE_PRIVATE : 이 앱안에서 데이터 공유
        sharedDataPost = getSharedPreferences(PREFS_POST , MODE_PRIVATE);
        // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언
        SharedPreferences.Editor editor = sharedDataPost.edit();
        //사용자가 입력한 데이터를 저장
        if(!edTitle.equals("") && !edAddress.equals("") && ratingValue <6 && uriAlbum != null && !edPhoneNumber.equals("") && !edContent.equals("")){

        }else {

            String title = edTitle.getText().toString();
            String rating = String.valueOf(ratingValue);
            String content = edContent.getText().toString();
            String address = edAddress.getText().toString();
            String phone = edPhoneNumber.getText().toString();
            String photo = String.valueOf(uriAlbum);

            //한키에 여러 값 넣기
            valuePack = title + "#" + rating + "#" + content + "#" + address + "#" + phone + "#" + photo;
            //key, value를 이용하여 저장
            editor.putString("postWrite", valuePack);
            //메모리에 있는 데이터를 저장장치(postPrefs.xml)에 저장
            editor.commit();
        }
        Log.d("쉐어드", "WriteActivity" + ": onPause" +"사진 uri(uriAlbum) :  " + uriAlbum );

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("생명주기", "WriteActivity" + ": onStop");


        Log.d("쉐어드", "WriteActivity" + ": onStop" +"사진 uri(uriAlbum) :  " + uriAlbum + ": 쉐어드 : " + sharedDataPost);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("생명주기", "WriteActivity" + ": onDestroy");


        Log.d("쉐어드", "WriteActivity" + ": onDestroy" +"사진 uri(uriAlbum) :  " + uriAlbum + ": 쉐어드 : " + sharedDataPost);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("생명주기", "WriteActivity" + ": onRestart");
        Log.d("쉐어드", "WriteActivity" + ": onRestart" +"사진 uri(uriAlbum) :  " + uriAlbum + ": 쉐어드 : " + sharedDataPost);
    }


    @Override
    public void onClick(View view) {

    }
}


//대화상자 플래그먼트 생성: 작성중인 페이지를 불러올지 새로 작성할지 여부를 확인
//public class continueWriteDialogFragment extends DialogFragment{
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState){
//        //Builder 클래스를 사용하여 대화 상자 구성
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.writeDialog_title)
//                .setPositiveButton(R.string.writeDialog_yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int id) {
//
//                        ivSave = findViewById(R.id.write_done_iv);
//                        edTitle =findViewById(R.id.write_title_et);
//                        edScore = findViewById(R.id.write_et);
//                        edContent =findViewById(R.id.write_content_et);
//                        edAddress = findViewById(R.id.write_address_et);
//                        edPhoneNumber = findViewById(R.id.write_phoneNumber_et);
//                        tvPhoto = findViewById(R.id.write_photo_tv);
//
//
//                        //저장된 값을 불러오기 위해 같은 네임파일을 찾음
//                        SharedPreferences shared = getSharedPreferences("writeSharedFile" , MODE_PRIVATE);
//                        //key에 저장된 값이 있는지 확인, 아무값도 들어있지 않으면 ""를 반환
//                        String title = shared.getString("title" , "");
//
//                        String score = shared.getString("score", "");
//                        String content = shared.getString("content" , "");
//                        String address = shared.getString("address" , "");
//                        String phone = shared.getString("phone", "");
//
//                        //화면에 보여지는 뷰에 값을 넣음
//                        edTitle.setText(title);
//                        edScore.setText(score);
//                        edContent.setText(content);
//                        edAddress.setText(address);
//                        edPhoneNumber.setText(phone);
//
//
//                    }
//                })
//                .setNegativeButton(R.string.writeDialog_no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int id) {
//
//                        SharedPreferences shared = getSharedPreferences("writeSharedFile" , MODE_PRIVATE);
//                        SharedPreferences.Editor editor = shared.edit();
//                        editor.clear();
//                        editor.commit();
//
//                    }
//                });
//        return builder.create();
//
//    }
//}


//    // 이미지를 문자열로 바꾸는 메소드
//    public String BitmapToString(Bitmap bitmapAlbum){
//          ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmapAlbum.compress(Bitmap.CompressFormat.PNG , 100 , stream);
//        byte[] bytes = stream.toByteArray();
//        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
//        return temp;
//    }

//쉐어드에 저장된 값을 불러올지 말지 결정하는 메소드
//    private void continueWrite() {
//        //데이터를 저장하거나 읽어올 XML 파일에 대한 정보를 얻어온다
//        SharedPreferences shared = getSharedPreferences("writeSharedFile" , MODE_PRIVATE);
//        String title = shared.getString("title", null);
//        String score = shared.getString("score", null);
//        String content = shared.getString("content", null);
//        String address = shared.getString("address", null);
//        String phone = shared.getString("phone", null);
//
//        Log.d("쉐어드", "continueWrite"+ "이프문 밖" + " 제목 : " +title);
//        //쉐어드에 값이 없는 경우
//        if(title == "" && score == "" && content == "" && address == "" && phone == "") {
//
//            Toast.makeText(getApplicationContext(), "맛집을 작성해보세요", Toast.LENGTH_SHORT).show();
//            Log.d("쉐어드", "continueWrite"+ "쉐어드에 값이 없는 경우 " + " 제목 : " +title);
//        }
//        //쉐어드에 값이 한개라도 있는 경우
//        else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.writeDialog_title);
//            builder.setPositiveButton(R.string.writeDialog_yes, new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialogInterface, int id) {
//
//                    ivSave = findViewById(R.id.write_postSave_iv);
//                    edTitle = findViewById(R.id.write_title_et);
//
//                    edContent = findViewById(R.id.write_content_et);
//                    edAddress = findViewById(R.id.write_address_et);
//                    edPhoneNumber = findViewById(R.id.write_phoneNumber_et);
//
//                    //저장된 값을 불러오기 위해 같은 네임파일을 찾음
//                    SharedPreferences shared = getSharedPreferences("writeSharedFile", MODE_PRIVATE);
//                    //key에 저장된 값이 있는지 확인, 아무값도 들어있지 않으면 ""를 반환
//                    String title = shared.getString("title", "");
//                    String score = shared.getString("score", "");
//                    String content = shared.getString("content", "");
//                    String address = shared.getString("address", "");
//                    String phone = shared.getString("phone", "");
//
//                    //화면에 보여지는 뷰에 값을 넣음
//                    edTitle.setText(title);
////                    edScore.setText(score);
//                    edContent.setText(content);
//                    edAddress.setText(address);
//                    edPhoneNumber.setText(phone);
//                    Log.d("쉐어드", "continueWrite"+ "불러오기" + " 제목 : " +title);
//
//                }
//            });
//            //아니요를 누르면 쉐어드에 저장된 값을 지운다
//            builder.setNegativeButton(R.string.writeDialog_no, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int id) {
//                    SharedPreferences shared = getSharedPreferences("writeSharedFile", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = shared.edit();
////                    editor.remove("title");
////                    editor.remove("score");
////                    editor.remove("content");
////                    editor.remove("address");
////                    editor.remove("phone");
//                    editor.clear();
//                    editor.commit();
//                    Log.d("쉐어드", "continueWrite"+ "값을 지운다" + " 제목 : " + shared.getString("writeSharedFile" , "") );
//                }
//            });
//            builder.show();
//        }
//
//    }

//    private void sendArray(){
//        //사용자가 입력한 데이터를 저장
//        //JSON 이용하여 데이터 저장
//        JSONObject wrapJsonObject = new JSONObject();
//        JSONArray jsonArrayWrite = new JSONArray();
//        //사용자 입력 데이터 -> jsonObjectWrite -> jsonArrayWrite -> wrapJsonObject
//        // -> 최종 jsonWrapShred 쉐어드에 저장
//        try {
//
//            JSONObject jsonObjectWrite = new JSONObject();
//
//            jsonObjectWrite.put("jsonTitle", edTitle.getText().toString());
//            jsonObjectWrite.put("jsonRating", String.valueOf(ratingValue));
//            jsonObjectWrite.put("jsonUriAlbum", String.valueOf(uriAlbum));
//            jsonObjectWrite.put("jsonAddress", edAddress.getText().toString());
//            jsonObjectWrite.put("jsonPhone", edPhoneNumber.getText().toString());
//
//            jsonArrayWrite.put(jsonObjectWrite);
////                  wrapJsonObject.put("itemWrap", jsonArrayWrite);
//
//            wrapJsonObject.put("list",jsonArrayWrite);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//            //쉐어드 선언
//            jsonWrapShred = getSharedPreferences("jsonItemShared", MODE_PRIVATE);
//            SharedPreferences.Editor jsonEditor = jsonWrapShred.edit();
//
//            jsonEditor.putString("jsonKey" , wrapJsonObject.toString());
//
//            jsonEditor.commit();
//    }