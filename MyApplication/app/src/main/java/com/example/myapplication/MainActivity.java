package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

// 로그인 화면 관련 class
public class MainActivity extends AppCompatActivity {
    EditText UsernameEt, PasswordEt;
    BackPressCloseHandler backPressCloseHandler = new BackPressCloseHandler(this);
    SharedPreferences mPref; // 로그인 유지를 위한 sharedpreference
    String loginid;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref= PreferenceManager.getDefaultSharedPreferences(this);
        // 이미 로그인을 하고 로그아웃을 하지 않았을 경우
        if(!mPref.getString("username","").equals(""))
        {
            loginid = mPref.getString("username", "");
            pass =  mPref.getString("password", "");
            String type ="cache";
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, loginid, pass);
        }
        // 첫 어플 실행시 파일 다운 관련 권한 설정
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("파일을 다운받기 위해서는 접근 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        // 로그 아웃을 안했을 경우, 로그인 유지
        if(mPref.getString("username","").equals(""))
        setContentView(R.layout.activity_main);

        UsernameEt = (EditText)findViewById(R.id.etUserName);
        PasswordEt = (EditText)findViewById(R.id.etPassword);

    }
    // 로그인 버튼 클릭 시
    public void OnLogin(View view) {
        String username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String type = "login";
        // 로그인을 위한 backgroud thread 실행
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, username, password);

    }

// 뒤로가기
    public class BackPressCloseHandler {

        private long BackKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > BackKeyPressedTime + 2000) {
                BackKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }

            if (System.currentTimeMillis() <= BackKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
            }
        }

        private void showGuide() {
            toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

}
