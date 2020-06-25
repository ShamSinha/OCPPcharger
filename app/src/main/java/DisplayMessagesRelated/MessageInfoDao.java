package DisplayMessagesRelated;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;



public interface MessageInfoDao {

    @Insert
    public void insert(MessageInfoType.MessageInfo messageInfo);

    @Update
    public void update(MessageInfoType.MessageInfo messageInfo);

    @Query("SELECT * FROM MessageInfoType.MessageInfo WHERE id = :id ")
    MessageInfoType.MessageInfo getMessageInfoById(int id );

    @Query("SELECT * FROM MessageInfoType.MessageInfo WHERE priority = :priority ")
    List<MessageInfoType.MessageInfo> getMessageInfoByPriority(String priority);

    @Query("SELECT * FROM MessageInfoType.MessageInfo WHERE state = :state")
    List<MessageInfoType.MessageInfo> getMessageInfoByState(String state);


}
