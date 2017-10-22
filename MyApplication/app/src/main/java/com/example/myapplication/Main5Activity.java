package com.example.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class Main5Activity extends AppCompatActivity {
    String subj;
    String dopt;
    ArrayList<String> subseq;
    ArrayList<String> subject;
    ArrayList<String> codeList;
    ArrayList<String> list;
    Map<String,String> map;
    Map<String,String> map1;
    String subseqno;
    String listOpt;
    ArrayList<String>content;
    ArrayList<String>title;
    ArrayList<String>desc;
    public Getting g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        subj = intent.getStringExtra("sub");
        subseq = intent.getStringArrayListExtra("subseq");
        subseqno = intent.getStringExtra("subseqno");
        subject = intent.getStringArrayListExtra("subject");
        list = intent.getStringArrayListExtra("list");
        listOpt = intent.getStringExtra("listOpt");
        codeList = intent.getStringArrayListExtra("codeList");
        content = intent.getStringArrayListExtra("content");
        map = (Map<String, String>)intent.getSerializableExtra("map");
        map1 = (Map<String, String>)intent.getSerializableExtra("map1");
        title = intent.getStringArrayListExtra("title");
        desc = intent.getStringArrayListExtra("desc");

        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);

        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);

        TextView tv = new TextView(this);
        tv.setText(subj+"\n");
        ll.addView(tv);
        Button b = new Button(this);
        b.setText("BACK");
        b.setOnClickListener(BACK);
        ll.addView(b);
        int count=0;
        for (int i = 0; i < content.size(); i++) {
            TextView view = new TextView(this);
            view.append(content.get(i));
            if(content.get(i).equals("\n첨부파일\n")) {
                count++;
            }
            if(count>0) {
                view.append("\n------------------------------------------------------------------------\n");
                if(!content.get(i).equals("\n첨부파일\n")) {
                    view.setOnClickListener(CLICK);
                    view.setTag(count - 1);

                    count++;
                }

            }
            ll.addView(view);
        }
        this.setContentView(sv);

            }
    class Getting extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String down = params[0];
            StringBuilder cookies = new StringBuilder();
            try {
                map1.remove("host_test");
                for( String key : map1.keySet() ){
                    cookies.append(key+"="+map.get(key)+";");
                    cookies.append(" ");
                }

                String url = "http://info2.kw.ac.kr/servlet/controller.library.DownloadServlet" + "?p_savefile=" + URLEncoder.encode(title.get(Integer.parseInt(down)), "utf-8") + "&p_realfile=" + URLEncoder.encode(desc.get(Integer.parseInt(down)), "utf-8");

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("유캠퍼스");
                request.addRequestHeader("Cookie",cookies.toString() );
                request.setTitle(desc.get(Integer.parseInt(down)));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                             request.allowScanningByMediaScanner();
                                               request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                           }
                                      request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, desc.get(Integer.parseInt(down)));
                                          // get download service and enqueue file
                                     DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        manager.enqueue(request);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return subject.toString();
        }

        protected void onPostExecute(String str) {

        }
    }
    public Button.OnClickListener BACK = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Main5Activity.this,Main4Activity.class);
            intent.putExtra("sub",subj);
            intent.putStringArrayListExtra("subject",subject);
            intent.putStringArrayListExtra("subseq",subseq);
            intent.putExtra("subseqno",subseqno);
            intent.putExtra("map", (Serializable) map);
            intent.putExtra("map1", (Serializable) map1);
            intent.putExtra("listOpt",listOpt);
            intent.putStringArrayListExtra("list",list);
            intent.putStringArrayListExtra("codeList",codeList);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Main5Activity.this,Main4Activity.class);
        intent.putExtra("sub",subj);
        intent.putStringArrayListExtra("subject",subject);
        intent.putStringArrayListExtra("subseq",subseq);
        intent.putExtra("subseqno",subseqno);
        intent.putExtra("map", (Serializable) map);
        intent.putExtra("map1", (Serializable) map1);
        intent.putExtra("content",content);
        intent.putExtra("listOpt",listOpt);
        intent.putStringArrayListExtra("list",list);
        intent.putStringArrayListExtra("codeList",codeList);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


    public View.OnClickListener CLICK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            dopt = tag.toString();
            Getting g = new Getting();
            g.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,dopt);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                }
            }, 3000);

        }
    };

}
