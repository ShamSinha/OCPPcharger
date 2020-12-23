package com.example.chargergui;

import android.app.Application;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MyApplication extends Application {
    // GPIO Pin Name
    private static final String GPIO_NAME = "";

    private Gpio gpio;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            gpio = manager.openGpio(GPIO_NAME);
        } catch (IOException e) {
            Log.w("TAG", "Unable to access GPIO", e);
        }
        try {
            configureInput(gpio);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void configureInput(Gpio gpio) throws IOException {
        // Initialize the pin as an input
        gpio.setDirection(Gpio.DIRECTION_IN);
        // High voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_HIGH);

        // Register for all state changes
        gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
        gpio.registerGpioCallback(gpioCallback);
    }

    private GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            // Read the active low pin state
            try {
                if (gpio.getValue()) {
                    // Pin is HIGH
                } else {
                    // Pin is LOW
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w("TAG", gpio + ": Error event " + error);
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        gpio.unregisterGpioCallback(gpioCallback);
    }
}
