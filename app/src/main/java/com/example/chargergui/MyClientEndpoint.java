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
import DataType.ChargingStationType;
import EnumDataType.MessageFormatEnumType;
import EnumDataType.MessagePriorityEnumType;
import EnumDataType.MessageStateEnumType;
import EnumDataType.ResetEnumType;
import UseCasesOCPP.BootNotificationResponse;
import UseCasesOCPP.CostUpdatedRequest;
import UseCasesOCPP.IdTokenInfoType;
import DataType.IdTokenType;
import DataType.MessageContentType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.RegistrationStatusEnumType;
import EnumDataType.ResetStatusEnumType;
import EnumDataType.TransactionEventEnumType;
import UseCasesOCPP.MessageInfoType;
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
    private static MyClientEndpoint instance = new MyClientEndpoint(); // Eagerly Loading of single ton instance

    private MyClientEndpoint(){
        // private to prevent anyone else from instantiating
    }

    public static MyClientEndpoint getInstance(){
        return instance;
    }

    //BootNotificationResponse
    private BootNotificationResponse bootNotificationResponse = new BootNotificationResponse();

    //AuthorizeResponse
    private IdTokenInfoType idInfo = new IdTokenInfoType();

    //SetDisplayMessageRequest
    private MessageInfoType messageInfo = new MessageInfoType();

    //CostUpdatedRequest
    private CostUpdatedRequest costUpdated = new CostUpdatedRequest();


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
                    text.append("\nBoot status: "+ bootNotificationResponse.getBootStatus() + "\n");
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
            JSONObject responsePayload ;   // responsePayload is JSON payload requested by CSMS.
            JSONObject requestPayload = ((CALL) msg).getPayload() ; // get JSON payload from server request
            switch (CALL.Action) {
                case "CostUpdated":
                    processCostUpdatedRequest(requestPayload);
                    responsePayload = CostUpdatedResponse.payload();
                    break;
                case "SetDisplayMessage":
                    JSONObject DisplayMessage = requestPayload.getJSONObject("message");
                    processDisplayMessageRequest(DisplayMessage);
                    responsePayload = SetDisplayMessageResponse.payload();
                    break;
                case "Reset":
                    AfterResetCommand(ResetEnumType.valueOf(requestPayload.getString("type")));
                    responsePayload = ResetResponse.payload();
                    break;
                /*case "ReserveNow" :
                    ReserveNowResponse reserveNowResponse = new ReserveNowResponse();
                    payload = reserveNowResponse.payload();
                    break;*/

                default:
                    throw new IllegalStateException("Unexpected value: " + CALL.Action);
            }
            CALLRESULT callresult = new CALLRESULT(responsePayload);
            session.getBasicRemote().sendObject(callresult);
        }
        if (msg instanceof CALLRESULT) {

            JSONObject respondedPayload ;  // respondedPayload is a CALL message Response from CSMS

            if (CALLRESULT.MessageId.equals(CALL.MessageId)) {
                respondedPayload = ((CALLRESULT) msg).getPayload();
                switch (CALL.Action) {

                    case "BootNotification":

                        processBootResponse(respondedPayload);
                        break;

                    case "Authorize":

                        JSONObject authResponse = respondedPayload.getJSONObject("idTokenInfo");
                        processAuthResponse(authResponse);
                        break;

                    case "HeartBeat":
                        String currentTime = respondedPayload.getString("currentTime");

                        break;
                    case "StatusNotification":

                        break;

                    case "TransactionEvent":

                        double totalCost = respondedPayload.getDouble("totalCost");

                        break;
                    case "ChangeAvailability":

                        break;

                }

            }
        }
        if (msg instanceof CALLERROR){

        }


    }

    public void AfterResetCommand(ResetEnumType type) {
        if(type == ResetEnumType.Immediate && ResetResponse.status == ResetStatusEnumType.Accepted) {
            IdTokenType.setIdToken(null);
            IdTokenType.setType(null);
            TransactionEventRequest.eventType =TransactionEventEnumType.Started ;
        }

        if(type == ResetEnumType.OnIdle && ResetResponse.status == ResetStatusEnumType.Accepted){

        }
    }
    private void processDisplayMessageRequest(JSONObject j) throws JSONException {

        messageInfo.setId(j.getInt("id"));
        messageInfo.setPriority(MessagePriorityEnumType.valueOf(j.getString("priority")));
        messageInfo.setState(MessageStateEnumType.valueOf(j.getString("state")));
        messageInfo.setStartDateTime(j.getString("startDateTime"));
        messageInfo.setEndDataTime(j.getString("endDataTime"));
        messageInfo.setTransactionId(j.getString("transactionId"));

        MessageContentType messageContent = new MessageContentType();
        JSONObject displaymessage = j.getJSONObject("message");
        messageContent.setContent(displaymessage.getString("content"));
        messageContent.setFormat(MessageFormatEnumType.valueOf(displaymessage.getString("format")));
        messageContent.setLanguage(displaymessage.getString("language"));

        messageInfo.setMessage(messageContent);
    }

    private void processAuthResponse(JSONObject j2) throws JSONException {
        idInfo.setStatus(AuthorizationStatusEnumType.valueOf(j2.getString("status")));
        idInfo.setCacheExpiryDateTime(j2.getString("cacheExpiryDateTime"));
        idInfo.setChargingPriority(j2.getInt("chargingPriority"));
        MessageContentType personalMessage = new MessageContentType();
        JSONObject j3 = j2.getJSONObject("personalMessage");
        personalMessage.setContent(j3.getString("content"));
        personalMessage.setLanguage(j3.getString("language"));
        personalMessage.setFormat(MessageFormatEnumType.valueOf(j3.getString("format")));
        idInfo.setPersonalMessage(personalMessage);
    }

    private void processBootResponse(JSONObject jsonObject) throws JSONException {
        bootNotificationResponse.setBootStatus(RegistrationStatusEnumType.valueOf(jsonObject.getString("status"))) ;
        bootNotificationResponse.setBootInterval(jsonObject.getInt("interval"));
    }

    private void processCostUpdatedRequest(JSONObject jsonObject) throws JSONException {
        costUpdated.setTotalCost((float)jsonObject.getDouble("totalCost"));
        costUpdated.setTransactionId(jsonObject.getString("transactionId"));
    }

    //BOOT
    public BootNotificationResponse getBootNotificationResponse(){
        return bootNotificationResponse;
    }

    //AUTHORIZE
    public IdTokenInfoType getIdInfo() {
        return idInfo;
    }

    public MessageInfoType getMessageInfo(){
        return messageInfo ;
    }

    public CostUpdatedRequest getCostUpdated(){
        return costUpdated ;
    }




}
