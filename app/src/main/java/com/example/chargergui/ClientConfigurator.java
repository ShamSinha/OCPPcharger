package com.example.chargergui;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;

import static java.util.Arrays.asList;

public class ClientConfigurator extends ClientEndpointConfig.Configurator {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void beforeRequest(Map<String, List<String>> headers) {

        headers.put("Authorization", asList("Basic " +  Base64.getEncoder().encodeToString("user:password".getBytes()))); // byte array to string

    }

    @Override
    public void afterResponse(HandshakeResponse hr) {
        super.afterResponse(hr);
    }
}
