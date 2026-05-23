package com.example.chargergui;

import android.util.Log;

import java.util.Arrays;

import Hardware.GpioProcessor;
import Hardware.Permissions;
import Hardware.Utils;

public class StationSignalController {
    private static final String TAG = "StationSignal";

    private static final int GREEN_PIN = 4;
    private static final int BLUE_PIN = 5;
    private static final int AMBER_PIN = 6;
    private static final int RED_PIN = 7;
    private static final int WHITE_PIN = 8;
    private static final int RFID_BLUE_PIN = 9;
    private static final int RFID_GREEN_PIN = 10;
    private static final int RFID_RED_PIN = 11;

    private GpioProcessor.Gpio green;
    private GpioProcessor.Gpio blue;
    private GpioProcessor.Gpio amber;
    private GpioProcessor.Gpio red;
    private GpioProcessor.Gpio white;
    private GpioProcessor.Gpio rfidBlue;
    private GpioProcessor.Gpio rfidGreen;
    private GpioProcessor.Gpio rfidRed;
    private boolean hardwareReady;
    private StationSignal currentSignal = StationSignal.BOOTING;
    private volatile int rfidBlinkToken;

    public StationSignalController() {
        prepareHardware();
    }

    public StationSignal getCurrentSignal() {
        return currentSignal;
    }

    public void show(StationSignal signal) {
        currentSignal = signal;
        if (!hardwareReady) {
            Log.d(TAG, "Signal " + signal.name() + " selected without GPIO access");
            return;
        }

        allOff();
        switch (signal) {
            case READY:
                high(green);
                break;
            case AUTHORIZE:
            case CHARGING:
                high(blue);
                break;
            case BOOTING:
            case PLUG_IN:
                high(amber);
                break;
            case COMPLETE:
                high(white);
                break;
            case FAULT:
                high(red);
                break;
        }
    }

    public void close() {
        allOff();
        rfidAllOff();
    }

    public void showRfidReady() {
        rfidBlinkToken++;
        if (!hardwareReady) {
            Log.d(TAG, "RFID scanner LED ready selected without GPIO access");
            return;
        }
        rfidAllOff();
        high(rfidBlue);
    }

    public void showRfidReading() {
        showRfidReady();
    }

    public void showRfidOff() {
        rfidBlinkToken++;
        rfidAllOff();
    }

    public void blinkRfidAccepted() {
        blinkRfid(rfidGreen);
    }

    public void blinkRfidRejected() {
        blinkRfid(rfidRed);
    }

    private void prepareHardware() {
        if (!Utils.rootAccess()) {
            Log.d(TAG, "Root access unavailable; using on-screen signal only");
            return;
        }

        Permissions.GivePermissionToGpio(Arrays.asList(
                GREEN_PIN,
                BLUE_PIN,
                AMBER_PIN,
                RED_PIN,
                WHITE_PIN,
                RFID_BLUE_PIN,
                RFID_GREEN_PIN,
                RFID_RED_PIN
        ));

        GpioProcessor gpioProcessor = new GpioProcessor();
        green = gpioProcessor.getPin(GREEN_PIN);
        blue = gpioProcessor.getPin(BLUE_PIN);
        amber = gpioProcessor.getPin(AMBER_PIN);
        red = gpioProcessor.getPin(RED_PIN);
        white = gpioProcessor.getPin(WHITE_PIN);
        rfidBlue = gpioProcessor.getPin(RFID_BLUE_PIN);
        rfidGreen = gpioProcessor.getPin(RFID_GREEN_PIN);
        rfidRed = gpioProcessor.getPin(RFID_RED_PIN);

        out(green);
        out(blue);
        out(amber);
        out(red);
        out(white);
        out(rfidBlue);
        out(rfidGreen);
        out(rfidRed);
        hardwareReady = true;
    }

    private void allOff() {
        low(green);
        low(blue);
        low(amber);
        low(red);
        low(white);
    }

    private void rfidAllOff() {
        low(rfidBlue);
        low(rfidGreen);
        low(rfidRed);
    }

    private void blinkRfid(final GpioProcessor.Gpio gpio) {
        if (!hardwareReady) {
            Log.d(TAG, "RFID scanner blink selected without GPIO access");
            return;
        }

        final int token = ++rfidBlinkToken;
        new Thread(new Runnable() {
            @Override
            public void run() {
                rfidAllOff();
                for (int i = 0; i < 3 && token == rfidBlinkToken; i++) {
                    high(gpio);
                    sleep(180);
                    low(gpio);
                    sleep(160);
                }
            }
        }).start();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void out(GpioProcessor.Gpio gpio) {
        if (gpio != null) {
            gpio.out();
        }
    }

    private void high(GpioProcessor.Gpio gpio) {
        if (gpio != null) {
            gpio.high();
        }
    }

    private void low(GpioProcessor.Gpio gpio) {
        if (gpio != null) {
            gpio.low();
        }
    }
}
