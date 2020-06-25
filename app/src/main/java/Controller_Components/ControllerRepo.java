package Controller_Components;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class ControllerRepo {

    private ControllerDao controllerDao;


    public ControllerRepo(Context context){
        ControllerDatabase database = ControllerDatabase.getInstance(context);
        controllerDao = database.controllerDao() ;
    }

    public void insert(Controller controller){
        new InsertControllerAsyncTask(controllerDao).execute(controller);

    }
    public void update(Controller controller){
        new UpdateControllerAsyncTask(controllerDao).execute(controller);
    }

    public List<Controller> getAllController(){
        return controllerDao.getAllController() ;
    }

    public Controller getController( String component , String variable){
        return controllerDao.getController(component , variable) ;
    }

    private static class InsertControllerAsyncTask extends AsyncTask<Controller,Void,Void>{
        private ControllerDao controllerDao;

        private InsertControllerAsyncTask(ControllerDao controllerDao){
            this.controllerDao = controllerDao ;
        }
        @Override
        protected Void doInBackground(Controller... controllers) {
            controllerDao.insert(controllers[0]);
            return null;
        }
    }

    private static class UpdateControllerAsyncTask extends AsyncTask<Controller,Void,Void>{
        private ControllerDao controllerDao;

        private UpdateControllerAsyncTask(ControllerDao controllerDao){
            this.controllerDao = controllerDao ;
        }
        @Override
        protected Void doInBackground(Controller... controllers) {
            controllerDao.update(controllers[0]);
            return null;
        }
    }



}