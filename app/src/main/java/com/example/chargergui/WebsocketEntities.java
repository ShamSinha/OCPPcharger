package com.example.chargergui;


import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class  WebsocketEntities {
    @Entity
    public static class CallSent {

        @PrimaryKey
        public String MessageId;

        public Boolean isCallSent;
        public Boolean isCallResultArrived;

        public CallSent(Boolean isCallSent, Boolean isCallResultArrived) {
            this.isCallSent = isCallSent;
            this.isCallResultArrived = isCallResultArrived;
        }

        public void setMessageId(String messageId) {
            MessageId = messageId;
        }

        public String getMessageId() {
            return MessageId;
        }

        public Boolean getCallSent() {
            return isCallSent;
        }

        public Boolean getCallResultArrived() {
            return isCallResultArrived;
        }

    }

    @Entity
    public static class CallArrived{

        @PrimaryKey
        public String MessageId;

        public Boolean isCallArrived;
        public Boolean isCallResultSent;

        public CallArrived(Boolean isCallArrived, Boolean isCallResultSent) {
            this.isCallArrived = isCallArrived;
            this.isCallResultSent = isCallResultSent;
        }

        public void setMessageId(String messageId) {
            MessageId = messageId;
        }

        public String getMessageId() {
            return MessageId;
        }

        public Boolean getCallArrived() {
            return isCallArrived;
        }

        public Boolean getCallResultSent() {
            return isCallResultSent;
        }
    }
}
