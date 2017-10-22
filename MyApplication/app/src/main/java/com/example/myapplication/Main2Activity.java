package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
// 과목이름들을 보여주는 페이지
public class Main2Activity extends AppCompatActivity {

    ArrayList<String> subject;
    Map<String, String> map;
    Map<String, String> map1;
    ArrayList<String> subseq;
    SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TextView[] sub = new TextView[10];
        int[] id = {R.id.sub1, R.id.sub2, R.id.sub3, R.id.sub4, R.id.sub5, R.id.sub6, R.id.sub7, R.id.sub8, R.id.sub9, R.id.sub10};

        Intent intent = getIntent();
        subject = intent.getStringArrayListExtra("subject");
        subseq = intent.getStringArrayListExtra("subseq");
        map = (Map<String, String>) intent.getSerializableExtra("map");
        map1 = (Map<String, String>) intent.getSerializableExtra("map1");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        for (int i = 0; i < 10; i++) {
            sub[i] = (TextView) findViewById(id[i]);
        }
        // 과목 이름들 click event 생성
        for (int i = 0; i < subject.size(); i++) {
            sub[i].setText(subject.get(i));
            sub[i].setOnClickListener(BTN_START);
            sub[i].setTag(i);
        }
    }
  // 과목 이름 click 시 button
    public View.OnClickListener BTN_START = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
            intent.putExtra("sub", subject.get((Integer) tag));
            intent.putStringArrayListExtra("subject", subject);
            intent.putStringArrayListExtra("subseq",subseq);
            intent.putExtra("subseqno", subseq.get((Integer) tag));
            intent.putExtra("map", (Serializable) map);
            intent.putExtra("map1", (Serializable) map1);
            startActivity(intent);
            finish();
        }
    };
 // logout 버튼 click 시
    public void LOGOUT(View view) {
        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        Intent intent1 = new Intent(Main2Activity.this,MyService.class);
        mPref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();
        stopService(intent1);
        startActivity(intent);
        finish();
    }
// 뒤로가기 버튼 누를 시
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        Intent intent1 = new Intent(Main2Activity.this,MyService.class);
        mPref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();
        stopService(intent1);
        startActivity(intent);
        finish();
    }
   // 알람 버튼 누를 시
    public void Noti(View view) {
        Toast.makeText(getApplicationContext(),"알림 시작",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Main2Activity.this,MyService.class);
        intent.putStringArrayListExtra("subject",subject);
        intent.putStringArrayListExtra("subseq",subseq);
        intent.putExtra("map", (Serializable) map);
        intent.putExtra("map1", (Serializable) map1);
        startService(intent);
    }

  // 알람 해제 버튼 누를 시
    public void Finish(View view) {
        Toast.makeText(getApplicationContext(),"알림 취소",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Main2Activity.this,MyService.class);
        mPref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        String stop = "stop";
        editor.putString("stop",stop );
        editor.commit();
        stopService(intent);
    }

}