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
import EnumDataType.RPCErrorCodes;
import EnumDataType.RegistrationStatusEnumType;
import EnumDataType.ResetEnumType;
import EnumDataType.ResetStatusEnumType;
import EnumDataType.SetNetworkProfileStatusEnumType;
import EnumDataType.SetVariableStatusEnumType;
import TransactionRelated.TransactionEventEnumType;
import UseCasesOCPP.BootNotificationResponse;
import UseCasesOCPP.SendRequestToCSMS;

@ClientEndpoint(
        decoders = {MessageDecoder.class},
        encoders = {MessageEncoder.class,MessageEncodeResult.class , MessageEncodeError.class},
        subprotocols = {"ocpp2.1", "ocpp2.0.1"},
        configurator = ClientConfigurator.class
)

public class MyClientEndpoint  {

    private static final String DEFAULT_CSMS_URL = "ws://10.0.2.2:8080/CSMSWebsocketServer-1/CS01";

    public interface ConnectionListener {
        void onBootAccepted(int interval);
        void onBootRejected(RegistrationStatusEnumType status, int interval);
        void onConnectionFailed(String message);
    }

    public interface CostUpdateListener {
        void onCostUpdated(String transactionId, float totalCost);
    }

    private Session session ;
    private ConnectionListener connectionListener;
    private CostUpdateListener costUpdateListener;
    private float latestCost;
    private String latestCostTransactionId = "";

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
    private IdTokenInfoEntity idInfo = new IdTokenInfoEntity("Invalid", "", 0, 0, new MessageContent());


    public Session getOpenSession() {
        return session;
    }

    public IdTokenInfoEntity getIdInfo() {
        return idInfo;
    }

    public void setCostUpdateListener(CostUpdateListener costUpdateListener) {
        this.costUpdateListener = costUpdateListener;
    }

    public float getLatestCost() {
        return latestCost;
    }

    public String getLatestCostTransactionId() {
        return latestCostTransactionId;
    }

    public NetworkProfileRepo networkProfileRepo ;



    void ConnectClientToServer(final TextView text) {
        ConnectClientToServer(text, null);
    }

