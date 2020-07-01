package com.example.chargergui;


import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NetworkProfile {

    public static class NetworkConnectionProfileType{

        public String ocppVersion ;
        public String ocppTransport ;
        public String ocppCsmsUrl ;
        public int messageTimeOut ;
        public String ocppInterface ;

        public NetworkConnectionProfileType(String ocppVersion, String ocppTransport, String ocppCsmsUrl, int messageTimeOut, String ocppInterface) {
            this.ocppVersion = ocppVersion;
            this.ocppTransport = ocppTransport;
            this.ocppCsmsUrl = ocppCsmsUrl;
            this.messageTimeOut = messageTimeOut;
            this.ocppInterface = ocppInterface;
        }

        public String getOcppVersion() {
            return ocppVersion;
        }

        public String getOcppTransport() {
            return ocppTransport;
        }

        public String getOcppCsmsUrl() {
            return ocppCsmsUrl;
        }

        public int getMessageTimeOut() {
            return messageTimeOut;
        }

        public String getOcppInterface() {
            return ocppInterface;
        }
    }
    @PrimaryKey
    public int configurationSlot ;

    @Embedded
    public NetworkConnectionProfileType connectionData ;

    public NetworkProfile(NetworkConnectionProfileType connectionData) {
        this.connectionData = connectionData;
    }

    public int getConfigurationSlot() {
        return configurationSlot;
    }

    public void setConfigurationSlot(int configurationSlot) {
        this.configurationSlot = configurationSlot;
    }

    public NetworkConnectionProfileType getConnectionData() {
        return connectionData;
    }

}
