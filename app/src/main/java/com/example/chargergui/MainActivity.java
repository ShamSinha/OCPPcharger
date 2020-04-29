package com.example.chargergui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {
    private static final String TAG = "main";
    // private final String DEVICE_NAME="BATTERYMETER";

    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    public OutputStream outputStream;
    public InputStream inputStream;
    boolean deviceConnected = false;
    Button welcome ;
    TextView textView;
    TextView websocket ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.viewblue);
        textView.setVisibility(View.INVISIBLE);
        welcome = (Button) findViewById(R.id.button);
        websocket = (TextView) findViewById(R.id.viewwebsocket);
        websocket.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        welcome.setEnabled(false);

        MyClientEndpoint myClientEndpoint = new MyClientEndpoint();
        myClientEndpoint.ConnectClientToServer(websocket);

        if(BTinit()){
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.connmicrocontroller);
            if (BTconnect()){
                textView.setText(R.string.connmicrocontroller + R.string.inoutwork);
                welcome.setEnabled(true);
            }
        }
        BluetoothThreadforCablePlug();
    }

    public void OnClickWelcome(View view){
        Intent w = new Intent(MainActivity.this, WelcomeAndStart.class);
        startActivity(w);
    }

    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                String DEVICE_ADDRESS = "98:D3:32:71:14:A8";
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
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




    /*public void onClickSend(View view) {
        String string = editText.getText().toString();
        string.concat("\n");
        try {
            outputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.append("\nSent Data:"+string+"\n");

    }

    public void onClickStop(View view) throws IOException {
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
        setUiEnabled(false);
        deviceConnected=false;
        textView.append("\nConnection Closed!\n");
    }*/
















