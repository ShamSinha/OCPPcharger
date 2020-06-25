package DisplayMessagesRelated;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class MessageInfoType {

    public static class MessageContent {

        public String format;
        public String language;
        public String content;
    }

    @Entity
    public static class MessageInfo {
        @PrimaryKey
        public int id;

        public String priority;
        public String state;
        public String startDateTime;
        public String endDataTime;
        public String transactionId;
        @Embedded public MessageContent message;

        public MessageInfo(String priority, String state, String startDateTime, String endDataTime, String transactionId, MessageContent message) {
            this.priority = priority;
            this.state = state;
            this.startDateTime = startDateTime;
            this.endDataTime = endDataTime;
            this.transactionId = transactionId;
            this.message = message;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getPriority() {
            return priority;
        }

        public String getState() {
            return state;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public String getEndDataTime() {
            return endDataTime;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public MessageContent getMessage() {
            return message;
        }
    }

}
