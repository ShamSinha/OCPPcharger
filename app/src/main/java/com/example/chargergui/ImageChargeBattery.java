package com.example.chargergui;

import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

public class ImageChargeBattery {

    public ImageChargeBattery(double soc, ImageView imageView){
        int SOC = roundto10(soc) ;
        if(SOC==0) {
            imageView.setImageResource(R.drawable.charge0);

        }
        else if(SOC==10) {
            imageView.setImageResource(R.drawable.charge10);
        }
        else if(SOC==20) {
            imageView.setImageResource(R.drawable.charge20);
        }
        else if(SOC==30) {
            imageView.setImageResource(R.drawable.charge30);
        }
        else if(SOC==40) {
            imageView.setImageResource(R.drawable.charge40);
        }
        else if(SOC==50) {
            imageView.setImageResource(R.drawable.charge50);
        }
        else if(SOC==60) {
            imageView.setImageResource(R.drawable.charge60);
        }
        else if(SOC==70) {
            imageView.setImageResource(R.drawable.charge70);
        }
        else if(SOC==80) {
            imageView.setImageResource(R.drawable.charge80);
        }
        else if(SOC==90) {
            imageView.setImageResource(R.drawable.charge90);
        }

        else if(SOC==100) {
            imageView.setImageResource(R.drawable.charge100);
        }

    }

    private int roundto10(double soc)
    {
        // Smaller multiple
        int n = (int) Math.round(soc);
        int a = (n / 10) * 10;

        // Larger multiple
        int b = a + 10;

        // Return of closest of two
        return (n - a > b - n)? b : a;
    }
}

