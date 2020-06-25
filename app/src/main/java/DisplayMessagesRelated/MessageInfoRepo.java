package DisplayMessagesRelated;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;


public class MessageInfoRepo {
    private MessageInfoDao messageInfoDao;

    public MessageInfoRepo(Context context){
        MessageInfoDatabase database = MessageInfoDatabase.getInstance(context);
        messageInfoDao = database.messageInfoDao() ;

    }

    public void insert(MessageInfoEntity.MessageInfo messageInfo){
        new MessageInfoRepo.InsertMessageInfoAsyncTask(messageInfoDao).execute(messageInfo);

    }
    public void update(MessageInfoEntity.MessageInfo messageInfo){
        new MessageInfoRepo.UpdateMessageInfoAsyncTask(messageInfoDao).execute(messageInfo);
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



    private static class InsertMessageInfoAsyncTask extends AsyncTask<MessageInfoEntity.MessageInfo,Void,Void> {
        private MessageInfoDao messageInfoDao;

        private InsertMessageInfoAsyncTask(MessageInfoDao messageInfoDao){
            this.messageInfoDao = messageInfoDao ;
        }
        @Override
        protected Void doInBackground(MessageInfoEntity.MessageInfo... messageInfos) {
            messageInfoDao.insert(messageInfos[0]);
            return null;
        }
    }

    private static class UpdateMessageInfoAsyncTask extends AsyncTask<MessageInfoEntity.MessageInfo,Void,Void>{
        private MessageInfoDao messageInfoDao;

        private UpdateMessageInfoAsyncTask(MessageInfoDao messageInfoDao){
            this.messageInfoDao = messageInfoDao ;
        }
        @Override
        protected Void doInBackground(MessageInfoEntity.MessageInfo... messageInfos) {
            messageInfoDao.update(messageInfos[0]);
            return null;
        }
    }



}
