package ru.max314.wwwnanohttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import ru.max314.Torque.TorqueHelper;

public class MainActivity extends AppCompatActivity {

    private static final Logger LOG = Logger.getLogger(NanoHTTPD.class.getName());

    TorqueHelper torqueHelper = new TorqueHelper(this);
    HTTPServer httpServer = new HTTPServer();
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) this.findViewById(R.id.webView);

    }

    public void startClick(View view) {
        torqueHelper.start();
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.log(Level.INFO,"Started");
    }



    @Override
    protected void onPause() {
        super.onPause();
        torqueHelper.stop();
        httpServer.stop();
    }

    public void testClick(View view) {
        torqueHelper.refresh();
    }

    public void webClick(View view) {
        webView.loadUrl("http::/localhost:8080/");
    }
}
