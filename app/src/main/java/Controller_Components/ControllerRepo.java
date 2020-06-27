package Controller_Components;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerRepo {

    private ControllerDao controllerDao;


    public ControllerRepo(Context context){
        ControllerDatabase database = ControllerDatabase.getInstance(context);
        controllerDao = database.controllerDao() ;
    }

    public void insert(final Controller controller){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                controllerDao.insert(controller);
            }
        });
        executorService.shutdown();

    }
    public void update(final Controller controller){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                controllerDao.update(controller);
            }
        });
        executorService.shutdown();
    }

    public List<Controller> getAllController(){
        return controllerDao.getAllController() ;
    }

    public Controller getController( String component , String variable){
        return controllerDao.getController(component , variable) ;
    }
    public boolean isComponent(String component){
        return controllerDao.isComponent(component)==1 ;
    }

    public boolean isVariable(String component , String variable){
        return controllerDao.isVariable(component, variable) ==1 ;
    }

    public boolean updateController(String component , String variable, String attributeValue , String attributeType){
        return controllerDao.updateController(component,variable,attributeValue,attributeType) == 1 ;
    }

}