package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 김승훈 on 2016-11-15.
 */
public class ServiceThread extends Thread {
    Handler handler;
    String username;
    String password;
    String login_type = "2";
    String redirect_url = "http%3A%2F%2Finfo.kw.ac.kr%2F";
    String layout_opt="N";
    String gubun_code ="11";
    String languageName="KOREAN";
    String style = "학부생";
    Map<String, String> loginCookies;
    Map<String, String> loginCookies1;
    ArrayList<String> subject = new ArrayList<>();
    ArrayList<String> subseq = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> list1 = new ArrayList<>();
    ArrayList<String> list2 = new ArrayList<>();
    ArrayList<String> size  = new ArrayList<>();
    ArrayList<String> size1 = new ArrayList<>();
    ArrayList<String> size2 = new ArrayList<>();
    ArrayList<String> size3 = new ArrayList<>();
    ArrayList<String> size4 = new ArrayList<>();
    ArrayList<String> size5 = new ArrayList<>();
    int count1 = 0;
    boolean isRun =true;
    public ServiceThread(Handler handler, String loginid, String pass){
        this.handler = handler;
        this.username = loginid;
        this.password = pass;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }


    public void run(){
        //반복적으로 수행할 작업을 한다.
        while(isRun){

            try{
                list.clear();
                subject.clear();
                subseq.clear();
                Connection.Response res = Jsoup
                        .connect("https://info.kw.ac.kr/webnote/login/login_proc.php")
                        .data("member_no", username, "password", password, "gubun_code", gubun_code, "login_type", login_type, "style", style, "layout_opt", layout_opt, "redirect_url", redirect_url,"p_language",languageName)
                        .method(Connection.Method.POST)
                        .execute();

                loginCookies = res.cookies();
                Thread.sleep(200);
                Connection.Response res1 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                        .cookies(loginCookies).maxBodySize(0)
                        .method(Connection.Method.GET)
                        .execute();

                loginCookies1 = res1.cookies();
                loginCookies1.putAll(loginCookies);

                Thread.sleep(200);
                Connection.Response res2 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.homepage.KwuMainServlet?p_process=openStu&p_grcode=")
                        .cookies(loginCookies1).cookies(loginCookies).maxBodySize(0)
                        .method(Connection.Method.POST)
                        .execute();

                loginCookies1.remove("host_test");

                Thread.sleep(200);
                Document doc1 = res2.parse();
                Elements elements1 = doc1.select(".list_txt");

                for (Element element : elements1) {
                    if(element.text().contains("[학부]"))
                    {
                        int loc =element.text().indexOf("]");
                        int loc1 = element.text().indexOf("(");
                        String sub = element.text().substring(loc+1,loc1-1);
                        subject.add(sub);
                    }
                }

                Elements elements2 = doc1.select("a");

                for(Element element : elements2)
                {
                    element.absUrl("href");
                    if(element.toString().contains("_goEduPage")) {
                        int loc = element.toString().indexOf("(");
                        int loc1 = element.toString().indexOf(")");
                        String sub = element.toString().substring(loc + 1, loc1);
                        subseq.add(sub);
                    }
                }

                Thread.sleep(100);
                for(int i = 0; i<subseq.size(); i++)
                {
                    String _subj = subseq.get(i).split(",")[0];
                    String _year = subseq.get(i).split(",")[1];
                    String _subjseq = subseq.get(i).split(",")[2];
                    String _class = subseq.get(i).split(",")[3];

                    String p_subj=_subj.substring(1,_subj.length()-1);
                    String p_year=_year.substring(1,_year.length()-1);
                    String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
                    String p_class=_class.substring(1,_class.length()-1);

                    Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.ContentsLessonServlet?p_process=listPage")
                            .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class)
                            .cookies(loginCookies).cookies(loginCookies1).maxBodySize(0)
                            .method(Connection.Method.POST).ignoreHttpErrors(true)
                            .execute();

                    Document doc2 = res3.parse();
                    Elements elements4 = doc2.select(".tl_tit_l");
                    Elements elements5 = doc2.select(".mid2 .t_l2");
                    Elements elements6 = doc2.select(".mid2 .t_c");


                    int j = 0;
                    int count =0;
                    String temp = "";
                    for (Element element1 : elements4) {
                        if(count<8){
                            count++;
                            continue;
                        }
                        temp =  temp +" "+ element1.text();
                        j++;
                        if(j==2) {
                            list.add(temp.trim());
                            j=0;
                            temp ="";
                        }
                    }
                    j= 0;
                    for (Element element2 :elements5 ) {
                        temp = temp + " " + element2.text();
                        j++;
                        if (j == 2) {
                            list1.add(temp.trim());
                            j = 0;
                            temp = "";
                        }
                    }
                    j=0;
                    for (Element element3 :elements6 ) {
                        temp = temp + " " + element3.text();
                        j++;
                        if (j == 3) {
                            list2.add(temp.trim());
                            j = 0;
                            temp = "";
                        }
                    }

                    for(j=0;j<list.size();j++)
                    {
                        temp = list.get(j) +"\n"+list1.get(j)+"\n"+list2.get(j);
                        list.remove(j);
                        list.add(j,temp);
                    }

                    if(count1 != 0 && list.size()>Integer.parseInt(size.get(i)))
                    {
                        int num = list.size() - Integer.parseInt(size.get(i));
                        for(int k = 0; k <num; k++) {
                            Bundle b = new Bundle();
                            Message msg = new Message();
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                    if(count1!=0)
                    size.remove(i);
                    size.add(i,String.valueOf(list.size()));
                    Thread.sleep(100);
                    list.clear();
                    list1.clear();
                    list2.clear();
                }


                for(int i =0; i<subseq.size();i++) {
                    int j = 0;
                    int count = 0;
                    String p = "";
                    String _subj = subseq.get(i).split(",")[0];
                    String _year = subseq.get(i).split(",")[1];
                    String _subjseq = subseq.get(i).split(",")[2];
                    String _class = subseq.get(i).split(",")[3];

                    String p_subj=_subj.substring(1,_subj.length()-1);
                    String p_year=_year.substring(1,_year.length()-1);
                    String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
                    String p_class=_class.substring(1,_class.length()-1);

                    while (true) {
                        j++;
                        p = String.valueOf(j);
                        Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.AssPdsStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
                                .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_pageno", p)
                                .cookies(loginCookies1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                                .method(Connection.Method.POST)
                                .execute();

                        if (res3.statusCode() == 200) {
                            Document doc2 = res3.parse();
                            Elements elements3 = doc2.select(".mid2 .tl_c");
                            Elements elements4 = doc2.select(".mid2 .tl_l");
                            Elements  bdseqs = doc2.select(".link_b2 a[href]");

                            int k = 0;
                            String temp = "";
                            if(bdseqs.isEmpty())
                                break;
                            for (Element element1 : elements3) {
                                temp = temp + " " + element1.text();
                                k++;
                                if (k == 5) {
                                    list.add(temp);
                                    k = 0;
                                    temp = "";
                                }
                            }
                            k = 0;
                            if (j != 1) k = count;
                            for (Element element2 : elements4) {
                                temp = list.get(k) + " " + element2.text();
                                list.remove(k);
                                list.add(k, temp.trim());
                                k++;
                            }
                            count = list.size();
                        } else break;
                    }

                    if(count1 != 0 && list.size()>Integer.parseInt(size1.get(i)))
                    {
                        int num = list.size() - Integer.parseInt(size1.get(i));
                        for(int k = 0; k <num; k++) {
                            Bundle b = new Bundle();
                            Message msg = new Message();
                            b.putString("msg",list.get(k));
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                    if(count1!=0)
                    size1.remove(i);
                    size1.add(i,String.valueOf(list.size()));
                    list.clear();
                    Thread.sleep(100);
                }


                for(int i =0; i<subseq.size();i++) {
                    int j = 0;
                    int count = 0;
                    String p = "";
                    String _subj = subseq.get(i).split(",")[0];
                    String _year = subseq.get(i).split(",")[1];
                    String _subjseq = subseq.get(i).split(",")[2];
                    String _class = subseq.get(i).split(",")[3];

                    String p_subj = _subj.substring(1, _subj.length() - 1);
                    String p_year = _year.substring(1, _year.length() - 1);
                    String p_subjseq = _subjseq.substring(1, _subjseq.length() - 1);
                    String p_class = _class.substring(1, _class.length() - 1);
                    while (true) {
                        j++;
                        p = String.valueOf(j);
                        Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.NoticeStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
                                .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_pageno", p)
                                .cookies(loginCookies1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                                .method(Connection.Method.POST)
                                .execute();

                        if (res3.statusCode() == 200) {
                            Document doc2 = res3.parse();
                            Elements elements3 = doc2.select(".mid2 .tl_c");
                            Elements elements4 = doc2.select(".mid2 .tl_l");
                            Elements bdseqs = doc2.select(".link_b2 a[href]");


                            if (bdseqs.isEmpty())
                                break;
                            int k = 0;
                            String temp = "";
                            for (Element element1 : elements3) {
                                temp = temp + " " + element1.text();
                                k++;
                                if (k == 6) {
                                    list.add(temp);
                                    k = 0;
                                    temp = "";
                                }
                            }
                            k = 0;
                            if (j != 1) k = count;
                            for (Element element2 : elements4) {
                                temp = list.get(k) + " " + element2.text();
                                list.remove(k);
                                list.add(k, temp.trim());
                                k++;
                            }
                            count = list.size();
                        } else break;
                    }
                    if(count1 != 0 && list.size()>Integer.parseInt(size2.get(i)))
                    {
                        int num = list.size() - Integer.parseInt(size2.get(i));
                        for(int k = 0; k <num; k++) {
                            Bundle b = new Bundle();
                            Message msg = new Message();
                            b.putString("msg",list.get(k));
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                    if(count1!=0)
                    size2.remove(i);
                    size2.add(i,String.valueOf(list.size()));
                    list.clear();
                    Thread.sleep(100);
                }


                for(int i =0 ; i< subseq.size();i++) {
                    String _subj = subseq.get(i).split(",")[0];
                    String _year = subseq.get(i).split(",")[1];
                    String _subjseq = subseq.get(i).split(",")[2];
                    String _class = subseq.get(i).split(",")[3];

                    String p_subj=_subj.substring(1,_subj.length()-1);
                    String p_year=_year.substring(1,_year.length()-1);
                    String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
                    String p_class=_class.substring(1,_class.length()-1);
                    Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.ReportStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
                            .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class)
                            .cookies(loginCookies1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                            .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                            .cookies(loginCookies).maxBodySize(0)
                            .method(Connection.Method.POST)
                            .execute();

                    Document doc2 = res3.parse();
                    Elements elements3 = doc2.select(".mid2 .t_c");
                    Elements elements4 = doc2.select(".mid2 .t_l2");
                    int j = 0;
                    String temp = "";


                    for (Element element1 : elements3) {
                        if (j == 2) {
                            temp = temp + "\n" + element1.text();
                        } else {
                            if (j != 3)
                                temp = temp + " " + element1.text();
                        }
                        j++;
                        if (j == 5) {
                            list.add(temp);
                            j = 0;
                            temp = "";
                        }
                    }
                    j = 0;
                    for (Element element2 : elements4) {
                        temp = list.get(j) + " " + element2.text();
                        list.remove(j);
                        list.add(j, temp.trim());
                        j++;
                    }

                    if(count1 != 0 && list.size()>Integer.parseInt(size3.get(i)))
                    {
                        int num = list.size() - Integer.parseInt(size3.get(i));
                        for(int k = 0; k <num; k++) {
                            Bundle b = new Bundle();
                            Message msg = new Message();
                            b.putString("msg",list.get(k));
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                    if(count1!=0)
                    size3.remove(i);
                    size3.add(i,String.valueOf(list.size()));
                    list.clear();
                    Thread.sleep(100);
                }

                for(int i=0; i<subseq.size();i++) {
                    String _subj = subseq.get(i).split(",")[0];
                    String _year = subseq.get(i).split(",")[1];
                    String _subjseq = subseq.get(i).split(",")[2];
                    String _class = subseq.get(i).split(",")[3];

                    String p_subj=_subj.substring(1,_subj.length()-1);
                    String p_year=_year.substring(1,_year.length()-1);
                    String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
                    String p_class=_class.substring(1,_class.length()-1);

                    Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.ExamAnyPaperStuServlet?p_process=listPage")
                            .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class)
                            .cookies(loginCookies).cookies(loginCookies1).maxBodySize(0)
                            .method(Connection.Method.POST)
                            .execute();

                    Document doc2 = res3.parse();
                    Elements elements3 = doc2.select(".mid2 .t_c");
                    Elements elements4 = doc2.select(".mid2 .t_l2");
                    int j = 0;
                    String temp = "";
                    for (Element element1 : elements3) {
                        if (j == 4)
                            temp = temp + "\n" + element1.text();
                        else {
                            if (j != 5)
                                temp = temp + " " + element1.text();
                        }
                        j++;
                        if (j == 6) {
                            list.add(temp);
                            j = 0;
                            temp = "";
                        }
                    }
                    j = 0;
                    for (Element element2 : elements4) {
                        temp = list.get(j) + " " + element2.text();
                        list.remove(j);
                        list.add(j, temp.trim());
                        j++;
                    }

                    if(count1 != 0 && list.size()>Integer.parseInt(size4.get(i)))
                    {
                        int num = list.size() - Integer.parseInt(size4.get(i));
                        for(int k = 0; k <num; k++) {
                            Bundle b = new Bundle();
                            Message msg = new Message();
                            b.putString("msg",list.get(k));
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                    if(count1!=0)
                   size4.remove(i);
                    size4.add(i,String.valueOf(list.size()));
                    list.clear();
                    Thread.sleep(100);
                }

                list.clear();

                for(int i =0; i<subseq.size();i++) {
                    int j = 0;
                    int count = 0;
                    String p = "";
                    String _subj = subseq.get(i).split(",")[0];
                    String _year = subseq.get(i).split(",")[1];
                    String _subjseq = subseq.get(i).split(",")[2];
                    String _class = subseq.get(i).split(",")[3];

                    String p_subj=_subj.substring(1,_subj.length()-1);
                    String p_year=_year.substring(1,_year.length()-1);
                    String p_subjseq=_subjseq.substring(1,_subjseq.length()-1);
                    String p_class=_class.substring(1,_class.length()-1);

                    while (true) {
                        j++;
                        p = String.valueOf(j);
                        Connection.Response res3 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.learn.PdsStuServlet?p_process=listPage&p_process=&p_grcode=N000003")
                                .data("p_subj", p_subj, "p_year", p_year, "p_subjseq", p_subjseq, "p_class", p_class, "p_pageno", p)
                                .cookies(loginCookies1).header("Host", "info2.kw.ac.kr").header("Referer", "http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)").maxBodySize(0)
                                .method(Connection.Method.POST)
                                .execute();

                        if (res3.statusCode() == 200) {
                            Document doc2 = res3.parse();
                            Elements elements3 = doc2.select(".mid2 .tl_c");
                            Elements elements4 = doc2.select(".mid2 .tl_l");
                            Elements  bdseqs = doc2.select(".link_b2 a[href]");

                            int k = 0;
                            String temp = "";
                            if(bdseqs.isEmpty())
                                break;
                            for (Element element1 : elements3) {
                                temp = temp + " " + element1.text();
                                k++;
                                if (k == 6) {
                                    list.add(temp);
                                    k = 0;
                                    temp = "";
                                }
                            }
                            k = 0;
                            if (j != 1) k = count;
                            for (Element element2 : elements4) {
                                temp = list.get(k) + " " + element2.text();
                                list.remove(k);
                                list.add(k, temp.trim());
                                k++;
                            }
                            count = list.size();
                        } else break;
                    }

                    if(count1 != 0 && list.size()>Integer.parseInt(size5.get(i)))
                    {
                        int num = list.size() - Integer.parseInt(size5.get(i));
                        Log.v("FDFDFDF",String.valueOf(num));
                        for(int k = 0; k <num; k++) {
                            Bundle b = new Bundle();
                            Message msg = new Message();
                            b.putString("msg",list.get(k));
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }
                    if(count1!=0)
                    size5.remove(i);
                    size5.add(i,String.valueOf(list.size()));
                    Log.v("size5",String.valueOf(size5.get(i)));
                    list.clear();
                    Thread.sleep(100);
                }

                list.clear();
                
                if(count1!=0)
                Thread.sleep(3000);
                Thread.sleep(3000);
                count1=1;

            }catch (Exception e) {}
        }
    }



}
