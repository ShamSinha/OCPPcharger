package com.example.chargergui;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

import java.io.IOException;

public class MyGPIO {

    public void configureInput(Gpio gpio,GpioCallback gpioCallback) throws IOException {
        // Initialize the pin as an input
        gpio.setDirection(Gpio.DIRECTION_IN);
        // High voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_HIGH);

        // Register for all state changes
        gpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
        gpio.registerGpioCallback(gpioCallback);
    }



    public void unregisterGPIO(Gpio gpio,GpioCallback gpioCallback){
        gpio.unregisterGpioCallback(gpioCallback);
    }
}
