package com.nova.noterestaurantapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nova.noterestaurantapplication.R;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText etID, etPassWord, etPassWordConfirm;

    private Button btnDone;

    //아이디, 비밀번호를 입력 한 값을 LoginActivity로 보냄
    //비밀번호와 비밀번호 확인란의 값이 같아야 LoginActivity로 이동
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        etID = findViewById(R.id.registerId_et);
        etPassWord = findViewById(R.id.registerPw_et);
        etPassWordConfirm = findViewById(R.id.reRegisterPw_et);
        btnDone = findViewById(R.id.register_bt);


        // 비밀번호 일치 검사
        etPassWordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = etPassWord.getText().toString();
                String confirm = etPassWordConfirm.getText().toString();

                if(password.equals(confirm)) {
                    etPassWord.setBackgroundColor(Color.GREEN);
                    etPassWordConfirm.setBackgroundColor(Color.GREEN);
                }else{
                    etPassWord.setBackgroundColor(Color.RED);
                    etPassWordConfirm.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아이디 입력 확인
                if(etID.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    //아이디 입력자리에 포커스 준다
                    etID.requestFocus();
                    return;
                }
                //비밀번호 입력 확인
                if(etPassWord.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    //아이디 입력자리에 포커스 준다
                    etPassWord.requestFocus();
                    return;
                }
                //비밀번호 확인란 입력 확인
                if(etPassWordConfirm.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요", Toast.LENGTH_SHORT).show();
                    //아이디 입력자리에 포커스 준다
                    etPassWordConfirm.requestFocus();
                    return;
                }
                //비밀번호와 비밀번호 확인란 입력 확인
                if(!etPassWord.getText().toString().equals(etPassWordConfirm.getText().toString())){

                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    //아이디 입력자리에 포커스 준다
                    //
                    etPassWord.setText("");
                    etPassWordConfirm.setText("");
                    etPassWord.requestFocus();
                    return;
                }
                Intent resultIntent = new Intent();
                //resultIntent에 아이디와 비밀번호 입력 값을 받는다
                resultIntent.putExtra("id", etID.getText().toString());
                resultIntent.putExtra("pw",etPassWord.getText().toString());
                //LoginActivity로 아이디와 비밀번호 입력받은 값을 보낸다
                setResult(RESULT_OK , resultIntent);
                finish();
            }
        });
    }
}