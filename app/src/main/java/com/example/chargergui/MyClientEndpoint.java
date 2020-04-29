package com.example.chargergui;

import android.content.Intent;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint ;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen ;
import javax.websocket.OnClose ;
import javax.websocket.OnMessage ;
import javax.websocket.OnError ;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.Decoder;
import javax.websocket.Encoder ;
import javax.websocket.WebSocketContainer;

import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import ChargingStationResponse.CostUpdatedResponse;
import ChargingStationResponse.ReserveNowResponse;
import ChargingStationResponse.ResetResponse;
import ChargingStationResponse.SetDisplayMessageResponse;
import Controller_Components.OCPPCommCtrlr;
import DataType.IdTokenInfoType;
import DataType.IdTokenType;
import DataType.MessageContentType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.RegistrationStatusEnumType;
import EnumDataType.ResetStatusEnumType;
import EnumDataType.TransactionEventEnumType;
import UseCasesOCPP.SendRequestToCSMS;

@ClientEndpoint(
        decoders = {MessageDecoder.class},
        encoders = {MessageEncoder.class,MessageEncodeResult.class , MessageEncodeError.class},
        subprotocols = {"ocpp2.0.1"},
        configurator = ClientConfigurator.class
)

public class MyClientEndpoint  {

    private Session session ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS() ;


    Session ConnectClientToServer(final TextView text) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToWebSocket(text);
            }
        });
        thread.start();
        return session;
    }

    private void connectToWebSocket(TextView text) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        URI uri = URI.create("ws://0f53e667.ngrok.io/mavenjavafxserver/chat");
        try {
            session = container.connectToServer(this, uri);
            if(session != null){
                toCSMS.sendBootNotificationRequest();
                text.setText("Connected to Session : \n" + session.getId() + "\n" +  R.string.conncsms);
            }
        } catch (DeploymentException e) {
            e.printStackTrace();
            text.setText("Deployment Exception"+ R.string.conncsmsnot);
        } catch (IOException e) {
            e.printStackTrace();
            text.setText("IO Exception" + R.string.conncsmsnot);
        } catch (EncodeException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void SendRequestToServer(final CALL call) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    session.getBasicRemote().sendObject(call);
                    //textView1.setText("Message Sent");
                } catch (IOException | EncodeException e) {
                    //textView1.setText("IOException in sending Message");
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
    }
    public void SendResponseToServer(final CALLRESULT callresult){
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    session.getBasicRemote().sendObject(callresult);
                    //textView1.setText("Message Sent");
                } catch (IOException | EncodeException e) {
                    //textView1.setText("IOException in sending Message");
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
    }

    public void SendResponseErrorToServer(final CALLERROR callerror){
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    session.getBasicRemote().sendObject(callerror);
                    //textView1.setText("Message Sent");
                } catch (IOException | EncodeException e) {
                    //textView1.setText("IOException in sending Message");
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
    }



    @OnOpen
    public void onOpen() throws IOException, DeploymentException, URISyntaxException {
        String content = "Websocket connection is alive";
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes("UTF-8"));
        try {
            session.getBasicRemote().sendPing(buffer);
        } catch (IOException e) {
            e.printStackTrace();
           // new MyClientEndpoint();
        }


    }


    @OnMessage
    public void onMessage(WebsocketMessage msg) throws JSONException, IOException, EncodeException {

        if(msg instanceof CALL){
            JSONObject payload = null ;

            switch (CALL.Action) {
                case "CostUpdated":
                    ((CALL) msg).getPayload().getString("totalCost");
                    payload = CostUpdatedResponse.payload();
                    break;
                case "SetDisplayMessage":

                    payload = SetDisplayMessageResponse.payload();
                    break;
                case "Reset":
                    AfterResetCommand(((CALL) msg).getPayload().getString("type"));
                    payload = ResetResponse.payload();
                    break;
                /*case "ReserveNow" :
                    ReserveNowResponse reserveNowResponse = new ReserveNowResponse();
                    payload = reserveNowResponse.payload();
                    break;*/

            }
            CALLRESULT callresult = new CALLRESULT(payload);
            session.getBasicRemote().sendObject(callresult);
        }
        if (msg instanceof CALLRESULT) {
            JSONObject payload = null ;
            if (CALLRESULT.MessageId.equals(CALL.MessageId)) {
                payload = ((CALLRESULT) msg).getPayload();
                switch (CALL.Action) {

                    case "BootNotification":
                        RegistrationStatusEnumType r = RegistrationStatusEnumType.valueOf(payload.getString("status"));
                        OCPPCommCtrlr.HeartbeatInterval = payload.getInt("interval") ;
                        break;
                    case "Authorize":

                        JSONObject j2 = payload.getJSONObject("idTokenInfo");
                        IdTokenInfoType.status = AuthorizationStatusEnumType.valueOf(j2.getString("status") );
                        IdTokenInfoType.cacheExpiryDateTime = j2.getString("cacheExpiryDateTime");
                        MessageContentType.content = j2.getJSONObject("personalMessage").getString("content");

                        break;
                    case "HeartBeat":
                        String currentTime = payload.getString("currentTime");

                        break;
                    case "StatusNotification":



                        break;
                    case "TransactionEvent":
                        double totalCost = payload.getDouble("totalCost");



                        break;
                    case "ChangeAvailability":



                        break;

                }

            }
        }
        if (msg instanceof CALLERROR){

        }


    }

    public void AfterResetCommand(String type) {
        if(type == "Immediate" && ResetResponse.status == ResetStatusEnumType.Accepted) {
            IdTokenType.setIdToken(null);
            IdTokenType.setType(null);
            TransactionEventRequest.eventType =TransactionEventEnumType.Started ;
        }


    }











}
