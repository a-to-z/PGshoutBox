package com.pakgamers.pgshoutbox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Arrays;
import android.os.Handler;
import java.lang.Runnable;

public class MainActivity extends ActionBarActivity {

    //URL address
    String url = "http://www.pakgamers.com/forums/vbshout.php?do=detach&instanceid=1";
    Handler handler;

    WebView webView;
    TextView textView;
    WebAppInterface jInterface,set;
    Context context = this;
    EditText editText;
    Button sButton;


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

        CookieManager.getInstance().setAcceptCookie(true);

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

                textView.setText("onPageFinished");
                handler = new Handler();
                handler.postDelayed(scrape,5000);

            }
        });

        webView.loadUrl(url);


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

            final ListView listView = (ListView) findViewById(R.id.listView);
            final String[] shouts = new String[40];
            final ArrayAdapter<String> listAdapter;
            String[] smgs = new String[40];
            String[] snames = new String[40];
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
            for (int a = 0; a < 40; a++) {
                shouts[a] = snames[a] + ": " + smgs[a];
            }

            ArrayList<String> fShouts = new ArrayList<String>();
            fShouts.addAll(Arrays.asList(shouts));
            listAdapter = new ArrayAdapter<String>(context, R.layout.simplerow, fShouts);
            listView.setAdapter(listAdapter);
            //textView.setText(shouts[0]);


            handler.postDelayed(scrape,5000);
        }
    };
}
