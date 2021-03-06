/*

http://de-core-docs.readthedocs.org/ru/master/for-sites/for-nitro.html#id6
 */
package ru.max314.Torque;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import org.prowl.torque.remote.ITorqueService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.max314.util.LogHelper;


/**
 * Created by max on 06.11.2015.
 */
public class TorqueHelper {

    Context parentContext;
    ITorqueService torqueService = null;
    HashMap<String, String> allPIDs = new HashMap<String, String>();
    boolean connectedToECU = false;
    String[] activePIDse;
    String[] ecuSupportedPIDs = new String[0];
    List<PidInfo> pidInfos = new ArrayList<PidInfo>();

    private static final LogHelper LOG = new LogHelper(TorqueHelper.class);

    public TorqueHelper(Context parentContext) {
        LOG.d("constaruct");
        this.parentContext = parentContext;
    }

    public void start() {
        if (torqueService!=null){
            stop();
        }
        LOG.d("start()");
        Intent intent = new Intent();
        intent.setClassName("org.prowl.torque", "org.prowl.torque.remote.TorqueService");
        boolean successfulBind = parentContext.bindService(intent, connection, 0);
        LOG.d("successfulBind"+successfulBind);
    }

    public void stop(){
        if (isConnetToTorgue())
            parentContext.unbindService(connection);
    }

    /**
     * Соединение с программой
     * @return
     */
    public boolean isConnetToTorgue(){
        return !(torqueService==null);
    }

    public boolean isConnectedToECU(){
        return connectedToECU;
    }

    public void refresh(){
        LOG.d("refresh()");
        if (torqueService==null)
            return;
        LOG.d("dorefresh()");
        doRefresh();
    }

    private void doRefresh() {

        clearData();
        try {
            connectedToECU = torqueService.isConnectedToECU();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!isConnetToTorgue())
            return;
        readData();

    }

    private void clearData(){
        connectedToECU = false;
        pidInfos.clear();

    }
    private void readData(){
        try {
            String[] pids = activePIDse;
            float[] values = torqueService.getPIDValues(pids);
            pidInfos.clear();
            for (int i = 0; i < pids.length; i++) {
                pidInfos.add(new PidInfo(pids[i],allPIDs.get(pids[i]),values[i]));
            }
            dumpInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void dumpInfo(){
        LOG.d("dump");
        for (int i = 0; i < pidInfos.size(); i++) {
            LOG.d(pidInfos.get(i).toString());
        }

    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            LOG.d("onServiceConnected");
            torqueService = ITorqueService.Stub.asInterface(service);
            fillAllData();
        }

        public void onServiceDisconnected(ComponentName name) {
            LOG.d("onServiceDisconnected");
            torqueService = null;
        }
    };

    private void fillAllData(){
        String[] allPid = new String[0];
        String[] allPidDesc = new String[0];
        try {
            allPid = torqueService.listAllPIDs();
            allPidDesc = torqueService.getPIDInformation(allPid);
            activePIDse = torqueService.listActivePIDs();
            connectedToECU = torqueService.isConnectedToECU();
            if (connectedToECU)
                ecuSupportedPIDs = torqueService.listECUSupportedPIDs();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LOG.d("all pids");
        for (int i=0;i<allPid.length;i++){
            //String[] pid= allPid[i].split("\\,");
            allPIDs.put(allPid[i],allPidDesc[i]);
            LOG.d(String.format("%s = %s",allPid[i],allPidDesc[i]));
        }
        LOG.d("all supported pids");
        for (int i=0;i<ecuSupportedPIDs.length; i++) {
            LOG.d(String.format("%s",ecuSupportedPIDs[i]));
        }

    }

    public class PidInfo{

        public PidInfo(String pid, String info, float value) {
            this.pid = pid;
            this.info = info;
            this.value = value;
        }

        private String pid;
        private String info;
        private float value;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "PidInfo{" +
                    "pid='" + pid + '\'' +
                    ", info='" + info + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

}

