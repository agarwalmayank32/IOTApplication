package com.mayankag.iotapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WifiOrBluetoothSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_or_bluetooth_selection);



    }

    public void onClickWifiSelect(View view) {
        startActivity(new Intent(this,WifiDeviceList.class));
    }

    public void onClickBluetoothSelect(View view) {
        startActivity(new Intent(this,BluetoothDeviceList.class));
    }

}
