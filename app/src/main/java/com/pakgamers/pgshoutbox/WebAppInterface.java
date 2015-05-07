package com.pakgamers.pgshoutbox;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by amr on 07/05/15.
 */
public class WebAppInterface {

    Context context;
    String html = "nothing :(";

    WebAppInterface(Context c){
        context = c;
    }

    @JavascriptInterface
    public void showHTML(String _html){
        html = _html;
    }


}
