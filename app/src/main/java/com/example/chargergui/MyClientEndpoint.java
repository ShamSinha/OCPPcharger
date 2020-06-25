package com.example.chargergui;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.auth.Credentials;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import ChargingStationResponse.ResetResponse;
import ChargingStationResponse.SetDisplayMessageResponse;
import DataType.ChargingStationType;
import AuthorizationRelated.IdTokenType;
import DisplayMessagesRelated.DisplayMessageStatusEnumType;
import DisplayMessagesRelated.MessageInfoRepo;
import EnumDataType.AttributeEnumType;
import AuthorizationRelated.AuthorizationStatusEnumType;
import DisplayMessagesRelated.MessageFormatEnumType;
import DisplayMessagesRelated.MessagePriorityEnumType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.RegistrationStatusEnumType;
import EnumDataType.ResetEnumType;
import EnumDataType.ResetStatusEnumType;
import EnumDataType.TransactionEventEnumType;
import UseCasesOCPP.BootNotificationResponse;
import UseCasesOCPP.CostUpdatedRequest;
import AuthorizationRelated.IdTokenInfoType;
import DisplayMessagesRelated.MessageInfoType;
import UseCasesOCPP.SendRequestToCSMS;

@ClientEndpoint(
        decoders = {MessageDecoder.class},
        encoders = {MessageEncoder.class,MessageEncodeResult.class , MessageEncodeError.class},
        subprotocols = {"ocpp2.0.1"},
        configurator = ClientConfigurator.class
)

public class MyClientEndpoint  {

    private Session session ;
    private Context context ;

    private static MyClientEndpoint instance = new MyClientEndpoint(); // Eagerly Loading of single ton instance

    private MyClientEndpoint(){
        // private to prevent anyone else from instantiating
    }

    public static MyClientEndpoint getInstance(){
        return instance;
    }

    private static boolean isCALLarrived = false;

    private SendRequestToCSMS toCSMS = new SendRequestToCSMS();

    //BootNotificationResponse
    private BootNotificationResponse bootNotificationResponse = new BootNotificationResponse();

    //AuthorizeResponse
    private IdTokenInfoType idInfo = new IdTokenInfoType();

    //SetDisplayMessageRequest
    private MessageInfoType messageInfo = new MessageInfoType();

    //CostUpdatedRequest
    private CostUpdatedRequest costUpdated = new CostUpdatedRequest();

    public Session getOpenSession() {
        return session;
    }

