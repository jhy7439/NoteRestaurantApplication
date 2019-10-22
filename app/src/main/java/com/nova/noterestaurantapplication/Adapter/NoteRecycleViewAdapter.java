package com.nova.noterestaurantapplication.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.nova.noterestaurantapplication.Activity.EditNoteActivity;
import com.nova.noterestaurantapplication.Activity.NoteActivity;
import com.nova.noterestaurantapplication.Activity.ShowActivity;
import com.nova.noterestaurantapplication.Item.NoteItem;
import com.nova.noterestaurantapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

//List의 셀 하나하나의 맞게 데이터를 가공한다


public class NoteRecycleViewAdapter extends RecyclerView.Adapter<NoteRecycleViewAdapter.NoteViewHolder> {

    //각 아이템에 들어갈 데이터리스트
    public List<NoteItem> noteItemDataSet;
    Context context;



    //생성자
    public NoteRecycleViewAdapter(Context context ,int resouce, List<NoteItem> itemList ){
        //
        this.noteItemDataSet = itemList;
        this.context = context;
    }

    //ViewHolder에 설정한 View 안에 있는 위젯들에 데이터를 세팅한다
    //화면에 셀이 그려질 때 마다 호출됨

    //넘겨 받은 데이터를 화면에 출력하는 역할
    //재활용 되는 뷰가 호출하여 실행되는 메소드
    //뷰 홀더를 전달하고 어댑터는 position의 데이터를 결합
    //인자를 통해 전달된 ViewHolder 객체에 position에 기반한 데이터를 표시
    @Override
    public void onBindViewHolder(NoteViewHolder noteViewHolder, int position){
        //위치를 기준으로 데이터 모델 가져오기
        noteViewHolder.tvTitle.setText(noteItemDataSet.get(position).getTitle());
        noteViewHolder.ratingBar.setRating(noteItemDataSet.get(position).getRating());
        noteViewHolder.tvAddress.setText(noteItemDataSet.get(position).getAddress());
        noteViewHolder.tvPhone.setText(noteItemDataSet.get(position).getPhone());
        noteViewHolder.ivSetPhoto.setImageURI(Uri.parse(noteItemDataSet.get(position).getPhoto()));

    }

    //NoteViewHolder:construct()
    //리사이클러뷰 한개 아이템에 있는 View와 데이터를 담는 변수 연결
    public class NoteViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle , tvAddress, tvPhone;
        public RatingBar ratingBar;
        //앨범에서 가져온 이미지를 놓는다
        public ImageView ivSetPhoto;
        public ImageView ivEdit;

