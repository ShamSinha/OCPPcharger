package com.example.chargergui;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class BoardDefaults {
    private static final String DEVICE_RPI3 = "rpi3";

    /**
     * Return the GPIO pin that the Button is connected on.
     */
    public static String getGPIOForCable() {
        if (DEVICE_RPI3.equals(Build.DEVICE)) {
            return "BCM2";
        }
        throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
    }
    public static String getGPIOForPowerPath() {
        if (DEVICE_RPI3.equals(Build.DEVICE)) {
            return "BCM3";
        }
        throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
    }
    public static String getGPIOForEnergy() {
        if (DEVICE_RPI3.equals(Build.DEVICE)) {
            return "BCM4";
        }
        throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
    }
}
