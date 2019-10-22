package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nova.noterestaurantapplication.R;

public class LoginActivity extends AppCompatActivity {


    //requestCode를 만들어 준다
    static final int loginCode = 0;

    Button loginButton, singUpButton;
    EditText idInput, pwInput;

    static String passWordIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //로그인 , 회원가입 버튼
        loginButton = findViewById(R.id.logIn_btn);
        singUpButton = findViewById(R.id.signUp_btn);

        //아이디와 비밀번호 입력란
        idInput = findViewById(R.id.id_et);
        pwInput = findViewById(R.id.pw_et);

        //로그인 버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("LoginActivity",  "온 클릭  "+"비밀번호 값 : " + pwInput + "비밀번호 확인 값 : " + passWordIntent);

                if(!pwInput.getText().toString().equals(passWordIntent)){
                    Log.d("LoginActivity", "이프문  "+"비밀번호 값 : " + pwInput + "비밀번호 확인 값 : " + passWordIntent);

                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();

                }
                else{
                    Log.d("LoginActivity", "엘스  "+"비밀번호 값 : " + pwInput + "비밀번호 확인 값 : " + passWordIntent);
                    Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //회원가입 버튼 눌렀을 때
        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, LoginRegisterActivity.class);
                //엑티비티에 정보를 보낸다
                startActivityForResult(registerIntent, loginCode);

            }
        });

    }

    //아이디 ,비밀번호 값을 받아오는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == loginCode && resultCode == RESULT_OK) {
            idInput.setText(data.getStringExtra("id"));
            passWordIntent = data.getStringExtra("pw");

            Log.d("LoginActivity", "아이디 값 : " + idInput + "비밀번호 값 : " + passWordIntent);
        }


    }

}
