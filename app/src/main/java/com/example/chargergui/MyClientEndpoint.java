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
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import AuthorizationRelated.IdTokenInfoEntity;
import AuthorizationRelated.IdTokenRepo;
import AuthorizationRelated.MessageContent;
import ChargingStationDetails.ChargingStation;
import ChargingStationDetails.ChargingStationRepo;
import ChargingStationDetails.ChargingStationStatesRepo;
import ChargingStationDetails.ChargingStationType;
import ChargingStationRequest.BootNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import ChargingStationResponse.CostUpdatedResponse;
import ChargingStationResponse.GetDisplayMessagesResponse;
import ChargingStationResponse.GetVariablesResponse;
import ChargingStationResponse.ResetResponse;
import ChargingStationResponse.SetDisplayMessagesResponse;
import ChargingStationResponse.SetNetworkProfileResponse;
import ChargingStationResponse.SetVariablesResponse;
import Controller_Components.ControllerRepo;
import DataType.ComponentType;
import DataType.GetVariableResultType;
import DataType.ModemType;
import DataType.SetVariableResultType;
import DataType.VariableType;
import DisplayMessagesRelated.DisplayMessageStatusEnumType;
import DisplayMessagesRelated.GetDisplayMessagesStatusEnumType;
import DisplayMessagesRelated.MessageContentType;
import DisplayMessagesRelated.MessageFormatEnumType;
import DisplayMessagesRelated.MessageInfoEntity;
import DisplayMessagesRelated.MessageInfoRepo;
import DisplayMessagesRelated.MessageInfoType;
import DisplayMessagesRelated.MessagePriorityEnumType;
import DisplayMessagesRelated.MessageStateEnumType;
import DisplayMessagesRelated.NotifyDisplayMessagesRequest;
import EnumDataType.AttributeEnumType;
import EnumDataType.GetVariableStatusEnumType;
import EnumDataType.MutabilityEnumType;
import EnumDataType.OCPPInterfaceEnumType;
import EnumDataType.OCPPTransportEnumType;
import EnumDataType.OCPPVersionEnumType;
import EnumDataType.RegistrationStatusEnumType;
import EnumDataType.ResetEnumType;
import EnumDataType.ResetStatusEnumType;
import EnumDataType.SetNetworkProfileStatusEnumType;
import EnumDataType.SetVariableStatusEnumType;
import TransactionRelated.TransactionEventEnumType;
import UseCasesOCPP.BootNotificationResponse;
import UseCasesOCPP.SendRequestToCSMS;

import static android.webkit.URLUtil.isValidUrl;

@ClientEndpoint(
        decoders = {MessageDecoder.class},
        encoders = {MessageEncoder.class,MessageEncodeResult.class , MessageEncodeError.class},
        subprotocols = {"ocpp2.0.1"},
        configurator = ClientConfigurator.class
)

public class MyClientEndpoint  {

    private Session session ;

    private static MyClientEndpoint instance = new MyClientEndpoint(); // Eagerly Loading of single ton instance

    private MyClientEndpoint(){
        // private to prevent anyone else from instantiating
    }

    public static MyClientEndpoint getInstance(){
        return instance;
    }

    private WeakReference<Context> context ;

    public void init(Context context1){
        context = new WeakReference<>(context1) ;
    }

    private SendRequestToCSMS toCSMS = new SendRequestToCSMS();

    //BootNotificationResponse
    private BootNotificationResponse bootNotificationResponse = new BootNotificationResponse();


    public Session getOpenSession() {
        return session;
    }

    public NetworkProfileRepo networkProfileRepo ;



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
        // session = container.connectToServer(this, uri);

        networkProfileRepo = new NetworkProfileRepo(context.get());
        NetworkProfile networkProfile = networkProfileRepo.getNetworkProfile(1) ;

        URI uri = URI.create(networkProfile.getConnectionData().getOcppCsmsUrl());

