package com.example.myapplication;

/**
 * Created by 김승훈 on 2016-11-11.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;


import android.preference.PreferenceManager;
import android.util.Log;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * Created by 김승훈 on 2016-07-18.
 */
// 로그인 위한 thread
public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    BackgroundWorker(Context ctx) {
        context = ctx;
    }
    String loginid;
    String loginpw;
    ArrayList<String> subject = new ArrayList<>(); // 과목 이름들 저장 위한 list
    ArrayList<String> subseq = new ArrayList<>(); // 과목 번호들을 저장 위한 list
    Map<String, String> loginCookies; // cookie 값 저장
    Map<String, String> loginCookies1; // cookie 값 저장
    SharedPreferences mPref;
    String cache;

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        cache = type;
        String username =params[1];
        String password = params[2];
        String login_type = "2";
        String redirect_url = "http%3A%2F%2Finfo.kw.ac.kr%2F";
        String layout_opt="N";
        String gubun_code ="11";
        String languageName="KOREAN";
        String style = "학부생";
        loginid = username;
        loginpw = password;
        mPref= PreferenceManager.getDefaultSharedPreferences(context);

        // u campus login url
        String login_url = "https://info.kw.ac.kr/webnote/login/login_proc.php";
        if(type.equals("login") || type.equals("cache")) {
            try {

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                String loginQuery =  "login_type=2&redirect_url=http%3A%2F%2Finfo.kw.ac.kr%2F" +
                                     "&layout_opt=&gubun_code=11&p_language=KOREAN&image.x=19&image.y=18"
                        + "&member_no=" + username + "&password=" + password;
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Referer","http://info.kw.ac.kr/");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                DataOutputStream output = new DataOutputStream(httpURLConnection.getOutputStream());
                output.writeBytes(loginQuery);
                output.close();


                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String result="";
                String line="";

                while((line=bufferedReader.readLine())!=null){
                    result+=line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                // 로그인 성공시
                if(result.contains("open")) {
                    // 로그인 페이지 parsing
                    Connection.Response res = Jsoup
                            .connect("https://info.kw.ac.kr/webnote/login/login_proc.php")
                            .data("member_no", username, "password", password, "gubun_code", gubun_code, "login_type", login_type, "style", style, "layout_opt", layout_opt, "redirect_url", redirect_url,"p_language",languageName)
                            .method(Connection.Method.POST)
                            .execute();
                    // 로그인 cookie 값
                    loginCookies = res.cookies();

                    Connection.Response res1 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.homepage.MainServlet?p_gate=univ&p_process=main&p_page=learning&p_kwLoginType=cookie&gubun_code=11")
                            .cookies(loginCookies).maxBodySize(0)
                            .method(Connection.Method.GET)
                            .execute();
                    // jsessionid 포함한 cookie 값
                    loginCookies1 = res1.cookies();
                    loginCookies1.putAll(loginCookies);
                    // u campus 첫 화면 parsing
                    Connection.Response res2 = Jsoup.connect("http://info2.kw.ac.kr/servlet/controller.homepage.KwuMainServlet?p_process=openStu&p_grcode=")
                            .cookies(loginCookies1).cookies(loginCookies).maxBodySize(0)
                            .method(Connection.Method.POST)
                            .execute();

                    Document doc1 = res2.parse();
                    Elements elements1 = doc1.select(".list_txt");
                    // 과목 이름 저장
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
                    // 과목 번호 저장
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

                    return result = "login success";
                }
                // 로그인 실패시
                else
                    return result = "login not success";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 로그인 상태 메시지
    @Override
    protected void onPreExecute() {
        mPref= PreferenceManager.getDefaultSharedPreferences(context);
            alertDialog = new AlertDialog.Builder(context).create();
        if(mPref.getString("username","").equals("")) {
            alertDialog.setTitle("Login Status");
        }
    }

    // 로그인 여부 메시지
    @Override
    protected void onPostExecute(String result) {
        if(cache.equals("login")) {
            alertDialog.setMessage(result);
            alertDialog.show();
        }
        if(Objects.equals(result, "login not success")) {

        }
        // 로그인 성공시, intent 넘김( 과목이름들을 보여주는 페이지)
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                public void run(){
                    alertDialog.dismiss();
                    Intent intent = new Intent(context, Main2Activity.class);
                    intent.putExtra("map", (Serializable) loginCookies);
                    intent.putExtra("map1", (Serializable) loginCookies1);
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putString("username", loginid);
                    editor.putString("password",loginpw);
                    editor.commit();
                    intent.putStringArrayListExtra("subject",subject);
                    intent.putStringArrayListExtra("subseq",subseq);
                    context.startActivity(intent);
                }
            }, 2000);
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}