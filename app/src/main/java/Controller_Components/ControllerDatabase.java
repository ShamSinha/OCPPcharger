package Controller_Components;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import EnumDataType.DataEnumType;
import EnumDataType.MutabilityEnumType;

@Database(entities = {Controller.class} , version = 1)
public abstract class ControllerDatabase extends RoomDatabase {

    private static ControllerDatabase instance ;

    public abstract ControllerDao controllerDao();

    public static synchronized ControllerDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),ControllerDatabase.class,"controller_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(controllerCallback)
                    .build();
        }
        return instance ;
    }

    private static ControllerDatabase.Callback controllerCallback = new ControllerDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{

        private ControllerDao controllerDao ;
        private PopulateDbAsyncTask(ControllerDatabase db){
            controllerDao = db.controllerDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            controllerDao.insert(new Controller("AuthCtrlr","Enabled", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),"True"));
            controllerDao.insert(new Controller("AuthCtrlr","OfflineTxForUnknownIdEnabled", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("AuthCtrlr","AuthorizeRemoteStart", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("AuthCtrlr","LocalAuthorizeOffline", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("AuthCtrlr","LocalPreAuthorize", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));

            controllerDao.insert(new Controller("DisplayMessageCtrlr","Enabled", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("DisplayMessageCtrlr","Available", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("DisplayMessageCtrlr","DisplayMessages", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("DisplayMessageCtrlr","PersonalMessageSize", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("DisplayMessageCtrlr","SupportedFormats", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.MemberList.toString(),""));
            controllerDao.insert(new Controller("DisplayMessageCtrlr","SupportedPriorities", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.MemberList.toString(),""));

            controllerDao.insert(new Controller("OCPPCommCtrlr","HeartbeatInterval", MutabilityEnumType.ReadWrite.toString(),"seconds", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","RetryBackOffRepeatTimes", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","RetryBackOffRandomRange", MutabilityEnumType.ReadWrite.toString(),"seconds", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","RetryBackOffWaitMinimum", MutabilityEnumType.ReadWrite.toString(),"seconds", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","WebSocketPingInterval", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","DefaultMessageTimeout", MutabilityEnumType.ReadWrite.toString(),"seconds", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","ResetRetries", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("OCPPCommCtrlr","UnlockOnEVSideDisconnect", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));

            controllerDao.insert(new Controller("TxCtrlr","ChargingBeforeAcceptedEnabled", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("TxCtrlr","StopTxOnEVSideDisconnect", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("TxCtrlr","TxBeforeAcceptedEnabled", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("TxCtrlr","StopTxOnInvalidId", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("TxCtrlr","EVConnectionTimeOut", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("TxCtrlr","MaxEnergyOnInvalidId", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));

            controllerDao.insert(new Controller("SecurityCtrlr","CertificateEntries", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.integer.toString(),"0"));
            controllerDao.insert(new Controller("SecurityCtrlr","SecurityProfile", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.integer.toString(),""));
            controllerDao.insert(new Controller("SecurityCtrlr","AdditionalRootCertificateCheck", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("SecurityCtrlr","Identity", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.string.toString(),""));
            controllerDao.insert(new Controller("SecurityCtrlr","BasicAuthPassword", MutabilityEnumType.WriteOnly.toString(),"", DataEnumType.string.toString(),""));
            controllerDao.insert(new Controller("SecurityCtrlr","OrganizationName", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.string.toString(),""));

            controllerDao.insert(new Controller("TariffCostCtrlr","TariffFallbackMessage", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.string.toString(),""));
            controllerDao.insert(new Controller("TariffCostCtrlr","TotalCostFallbackMessage", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.string.toString(),""));
            controllerDao.insert(new Controller("TariffCostCtrlr","Currency", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.string.toString(),"INR"));

            controllerDao.insert(new Controller("ReservationCtrlr","Enabled", MutabilityEnumType.ReadWrite.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("ReservationCtrlr","Available", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.Boolean.toString(),""));
            controllerDao.insert(new Controller("ReservationCtrlr","NonEvseSpecific", MutabilityEnumType.ReadOnly.toString(),"", DataEnumType.Boolean.toString(),""));

            return null;
        }
    }
}
