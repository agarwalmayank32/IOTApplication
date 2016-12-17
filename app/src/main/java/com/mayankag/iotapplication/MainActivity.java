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
import android.widget.SeekBar;
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
    SeekBar seekBar1,seekBar2;

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    BluetoothAdapter btAdapter;

    ThreadConnected myThreadConnected;
    BluetoothSocket bluetoothSocket;

    int Light1OFF = 97, Light1ON = 98;
    int FanON = 99,FanOFF = 100;
    int AcON = 101,AcOFF = 102;
    int TvON = 103,TvOFF = 104;
    int ProjectorON = 105,ProjectorOFF = 106;
    int PcON = 107,PcOFF = 108;
    int Light2ON = 109,Light2OFF =110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothSocket = BluetoothDeviceList.btSocket;

        LightIntensity1 = (LinearLayout) findViewById(R.id.LL5);
        LightIntensity2 = (LinearLayout) findViewById(R.id.LL6);

        Light1 = (ImageButton) findViewById(R.id.LightButton);
        seekBar1 = (SeekBar)findViewById(R.id.SeekBar1);
        Fan = (ImageButton) findViewById(R.id.FanButton);
        Ac = (ImageButton) findViewById(R.id.AcButton);
        Tv = (ImageButton) findViewById(R.id.TvButton);
        Projector = (ImageButton) findViewById(R.id.ProjectorButton);
        Pc = (ImageButton) findViewById(R.id.PcButton);
        Light2 = (ImageButton) findViewById(R.id.LightButton2);
        seekBar2 = (SeekBar)findViewById(R.id.SeekBar2);


        seekBar1.setMax(9);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sendMessage(i + 48);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar2.setMax(9);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sendMessage(i + 65);
                //Toast.makeText(MainActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
                                finish();
                                cancel();
                            }
                        }});
                }
            }
        }

        void write(byte buffer) {
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

            sendMessage(Light1OFF);


        } else {
            LightIntensity1.setVisibility(View.INVISIBLE);
            Light1.setBackgroundResource(R.drawable.light_off);
            stateLight1 = 0;

            sendMessage(Light1ON);
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

            sendMessage(FanON);

        } else {
            Fan.setAnimation(null);
            stateFan = 0;

            sendMessage(FanOFF);
        }
    }

    public void onClickAcChange(View view) {

        if (stateAc == 0) {
            Ac.setBackgroundResource(R.drawable.ac_on);
            stateAc = 1;

            sendMessage(AcON);

        } else {
            Ac.setBackgroundResource(R.drawable.ac_off);
            stateAc = 0;

            sendMessage(AcOFF);

        }
    }


    public void onClickTvChange(View view) {
        if (stateTv == 0) {
            Tv.setBackgroundResource(R.drawable.tv_on);
            stateTv = 1;

            sendMessage(TvON);

        } else {
            Tv.setBackgroundResource(R.drawable.tv_off);
            stateTv = 0;

            sendMessage(TvOFF);

        }
    }

    public void onClickProjectorChange(View view) {
        if (stateProjector == 0) {
            Projector.setBackgroundResource(R.drawable.projector_on);
            stateProjector = 1;

            sendMessage(ProjectorON);

        } else {
            Projector.setBackgroundResource(R.drawable.projector_off);
            stateProjector = 0;

            sendMessage(ProjectorOFF);

        }
    }

    public void onClickPcChange(View view) {
        if (statePc == 0) {
            Pc.setBackgroundResource(R.drawable.pc_on);
            statePc = 1;

            sendMessage(PcON);

        } else {
            Pc.setBackgroundResource(R.drawable.pc_off);
            statePc = 0;

            sendMessage(PcOFF);

        }
    }

    public void onClickLight2Change(View view) {
        if (stateLight2 == 0) {
            LightIntensity2.setVisibility(View.VISIBLE);
            Light2.setBackgroundResource(R.drawable.light_on);
            stateLight2 = 1;

            sendMessage(Light2ON);

        } else {
            LightIntensity2.setVisibility(View.INVISIBLE);
            Light2.setBackgroundResource(R.drawable.light_off);
            stateLight2 = 0;

            sendMessage(Light2OFF);
        }
    }

    public void sendMessage(int mess)
    {
        if(myThreadConnected!=null){
            //byte[] bytesToSend = ByteBuffer.allocate(4).putInt(mess).array();
            //myThreadConnected.write(bytesToSend);
            myThreadConnected.write((byte)mess);
        }
    }
}