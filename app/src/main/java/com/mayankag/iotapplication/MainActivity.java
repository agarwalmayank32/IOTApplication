package com.mayankag.iotapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private LinearLayout LightIntensity1,LightIntensity2;
    private ImageButton Light1,Light2,Fan,Ac,Tv,Projector,Pc;
    private int stateLight1 = 0;
    private int stateLight2 = 0;
    private int stateFan = 0;
    private int stateAc = 0;
    private int stateTv = 0;
    private int stateProjector = 0;
    private int statePc = 0;

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    BluetoothAdapter btAdapter;

    private UUID myUUID;
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
        myName = myUUID.toString();

        // Device does not support Bluetooth
        if (btAdapter == null) {
            Toast.makeText(MainActivity.this,"Bluetooth is Not Supported",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.android.bluetooth"); //Set a particular package
            intent.putExtra(Intent.EXTRA_STREAM,"Light1");
            startActivity(intent);

            enableBluetooth();

            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Toast.makeText(MainActivity.this,device.toString(),Toast.LENGTH_SHORT).show();
                }
            }


            //To check the packages available for particular intent
            PackageManager pm = getPackageManager();
            List appsList = pm.queryIntentActivities(intent, 0);
            if(appsList.size() > 0) {
                Toast.makeText(MainActivity.this,String.valueOf(appsList.size()),Toast.LENGTH_SHORT).show();
            }
        }

        LightIntensity1 = (LinearLayout)findViewById(R.id.LL5);
        LightIntensity2 = (LinearLayout)findViewById(R.id.LL6);

        Light1 = (ImageButton)findViewById(R.id.LightButton);
        Fan = (ImageButton)findViewById(R.id.FanButton);
        Ac = (ImageButton)findViewById(R.id.AcButton);
        Tv = (ImageButton)findViewById(R.id.TvButton);
        Projector = (ImageButton)findViewById(R.id.ProjectorButton);
        Pc = (ImageButton)findViewById(R.id.PcButton);
        Light2 = (ImageButton)findViewById(R.id.LightButton2);
    }

    //his will automatically enable Bluetooth
    public void enableBluetooth(){
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        else
        {
            Toast.makeText(MainActivity.this,"Bluetooth Already Enabled",Toast.LENGTH_SHORT).show();
        }
    }

    // When startActivityForResult completes...
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            Toast.makeText(MainActivity.this,"Bluetooth Enabled",Toast.LENGTH_SHORT).show();
            setup();

        }
        else{
            Toast.makeText(this, "Bluetooth Option Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        enableBluetooth();
        setup();
    }

    private void setup() {
        myThreadBeConnected = new ThreadBeConnected();
        myThreadBeConnected.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadBeConnected!=null){
            myThreadBeConnected.cancel();
        }
    }

    public void onClickLight1Change(View view) {
        if(stateLight1==0)
        {
            LightIntensity1.setVisibility(View.VISIBLE);
            Light1.setBackgroundResource(R.drawable.light_on);
            stateLight1=1;
        }
        else
        {
            LightIntensity1.setVisibility(View.INVISIBLE);
            Light1.setBackgroundResource(R.drawable.light_off);
            stateLight1=0;
        }
    }

    public void onClickFanChange(View view) {

        if(stateFan==0)
        {
            final Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
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
            stateFan=1;
        }
        else
        {
            Fan.setAnimation(null);
            stateFan=0;
        }
    }

    public void onClickAcChange(View view) {

        if(stateAc==0)
        {
            Ac.setBackgroundResource(R.drawable.ac_on);
            stateAc=1;
        }
        else
        {
            Ac.setBackgroundResource(R.drawable.ac_off);
            stateAc=0;
        }
    }


    public void onClickTvChange(View view) {
        if(stateTv==0)
        {
            Tv.setBackgroundResource(R.drawable.tv_on);
            stateTv=1;
        }
        else
        {
            Tv.setBackgroundResource(R.drawable.tv_off);
            stateTv=0;
        }
    }

    public void onClickProjectorChange(View view) {
        if(stateProjector==0)
        {
            Projector.setBackgroundResource(R.drawable.projector_on);
            stateProjector=1;
        }
        else
        {
            Projector.setBackgroundResource(R.drawable.projector_off);
            stateProjector=0;
        }
    }

    public void onClickPcChange(View view) {
        if(statePc==0)
        {
            Pc.setBackgroundResource(R.drawable.pc_on);
            statePc=1;
        }
        else
        {
            Pc.setBackgroundResource(R.drawable.pc_off);
            statePc=0;
        }
    }

    public void onClickLight2Change(View view) {
        if(stateLight2==0 )
        {
            LightIntensity2.setVisibility(View.VISIBLE);
            Light2.setBackgroundResource(R.drawable.light_on);
            stateLight2=1;
        }
        else
        {
            LightIntensity2.setVisibility(View.INVISIBLE);
            Light2.setBackgroundResource(R.drawable.light_off);
            stateLight2=0;
        }
    }
}
