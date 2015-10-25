package com.pakgamers.pgshoutbox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.webkit.CookieManager;

import java.util.ArrayList;
import android.os.Handler;
import java.lang.Runnable;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity{

    //URL address
    String url = "http://www.pakgamers.com/forums/vbshout.php?do=detach&instanceid=1";
    Handler handler;

    WebView webView;
    TextView textView;
    WebAppInterface jInterface,set;
    Context context = this;
    EditText editText;
    Button sButton;
    CookieManager cookieManager;
    SimpleAdapter simpleAdapter;
    ListView listView;
    String[] smgs = new String[40];
    String[] snames = new String[40];
    String[] stime = new String[40];
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
    HashMap<String,String> items;
    Boolean loadFinish=false;
    Boolean loggedin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.webView);
        textView = (TextView)findViewById(R.id.textView);
        jInterface = new WebAppInterface(this);
        set = new WebAppInterface(this);
        editText = (EditText) findViewById(R.id.editText);
        sButton = (Button) findViewById(R.id.button);
        editText.clearFocus();



        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.93 Safari/537.36");
        webView.setVisibility(View.INVISIBLE);

        webView.addJavascriptInterface(jInterface, "HtmlViewer");
        webView.addJavascriptInterface(set, "parse");


        listView = (ListView) findViewById(R.id.listView);
        simpleAdapter = new SimpleAdapter(
                context,
                list,
                R.layout.simplerow,
                new String[]{"line1", "line2", "line3"},
                new int[]{R.id.line_a, R.id.line_c, R.id.line_b});

        listView.setAdapter(simpleAdapter);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                //load html

                loadFinish = true;
                loggedin = false;
                String cookies = cookieManager.getCookie("http://www.pakgamers.com");
                String temp[] = cookies.split(";");
                for (String ar1 : temp) {
                    if (ar1.contains("vbseo_loggedin=yes")) {
                        loggedin = true;
                    }

                }
                invalidateOptionsMenu();

                editText.clearFocus();
                textView.setText("onPageFinished");
                handler = new Handler();
                handler.postDelayed(scrape, 500);
                handler.postDelayed(unIdle, 500);


            }
        });

        webView.loadUrl(url);
        editText.clearFocus();

        sButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText(editText.getText());
                webView.loadUrl("javascript:window.parse.showHTML(document.getElementById('dbtech_vbshout_editor1').value = '"+editText.getText()+"');");
                webView.loadUrl("javascript:window.document.getElementsByName('dbtech_vbshout_savebutton')[0].click();");

                editText.setText("");
            }
        });

    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

            if(loadFinish==true) {
                if (loggedin == true) {
                    menu.findItem(R.id.login).setVisible(false);
                    menu.findItem(R.id.signout).setVisible(true);
                } else {
                    menu.findItem(R.id.login).setVisible(true);
                    menu.findItem(R.id.signout).setVisible(false);
                }
            }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {

            case R.id.login:
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(i, 33101);


            case R.id.signout:
                cookieManager.removeAllCookie();
                webView.clearCache(true);
                webView.loadUrl(url);
                loggedin = false;
                invalidateOptionsMenu();

            /*case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(" Version 1.2 \n App was created by Cyanogen Labs \n www.cyanogenlabs.com \n ");
                builder.setTitle("About");
                AlertDialog dialog = builder.create();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                dialog.show();
                messageText.setMovementMethod(LinkMovementMethod.getInstance());*/


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();

        switch(requestCode) {
            case (33101) : {
                if (resultCode == LoginActivity.RESULT_OK) {
                    String loginJS = "javascript:window.document.getElementById('navbar_username').value = '"+extras.getString("username")+"';" +
                            "window.document.getElementById('navbar_password').value = '"+extras.getString("password")+"';" +
                            "window.document.getElementById('cb_cookieuser_navbar').checked = true;" +
                            "window.document.getElementsByClassName('login')[0].click();";
                    webView.loadUrl(loginJS);

                }
                break;
            }
        }
    }

    Runnable scrape = new Runnable() {
        @Override
        public void run() {
            webView.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByName('dbtech_vbshout_content')[0].innerHTML);");

            handler.postDelayed(populateList, 500);
            handler.postDelayed(scrape, 500);
        }
    };

    Runnable unIdle = new Runnable() {
        @Override
        public void run() {
            webView.loadUrl("javascript:window.parse.showHTML(document.getElementById('dbtech_vbshout_editor1').value = '/unban ammarzubair');");
            webView.loadUrl("javascript:window.document.getElementsByName('dbtech_vbshout_savebutton')[0].click();");

            handler.postDelayed(unIdle,100000);
        }
    };

    Runnable populateList = new Runnable() {
        @Override
        public void run() {
            Document doc = Jsoup.parse(jInterface.html);
            Elements msg = doc.select("span[name=dbtech_vbshout_shout][style]");
            Elements time = doc.select("span[name=dbtech_vbshout_shout]");
            /*int i =0;
            for(int s=0;s<80;s+=2){
                stime[i] = time.
                i++;
            }*/
            Elements names = doc.select("a.popupctrl > font");
            int i = 0;
            int j=0;
            int k=0;
            for (Element e : time) {
                if(i%2==0){
                    stime[j] = e.ownText();
                    j++;
                }
                if(i%2==1) {
                    smgs[k] = e.ownText();
                    String temp = "";
                    temp = e.select("a").attr("href");
                    if (temp != "") {
                        smgs[k] += " " + temp;
                    }

                    Elements emo = e.select("img");
                    for (Element f : emo) {
                        smgs[k] += " :" + f.attr("title") + ":";
                    }
                    k++;
                }
                i++;
            }
            i = 0;
            for (Element e : names) {
                snames[i] = e.text();
                i++;
            }

            list.clear();
            for(int b=0;b<40;b++){
                items = new HashMap<String,String>();
                items.put("line1",snames[b]+":");
                items.put("line2",stime[b]);
                items.put("line3", smgs[b]);
                list.add(items);
            }
            simpleAdapter.notifyDataSetChanged();
        }
    };

}