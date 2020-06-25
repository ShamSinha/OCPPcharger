package DisplayMessagesRelated;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;


public class MessageInfoRepo {
    private MessageInfoDao messageInfoDao;

    public MessageInfoRepo(Context context){
        MessageInfoDatabase database = MessageInfoDatabase.getInstance(context);
        messageInfoDao = database.messageInfoDao() ;

    }

    public void insert(MessageInfoType.MessageInfo messageInfo){
        new MessageInfoRepo.InsertMessageInfoAsyncTask(messageInfoDao).execute(messageInfo);

    }
    public void update(MessageInfoType.MessageInfo messageInfo){
        new MessageInfoRepo.UpdateMessageInfoAsyncTask(messageInfoDao).execute(messageInfo);
    }

    public MessageInfoType.MessageInfo getMessageInfoById(int id ){
        return messageInfoDao.getMessageInfoById(id) ;
    }

    public List<MessageInfoType.MessageInfo> getMessageInfoByPriority(String priority){
        return messageInfoDao.getMessageInfoByPriority(priority);
    }

    public List<MessageInfoType.MessageInfo> getMessageInfoByState(String state){
        return messageInfoDao.getMessageInfoByState(state) ;
    }


    private static class InsertMessageInfoAsyncTask extends AsyncTask<MessageInfoType.MessageInfo,Void,Void> {
        private MessageInfoDao messageInfoDao;

        private InsertMessageInfoAsyncTask(MessageInfoDao messageInfoDao){
            this.messageInfoDao = messageInfoDao ;
        }
        @Override
        protected Void doInBackground(MessageInfoType.MessageInfo... messageInfos) {
            messageInfoDao.insert(messageInfos[0]);
            return null;
        }
    }

    private static class UpdateMessageInfoAsyncTask extends AsyncTask<MessageInfoType.MessageInfo,Void,Void>{
        private MessageInfoDao messageInfoDao;

        private UpdateMessageInfoAsyncTask(MessageInfoDao messageInfoDao){
            this.messageInfoDao = messageInfoDao ;
        }
        @Override
        protected Void doInBackground(MessageInfoType.MessageInfo... messageInfos) {
            messageInfoDao.update(messageInfos[0]);
            return null;
        }
    }



}
