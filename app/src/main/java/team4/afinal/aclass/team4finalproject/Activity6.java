package team4.afinal.aclass.team4finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import team4.afinal.aclass.team4finalproject.bean.NotiBean;
import team4.afinal.aclass.team4finalproject.bean.ReceiverBean;
import team4.afinal.aclass.team4finalproject.fcm.MyFirebaseMessagingService;
import team4.afinal.aclass.team4finalproject.util.Utils;

import static team4.afinal.aclass.team4finalproject.Activity1.mLoginedInfoBean;


public class Activity6 extends AppCompatActivity {

    private ImageView imgReceiver;
    private TextView txtName, txtField, txtPay, txtContents;
    public static List<ReceiverBean> mReceiverBeanList = new ArrayList<ReceiverBean>();

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    private ReceiverBean mReceiverBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        imgReceiver = findViewById(R.id.imgReceiver);
        txtName = findViewById(R.id.txtName);
        txtField = findViewById(R.id.txtField);
        txtPay = findViewById(R.id.txtPay);
        txtContents = findViewById(R.id.txtContents);

        final ReceiverBean mReceiverBean = (ReceiverBean) getIntent().getSerializableExtra("detail");

        try {
            new DownImgTask(imgReceiver).execute(new URL(mReceiverBean.imgUrl));
        } catch (Exception e){
            e.printStackTrace();
        }

        txtName.setText(mReceiverBean.studentId);
        txtField.setText(mReceiverBean.field);
        txtPay.setText(mReceiverBean.pay);
        txtContents.setText(mReceiverBean.contents);

        // 요청 이벤트
        Button btnRequest = findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity6.this);
                builder.setTitle("요청하기");
                builder.setMessage( mReceiverBean.studentId + "님께 요청하시겠습니까?");
                builder.setCancelable(false);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //프로그레스 바 보이기
                        Utils.showProgress(Activity6.this);

                        //fcm 메시지 보내기
                        Sender fcm = new Sender(MyFirebaseMessagingService.API_KEY);
                        Message message = new Message.MessageBuilder()
                                .toToken(mReceiverBean.key) // single android/ios device
                                .addData("title", "회원님이 작성하신 글에 요청이 들어왔습니다.")
                                .addData("content", mReceiverBean.studentId+"회원님의 재능나눔 받기 글에 요청이 들어왔습니다.")
                                .addData("notiType", "A")
                                .build();
                        fcm.send(message);

                        upload();

                        //프로그레스 바 숨기기
                        Utils.hideProgress(Activity6.this);
                        Toast.makeText(Activity6.this,mReceiverBean.studentId + "님께 요청이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }); // setPositive

                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Activity6.this, "요청이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }); // setNegative
                builder.create().show();
            }
        }); // end 요청하기

        // 로고 클릭 시 메인화면 이동
        Button btnGoMain = findViewById(R.id.btnGoMain);
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity6.this, Activity3.class);
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
        bean.studentID = mReceiverBean.studentId;
        bean.kakaoID= Activity1.mLoginedInfoBean.kakaoID;
        bean.notiId = mReceiverBean.name;
        bean.category = "2";

        firebaseRef.child("noti").child(bean.studentID).child(bean.notiId).setValue(bean);
        Toast.makeText(Activity6.this, "요청보내기 성공!!",Toast.LENGTH_SHORT).show();

        finish();

    }//end Upload
}
