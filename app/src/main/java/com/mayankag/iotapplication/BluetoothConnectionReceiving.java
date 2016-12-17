package com.mayankag.iotapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnectionReceiving extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    BluetoothAdapter btAdapter;

    private UUID myUUID;
    private String myName;

    ThreadBeConnected myThreadBeConnected;
    ThreadConnected myThreadConnected;

    TextView Message;
    LinearLayout inputPane;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection_receiving);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this, "FEATURE BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Device does not support Bluetooth
        if (btAdapter == null) {
            Toast.makeText(this,"Bluetooth is Not Supported",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
        myName = myUUID.toString();

        Message = (TextView)findViewById(R.id.Message);
        inputPane = (LinearLayout)findViewById(R.id.inputpane);
        input = (EditText)findViewById(R.id.input);


    }

    @Override
    protected void onStart() {
        super.onStart();

        enableBluetooth();
        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadBeConnected!=null){
            myThreadBeConnected.cancel();
        }
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
            Toast.makeText(this,"Bluetooth Enabled",Toast.LENGTH_SHORT).show();
            setup();
        }
        else{
            Toast.makeText(this, "Bluetooth Option Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void setup() {
        myThreadBeConnected = new ThreadBeConnected();
        myThreadBeConnected.start();
    }

    private class ThreadBeConnected extends Thread {

        private BluetoothServerSocket bluetoothServerSocket = null;

        ThreadBeConnected() {
            try {
                bluetoothServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(myName, myUUID);
                Message.setText("Waiting For Incoming Connection");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            BluetoothSocket bluetoothSocket;
            if(bluetoothServerSocket!=null){
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                    BluetoothDevice remoteDevice = bluetoothSocket.getRemoteDevice();

                    final String strConnected = "Connected to " + remoteDevice.getName();
                    //connected
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            Message.setText(strConnected);
                            inputPane.setVisibility(View.VISIBLE);
                        }});

                    startThreadConnected(bluetoothSocket);

                } catch (IOException e) {
                    e.printStackTrace();

                    final String eMessage = e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            Message.setText("something wrong: \n" + eMessage);
                        }});
                }
            }else{
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                       Message.setText("bluetoothServerSocket == null");
                    }});
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(), "Connection terminated", Toast.LENGTH_LONG).show();

            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setup();
        }
    }

    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);

                    final String msgReceived = new String(buffer, 0, bytes);

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            Message.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    e.printStackTrace();
                    final String msgConnectionLost = e.getMessage();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Message.setText("Connection Terminated");
                        }});
                }
            }
        }

        void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
                connectedOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void onClickMessageSend(View view) {
        if(myThreadConnected!=null){
            //int y =102333;
            //byte[] bytesToSend = bigIntToByteArray(y);
            byte[] bytesToSend = input.getText().toString().getBytes();
            myThreadConnected.write(bytesToSend);
        }

    }
}
