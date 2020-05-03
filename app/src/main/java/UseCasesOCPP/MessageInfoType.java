package UseCasesOCPP;

import DataType.ComponentType;
import DataType.MessageContentType;
import EnumDataType.MessagePriorityEnumType;
import EnumDataType.MessageStateEnumType;

public class MessageInfoType {

    private int id ;
    private MessagePriorityEnumType priority ;
    private MessageStateEnumType state ;
    private String startDateTime ;
    private String endDataTime ;
    private String transactionId ;
    private MessageContentType message ;

    public void setId(int id) {
        this.id = id;
    }

    public  void setPriority(MessagePriorityEnumType priority) {
        this.priority = priority;
    }

    public  void setState(MessageStateEnumType state) {
        this.state = state;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDataTime(String endDataTime) {
        this.endDataTime = endDataTime;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setMessage(MessageContentType message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public MessagePriorityEnumType getPriority() {
        return priority;
    }

    public MessageStateEnumType getState() {
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

    public MessageContentType getMessage() {
        return message;
    }
}
