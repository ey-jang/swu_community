package team4.afinal.aclass.team4finalproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team4.afinal.aclass.team4finalproject.adapter.ExchangeAdapter;
import team4.afinal.aclass.team4finalproject.adapter.NotiAdapter;
import team4.afinal.aclass.team4finalproject.bean.ExchangeBean;
import team4.afinal.aclass.team4finalproject.bean.NotiBean;

public class Activity16 extends AppCompatActivity {

    private FirebaseDatabase mDatabase;


    private List<NotiBean> mNotiList = new ArrayList<>();
    private NotiAdapter mNotiAdapter;
    private ListView lstAlram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_16);

        lstAlram = findViewById(R.id.lstAlarm);
        Activity1 act1 = new Activity1();

        mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference noti = mDatabase.getReference().child("noti").child(act1.mLoginedInfoBean.num);
        if(noti!=null) {
                noti.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //실시간으로 서버가 변경된 내용이 있을 경우 호출된다.

                            //기존 리스트는 날려버린다.
                            if (mNotiList != null) {
                                mNotiList.clear();
                            }

                            //리스트를 서버로부터 온 데이터로 새로 만든다.
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    NotiBean bean = snapshot.getValue(NotiBean.class);
                                    mNotiList.add(bean);
                            }

                            mNotiAdapter = new NotiAdapter(Activity16.this, mNotiList);
                            lstAlram.setAdapter(mNotiAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

    }
}