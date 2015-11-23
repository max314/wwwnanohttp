package ru.max314.wwwnanohttp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ru.max314.Torque.TorqueHelper;
import ru.max314.util.LogHelper;

public class MyServiceTest extends Service {
    public static final String c_COMMAND = "Command";
    public static final String c_STOP = "Stop";
    public static final String c_REFRESH = "Refresh";

    private static final LogHelper LOG = new LogHelper(MyServiceTest.class);
    TorqueHelper torqueHelper = null;

    public MyServiceTest() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.d("onStartCommand");
        if (intent!=null){
            String command = intent.getStringExtra(c_COMMAND);
            LOG.d("Command "+command);
            if(c_STOP.equals(command)){
                stopSelf();
            }else if (c_REFRESH.equals(command)){
                doRefresh();
            }else {
                doConnect();
            }
        }
        doStart();
        return Service.START_NOT_STICKY;
    }

    private void doStart() {
        LOG.d("doStart");
        if (torqueHelper==null){
            LOG.d("doStart == null");
            TorqueHelper torqueHelper = new TorqueHelper(this);
        }
    }

    private void doConnect() {
        torqueHelper.start();
    }

    private void doRefresh(){
        LOG.d("doRefrsh()");
        torqueHelper.refresh();
    }

    @Override
    public void onDestroy() {
        LOG.d("onDestroy");
        torqueHelper.stop();
        super.onDestroy();
    }
}
