package com.example.chargergui;

import android.widget.ImageView;

public class ImageSetBattery {

    public ImageSetBattery(float soc, ImageView imageView){
        int SOC = roundto10(soc) ;
        if(SOC==0) {
            imageView.setImageResource(R.drawable.drawingbat0);
        }
       else if(SOC==10) {
            imageView.setImageResource(R.drawable.drawingbat10);
       }
        else if(SOC==20) {
            imageView.setImageResource(R.drawable.drawingbat20);
        }
        else if(SOC==30) {
            imageView.setImageResource(R.drawable.drawingbat30);
        }
        else if(SOC==40) {
            imageView.setImageResource(R.drawable.drawingbat40);
        }
        else if(SOC==50) {
            imageView.setImageResource(R.drawable.drawingbat50);
        }
        else if(SOC==60) {
            imageView.setImageResource(R.drawable.drawingbat60);
        }
        else if(SOC==70) {
            imageView.setImageResource(R.drawable.drawingbat70);
        }
        else if(SOC==80) {
            imageView.setImageResource(R.drawable.drawingbat80);
        }
        else if(SOC==90) {
            imageView.setImageResource(R.drawable.drawingbat90);
        }

        else if(SOC==100) {
            imageView.setImageResource(R.drawable.drawingbat100);
        }

    }

    private int roundto10(float soc)
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
