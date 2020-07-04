package DisplayMessagesRelated;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MessageInfoRepo {
    private MessageInfoDao messageInfoDao;

    public MessageInfoRepo(Context context){
        MessageInfoDatabase database = MessageInfoDatabase.getInstance(context);
        messageInfoDao = database.messageInfoDao() ;
    }

    public void insert(final MessageInfoEntity.MessageInfo messageInfo){
            ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                messageInfoDao.insert(messageInfo);
            }
        });
        executorService.shutdown();

    }
    public void update(final MessageInfoEntity.MessageInfo messageInfo){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                messageInfoDao.update(messageInfo);
            }
        });
        executorService.shutdown();
    }

    public MessageInfoEntity.MessageInfo getMessageInfoById(int id ){
        return messageInfoDao.getMessageInfoById(id) ;
    }

    public List<MessageInfoEntity.MessageInfo> getMessageInfoByPriority(String priority){
        return messageInfoDao.getMessageInfoByPriority(priority);
    }

    public List<MessageInfoEntity.MessageInfo> getMessageInfoByState(String state){
        return messageInfoDao.getMessageInfoByState(state) ;
    }

}
