package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
// 해당 페이지의 목록들을 보여주는 페이지
public class Main4Activity extends AppCompatActivity {
    String subj;
    String subseqno;
    ArrayList<String> subseq;
    ArrayList<String> subject;
    ArrayList<String> list;
    ArrayList<String> codeList;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    Map<String,String> map;
    Map<String,String> map1;
    String listOpt;
    String subOpt;
    String p_bdseq;
    ArrayList<String> content = new ArrayList<String>();
    public Getting g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String f_list;
        subj = intent.getStringExtra("sub");
        subseqno = intent.getStringExtra("subseqno");
        subseq = intent.getStringArrayListExtra("subseq");
        subject = intent.getStringArrayListExtra("subject");
        codeList = intent.getStringArrayListExtra("codeList");
        map = (Map<String, String>)intent.getSerializableExtra("map");
        map1 = (Map<String, String>)intent.getSerializableExtra("map1");
        listOpt = intent.getStringExtra("listOpt");


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

        if(intent.hasExtra("list")) {
                list = intent.getStringArrayListExtra("list");
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).indexOf(" ") == 0) {
                        f_list = list.get(i).substring(5);
                        list.remove(i);
                        list.add(i, f_list);
                    }
                    TextView view = new TextView(this);
                    view.append("------------------------------------------------------------------------\n");
                    view.append(list.get(i) + "\n");
                    if(listOpt.equals("1") || listOpt.equals("2")) {
                        view.setOnClickListener(CLICK);
                    }
                    view.setTag(i);
                    ll.addView(view);
                }
            }
            else{

            }

        this.setContentView(sv);
        g = new Getting();

    }

    class Getting extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            String _subj = subseqno.split(",")[0];
            String _year = subseqno.split(",")[1];
            String _subjseq = subseqno.split(",")[2];
            String _class = subseqno.split(",")[3];
            String opt = params[0];
            p_bdseq = codeList.get(Integer.parseInt(opt));
            String p_subj=_subj.substring(1,_subj.length()-1);
            String p_year=_year.substring(1,_year.length()-1);
            String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
            String p_class=_class.substring(1,_class.length()-1);

            try {
                if(listOpt.equals("0")) {

                }
                else if(listOpt.equals("1")){
                    map1.remove("host_test");
                        Connection.Response res = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.AssPdsStuServlet?p_process=view&p_process=&p_grcode=N000003")
                                .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_bdseq",p_bdseq)
                                .cookies(map1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                                .method(Connection.Method.POST).ignoreHttpErrors(true)
                                .execute();

                            Document doc1 = res.parse();
                            Elements elements1 = doc1.select(".mid2 .tl_tit_fs9");
                            Elements elements2 = doc1.select(".mid2 .tl_l2");
                            Elements elements3 = doc1.select(".link_b2 a[href]");
                            Elements downloads = doc1.select(".link_b2 a");


                    content.add("제목:\n");
                    if(elements1.text() != null)
                    content.add(elements1.text());
                    content.add("\n내용\n");
                    if(elements2.text() != null)
                    content.add(elements2.text());
                    content.add("\n첨부파일\n");
                    if(elements3.text() != null) {
                        for(Element element : elements3)
                        content.add(element.text());
                    }
                    for(Element download : downloads)
                    {
                        String link = download.attr("href");
                        String _title = link.substring(link.indexOf("(") + 2, link.indexOf(",") - 1);
                        String _desc = link.substring(link.indexOf(",") + 2, link.indexOf(");") - 1);
                        title.add(_title);
                        desc.add(_desc);
                    }

                }
                else if(listOpt.equals("2")){
                    map1.remove("host_test");
                    Connection.Response res = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.NoticeStuServlet?p_process=view&p_process=&p_grcode=N000003")
                            .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_bdseq",p_bdseq)
                            .cookies(map1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                            .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                            .method(Connection.Method.POST).ignoreHttpErrors(true)
                            .execute();

                    Document doc1 = res.parse();
                    Elements elements1 = doc1.select(".mid2 .tl_tit_fs9");
                    Elements elements2 = doc1.select(".mid2 .tl_l2");
                    Elements elements3 = doc1.select(".link_b2 a[href]");
                    Elements downloads = doc1.select(".link_b2 a");

                    content.add("제목:\n");
                    if(elements1.text() != null)
                        content.add(elements1.text());
                    content.add("\n내용\n");
                    if(elements2.text() != null)
                        content.add(elements2.text());
                    content.add("\n첨부파일\n");
                    if(elements3.text() != null) {
                        for(Element element : elements3)
                            content.add(element.text());
                    }


                    for(Element download : downloads)
                    {
                        String link = download.attr("href");
                        String _title = link.substring(link.indexOf("(") + 2, link.indexOf(",") - 1);
                        String _desc = link.substring(link.indexOf(",") + 2, link.indexOf(");") - 1);
                        title.add(_title);
                        desc.add(_desc);
                    }


                }
                else if(listOpt.equals("3")){
                }
                else if(listOpt.equals("4")){
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return subject.toString();
        }
        protected void onPostExecute(String str) {

        }


    }



    public View.OnClickListener CLICK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            subOpt = tag.toString();
            g.execute(subOpt);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                        Intent intent = new Intent(Main4Activity.this, Main5Activity.class);
                        intent.putExtra("sub",subj);
                        intent.putStringArrayListExtra("subject",subject);
                        intent.putStringArrayListExtra("subseq",subseq);
                        intent.putExtra("subseqno",subseqno);
                        intent.putExtra("map", (Serializable) map);
                        intent.putExtra("map1", (Serializable) map1);
                        intent.putStringArrayListExtra("codeList",codeList);
                        intent.putExtra("listOpt",listOpt);
                        intent.putStringArrayListExtra("content",content);
                        intent. putStringArrayListExtra("list",list);
                        intent.putStringArrayListExtra("title",title);
                        intent.putStringArrayListExtra("desc",desc);
                        startActivity(intent);
                        finish();
                }
            }, 1000);

        }
    };

    public Button.OnClickListener BACK = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Main4Activity.this,Main3Activity.class);
            intent.putExtra("sub",subj);
            intent.putStringArrayListExtra("subject",subject);
            intent.putStringArrayListExtra("subseq",subseq);
            intent.putExtra("subseqno",subseqno);
            intent.putExtra("map", (Serializable) map);
            intent.putExtra("map1", (Serializable) map1);
            intent.putStringArrayListExtra("list",list);
            startActivity(intent);
            finish();
        }
    };


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Main4Activity.this,Main3Activity.class);
        intent.putExtra("sub",subj);
        intent.putStringArrayListExtra("subject",subject);
        intent.putStringArrayListExtra("subseq",subseq);
        intent.putExtra("subseqno",subseqno);
        intent.putExtra("map", (Serializable) map);
        intent.putExtra("map1", (Serializable) map1);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
