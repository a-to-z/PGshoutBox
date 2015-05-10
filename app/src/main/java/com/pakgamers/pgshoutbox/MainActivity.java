package com.pakgamers.pgshoutbox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
        webView.getSettings().setUserAgentString("Chrome/42.0.2311.135");
        webView.setVisibility(View.INVISIBLE);

        webView.addJavascriptInterface(jInterface, "HtmlViewer");
        webView.addJavascriptInterface(set, "parse");



        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                //load html

                String loggedin = "";
                String cookies = cookieManager.getCookie("http://www.pakgamers.com");
                String temp[]=cookies.split(";");
                for (String ar1:temp){
                    if(ar1.contains("vbseo_loggedin=yes")){
                        loggedin = ar1;
                    }
                }


                if (loggedin==""){

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
        //            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
         //           layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                    alert.setTitle("Login");
                    alert.setMessage("Enter Username and Password");

                    final EditText user = new EditText(context);
                    final EditText pass = new EditText(context);
        //            ViewGroup.LayoutParams userP = user.getLayoutParams();
          //          ViewGroup.LayoutParams passP = pass.getLayoutParams();
            //        userP.width = ViewGroup.LayoutParams.MATCH_PARENT;
              //      passP.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    user.setTextColor(Color.BLACK);
                    pass.setTextColor(Color.BLACK);
                    user.setHint("Username");
                    pass.setHint("Password");
                    layout.addView(user);
                    layout.addView(pass);

                    alert.setView(layout);

                    alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String loginJS = "javascript:window.document.getElementById('navbar_username').value = '"+user.getText()+"';" +
                                    "window.document.getElementById('navbar_password').value = '"+pass.getText()+"';" +
                                    "window.document.getElementById('cb_cookieuser_navbar').checked = true;" +
                                    "window.document.getElementsByClassName('login')[0].click();";
                            webView.loadUrl(loginJS);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.show();

                };
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Runnable scrape = new Runnable() {
        @Override
        public void run() {

            webView.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByName('dbtech_vbshout_content')[0].innerHTML);");


        /*    Document doc = Jsoup.parse(jInterface.html);
            Elements msg = doc.select("span[name=dbtech_vbshout_shout][style]");
            Elements names = doc.select("a.popupctrl > font");


            int i = 0;
            for (Element e : msg) {
                smgs[i] = e.ownText();
                i++;
            }
            i = 0;
            for (Element e : names) {
                snames[i] = e.ownText();
                i++;
            }

        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String,String> item;
            for(int b=0;b<40;b++){
                item = new HashMap<String,String>();
                item.put("line1",snames[b]+":");
                item.put("line2",smgs[b]);
                list.add(item);
            }

            simpleAdapter = new SimpleAdapter(context,list,R.layout.simplerow,
                    new String[]{"line1","line2"},
                    new int[]{R.id.line_a,R.id.line_b});*/
            /*TextView t1 = (TextView) findViewById(R.id.line_a);
            TextView t2 = (TextView) findViewById(R.id.line_b);
            Typeface raleBold = Typeface.createFromAsset(getAssets(),"Raleway-SemiBold.ttf");
            Typeface raleReg = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
            t1.setTypeface(raleBold);
            t2.setTypeface(raleReg);*/

        //    listView.setAdapter(simpleAdapter);
            handler.postDelayed(populateList, 2000);
            handler.postDelayed(scrape, 3000);
        }
    };

    Runnable unIdle = new Runnable() {
        @Override
        public void run() {
            webView.loadUrl("javascript:window.parse.showHTML(document.getElementById('dbtech_vbshout_editor1').value = '/unban nanow');");
            webView.loadUrl("javascript:window.document.getElementsByName('dbtech_vbshout_savebutton')[0].click();");

            handler.postDelayed(unIdle,120000);
        }
    };

    Runnable populateList = new Runnable() {
        @Override
        public void run() {
            listView = (ListView) findViewById(R.id.listView);
            Document doc = Jsoup.parse(jInterface.html);
            Elements msg = doc.select("span[name=dbtech_vbshout_shout][style]");
            Elements names = doc.select("a.popupctrl > font");
            int i = 0;
            for (Element e : msg) {
                smgs[i] = e.ownText();
                i++;
            }
            i = 0;
            for (Element e : names) {
                snames[i] = e.ownText();
                i++;
            }

            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String,String> item;
            for(int b=0;b<40;b++){
                item = new HashMap<String,String>();
                item.put("line1",snames[b]+":");
                item.put("line2",smgs[b]);
                list.add(item);
            }

            simpleAdapter = new SimpleAdapter(context,list,R.layout.simplerow,
                    new String[]{"line1","line2"},
                    new int[]{R.id.line_a,R.id.line_b});
            /*TextView t1 = (TextView) findViewById(R.id.line_a);
            TextView t2 = (TextView) findViewById(R.id.line_b);
            Typeface raleBold = Typeface.createFromAsset(getAssets(),"Raleway-SemiBold.ttf");
            Typeface raleReg = Typeface.createFromAsset(getAssets(),"Raleway-Regular.ttf");
            t1.setTypeface(raleBold);
            t2.setTypeface(raleReg);*/

            listView.setAdapter(simpleAdapter);
        }
    };

}