        ChargingStationRepo chargingStationRepo = new ChargingStationRepo(context.get());

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
            text.append("\nIO Exception" + R.string.conncsmsnot + "\n");
        }


        if(session != null){
            text.append("Connection with CSMS Established");
            text.append("\nConnected to Session :"+ session.getId() + "\n" );
            text.append("\nBoot Reason: "+ BootNotificationRequest.getReason()+"\n");

            ChargingStation chargingStation = chargingStationRepo.getChargingStationType() ;

            ChargingStationType.setSerialNumber(chargingStation.getSerialNumber());
            ChargingStationType.setModel(chargingStation.getModel());
            ChargingStationType.setVendorName(chargingStation.getVendorName());
            ChargingStationType.setFirmwareVersion(chargingStation.getFirmwareVersion());
            ModemType.setIccid(chargingStation.getModem().iccid);
            ModemType.setImsi(chargingStation.getModem().imsi);

            text.append("\nCharging Station\n");
            text.append("\nserialNumber: "+ChargingStationType.serialNumber+"\n");
            text.append("\nmodel: "+ChargingStationType.model+"\n");
            text.append("\nvendorName: "+ChargingStationType.vendorName+"\n");
            text.append("\nfirmwareVersion: "+ChargingStationType.firmwareVersion+"\n");
            text.append("\nmodem iccid:"+ ModemType.iccid+"\n");
            text.append("\nmodem imsi:"+ ModemType.imsi+"\n");
            text.append("\nSending BootNotificationRequest to CSMS\n");
            try {
                toCSMS.sendBootNotificationRequest();
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
    public void onMessage(WebsocketMessage msg) throws JSONException {
        Log.d("TAG", "Websocket Message Received");
        if (msg instanceof CALL) {

            Log.d("TAG", "CALL received: " + CALL.getAction());
            JSONObject responsePayload = new JSONObject();   // responsePayload is JSON payload requested by CSMS.
            JSONObject requestPayload = ((CALL) msg).getPayload(); // get JSON payload from server request
            Log.d("TAG", "requestPayload: " + requestPayload);
            switch (CALL.getAction()) {
                case "CostUpdated":

                    responsePayload = processCostUpdatedRequest(requestPayload);

                    break;
                case "SetDisplayMessages":
                    JSONObject setDisplayMessage = requestPayload.getJSONObject("message");
                    DisplayMessageStatusEnumType status = processSetDisplayMessageRequest(setDisplayMessage);
                    SetDisplayMessagesResponse.setStatus(status);
                    responsePayload = SetDisplayMessagesResponse.payload();
                    break;

                case "GetDisplayMessages":

                    int requestId = requestPayload.getInt("requestId");
                    List<JSONObject> notifyDisplayMessage = new ArrayList<>();
                    List<MessageInfoEntity.MessageInfo> messageInfoEntityList = new ArrayList<>();

                    MessageInfoRepo messageInfoRepo = new MessageInfoRepo(context.get());
                    if (requestPayload.has("id")) {
                        int id = requestPayload.getInt("id");
                        if (messageInfoRepo.getMessageInfoById(id) != null) {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Accepted);
                            messageInfoEntityList.add(messageInfoRepo.getMessageInfoById(id));
                            notifyDisplayMessage = processGetDisplayMessages(messageInfoEntityList);
                        } else {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Unknown);
                        }
                    } else if (requestPayload.has("priority")) {
                        String priority = requestPayload.getString("priority");
                        if (messageInfoRepo.getMessageInfoByPriority(priority) != null) {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Accepted);
                            messageInfoEntityList = messageInfoRepo.getMessageInfoByPriority(priority);
                            notifyDisplayMessage = processGetDisplayMessages(messageInfoEntityList);
                        } else {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Unknown);
                        }
                    } else if (requestPayload.has("state")) {
                        String state = requestPayload.getString("state");
                        if (messageInfoRepo.getMessageInfoByState(state) != null) {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Accepted);
                            messageInfoEntityList = messageInfoRepo.getMessageInfoByState(state);
                            notifyDisplayMessage = processGetDisplayMessages(messageInfoEntityList);
                        } else {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Unknown);
                        }
                    }
                    sendResponse(new CALLRESULT(GetDisplayMessagesResponse.payload()));

                    if (GetDisplayMessagesResponse.getStatus().equals(GetDisplayMessagesStatusEnumType.Accepted)) {
                        NotifyDisplayMessagesRequest.setRequestId(requestId);
                        for (int k = 0; k < notifyDisplayMessage.size(); k++) {
                            if (k == notifyDisplayMessage.size() - 1) {
                                NotifyDisplayMessagesRequest.setTbc(false);
                            } else {
                                NotifyDisplayMessagesRequest.setTbc(true);
                            }
                            sendRequest(new CALL("NotifyDisplayMessages", NotifyDisplayMessagesRequest.payload(notifyDisplayMessage.get(k))));
                        }
                    }

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
                    JSONArray setVariableData = requestPayload.getJSONArray("setVariableData");

                    responsePayload = processSetVariablesRequest(setVariableData) ;

                    break;
                case "GetVariables":
                    JSONArray getVariableData = requestPayload.getJSONArray("getVariableData");

                    responsePayload = processGetVariablesRequest(getVariableData) ;

                    break;
                case "SetNetworkProfile" :

                    responsePayload = processSetNetworkProfileRequest(requestPayload);

                default:
                    throw new IllegalStateException("Unexpected value: " + CALL.getAction());
                }


            }
            if (msg instanceof CALLRESULT) {

                Log.d("TAG", "CALL received: " + CALL.getAction());
                JSONObject respondedPayload;  // respondedPayload is a CALL message Response from CSMS
                if (CALLRESULT.getMessageId().equals(CALL.getMessageId())) {

                    Log.d("TAG", "CALLRESULT received: " + CALL.getAction());
                    respondedPayload = ((CALLRESULT) msg).getPayload();
                    Log.d("TAG", "respondedPayload: " + respondedPayload);
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
            if (msg instanceof CALLERROR) {

            }

    }

    public void AfterResetCommand(ResetEnumType type) {
        if(type == ResetEnumType.Immediate && ResetResponse.status == ResetStatusEnumType.Accepted) {

            TransactionEventRequest.eventType =TransactionEventEnumType.Started ;
        }

        if(type == ResetEnumType.OnIdle && ResetResponse.status == ResetStatusEnumType.Accepted){

        }
    }
    private DisplayMessageStatusEnumType processSetDisplayMessageRequest(JSONObject j) throws JSONException {

        MessageInfoRepo messageInfoRepo = new MessageInfoRepo(context.get());

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

        MessageInfoEntity.MessageContent messageContent = new MessageInfoEntity.MessageContent();
        JSONObject displayMessage = j.getJSONObject("message");
        messageContent.content = displayMessage.getString("content");
        messageContent.format = displayMessage.getString("format");
        messageContent.language = displayMessage.getString("language");

        MessageInfoEntity.MessageInfo message = new MessageInfoEntity.MessageInfo(priority,state,startDateTime,endDateTime,transactionId,messageContent) ;
        message.setId(j.getInt("id"));
        messageInfoRepo.insert(message);

        return DisplayMessageStatusEnumType.Accepted ;
    }

    private void processAuthResponse(JSONObject j2) throws JSONException {

        IdTokenRepo idTokenRepo = new IdTokenRepo(context.get());
        ChargingStationStatesRepo chargingStationStatesRepo = new ChargingStationStatesRepo(context.get());

        String status = j2.getString("status");
        String cacheExpiryDateTime =  j2.getString("cacheExpiryDateTime");
        int chargingPriority = j2.getInt("chargingPriority");
        int evseId = j2.getInt("evseId") ;

        MessageContent personalMessage = new MessageContent();
        JSONObject j3 = j2.getJSONObject("personalMessage");
        personalMessage.setContent(j3.getString("content"));
        personalMessage.setLanguage(j3.getString("language"));
        personalMessage.setFormat(j3.getString("format")) ;

        idTokenRepo.deleteIdTokenInfo();

        String transactionId = idTokenRepo.getIdToken().getTransactionId();

        idTokenRepo.insertIdTokenInfo(new IdTokenInfoEntity(status,cacheExpiryDateTime,chargingPriority,evseId,personalMessage));
        if (status.equals("Accepted")) {
            chargingStationStatesRepo.updateAuthorized(transactionId ,true);
        }

    }

    private void processBootResponse(JSONObject jsonObject) throws JSONException {
        bootNotificationResponse.setBootStatus(RegistrationStatusEnumType.valueOf(jsonObject.getString("status"))) ;
        bootNotificationResponse.setBootInterval(jsonObject.getInt("interval"));
    }

    private JSONObject processCostUpdatedRequest(JSONObject requestPayload) throws JSONException {
        ChargeRepo chargeRepo = new ChargeRepo(context.get()) ;
        chargeRepo.updateCost((float)requestPayload.getDouble("totalCost"),requestPayload.getString("transactionId"));
        return CostUpdatedResponse.payload() ;
    }

    //BOOT
    public BootNotificationResponse getBootNotificationResponse(){
        return bootNotificationResponse;
    }

    private void sendResponse(final CALLRESULT callresult) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    MyClientEndpoint.getInstance().getOpenSession().getBasicRemote().sendObject(callresult);
                    Log.d("TAG", "Message Sent: " + CALL.getAction() + callresult.getPayload());

                } catch (IOException | EncodeException e) {
                    Log.e("ERROR", "IOException in BasicRemote");
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }

    private void sendRequest(final CALL call) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MyClientEndpoint.getInstance().getOpenSession().getBasicRemote().sendObject(call);
                    Log.d("TAG", "Message Sent: " + CALL.getAction() + call.getPayload());

                } catch (IOException | EncodeException e) {
                    Log.e("ERROR", "IOException in BasicRemote");
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }

    public List<JSONObject> processGetDisplayMessages(List<MessageInfoEntity.MessageInfo> messageInfoEntityList) throws JSONException {

        List<JSONObject> notify = new ArrayList<>();
        for(int i = 0 ; i <messageInfoEntityList.size() ; i++){

            MessageInfoEntity.MessageInfo m = messageInfoEntityList.get(i);
            MessageInfoType.setId(m.getId());
            MessageInfoType.setPriority(m.getPriority());
            MessageInfoType.setState(m.getState());
            MessageInfoType.setStartDateTime(m.getStartDateTime());
            MessageInfoType.setEndDataTime(m.getEndDataTime());
            MessageInfoType.setTransactionId(m.getTransactionId());
            MessageContentType.setFormat(MessageFormatEnumType.valueOf(m.getMessage().format));
            MessageContentType.setLanguage(m.getMessage().language);
            MessageContentType.setContent(m.getMessage().content);

            notify.add(i, MessageInfoType.getp()) ;
        }
        return notify ;
    }

    public JSONObject processSetVariablesRequest(JSONArray setVariableData) throws JSONException {

        JSONArray setVariableResult = new JSONArray();

        for (int i = 0; i < setVariableData.length(); i++) {
            JSONObject item = setVariableData.getJSONObject(i);
            String component = item.getString("component");
            String variable = item.getString("variable");
            String attributeValue = item.getString("attributeValue");
            AttributeEnumType attributeEnumType = AttributeEnumType.valueOf(item.getString("attributeEnumType"));
            ControllerRepo controllerRepo = new ControllerRepo(context.get());

            ComponentType componentType = new ComponentType(component);
            VariableType variableType = new VariableType(variable);

            if (!controllerRepo.isComponent(component)) {
                SetVariableResultType result = new SetVariableResultType(attributeEnumType, SetVariableStatusEnumType.UnknownComponent, componentType, variableType);
                setVariableResult.put(i, result.getp());
                continue;
            }
            if (!controllerRepo.isVariable(component, variable)) {
                SetVariableResultType result = new SetVariableResultType(attributeEnumType, SetVariableStatusEnumType.UnknownVariable, componentType, variableType);
                setVariableResult.put(i, result.getp());
                continue;
            }

            if (controllerRepo.updateController(component, variable, attributeValue, attributeEnumType.name())) {
                SetVariableResultType result = new SetVariableResultType(attributeEnumType, SetVariableStatusEnumType.Accepted, componentType, variableType);
                setVariableResult.put(i, result.getp());
            } else {
                SetVariableResultType result = new SetVariableResultType(attributeEnumType, SetVariableStatusEnumType.Rejected, componentType, variableType);
                setVariableResult.put(i, result.getp());
            }
        }
        return SetVariablesResponse.payload(setVariableResult);
    }

    public JSONObject processGetVariablesRequest(JSONArray getVariableData) throws JSONException {
        JSONArray getVariableResult = new JSONArray();

        for (int i = 0; i < getVariableData.length(); i++) {
            JSONObject item = getVariableData.getJSONObject(i);
            String component = item.getString("component");
            String variable = item.getString("variable");
            AttributeEnumType attributeEnumType = AttributeEnumType.valueOf(item.getString("attributeEnumType"));

            ControllerRepo controllerRepo = new ControllerRepo(context.get());

            ComponentType componentType = new ComponentType(component);
            VariableType variableType = new VariableType(variable);

            if (!controllerRepo.isComponent(component)) {
                GetVariableResultType result = new GetVariableResultType(GetVariableStatusEnumType.UnknownComponent, attributeEnumType, "", componentType, variableType);
                getVariableResult.put(i, result.getp());
                continue;
            }
            if (!controllerRepo.isVariable(component, variable)) {
                GetVariableResultType result = new GetVariableResultType(GetVariableStatusEnumType.UnknownVariable, attributeEnumType, "", componentType, variableType);
                getVariableResult.put(i, result.getp());
                continue;
            }

            if (!controllerRepo.getController(component, variable).getMutability().equals(MutabilityEnumType.WriteOnly.name())) {
                String attributeValue = controllerRepo.getController(component, variable).getvalue();
                GetVariableResultType result = new GetVariableResultType(GetVariableStatusEnumType.Accepted, attributeEnumType, attributeValue, componentType, variableType);
                getVariableResult.put(i, result.getp());
            } else {
                GetVariableResultType result = new GetVariableResultType(GetVariableStatusEnumType.Rejected, attributeEnumType, "", componentType, variableType);
                getVariableResult.put(i, result.getp());
            }
        }
        return GetVariablesResponse.payload(getVariableResult);
    }

    public JSONObject processSetNetworkProfileRequest(JSONObject requestPayload) throws JSONException {

        int configurationSlot = requestPayload.getInt("configurationSlot");
        JSONObject connectionData = requestPayload.getJSONObject("connectionData");

        String ocppVersion = OCPPVersionEnumType.valueOf(connectionData.getString("ocppVersion")).name();
        String ocppTransport = OCPPTransportEnumType.valueOf(connectionData.getString("ocppTransport")).name();
        String ocppCsmsUrl = connectionData.getString("ocppCsmsUrl");
        int messageTimeOut = connectionData.getInt("messageTimeOut");
        String ocppInterface = OCPPInterfaceEnumType.valueOf(connectionData.getString("ocppInterface")).name();

        if (isValidUrl(ocppCsmsUrl)) {
            NetworkProfile networkProfile = new NetworkProfile(new NetworkProfile.NetworkConnectionProfileType(ocppVersion, ocppTransport, ocppCsmsUrl, messageTimeOut, ocppInterface));
            networkProfile.setConfigurationSlot(configurationSlot);
            networkProfileRepo.insert(networkProfile);
            SetNetworkProfileResponse.setStatus(SetNetworkProfileStatusEnumType.Accepted);
        } else {
            SetNetworkProfileResponse.setStatus(SetNetworkProfileStatusEnumType.Rejected);
        }
        return SetNetworkProfileResponse.payload();
    }

}
