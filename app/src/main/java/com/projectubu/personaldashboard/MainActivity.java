package com.projectubu.personaldashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideabus.model.bluetooth.MyBluetoothLE;
import com.ideabus.model.data.CurrentAndMData;
import com.ideabus.model.data.DRecord;
import com.ideabus.model.data.DeviceInfo;
import com.ideabus.model.data.User;
import com.ideabus.model.data.VersionData;
import com.ideabus.model.protocol.BPMProtocol;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private BPMProtocol bpmProtocol;
    public static final String TAG = MainActivity.class.getSimpleName();
    private String userID = "123456789AB";
    private Integer age = 18;
    private  boolean isConnecting;
    private TextView bpm1,bpm2,bpm3;
    private LinearLayout btnread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bpm1 = findViewById(R.id.tv_stycole);
        bpm2 = findViewById(R.id.tv_stycole2);
        bpm3 = findViewById(R.id.tv_stycole3);
        btnread = findViewById(R.id.ly_startRead);
        btnread.setVisibility(View.GONE);

        btnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  bpmProtocol.readUserAndVersionData();
                bpmProtocol.readLastData();
            bpmProtocol.readHistorysOrCurrDataAndSyncTiming();

            }
        });
        initParam();
    }

    private void initParam(){
        Log.e(TAG,"initParam->");
        bpmProtocol =  BPMProtocol.getInstance(this,false,true,"N7jbSW#j7YrFd~sE");

    }
    public void initScreen(){

    }

    @Override
    protected void onStart() {
        Log.e(TAG,"onStart->");
        super.onStart();
        startScan();
        bpmProtocol.setOnConnectStateListener(connectStateListener);
        bpmProtocol.setOnDataResponseListener(dataResponseListener);
        bpmProtocol.setOnNotifyStateListener(notifyStateListener);
        bpmProtocol.setOnWriteStateListener(writeStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(bpmProtocol != null) {
            bpmProtocol.disconnect();
            bpmProtocol.stopScan();
        }
    }

    private void startScan(){
        if(!bpmProtocol.isSupportBluetooth(this)){
            Log.e(TAG,"not support BT");
            return;
        }
        Log.e(TAG,"startScan->");
        bpmProtocol.startScan(5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bpmProtocol.isConnected()) bpmProtocol.disconnect();
        bpmProtocol.stopScan();
    }


    private BPMProtocol.OnConnectStateListener connectStateListener = new BPMProtocol.OnConnectStateListener() {
        @Override
        public void onBtStateChanged(boolean isEnable) {
            if(isEnable){
                Log.e(TAG,"BLE is enabled");
                startScan();
            }else {
                Log.e(TAG,"BLE disable");
            }
        }

        @Override
        public void onScanResult(String mac, String name, int rssi) {
            Log.e(TAG,"onScanResult->"+name);
            if(!name.startsWith("n/a")){
                Log.e(TAG,"result -> "+name+" mac-> "+mac+" rssi-> "+rssi);
            }
            if(isConnecting)
                return;
            isConnecting = true;
            bpmProtocol.stopScan();
            if(name.startsWith("A")){
                //3G Model !
                bpmProtocol.connect(mac);
                btnread.setVisibility(View.VISIBLE);
            }else {
                //4G Model !
                btnread.setVisibility(View.VISIBLE);
                bpmProtocol.bond(mac);
            }
        }

        @Override
        public void onConnectionState(BPMProtocol.ConnectState connectState) {
            Log.e(TAG,"ConnectionState ->"+String.valueOf(connectState));
            switch (connectState){
                case Connected:
                    isConnecting = false;
                    btnread.setVisibility(View.VISIBLE);
                    break;
                case ConnectTimeout:
                    isConnecting = false;
                    btnread.setVisibility(View.GONE);

                    break;
                case Disconnect:
                    isConnecting = false;
                    btnread.setVisibility(View.GONE);
                    startScan();
                    break;
                case ScanFinish:
                    btnread.setVisibility(View.GONE);
                    startScan();
                    break;


            }
        }
    };
    private BPMProtocol.OnNotifyStateListener notifyStateListener = new BPMProtocol.OnNotifyStateListener() {
        @Override
        public void onNotifyMessage(String s) {
            Log.e(TAG,"Notify ->"+s);
        }
    };
    private BPMProtocol.OnDataResponseListener dataResponseListener = new BPMProtocol.OnDataResponseListener() {
        @Override
        public void onResponseReadHistory(DRecord dRecord) {

            Log.e(TAG,"ResponseReadHistory->"+ dRecord.toString());
            bpm1.setText(String.valueOf(dRecord.getMData().get(dRecord.getMData().size() - 1).getSystole()));
            bpm2.setText(String.valueOf(dRecord.getMData().get(dRecord.getMData().size() - 1).getDia()));
            bpm3.setText(String.valueOf(dRecord.getMData().get(dRecord.getMData().size() - 1).getMAM()));
        }

        @Override
        public void onResponseClearHistory(boolean b) {

        }

        @Override
        public void onResponseReadUserAndVersionData(User user, VersionData versionData) {

        }

        @Override
        public void onResponseWriteUser(boolean b) {

        }

        @Override
        public void onResponseReadLastData(CurrentAndMData currentAndMData, int i, int i1, int i2, boolean b) {
            bpm1.setText(currentAndMData.getSystole());
            bpm2.setText(currentAndMData.getDia());
            bpm3.setText(currentAndMData.getMAM());
        }

        @Override
        public void onResponseClearLastData(boolean b) {

        }

        @Override
        public void onResponseReadDeviceInfo(DeviceInfo deviceInfo) {

            Log.e(TAG,"ResponseReadDeviceTime->"+deviceInfo.getMeasurementTimes());
        }

        @Override
        public void onResponseReadDeviceTime(DeviceInfo deviceInfo) {
            Log.e(TAG,"ResponseReadDeviceTime->"+deviceInfo.getMeasurementTimes());
        }

        @Override
        public void onResponseWriteDeviceTime(boolean b) {

        }
    };
    private MyBluetoothLE.OnWriteStateListener writeStateListener = new MyBluetoothLE.OnWriteStateListener() {
        @Override
        public void onWriteMessage(boolean b, String s) {
            Log.e(TAG,"WRITE ->"+s);
        }
    };


}
