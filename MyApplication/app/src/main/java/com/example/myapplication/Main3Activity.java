package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
// 강의실, 강의자료, 공지사항, 과제, 퀴즈항목을 보여주는 페이지
public class Main3Activity extends AppCompatActivity {
    String subj;
    String subseqno;
    ArrayList<String> subseq;
    ArrayList<String> subject;
    ArrayList<String> list= new ArrayList<>();
    ArrayList<String> list1= new ArrayList<>();
    ArrayList<String> list2= new ArrayList<>();
    ArrayList<String> codeList= new ArrayList<>();
    Map<String,String> map;
    Map<String,String> map1;
    Object tag;
    String opt;
    public Getting g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TextView[] sub = new TextView[5];
        int [] id ={R.id.textView,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Intent intent = getIntent();
        subj = intent.getStringExtra("sub");
        subseqno = intent.getStringExtra("subseqno");
        subseq = intent.getStringArrayListExtra("subseq");
        subject = intent.getStringArrayListExtra("subject");
        map = (Map<String, String>)intent.getSerializableExtra("map");
        map1 = (Map<String, String>)intent.getSerializableExtra("map1");


        g = new Getting();
        for(int i=0;i<id.length;i++)
        {
            sub[i] = (TextView)findViewById(id[i]);
            sub[i].setOnClickListener(BTN_START);
            sub[i].setTag(i);
        }
        sub[0].setText("강의실");
        sub[1].setText("강의자료실");
        sub[2].setText("공지사항");
        sub[3].setText("과제");
        sub[4].setText("수시퀴즈");


    }

    class Getting extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            String _subj = subseqno.split(",")[0];
            String _year = subseqno.split(",")[1];
            String _subjseq = subseqno.split(",")[2];
            String _class = subseqno.split(",")[3];
            String opt = params[0];

        String p_subj=_subj.substring(1,_subj.length()-1);
        String p_year=_year.substring(1,_year.length()-1);
        String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
        String p_class=_class.substring(1,_class.length()-1);

