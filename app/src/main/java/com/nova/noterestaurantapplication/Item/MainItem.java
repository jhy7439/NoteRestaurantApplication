package com.nova.noterestaurantapplication.Item;

public class MainItem {

    //음식 사진 메인
    private int foodPhoto;
    //제목
    private String title;
    //소제목
    private String subTitle;
    //음식 소개 내용
    private String content;
    //주소
    private String address;
    //전화번호
    private String phone;

    public int getFoodPhoto(){
        return foodPhoto;
    }
    public String getTitle(){
        return title;
    }
    public String getSubTitle(){
        return subTitle;
    }
    public String getContent(){
        return content;
    }
    public String getPhone(){
        return phone;
    }
    public String getAddress(){
        return address;
    }


    public MainItem(int foodPhoto , String title , String subTitle, String content, String phone, String address){
        this.foodPhoto = foodPhoto;
        this.title = title;
        this.subTitle = subTitle;
        this.content =content;
        this.phone = phone;
        this.address = address;
    }






}