    void ConnectClientToServer(final TextView text) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToWebSocket(text);
            }
        });
        thread.start();
    }

    private void connectToWebSocket(TextView text)  {
        //WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        URI uri = URI.create("ws://0f53e667.ngrok.io/mavenjavafxserver/chat");

        ClientManager client = ClientManager.createClient();

        client.getProperties().put(ClientProperties.CREDENTIALS, new Credentials("ws_user", "password")); // Basic Authentication for Charging Station
        client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);


        try {
            client.connectToServer(this,uri) ;
        } catch (DeploymentException e) {
            e.printStackTrace();
            text.append("\nDeployment Exception"+ R.string.conncsmsnot + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            text.append("\nIO Exception" + R.string.conncsmsnot+"\n");
        }




        /*try {
            session = container.connectToServer(this, uri);

        } catch (DeploymentException e) {
            e.printStackTrace();
            text.append("\nDeployment Exception"+ R.string.conncsmsnot + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            text.append("\nIO Exception" + R.string.conncsmsnot+"\n");
        }*/
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
            try {
                session.getBasicRemote().sendObject(toCSMS.createBootNotificationRequest());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            text.append("\nBoot status: "+ bootNotificationResponse.getBootStatus() + "\n");
        }
    }

    @OnOpen
    public void onOpen(Session session1) throws IOException, DeploymentException, URISyntaxException {
        session = session1 ;
        /*String content = "Websocket connection is alive";
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes("UTF-8"));
        try {
            session.getBasicRemote().sendPing(buffer);
        } catch (IOException e) {
            e.printStackTrace();
           // new MyClientEndpoint();
        }
         */
    }


    @OnMessage
    public void onMessage(WebsocketMessage msg) throws JSONException, IOException, EncodeException {
        Log.d("TAG","Websocket Message Received");
        if(msg instanceof CALL){
            isCALLarrived = true ;
            Log.d("TAG","CALL received: " + CALL.getAction());
            JSONObject responsePayload = null;   // responsePayload is JSON payload requested by CSMS.
            JSONObject requestPayload = ((CALL) msg).getPayload() ; // get JSON payload from server request
            Log.d("TAG", "requestPayload: " + requestPayload);
            switch (CALL.getAction()) {
                case "CostUpdated":
                    processCostUpdatedRequest(requestPayload);
                    //responsePayload = CostUpdatedResponse.payload();
                    break;
                case "SetDisplayMessages":
                    JSONObject DisplayMessage = requestPayload.getJSONObject("message");
                    DisplayMessageStatusEnumType status = processDisplayMessageRequest(DisplayMessage);
                    SetDisplayMessageResponse.setStatus(status);
                    responsePayload = SetDisplayMessageResponse.payload() ;
                    break;

                case "GetDisplayMessages":



                case "Reset":
                    AfterResetCommand(ResetEnumType.valueOf(requestPayload.getString("type")));
                    //responsePayload = ResetResponse.payload();
                    break;
                case "ReserveNow":

                    break;
                case "RequestStartTransaction":


                    break;
                case "TriggerMessage":

                    break;
                case "SetVariables":
                    JSONArray setvariableData = requestPayload.getJSONArray("setvariableData");

                    for(int i = 0 ; i < setvariableData.length() ; i++){
                        JSONObject item = setvariableData.getJSONObject(i);
                        String component = item.getString("component");
                        String variable =  item.getString("variable");
                        String attributeValue = item.getString("attributeValue");
                        AttributeEnumType attributeEnumType = AttributeEnumType.valueOf(item.getString("attributeEnumType"));

                    }


                    break;
                case "GetVariables":

                    break;


                default:
                    throw new IllegalStateException("Unexpected value: " + CALL.getAction());
            }
            //CALLRESULT callresult = new CALLRESULT(responsePayload);
            //Log.d("TAG","responsePayload: "+ responsePayload);
            //session.getBasicRemote().sendObject(callresult);
            //Log.d("TAG","Message Sent");
            isCALLarrived = false ;
        }
        if (msg instanceof CALLRESULT) {
            Log.d("TAG","CALL received: " + CALL.getAction());
            JSONObject respondedPayload ;  // respondedPayload is a CALL message Response from CSMS

            if (CALLRESULT.getMessageId().equals(CALL.getMessageId())) {
                Log.d("TAG","CALLRESULT received: " + CALL.getAction());
                respondedPayload = ((CALLRESULT) msg).getPayload();
                Log.d("TAG","respondedPayload: " + respondedPayload);
                switch (CALL.getAction()) {

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
    private DisplayMessageStatusEnumType processDisplayMessageRequest(JSONObject j) throws JSONException {

        MessageInfoRepo messageInfoRepo = new MessageInfoRepo(context);

        String priority = j.getString("priority");
        for (MessagePriorityEnumType s : MessagePriorityEnumType.values()) {
                if(!priority.equals(s.name())) {
                    return DisplayMessageStatusEnumType.NotSupportedPriority ;
                }
        }
        String state = j.getString("state") ;
        for (MessageStateEnumType s : MessageStateEnumType.values()){
            if(!state.equals(s.name())){
                return DisplayMessageStatusEnumType.NotSupportedState ;
            }
        }
        String startDateTime = j.getString("startDateTime");
        String endDateTime = j.getString("endDataTime");
        String transactionId = j.getString("transactionId");

        MessageInfoType.MessageContent messageContent = new MessageInfoType.MessageContent();
        JSONObject displayMessage = j.getJSONObject("message");
        messageContent.content = displayMessage.getString("content");
        messageContent.format = displayMessage.getString("format");
        messageContent.language = displayMessage.getString("language");

        MessageInfoType.MessageInfo message = new MessageInfoType.MessageInfo(priority,state,startDateTime,endDateTime,transactionId,messageContent) ;
        message.setId(j.getInt("id"));
        messageInfoRepo.insert(message);

        return DisplayMessageStatusEnumType.Accepted ;
    }

    private void processAuthResponse(JSONObject j2) throws JSONException {

        idInfo.setStatus(AuthorizationStatusEnumType.valueOf(j2.getString("status")));
        idInfo.setCacheExpiryDateTime(j2.getString("cacheExpiryDateTime"));
        idInfo.setChargingPriority(j2.getInt("chargingPriority"));
        MessageContent personalMessage = new MessageContent();
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

    public boolean getisCALLarrived(){
        return isCALLarrived;
    }


}