        try {
            // 강의실 클릭시
        if(opt.equals("0")) {
        Connection.Response res = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.ContentsLessonServlet?p_process=listPage")
        .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class)
        .cookies(map).cookies(map1).maxBodySize(0)
        .method(Connection.Method.POST).ignoreHttpErrors(true)
        .execute();

            Document doc1 = res.parse();
            Elements elements1 = doc1.select(".tl_tit_l");
            Elements elements2 = doc1.select(".mid2 .t_l2");
            Elements elements3 = doc1.select(".mid2 .t_c");


            int i = 0;
            int count =0;
            String temp = "";
            for (Element element1 : elements1) {
                if(count<8){
                    count++;
                    continue;
                }
                temp =  temp +" "+ element1.text();
                i++;
                if(i==2) {
                    list.add(temp.trim());
                    i=0;
                    temp ="";
                }
            }
            i = 0;
            for (Element element2 :elements2 ) {
                temp = temp + " " + element2.text();
                i++;
                if (i == 2) {
                    list1.add(temp.trim());
                    i = 0;
                    temp = "";
                }
            }
                i=0;
                for (Element element3 :elements3 ) {
                    temp = temp + " " + element3.text();
                    i++;
                    if (i == 3) {
                        list2.add(temp.trim());
                        i = 0;
                        temp = "";
                    }
            }

            for(i=0;i<list.size();i++)
            {
                temp = list.get(i) +"\n"+list1.get(i)+"\n"+list2.get(i);
                list.remove(i);
                list.add(i,temp);
            }

        }
        // 강의 자료 클릭 시
        else if(opt.equals("1")){
            map1.remove("host_test");
            int j=0;
            int count =0;
            String p="";
            while(true) {
                j++;
                p= String.valueOf(j);
                Connection.Response res = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.AssPdsStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
                        .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_pageno", p)
                        .cookies(map1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                        .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                        .method(Connection.Method.POST)//.ignoreHttpErrors(true)
                        .execute();

                if(res.statusCode() == 200) {
                    Document doc1 = res.parse();
                    Elements elements1 = doc1.select(".mid2 .tl_c");
                    Elements elements2 = doc1.select(".mid2 .tl_l");
                    Elements  bdseqs = doc1.select(".link_b2 a[href]");

                    if(bdseqs.isEmpty())
                        break;
                    String code ="";
                    for(Element bdseq : bdseqs) {
                        code = bdseq.toString();
                        codeList.add(code.substring(code.indexOf("(") + 2, code.indexOf(",") - 1));
                    }
                    int i = 0;
                    String temp = "";
                    for (Element element1 : elements1) {
                        temp = temp + " " + element1.text();
                        i++;

                        if (i == 5) {
                            list.add(temp);
                            i = 0;
                            temp = "";
                        }
                    }
                    i = 0;
                    if(j!=1) i = count;
                    for (Element element2 : elements2) {
                        temp = list.get(i) + " " + element2.text();
                        list.remove(i);
                        list.add(i, temp.trim());
                        i++;
                    }
                     count = list.size();
                }
                else break;
            }

        }
        // 공지사항 클릭 시
        else if(opt.equals("2")){
            map1.remove("host_test");
            int j=0;
            int count =0;
            String p="";
            while(true) {
                j++;
                p = String.valueOf(j);
                Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.NoticeStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
                        .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_pageno", p)
                        .cookies(map1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                        .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                        .method(Connection.Method.POST)
                        .execute();

                if(res3.statusCode() == 200) {
                    Document doc1 = res3.parse();
                    Elements elements1 = doc1.select(".mid2 .tl_c");
                    Elements elements2 = doc1.select(".mid2 .tl_l");
                    Elements  bdseqs = doc1.select(".link_b2 a[href]");

                    String code ="";
                    if(bdseqs.isEmpty())
                        break;
                    for(Element bdseq : bdseqs) {
                        code = bdseq.toString();
                        codeList.add(code.substring(code.indexOf("(") + 2, code.indexOf(",") - 1));
                    }

                    int i = 0;
                    String temp = "";
                    for (Element element1 : elements1) {
                        temp = temp + " " + element1.text();
                        i++;
                        if (i == 6) {
                            list.add(temp);
                            i = 0;
                            temp = "";
                        }
                    }
                    i = 0;
                    if(j!=1) i = count;
                    for (Element element2 : elements2) {
                        temp = list.get(i) + " " + element2.text();
                        list.remove(i);
                        list.add(i, temp.trim());
                        i++;
                    }
                    count = list.size();
                }
                else break;
            }
        }
        // 과제 클릭시
        else if(opt.equals("3")){
        Connection.Response res2 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.ReportStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
        .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class)
                .cookies(map1).header("Host","info2.kw.ac.kr").header("Referer","http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
        .cookies(map).maxBodySize(0)
        .method(Connection.Method.POST)
        .execute();

        Document doc1 = res2.parse();
            Elements elements1 = doc1.select(".mid2 .t_c");
            Elements elements2 = doc1.select(".mid2 .t_l2");
            int i=0;
            String temp= "";


            for (Element element1 : elements1) {
                if(i==2)
                {
                    temp = temp +"\n"+element1.text();
                }
                else {
                    if (i != 3)
                        temp = temp + " " + element1.text();
                }
                    i++;
                if(i==5) {
                    list.add(temp);
                    i=0;
                    temp ="";
                }
            }
            i = 0;
            for (Element element2 :elements2 ) {
                temp = list.get(i)+" "+element2.text();
                list.remove(i);
                list.add(i,temp.trim());
                i++;
            }

        }
        // 수시 퀴즈 클릭시
        else if(opt.equals("4")){
            Connection.Response res2 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.ExamAnyPaperStuServlet?p_process=listPage")
                    .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class)
                    .cookies(map).cookies(map1).maxBodySize(0)
                    .method(Connection.Method.POST)
                    .execute();

            Document doc1 = res2.parse();
            Elements elements1 = doc1.select(".mid2 .t_c");
            Elements elements2 = doc1.select(".mid2 .t_l2");
            int i=0;
            String temp= "";
            for (Element element1 : elements1) {
                if(i==4)
                    temp = temp + "\n"+element1.text();
                else {
                    if (i != 5)
                        temp = temp + " " + element1.text();
                }
                i++;
                if(i==6) {
                    list.add(temp);
                    i=0;
                    temp ="";
                }
            }
            i = 0;
            for (Element element2 :elements2 ) {
                temp = list.get(i)+" "+element2.text();
                list.remove(i);
                list.add(i,temp.trim());
                i++;
            }

        }
        } catch (IOException e) {
        e.printStackTrace();
        }
        return subject.toString();
        }
protected void onPostExecute(String str) {

        }


        }

    // 강의실, 강의자료, 공지사항, 과제, 수시퀴즈 click event
public View.OnClickListener BTN_START = new View.OnClickListener() {
@Override
public void onClick(View view) {
        tag = view.getTag();
        opt = tag.toString();
        g.execute(opt);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
public void run() {
        Intent intent = new Intent(Main3Activity.this,Main4Activity.class);
        intent.putExtra("sub",subj);
        intent.putStringArrayListExtra("subject",subject);
        intent.putStringArrayListExtra("subseq",subseq);
        intent.putExtra("subseqno",subseqno);
        intent.putExtra("map", (Serializable) map);
        intent.putExtra("map1", (Serializable) map1);
        intent.putExtra("listOpt",opt);
        intent.putStringArrayListExtra("codeList",codeList);
        if(list.isEmpty()){}
        else{intent.putStringArrayListExtra("list",list);}
        startActivity(intent);
        finish();
        }
        }, 1000);

        }
        };
// back 버튼 클릭시
public void BACK(View view) {

        Intent intent = new Intent(Main3Activity.this,Main2Activity.class);
        intent.putStringArrayListExtra("subject",subject);
        intent.putStringArrayListExtra("subseq",subseq);
        intent.putExtra("map", (Serializable) map);
        intent.putExtra("map1", (Serializable) map1);
        startActivity(intent);
        finish();
        }

// 뒤로가기 클릭시
    @Override
public void onBackPressed() {
    Intent intent = new Intent(Main3Activity.this,Main2Activity.class);
    intent.putStringArrayListExtra("subject",subject);
    intent.putStringArrayListExtra("subseq",subseq);
    intent.putExtra("map", (Serializable) map);
    intent.putExtra("map1", (Serializable) map1);
    startActivity(intent);
    finish();
    super.onBackPressed();
    }
}
