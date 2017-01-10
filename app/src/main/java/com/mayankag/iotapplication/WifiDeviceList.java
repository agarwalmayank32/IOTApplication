package com.mayankag.iotapplication;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WifiDeviceList extends AppCompatActivity {

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_device_list);

        if(wifiManager.isWifiEnabled())
        {

        }
        else
        {
            
        }

    }
}