    void ConnectClientToServer(final TextView text, ConnectionListener listener) {
        connectionListener = listener;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectToWebSocket(text);
            }
        });
        thread.start();
    }

    private void connectToWebSocket(final TextView text)  {
        //WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        // session = container.connectToServer(this, uri);

        Context appContext = context == null ? null : context.get();
        if (appContext == null) {
            notifyConnectionFailed("Android context is not initialized");
            appendText(text, "\nAndroid context is not initialized\n");
            return;
        }

        networkProfileRepo = new NetworkProfileRepo(appContext);
        NetworkProfile networkProfile = networkProfileRepo.getNetworkProfile(1) ;

        URI uri;
        try {
            uri = URI.create(resolveCsmsUrl(networkProfile));
        } catch (IllegalArgumentException e) {
            notifyConnectionFailed("Invalid CSMS websocket URL");
            appendText(text, "\nInvalid CSMS websocket URL\n");
            return;
        }

        ChargingStationRepo chargingStationRepo = new ChargingStationRepo(appContext);

        ClientManager client = ClientManager.createClient();

        client.getProperties().put(ClientProperties.CREDENTIALS, new Credentials("ws_user", "password")); // Basic Authentication for Charging Station
        client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);

        session = null;
        try {
            client.connectToServer(this,uri) ;
        } catch (DeploymentException e) {
            e.printStackTrace();
            notifyConnectionFailed("Deployment Exception: CSMS connection failed");
            appendText(text, "\nDeployment Exception"+ R.string.conncsmsnot + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            notifyConnectionFailed("IO Exception: CSMS connection failed");
            appendText(text, "\nIO Exception" + R.string.conncsmsnot + "\n");
        }


        if(session != null){
            appendText(text, "Connection with CSMS Established");
            appendText(text, "\nConnected to Session :"+ session.getId() + "\n" );
            appendText(text, "\nBoot Reason: "+ BootNotificationRequest.getReason()+"\n");

            ChargingStation chargingStation = chargingStationRepo.getChargingStationType() ;

            if (chargingStation != null) {
                ChargingStationType.setSerialNumber(chargingStation.getSerialNumber());
                ChargingStationType.setModel(chargingStation.getModel());
                ChargingStationType.setVendorName(chargingStation.getVendorName());
                ChargingStationType.setFirmwareVersion(chargingStation.getFirmwareVersion());
                if (chargingStation.getModem() != null) {
                    ModemType.setIccid(chargingStation.getModem().iccid);
                    ModemType.setImsi(chargingStation.getModem().imsi);
                }
            }

            appendText(text, "\nCharging Station\n");
            appendText(text, "\nserialNumber: "+ChargingStationType.serialNumber+"\n");
            appendText(text, "\nmodel: "+ChargingStationType.model+"\n");
            appendText(text, "\nvendorName: "+ChargingStationType.vendorName+"\n");
            appendText(text, "\nfirmwareVersion: "+ChargingStationType.firmwareVersion+"\n");
            appendText(text, "\nmodem iccid:"+ ModemType.iccid+"\n");
            appendText(text, "\nmodem imsi:"+ ModemType.imsi+"\n");
            appendText(text, "\nSending BootNotificationRequest to CSMS\n");
            try {
                toCSMS.sendBootNotificationRequest();
            } catch (JSONException e) {
                e.printStackTrace();
                notifyConnectionFailed("BootNotification payload could not be created");
            }
        }
    }

    private String resolveCsmsUrl(NetworkProfile networkProfile) {
        if (networkProfile == null
                || networkProfile.getConnectionData() == null
                || networkProfile.getConnectionData().getOcppCsmsUrl() == null
                || networkProfile.getConnectionData().getOcppCsmsUrl().trim().length() == 0) {
            return DEFAULT_CSMS_URL;
        }
        return networkProfile.getConnectionData().getOcppCsmsUrl().trim();
    }

    private void appendText(final TextView text, final String value) {
        if (text == null) {
            return;
        }
        text.post(new Runnable() {
            @Override
            public void run() {
                text.append(value);
            }
        });
    }

    private void notifyConnectionFailed(String message) {
        final ConnectionListener listener = connectionListener;
        if (listener != null) {
            listener.onConnectionFailed(message);
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
            boolean sendCallResult = false;
            JSONObject requestPayload = ((CALL) msg).getPayload(); // get JSON payload from server request
            Log.d("TAG", "requestPayload: " + requestPayload);
            switch (CALL.getAction()) {
                case "CostUpdated":

                    responsePayload = processCostUpdatedRequest(requestPayload);
                    sendCallResult = true;

                    break;
                case "SetDisplayMessage":
                case "SetDisplayMessages":
                    JSONObject setDisplayMessage = requestPayload.getJSONObject("message");
                    DisplayMessageStatusEnumType status = processSetDisplayMessageRequest(setDisplayMessage);
                    SetDisplayMessagesResponse.setStatus(status);
                    responsePayload = SetDisplayMessagesResponse.payload();
                    sendCallResult = true;
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
                        messageInfoEntityList = messageInfoRepo.getMessageInfoByPriority(priority);
                        if (messageInfoEntityList != null && !messageInfoEntityList.isEmpty()) {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Accepted);
                            notifyDisplayMessage = processGetDisplayMessages(messageInfoEntityList);
                        } else {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Unknown);
                        }
                    } else if (requestPayload.has("state")) {
                        String state = requestPayload.getString("state");
                        messageInfoEntityList = messageInfoRepo.getMessageInfoByState(state);
                        if (messageInfoEntityList != null && !messageInfoEntityList.isEmpty()) {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Accepted);
                            notifyDisplayMessage = processGetDisplayMessages(messageInfoEntityList);
                        } else {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Unknown);
                        }
                    } else {
                        messageInfoEntityList = messageInfoRepo.getAllMessageInfo();
                        if (messageInfoEntityList != null && !messageInfoEntityList.isEmpty()) {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Accepted);
                            notifyDisplayMessage = processGetDisplayMessages(messageInfoEntityList);
                        } else {
                            GetDisplayMessagesResponse.setStatus(GetDisplayMessagesStatusEnumType.Unknown);
                        }
                    }
                    sendResponse(new CALLRESULT(GetDisplayMessagesResponse.payload()));
                    responsePayload = new JSONObject();

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
                    break;

                case "Reset":
                    AfterResetCommand(ResetEnumType.valueOf(requestPayload.getString("type")));
                    responsePayload = ResetResponse.payload();
                    sendCallResult = true;
                    break;
                case "ReserveNow":
                    sendUnsupportedAction(CALL.getAction());
                    return;
                case "RequestStartTransaction":
                    sendUnsupportedAction(CALL.getAction());
                    return;
                case "TriggerMessage":
                    sendUnsupportedAction(CALL.getAction());
                    return;
                case "SetVariables":
                    JSONArray setVariableData = requestPayload.getJSONArray("setVariableData");

                    responsePayload = processSetVariablesRequest(setVariableData) ;
                    sendCallResult = true;

                    break;
                case "GetVariables":
                    JSONArray getVariableData = requestPayload.getJSONArray("getVariableData");

                    responsePayload = processGetVariablesRequest(getVariableData) ;
                    sendCallResult = true;

                    break;
                case "SetNetworkProfile" :

                    responsePayload = processSetNetworkProfileRequest(requestPayload);
                    sendCallResult = true;
                    break;

                default:
                    sendUnsupportedAction(CALL.getAction());
                    return;
                }

                if (sendCallResult) {
                    sendResponse(new CALLRESULT(responsePayload));
                }

            }
            if (msg instanceof CALLRESULT) {

                Log.d("TAG", "CALL received: " + CALL.getAction());
                JSONObject respondedPayload;  // respondedPayload is a CALL message Response from CSMS
                if (CALL.getMessageId() != null && CALL.getMessageId().equals(CALLRESULT.getMessageId())) {

                    Log.d("TAG", "CALLRESULT received: " + CALL.getAction());
                    respondedPayload = ((CALLRESULT) msg).getPayload();
                    Log.d("TAG", "respondedPayload: " + respondedPayload);
                    switch (CALL.getAction()) {

                        case "BootNotification":

                            processBootResponse(respondedPayload);
                            notifyBootResponse();
                            break;

                        case "Authorize":

                            JSONObject authResponse = respondedPayload.getJSONObject("idTokenInfo");
                            processAuthResponse(authResponse);
                            break;

                        case "Heartbeat":
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
        if (!isSupportedPriority(priority)) {
            return DisplayMessageStatusEnumType.NotSupportedPriority;
        }
        String state = j.getString("state") ;
        if (!isSupportedState(state)) {
            return DisplayMessageStatusEnumType.NotSupportedState;
        }
        String startDateTime = j.optString("startDateTime", "");
        String endDateTime = j.optString("endDateTime", j.optString("endDataTime", ""));
        String transactionId = j.optString("transactionId", "");

        MessageInfoEntity.MessageContent messageContent = new MessageInfoEntity.MessageContent();
        JSONObject displayMessage = j.getJSONObject("message");
        messageContent.content = displayMessage.optString("content", "");
        messageContent.format = displayMessage.optString("format", MessageFormatEnumType.UTF8.name());
        if (!isSupportedFormat(messageContent.format)) {
            return DisplayMessageStatusEnumType.NotSupportedMessageFormat;
        }
        messageContent.language = displayMessage.optString("language", "");

        MessageInfoEntity.MessageInfo message = new MessageInfoEntity.MessageInfo(priority,state,startDateTime,endDateTime,transactionId,messageContent) ;
        message.setId(j.getInt("id"));
        messageInfoRepo.insert(message);

        return DisplayMessageStatusEnumType.Accepted ;
    }

    private boolean isSupportedPriority(String priority) {
        for (MessagePriorityEnumType supportedPriority : MessagePriorityEnumType.values()) {
            if (supportedPriority.name().equals(priority)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedState(String state) {
        for (MessageStateEnumType supportedState : MessageStateEnumType.values()) {
            if (supportedState.name().equals(state)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedFormat(String format) {
        for (MessageFormatEnumType supportedFormat : MessageFormatEnumType.values()) {
            if (supportedFormat.name().equals(format)) {
                return true;
            }
        }
        return false;
    }

    private void processAuthResponse(JSONObject j2) throws JSONException {

        IdTokenRepo idTokenRepo = new IdTokenRepo(context.get());
        ChargingStationStatesRepo chargingStationStatesRepo = new ChargingStationStatesRepo(context.get());

        String status = j2.getString("status");
        String cacheExpiryDateTime =  j2.optString("cacheExpiryDateTime", "");
        int chargingPriority = j2.optInt("chargingPriority", 0);
        int evseId = 0;
        if (j2.has("evseId")) {
            if (j2.optJSONArray("evseId") != null && j2.optJSONArray("evseId").length() > 0) {
                evseId = j2.optJSONArray("evseId").optInt(0, 0);
            } else {
                evseId = j2.optInt("evseId", 0);
            }
        }

        MessageContent personalMessage = new MessageContent();
        if (j2.has("personalMessage")) {
            JSONObject j3 = j2.getJSONObject("personalMessage");
            personalMessage.setContent(j3.optString("content", ""));
            personalMessage.setLanguage(j3.optString("language", ""));
            personalMessage.setFormat(j3.optString("format", "")) ;
        }

        idTokenRepo.deleteIdTokenInfo();

        String transactionId = idTokenRepo.getIdToken() == null ? "" : idTokenRepo.getIdToken().getTransactionId();

        idInfo = new IdTokenInfoEntity(status,cacheExpiryDateTime,chargingPriority,evseId,personalMessage);
        idTokenRepo.insertIdTokenInfo(idInfo);
        if (status.equals("Accepted")) {
            chargingStationStatesRepo.updateAuthorized(transactionId ,true);
        }

    }

    private void processBootResponse(JSONObject jsonObject) throws JSONException {
        bootNotificationResponse.setBootStatus(RegistrationStatusEnumType.valueOf(jsonObject.getString("status"))) ;
        bootNotificationResponse.setBootInterval(jsonObject.getInt("interval"));
    }

    private void notifyBootResponse() {
        final ConnectionListener listener = connectionListener;
        if (listener == null || bootNotificationResponse.getBootStatus() == null) {
            return;
        }

        if (bootNotificationResponse.getBootStatus() == RegistrationStatusEnumType.Accepted) {
            listener.onBootAccepted(bootNotificationResponse.getBootInterval());
        } else {
            listener.onBootRejected(bootNotificationResponse.getBootStatus(), bootNotificationResponse.getBootInterval());
        }
    }

    private JSONObject processCostUpdatedRequest(JSONObject requestPayload) throws JSONException {
        latestCost = (float) requestPayload.getDouble("totalCost");
        latestCostTransactionId = requestPayload.optString("transactionId", "");
        CostUpdateListener listener = costUpdateListener;
        if (listener != null) {
            listener.onCostUpdated(latestCostTransactionId, latestCost);
        }
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

    private void sendUnsupportedAction(String action) {
        RPCErrorCodes code = OcppActionRegistry.isKnownAction(action)
                ? RPCErrorCodes.NotSupported
                : RPCErrorCodes.NotImplemented;
        String description = OcppActionRegistry.isKnownAction(action)
                ? "Action is recognized by OCPP 2.1 but not supported by this charging station"
                : "Action is not known by this charging station";
        sendError(new CALLERROR(code, description, new JSONObject()));
    }

    private void sendError(final CALLERROR callerror) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    if (MyClientEndpoint.getInstance().getOpenSession() == null
                            || !MyClientEndpoint.getInstance().getOpenSession().isOpen()) {
                        Log.e("ERROR", "OCPP websocket session is not open");
                        return;
                    }
                    MyClientEndpoint.getInstance().getOpenSession().getBasicRemote().sendObject(callerror);
                    Log.d("TAG", "CALLERROR Sent: " + callerror.getErrorCode());
                } catch (IOException | EncodeException e) {
                    Log.e("ERROR", "IOException in BasicRemote");
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }

    private void sendRequest(final CALL call) {
        CALL.setMessageIdIfCallHasToSent();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CALL.setMessageIdIfCallHasToSent();
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

        if (isValidOcppWebsocketUrl(ocppCsmsUrl)) {
            NetworkProfile networkProfile = new NetworkProfile(new NetworkProfile.NetworkConnectionProfileType(ocppVersion, ocppTransport, ocppCsmsUrl, messageTimeOut, ocppInterface));
            networkProfile.setConfigurationSlot(configurationSlot);
            networkProfileRepo.insert(networkProfile);
            SetNetworkProfileResponse.setStatus(SetNetworkProfileStatusEnumType.Accepted);
        } else {
            SetNetworkProfileResponse.setStatus(SetNetworkProfileStatusEnumType.Rejected);
        }
        return SetNetworkProfileResponse.payload();
    }

    private boolean isValidOcppWebsocketUrl(String ocppCsmsUrl) {
        if (ocppCsmsUrl == null) {
            return false;
        }
        try {
            URI uri = URI.create(ocppCsmsUrl);
            return ("ws".equalsIgnoreCase(uri.getScheme()) || "wss".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null
                    && uri.getPath() != null
                    && uri.getPath().length() > 1;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
