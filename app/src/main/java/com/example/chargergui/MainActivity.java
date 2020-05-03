package com.example.chargergui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import EnumDataType.RegistrationStatusEnumType;
import UseCasesOCPP.BootNotificationResponse;


public class MainActivity extends Activity {
    private static final String TAG = "main";
    // private final String DEVICE_NAME="BATTERYMETER";

    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    public OutputStream outputStream;
    public InputStream inputStream;
    boolean deviceConnected = false;
    TextView Boot;
    String DEVICE_ADDRESS = "98:D3:32:71:14:A8";
    MyClientEndpoint myClientEndpoint ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Boot = (TextView) findViewById(R.id.boottext);
        myClientEndpoint = MyClientEndpoint.getInstance() ;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(BTinit()){

            Boot.append("\nConnection to Meter Established.\n");
            if (BTconnect()){
                Boot.append("\n InputStream and OutputStream Established");
            }
        }
        StartConnection();
    }

    private void StartConnection(){

        myClientEndpoint.ConnectClientToServer(Boot);

        if(myClientEndpoint.getBootNotificationResponse().getBootStatus() == RegistrationStatusEnumType.Accepted){
            Intent i = new Intent(MainActivity.this , WelcomeAndStart.class);
            startActivity(i);
        }
        if(myClientEndpoint.getBootNotificationResponse().getBootStatus() != RegistrationStatusEnumType.Accepted) {
            new CountDownTimer(myClientEndpoint.getBootNotificationResponse().getBootInterval()*1000, 1000) {
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    StartConnection();
                }
            };
        }
    }

    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Boot.append("\nDevice doesnot Support Bluetooth\n");
        }
        assert bluetoothAdapter != null;
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Boot.append("\nPair the Meter first\n");
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                //DEVICE_ADDRESS = "98:D3:32:71:14:A8";
                Boot.append("\nConnecting To Meter  " + DEVICE_ADDRESS + "\n");
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    Boot.append(". ");
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }


    public void BluetoothThreadforCablePlug() {
    if (BTinit()) {
        if (BTconnect()) {
            deviceConnected = true;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    boolean stopThread;
                    stopThread = false;
                    while (!Thread.currentThread().isInterrupted() && !stopThread) {
                        try {
                            String string = "CPEV";
                            outputStream.write(string.getBytes());

                            int byteCount = inputStream.available();
                            if (byteCount > 0) {
                                byte[] mmBuffer = new byte[1024];
                                int numBytes; // bytes returned from read()
                                numBytes = inputStream.read(mmBuffer);
                                final String cableplug = new String(mmBuffer,0,numBytes,"UTF-8");
                                Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    public void run() {
                                        if(cableplug.equals("T")) {
                                            ChargingStationStates.setCablePluggedIn(true);
                                        }
                                        else if(cableplug.equals("F")){
                                            ChargingStationStates.setCablePluggedIn(false);
                                        }

                                    }
                                });

                            }
                        } catch (IOException ex) {
                            stopThread = true;
                        }
                    }
                }
            });

            thread.start();
        }
    }
}
}
















