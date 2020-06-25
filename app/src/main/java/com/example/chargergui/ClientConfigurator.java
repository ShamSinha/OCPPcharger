package com.example.chargergui;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;

import static java.util.Arrays.asList;

public class ClientConfigurator extends ClientEndpointConfig.Configurator {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void beforeRequest(Map<String, List<String>> headers) {

        //String chargingStationIdentity = "CS01";
        //headers.put("AUTHORIZATION", Collections.singletonList("Basic " + Base64.getEncoder().encodeToString((chargingStationIdentity+":password").getBytes()))); // byte array to string

        List<String> WebSocketProtocols = new ArrayList<String>();
        WebSocketProtocols.add(0,"ocpp2.0.1");
        WebSocketProtocols.add(1,"ocpp1.6");
        headers.put("SEC_WEBSOCKET_PROTOCOL", WebSocketProtocols);


    }

    @Override
    public void afterResponse(HandshakeResponse hr) {

        for (String key : hr.getHeaders().keySet()) {
            Log.d("TAG", key + ": " + hr.getHeaders().get(key));
        }

        super.afterResponse(hr);
    }
}
