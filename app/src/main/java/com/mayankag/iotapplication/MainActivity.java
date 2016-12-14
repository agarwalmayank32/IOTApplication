package com.mayankag.iotapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    LinearLayout LightIntensity;

    ImageButton Light,Fan,Ac,Tv,Projector,Pc;
    int stateLight = 0,stateFan = 0,stateAc = 0,stateTv = 0,stateProjector = 0,statePc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LightIntensity = (LinearLayout)findViewById(R.id.LL4);

        Light = (ImageButton)findViewById(R.id.LightButton);
        Fan = (ImageButton)findViewById(R.id.FanButton);
        Ac = (ImageButton)findViewById(R.id.AcButton);
        Tv = (ImageButton)findViewById(R.id.TvButton);
        Projector = (ImageButton)findViewById(R.id.ProjectorButton);
        Pc = (ImageButton)findViewById(R.id.PcButton);
    }

    public void onClickLightChange(View view) {
        if(stateLight==0)
        {
            LightIntensity.setVisibility(View.VISIBLE);
            Light.setBackgroundResource(R.drawable.light_on);
            stateLight=1;
        }
        else
        {
            LightIntensity.setVisibility(View.INVISIBLE);
            Light.setBackgroundResource(R.drawable.light_off);
            stateLight=0;
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
}
