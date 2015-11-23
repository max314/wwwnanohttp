package ru.max314.wwwnanohttp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import ru.max314.Torque.TorqueHelper;
import ru.max314.util.LogHelper;

public class MainActivity extends AppCompatActivity {

    private static final LogHelper LOG = new LogHelper(MainActivity.class);

    TorqueHelper torqueHelper = new TorqueHelper(this);
    //HTTPServer httpServer = new HTTPServer();
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) this.findViewById(R.id.webView);

    }

    public void startClick(View view) {
        torqueHelper.start();
//        try {
//            httpServer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        LOG.d("Started");
    }



    @Override
    protected void onPause() {
        super.onPause();
        torqueHelper.stop();
//        httpServer.stop();
    }

    public void testClick(View view) {
        torqueHelper.refresh();
    }

    public void webClick(View view) {
        webView.loadUrl("http::/localhost:8080/");
    }

    public void startService(View view) {
        Intent i= new Intent(this, MyServiceTest.class);
        i.putExtra("KEY1", "Value to be used by the service");
        this.startService(i);

    }

    public void stopService(View view) {
        Intent i= new Intent(this, MyServiceTest.class);
        i.putExtra(MyServiceTest.c_COMMAND, MyServiceTest.c_STOP);
        this.startService(i);
    }

    public void refreshService(View view) {
        Intent i= new Intent(this, MyServiceTest.class);
        i.putExtra(MyServiceTest.c_COMMAND, MyServiceTest.c_REFRESH);
        this.startService(i);
    }

    public void startTQService(View view) {
        Intent intent = new Intent();
        intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService");
        this.startService(intent);
    }

    public void getNewtwok(View view) {
        try {
            String buff = getNetwork();
            webView.loadData(buff,"text/html", "UTF-8");
            LOG.d("Network");
            LOG.d(buff);

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    private String getNetwork() throws SocketException {
        StringBuilder sb = new StringBuilder();
        // Iterate over all network interfaces.
        for (Enumeration<NetworkInterface> en =
             NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
        {
            NetworkInterface intf = en.nextElement();
            sb.append(String.format("{%s}\n<br>",intf.toString()));
            // Iterate over all IP addresses in each network interface.
            for (Enumeration<InetAddress> enumIPAddr =
                 intf.getInetAddresses(); enumIPAddr.hasMoreElements();)
            {
                InetAddress iNetAddress = enumIPAddr.nextElement();
                // Loop back address (127.0.0.1) doesn't count as an in-use
                // IP address.

                if (!iNetAddress.isLoopbackAddress())
                {
                    String sLocalIP = iNetAddress.getHostAddress().toString();
                    String sInterfaceName = intf.getName();
                    sb.append(String.format("{%s}:{%s}\n<br>",sInterfaceName,sLocalIP));
                }
            }
        }
        return sb.toString();
    }
}
