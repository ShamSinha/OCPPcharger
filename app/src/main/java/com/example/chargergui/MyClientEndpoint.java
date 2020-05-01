package com.example.chargergui;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint ;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen ;
import javax.websocket.OnMessage ;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import ChargingStationResponse.CostUpdatedResponse;
import ChargingStationResponse.ResetResponse;
import ChargingStationResponse.SetDisplayMessageResponse;
import Controller_Components.OCPPCommCtrlr;
import DataType.ChargingStationType;
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
    private SendRequestToCSMS toCSMS = new SendRequestToCSMS() ;


    void ConnectClientToServer(final TextView text) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToWebSocket(text);
            }
        });
        thread.start();
    }

    private void connectToWebSocket(TextView text) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        URI uri = URI.create("ws://0f53e667.ngrok.io/mavenjavafxserver/chat");
        try {
            session = container.connectToServer(this, uri);

            if(session != null){
                text.append("Connection with CSMS Established");
                text.append("\nConnected to Session :"+ session.getId() + "\n" );
                text.append("\nBoot Reason: "+ BootNotificationRequest.getReason()+"\n");
                text.append("\nCharging Station\n");
                text.append("\nserialNumber: "+ChargingStationType.serialNumber+"\n");
                text.append("\nmodel: "+ChargingStationType.model+"\n");
                text.append("\nvendorName: "+ChargingStationType.vendorName+"\n");
                text.append("\nfirmwareVersion: "+ChargingStationType.firmwareVersion+"\n");
                text.append("\nmodem: "+ChargingStationType.modem+"\n");
                text.append("\nSending BootNotificationRequest to CSMS\n");
                toCSMS.sendBootNotificationRequest();
                if(CALLRESULT.MessageId.equals(CALL.MessageId)){
                    text.append("\nBoot status: "+ StoreResponseFromCSMS.status + "\n");
                }
            }
        } catch (DeploymentException e) {
            e.printStackTrace();
            text.append("\nDeployment Exception"+ R.string.conncsmsnot + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            text.append("\nIO Exception" + R.string.conncsmsnot+"\n");
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

                } catch (IOException | EncodeException e) {

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

                } catch (IOException | EncodeException e) {

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

                } catch (IOException | EncodeException e) {

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
                        StoreResponseFromCSMS.status = RegistrationStatusEnumType.valueOf(payload.getString("status"));
                        if(StoreResponseFromCSMS.status == RegistrationStatusEnumType.Accepted) {
                            OCPPCommCtrlr.HeartbeatInterval = payload.getInt("interval");
                        }
                        if(StoreResponseFromCSMS.status == RegistrationStatusEnumType.Rejected || StoreResponseFromCSMS.status == RegistrationStatusEnumType.Pending ){
                            StoreResponseFromCSMS.interval = payload.getInt("interval");
                        }
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
