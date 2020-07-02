package team4.afinal.aclass.team4finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;

import org.pixsee.fcm.Message;
import org.pixsee.fcm.Sender;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.bean.GiverBean;
import team4.afinal.aclass.team4finalproject.bean.NotiBean;
import team4.afinal.aclass.team4finalproject.fcm.MyFirebaseMessagingService;
import team4.afinal.aclass.team4finalproject.util.Utils;

public class Activity5 extends AppCompatActivity {

    private ImageView imgGiver;
    private TextView txtName, txtField, txtPay, txtCareer, txtContents;
    public static List<GiverBean> mFoundBeanList = new ArrayList<GiverBean>();


    private GiverBean mGiverBean;


    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5);




        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        imgGiver = findViewById(R.id.imgGiver);
        txtName = findViewById(R.id.txtName);
        txtField = findViewById(R.id.txtField);
        txtPay = findViewById(R.id.txtPay);
        txtCareer = findViewById(R.id.txtCareer);
        txtContents = findViewById(R.id.txtContents);

        // DB 에서 정보 받아와 bean에 입력
        mGiverBean = (GiverBean) getIntent().getSerializableExtra("detail");

        try {
            new DownImgTask(imgGiver).execute(new URL(mGiverBean.imgUrl));
        } catch (Exception e){
            e.printStackTrace();
        }


        Log.e("SWU", mGiverBean.key);
        // 상세화면 텍스트를 bean값으로 설정
        txtName.setText(mGiverBean.studentId);
        txtField.setText(mGiverBean.field);
        txtPay.setText(mGiverBean.pay);
        txtCareer.setText(mGiverBean.career);
        txtContents.setText(mGiverBean.contents);

       /* if(mGiverBean != null) {
            Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
            Message message = new Message.MessageBuilder()
                    .toToken(mGiverBean.key) // single android/ios device
                    .addData("title", "글이 등록 되었습니다.")
                    .addData("content", "당신의 글에 댓글이 등록 되었습니다.")
                    .build();
            fcm.send(message);
        }*/

        // 요청 이벤트
       Button btnRequest = findViewById(R.id.btnRequest);
       btnRequest.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //요청하기 눌렀을 때 메시지 보내기
               if(mGiverBean != null) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(Activity5.this);
                   builder.setTitle("요청하기");
                   builder.setMessage( mGiverBean.studentId + "님께 요청하시겠습니까?");
                   builder.setCancelable(false);
                   builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           //프로그레스 바 보이기
                           Utils.showProgress(Activity5.this);

                           //fcm 메시지 보내기
                           Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
                           Message message = new Message.MessageBuilder()
                                   .toToken(mGiverBean.key) // single android/ios device
                                   .addData("title", "회원님이 작성하신 글에 요청이 들어왔습니다.")
                                   .addData("content", mGiverBean.studentId+"회원님의 재능나눔 주기 글에 요청이 들어왔습니다.")
                                   .addData("notiType", "A")
                                   .build();
                           fcm.send(message);
                           upload();

                           upload();

                           //프로그레스 바 숨기기
                           Utils.hideProgress(Activity5.this);
                           Toast.makeText(Activity5.this,mGiverBean.studentId + "님께 요청이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                       }
                   }); // setPositive

                   builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           Toast.makeText(Activity5.this, "요청이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                       }
                   }); // setNegative
                   builder.create().show();
               }
           }
       });

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity5.this, Activity3.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

    } // end onCreate()

    private void upload() {

        DatabaseReference firebaseRef = mDatabase.getReference();

        //Database에 저장한다.
        NotiBean bean= new NotiBean();

        bean.requestID = Activity1.mLoginedInfoBean.num;
        bean.studentID = mGiverBean.studentId;
        bean.kakaoID= Activity1.mLoginedInfoBean.kakaoID;
        bean.notiId = mGiverBean.name;
        bean.category = "1";

        firebaseRef.child("noti").child(bean.studentID).child(bean.notiId).setValue(bean);
        Toast.makeText(Activity5.this, "요청보내기 성공!!",Toast.LENGTH_SHORT).show();

        finish();

    }//end Upload


}