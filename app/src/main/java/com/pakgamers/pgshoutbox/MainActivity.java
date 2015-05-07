package com.pakgamers.pgshoutbox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    //URL address
    String url = "http://www.pakgamers.com/forums/vbshout.php?do=detach&instanceid=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webView = (WebView) findViewById(R.id.webView);
        final TextView textView = (TextView) findViewById(R.id.textView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Chrome/42.0.2311.135");
        webView.setVisibility(View.INVISIBLE);

        final WebAppInterface jInterface = new WebAppInterface(this);
        webView.addJavascriptInterface(jInterface, "HtmlViewer");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //load html
                webView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });

        webView.loadUrl(url);
        Document doc = Jsoup.parse(jInterface.html);
        Elements msg = doc.select("span[name=dbtech_vbshoutbox_shout]");


        textView.setText("wait for it");

        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here

                String lel = "";
                Document doc = Jsoup.parse(jInterface.html);
                Elements msg = doc.select("span[name=dbtech_vbshout_shout]");
                Elements lol = msg.select("span[style]");
                for (Element e : lol) {
                    lel += e.ownText() + "\n";
                }
                textView.setText(lel);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();

        /*AlertDialog.Builder rekt = new AlertDialog.Builder(this);
        rekt.setMessage("get rekt");
        rekt.setTitle("SENPAI");
        rekt.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss
                    }
                });
        rekt.setCancelable(true);
        rekt.create().show();*/
        //Toast.makeText(context, doc.title(), Toast.LENGTH_LONG);

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
}
