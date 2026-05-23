package DisplayMessagesRelated;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;



public interface MessageInfoDao {

    @Insert
    public void insert(MessageInfoEntity.MessageInfo messageInfo);

    @Update
    public void update(MessageInfoEntity.MessageInfo messageInfo);

    @Query("SELECT * FROM MessageInfo WHERE id = :id ")
    MessageInfoEntity.MessageInfo getMessageInfoById(int id );

    @Query("SELECT * FROM MessageInfo WHERE priority = :priority ")
    List<MessageInfoEntity.MessageInfo> getMessageInfoByPriority(String priority);

    @Query("SELECT * FROM MessageInfo WHERE state = :state")
    List<MessageInfoEntity.MessageInfo> getMessageInfoByState(String state);

    @Query("SELECT * FROM MessageInfo")
    List<MessageInfoEntity.MessageInfo> getAllMessageInfo();
}
