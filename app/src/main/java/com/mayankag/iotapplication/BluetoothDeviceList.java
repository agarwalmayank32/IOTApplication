package com.mayankag.iotapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothDeviceList extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    BluetoothAdapter btAdapter;

    private UUID myUUID;

    ListView deviceList;

    ArrayList<String> pairedDeviceArrayList;
    ArrayAdapter<String> pairedDeviceAdapter;

    ThreadConnectBTdevice myThreadConnectBTdevice;

    public static BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_list);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this, "FEATURE BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Device does not support Bluetooth
        if (btAdapter == null) {
            Toast.makeText(BluetoothDeviceList.this,"Bluetooth is Not Supported",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");

        deviceList = (ListView)findViewById(R.id.deviceList);

    }

    @Override
    protected void onStart() {
        super.onStart();

        enableBluetooth();
        setup();
    }

    //This will automatically enable Bluetooth
    public void enableBluetooth()
    {
        if (!btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_BLUETOOTH_ENABLE);
        }
    }

    // When startActivityForResult completes...
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            Toast.makeText(BluetoothDeviceList.this,"Bluetooth Enabled",Toast.LENGTH_SHORT).show();
            setup();
        }
        else{
            Toast.makeText(this, "Bluetooth Option Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device.getName());
            }

            pairedDeviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            deviceList.setAdapter(pairedDeviceAdapter);

            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String deviceName = pairedDeviceArrayList.get(position);
                    Toast.makeText(BluetoothDeviceList.this,"Trying to connect " + deviceName,Toast.LENGTH_SHORT).show();

                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    for (BluetoothDevice device : pairedDevices) {
                        if(device.getName().equals(deviceName))
                        {
                            myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                            myThreadConnectBTdevice.start();
                        }
                    }
                }});
        }
    }

    private class ThreadConnectBTdevice extends Thread {
        private BluetoothSocket bluetoothSocket = null;
        private final String bluetoothDevice;

        ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device.getName();

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                //textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if(eMessage.contains("socket might closed"))
                        {
                            Toast.makeText(BluetoothDeviceList.this,"Other Device is not ON",Toast.LENGTH_SHORT).show();
                        }
                    }});

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "Successfully connected to device " +bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        Toast.makeText(BluetoothDeviceList.this,msgconnected,Toast.LENGTH_SHORT).show();
                        btSocket = bluetoothSocket;
                        startActivity(new Intent(BluetoothDeviceList.this,MainActivity.class));
                    }});

                //startThreadConnected(bluetoothSocket);
            }
        }

        public void cancel() {
            Toast.makeText(getApplicationContext(), "close bluetoothSocket", Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