        //생성자로 뷰를 넘겨주고 super를 했다는 것은 ViewHoler는 View에 대한 참조를 유지한다는 것
        //NoteViewHolder의 생성자로 넘어온 itemView는 ViewHolder.itemView에 저장
        //ViewHolder 객체를 통해 접근이 가능
        public NoteViewHolder(View itemView){
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.noteItem_title_tv);
            ratingBar = (RatingBar) itemView.findViewById(R.id.noteItem_ratingBar);
            ivSetPhoto =(ImageView) itemView.findViewById(R.id.noteItem_iv);
            tvAddress = (TextView) itemView.findViewById(R.id.noteItem_address_tv);
            tvPhone = (TextView) itemView.findViewById(R.id.noteItem_phone_tv);
            ivEdit = (ImageView) itemView.findViewById(R.id.noteItem_edit_iv);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //아이템 삭제 전 삭제확인 버튼
                    //view.getContext() 로 안하면 오류 발생
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("맛집 포스트 삭제 확인 메세지");
                    builder.setMessage("삭제 하시겠습니까?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int pos = getAdapterPosition();
                                    //NoteActivity 리사이클러뷰 아이템 삭제
                                    removeItem(pos);
                                }
                            });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
                }
            });
            //아이템 전체 클릭시 맛집 포스트를 보여준다 ==> ShowActivity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Intent showIntent = new Intent(context , ShowActivity.class);
                    showIntent.putExtra("edit_title", noteItemDataSet.get(pos).getTitle());
                    showIntent.putExtra("edit_rating", noteItemDataSet.get(pos).getRating());
                    showIntent.putExtra("edit_photo", noteItemDataSet.get(pos).getPhoto());
                    showIntent.putExtra("edit_address", noteItemDataSet.get(pos).getAddress());
                    showIntent.putExtra("edit_phone", noteItemDataSet.get(pos).getPhone());
                    showIntent.putExtra("edit_content", noteItemDataSet.get(pos).getContent());
                    //
                    showIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(showIntent);
                }
            });
            //아이템 에디터 이미지 클릭시 EditNoteActivity로 이동, 아이템 데이터 포함
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   int pos = getAdapterPosition();
                   Intent editIntent = new Intent(context , EditNoteActivity.class);
                    editIntent.putExtra("edit_title", noteItemDataSet.get(pos).getTitle());
                    editIntent.putExtra("edit_rating", noteItemDataSet.get(pos).getRating());
                    editIntent.putExtra("edit_photo", noteItemDataSet.get(pos).getPhoto());
                    editIntent.putExtra("edit_address", noteItemDataSet.get(pos).getAddress());
                    editIntent.putExtra("edit_phone", noteItemDataSet.get(pos).getPhone());
                    editIntent.putExtra("edit_content", noteItemDataSet.get(pos).getContent());
                    editIntent.putExtra("position", getAdapterPosition());

                    editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(editIntent);
                    //NoteActivity 를 종료 시키면 온크리에이트에서 변경된 쉐어드의 값을 다시 불러옴
                    ((Activity)context).finish();

                    //어댑터 안에서 startActivityForResult 사용할 땐 ((Activity)context). 붙여야 함
                    //((Activity)context).startActivityForResult(editIntent, 3001);

                }
            });

        }
    }

    //아이템 제거 메소드
    //쉐어드에 저장 된 아이템 값 삭제
    //어댑터 리스트(noteItemDataSet)에서 삭제한 데이터를 제외 하여 쉐어드에 다시 저장
    public void removeItem(int position) {
        NoteItem removeNoteItem = noteItemDataSet.get(position);
        Log.d("쉐어드", "NoteRecycleViewAdapter" + ": removeItem" +" noteItemDataSet :"+"삭제 전 : "+ noteItemDataSet);

        //리사이클러뷰 position값 위치의 아이템 noteItemDataSet 리스으틔 데이터 삭제
        noteItemDataSet.remove(position);
        //리사이클러뷰 position값 위치의 아이템삭제
        notifyItemRemoved(position);
        //리사이클러뷰 아이템 개수 위치 조정
        notifyItemRangeChanged(position, noteItemDataSet.size());

        //삭제 적용된 리사이클러뷰 아이템 목록을 쉐어드에 저장한다
        SharedPreferences sharedDataPost = context.getSharedPreferences("noteList" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedDataPost.edit();
        try {
            //배열
            JSONArray adapterJsonArray = new JSONArray();
            for (int i = 0; i < noteItemDataSet.size(); i++) {
                //배열 내에 들어갈 json 오브젝트
                JSONObject sObject = new JSONObject();
                sObject.put("titleStr", noteItemDataSet.get(i).getTitle());
                sObject.put("ratingFlo", noteItemDataSet.get(i).getRating());
                sObject.put("photoStr", noteItemDataSet.get(i).getPhoto());
                sObject.put("addressStr", noteItemDataSet.get(i).getAddress());
                sObject.put("phoneStr", noteItemDataSet.get(i).getPhone());
                sObject.put("contentStr", noteItemDataSet.get(i).getContent());

                adapterJsonArray.put(sObject);
            }
            editor.putString("jsonDataStr", adapterJsonArray.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("쉐어드", "NoteRecycleViewAdapter" + ": removeItem" +" noteItemDataSet :"+"삭제 후 : "+ noteItemDataSet);

    }


    //리스트의 한 셀에 보여질 뷰를 생성(inflation) , 반환 값은 Holer에 뷰를 붙인 형태
    //리스트 내의 항목을 표시하기 위한 View를 생성하고, 해당 뷰를 관리(hold)할 ViewHolder를 생성하여 리턴
    @Override
    public NoteViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //뷰인 noteView와 레이아웃xml인 item_note를 인플레이트
        View noteView = inflater.inflate(R.layout.item_note, parent, false);

        //noteViewHolder에 noteView를 넣어줌
       NoteViewHolder noteViewHolder = new NoteViewHolder(noteView);

        return noteViewHolder;
    }


    //데이터의 개수 반환
    @Override
    public int getItemCount(){
        return noteItemDataSet.size();
    }

}



//        noteItem = noteItemDataSet.get(position);
//
//        noteViewHolder.tv_title.setText(noteItem.getTitle());
//        noteViewHolder.ratingBar.setRating(noteItem.getRating());
//        noteViewHolder.tv_address.setText(noteItem.getAddress());
//        noteViewHolder.tv_phone.setText(noteItem.getPhone());

//        //텍스트 뷰를 생성하고 뷰홀더에 xml파일과 연결?
//        TextView titleTextView = noteViewHolder.tv_title;
//        RatingBar ratingBar = noteViewHolder.ratingBar;
//        TextView addressTextView = noteViewHolder.tv_address;
//        TextView phoneTextView = noteViewHolder.tv_phone;
//        //덱스트 뷰에 데이터를 넣어준다
//        titleTextView.setText(noteItem.getTitle());
//        ratingBar.setRating(noteItem.getRating());
//        addressTextView.setText(noteItem.getAddress());
//        phoneTextView.setText(noteItem.getPhone());

//        noteViewHolder.tv_title.setText(noteList.get(position).getTitle());
//        noteViewHolder.ratingBar.setRating(noteList.get(position).getRating());
//        noteViewHolder.tv_address.setText(noteList.get(position).getAddress());
//        noteViewHolder.tv_phone.setText(noteList.get(position).getPhone());



//아이템 클릭 이벤트
//        if(noteListener != null){
//final int pos = position;
//        noteViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//@Override
//public boolean onLongClick(View view) {
//        noteListener.onItemClicked(pos);
//final View dialogView = LayoutInflater.from(context)
//        .inflate(R.layout.dialog_delete_item , null , false);
//
//final TextView dialogTitle = (TextView)dialogView.findViewById(R.id.dialog_title_tv);
//
//        dialogTitle.setText("맛집 포스트를 삭제 하시겠습니까?");
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setView(dialogView);
//
//        builder.setCancelable(true)
//        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialogInterface, int whichButton) {
//        removeItem(pos);
//        }
//        })
//        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialogInterface, int whichButton) {
//        dialogView.cancelLongPress();
//        }
//        });
//        AlertDialog dialog =builder.create();
//        dialog.show();
//        return true;
//        }
//        });
//        }


//아이템 제거 메소드
//쉐어드 저장 값도 삭제
//어댑터 리스트(noteItemDataSet)의 삭제한 데이터를 제외 하여 쉐어드에 다시 저장
//    public void removeItem(int position){
//        NoteItem removeNoteItem = noteItemDataSet.get(position);
//        Log.d("쉐어드", "NoteRecycleViewAdapter" + ": removeItem" + removeNoteItem.getNoteSeq());
//
//        //리사이클러뷰에서는 아이템이 삭제되어 보인다
//        noteItemDataSet.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position , noteItemDataSet.size());
//
//        //리사이클러뷰에 뿌려질 데이터를 저장한 쉐어드의 데이터를 불러와서
//        //삭제된 아이템의 위치를 찾고 remove메소드로 삭제
//        SharedPreferences sharedDataPost = context.getSharedPreferences("noteList", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedDataPost.edit();
//        //맵은 쉐어드의 전체 값를 가지고 있다
//        //쉐어드 전체 값을 jsonNoteList 에 담겠다
//        Map<String,?> jsonNoteList = sharedDataPost.getAll();
//        //키값 String , ?:값은 어떠한 타입이 와도 괜찮다
//        for(Map.Entry<String,?> entry : jsonNoteList.entrySet()){
//            String jsonString = entry.getValue().toString();
//            try {
//                JSONObject jsonObject =  new JSONObject(jsonString);
//                //아이템의 위치를 noteSeq에 받는다
//                String noteSeq = jsonObject.get("noteSeq").toString();
//                //noteSeq 위치 값에 해당하는 데이터를 지운다
//                editor.remove(noteSeq);
//                if(!noteSeq.equals(removeNoteItem.getNoteSeq()+"")){
//                    editor.putString(noteSeq, jsonObject.toString());
//                    editor.apply();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        editor.commit();
//
//        Log.d("쉐어드", "NoteRecycleViewAdapter" + ": removeItem" +"제거 마침"+ noteItemDataSet);
//    }

    //int -> String 형변환
  //  String whichStr = Integer.toString(position);