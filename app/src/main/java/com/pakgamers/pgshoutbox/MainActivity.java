package com.pakgamers.pgshoutbox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.webkit.CookieManager;

public class MainActivity extends ActionBarActivity {

    //URL address
    String url = "http://www.pakgamers.com/forums/vbshout.php?do=detach&instanceid=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webView = (WebView) findViewById(R.id.webView);
        final TextView textView = (TextView) findViewById(R.id.textView);

        CookieManager.getInstance().setAcceptCookie(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setUserAgentString("Chrome/42.0.2311.135");
        webView.setVisibility(View.VISIBLE);

        final WebAppInterface jInterface = new WebAppInterface(this);
        webView.addJavascriptInterface(jInterface, "HtmlViewer");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                //load html
                webView.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByName('dbtech_vbshout_content')[0].innerHTML);");

                final EditText editText = (EditText) findViewById(R.id.editText);
                Button sButton = (Button) findViewById(R.id.button);
                sButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        webView.loadUrl("javascript:document.getElementsByName('dbtech_vbshout_editor')[0].setAttribute('value','" + editText.getText() + "');");
                        webView.loadUrl("javascript:window.document.getElementsByName('dbtech_vbshout_savebutton')[0].click();");
                        editText.setText("");
                    }
                });

                textView.setText("loading finished");
                final TextView[] textViews = new TextView[20];

                CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
                    public void onFinish() {
                        // When timer is finished
                        // Execute your code here
                        String lel = "";
                        Document doc = Jsoup.parse(jInterface.html);
                        Elements msg = doc.select("span[name=dbtech_vbshout_shout][style]");
                        for (int i=0;i<20;i++) {
                            //textViews[i] = new TextView(this);
                            lel += msg.get(i).ownText() + "\n\n";
                        }
                        textView.setText(lel);



                        textView.setText("check online");
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();

            }
        });

        webView.loadUrl(url);


        textView.setText("wait for it");






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
