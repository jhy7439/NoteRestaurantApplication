package com.nova.noterestaurantapplication.Item;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;

import com.nova.noterestaurantapplication.Activity.WriteActivity;

import java.io.Serializable;

//엑티비티 간 객체 전달 할 때 get set 메소드가 있는 클래스를 만들고
// 반드시 implements Serializable 해주어야 한다
public class NoteItem implements Serializable {

    private String titleStr;
    private float ratingFlo;
    private String photoStr;
    private String addressStr;
    private String phoneStr;
    private String contentStr;
   // private long noteSeq;

    public NoteItem(String titleStr, float ratingFlo, String photoStr , String addressStr , String phoneStr , String contentStr) {
     //   this.noteSeq = System.currentTimeMillis();
        this.titleStr = titleStr;
        this.ratingFlo = ratingFlo;
        this.photoStr = photoStr;
        this.addressStr = addressStr;
        this.phoneStr = phoneStr;
        this.contentStr = contentStr;
        Log.d("생명주기", "NoteItem" + "생성자 : " + "레이팅 : "+ratingFlo);

    }


    //외부에서 받은 값을 내부로 넣어준다
//    public void setImage(int image){
//        this.imageInt = image;
//    }
    public void setTitle(String title){
        this.titleStr = title;
    }
    public void setPhoto(String photo){
        this.photoStr = photo;
    }
    public void setAddress(String address){
        this.addressStr = address;
    }
    public void setPhone(String phone){
        this.phoneStr = phone;
    }
    public void setRating(Float rating){
        this.ratingFlo = rating;
    }
    public void setContent(String content){
        this.contentStr = content;
    }



    //외부로 값을 리턴해서 보내준다
//    public int getImage(){
//        return this.imageInt;
//    }
    public String getTitle(){
        return this.titleStr;
    }
    public String getPhoto(){
        return this.photoStr;
    }
    public String getAddress(){
        return this.addressStr;
    }
    public String getPhone(){
        return this.phoneStr;
    }
    public float getRating(){
        Log.d("생명주기", "NoteItem" + "겟 : " + "레이팅 : "+ratingFlo);
        return this.ratingFlo;

    }
    public String getContent(){
        return this.contentStr;
    }

//    public long getNoteSeq() {
//        return noteSeq;
//    }
//
//    public void setNoteSeq(long noteSeq) {
//        this.noteSeq = noteSeq;
//    }
}
