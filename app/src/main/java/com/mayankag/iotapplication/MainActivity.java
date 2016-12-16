package com.mayankag.iotapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    private LinearLayout LightIntensity1, LightIntensity2;
    private ImageButton Light1, Light2, Fan, Ac, Tv, Projector, Pc;
    private int stateLight1 = 0;
    private int stateLight2 = 0;
    private int stateFan = 0;
    private int stateAc = 0;
    private int stateTv = 0;
    private int stateProjector = 0;
    private int statePc = 0;

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    BluetoothAdapter btAdapter;

    ThreadConnected myThreadConnected;
    BluetoothSocket bluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothSocket = BluetoothDeviceList.btSocket;

        LightIntensity1 = (LinearLayout) findViewById(R.id.LL5);
        LightIntensity2 = (LinearLayout) findViewById(R.id.LL6);

        Light1 = (ImageButton) findViewById(R.id.LightButton);
        Fan = (ImageButton) findViewById(R.id.FanButton);
        Ac = (ImageButton) findViewById(R.id.AcButton);
        Tv = (ImageButton) findViewById(R.id.TvButton);
        Projector = (ImageButton) findViewById(R.id.ProjectorButton);
        Pc = (ImageButton) findViewById(R.id.PcButton);
        Light2 = (ImageButton) findViewById(R.id.LightButton2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableBluetooth();
        startThreadConnected(bluetoothSocket);
    }

    //This will automatically enable Bluetooth
    public void enableBluetooth() {
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE);
        }
    }

    // When startActivityForResult completes
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bluetooth Option Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if(myThreadConnectBTdevice!=null){
//            myThreadConnectBTdevice.cancel();
//        }
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

            while(true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    final String msgReceived = new String(buffer, 0, bytes);

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,msgReceived,Toast.LENGTH_SHORT).show();
                        }});

                }
                catch (IOException e) {
                    e.printStackTrace();

                    final String msgConnectionLost = e.getMessage();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            if(msgConnectionLost.contains("socket closed")) {
                                Toast.makeText(MainActivity.this,"Connection Terminated",Toast.LENGTH_SHORT).show();
                                cancel();
                            }
                        }});
                }
            }
        }

        void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClickLight1Change(View view) {
        if (stateLight1 == 0) {
            LightIntensity1.setVisibility(View.VISIBLE);
            Light1.setBackgroundResource(R.drawable.light_on);
            stateLight1 = 1;

            sendMessage("Light 1 ON");

        } else {
            LightIntensity1.setVisibility(View.INVISIBLE);
            Light1.setBackgroundResource(R.drawable.light_off);
            stateLight1 = 0;

            sendMessage("Light 1 OFF");
        }
    }

    public void onClickFanChange(View view) {

        if (stateFan == 0) {
            final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
            Fan.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    Fan.startAnimation(animation);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            stateFan = 1;

            sendMessage("FAN ON");

        } else {
            Fan.setAnimation(null);
            stateFan = 0;

            sendMessage("FAN OFF");
        }
    }

    public void onClickAcChange(View view) {

        if (stateAc == 0) {
            Ac.setBackgroundResource(R.drawable.ac_on);
            stateAc = 1;

            sendMessage("AC ON");

        } else {
            Ac.setBackgroundResource(R.drawable.ac_off);
            stateAc = 0;

            sendMessage("AC OFF");

        }
    }


    public void onClickTvChange(View view) {
        if (stateTv == 0) {
            Tv.setBackgroundResource(R.drawable.tv_on);
            stateTv = 1;

            sendMessage("TV ON");

        } else {
            Tv.setBackgroundResource(R.drawable.tv_off);
            stateTv = 0;

            sendMessage("TV OFF");

        }
    }

    public void onClickProjectorChange(View view) {
        if (stateProjector == 0) {
            Projector.setBackgroundResource(R.drawable.projector_on);
            stateProjector = 1;

            sendMessage("PROJECTOR ON");

        } else {
            Projector.setBackgroundResource(R.drawable.projector_off);
            stateProjector = 0;

            sendMessage("PROJECTOR OFF");

        }
    }

    public void onClickPcChange(View view) {
        if (statePc == 0) {
            Pc.setBackgroundResource(R.drawable.pc_on);
            statePc = 1;

            sendMessage("PC ON");

        } else {
            Pc.setBackgroundResource(R.drawable.pc_off);
            statePc = 0;

            sendMessage("PC OFF");

        }
    }

    public void onClickLight2Change(View view) {
        if (stateLight2 == 0) {
            LightIntensity2.setVisibility(View.VISIBLE);
            Light2.setBackgroundResource(R.drawable.light_on);
            stateLight2 = 1;

            sendMessage("Light 2 ON");

        } else {
            LightIntensity2.setVisibility(View.INVISIBLE);
            Light2.setBackgroundResource(R.drawable.light_off);
            stateLight2 = 0;

            sendMessage("Light 2 OFF");
        }
    }

    public void sendMessage(String mess)
    {
        if(myThreadConnected!=null){
            byte[] bytesToSend = mess.getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

}