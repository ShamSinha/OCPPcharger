package SOCDisplayRelated;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import Controller_Components.ControllerRepo;

public class InputViewModel extends AndroidViewModel {

    private InputRepo repo ;
    private ControllerRepo controllerRepo;
    private LiveData<InputEntity> input ;
    public InputViewModel(@NonNull Application application) {
        super(application);
        repo = new InputRepo(application);
        controllerRepo = new ControllerRepo(application);
        input = repo.getInput();
    }

    public void insert(InputEntity input){
        repo.insert(input);
    }
    public void update(InputEntity input){
        repo.update(input);
    }

    public LiveData<InputEntity> getInput() {
        return input;
    }

    public void deleteAll(){
        repo.deleteAll();
    }

    public String getCurrency(){
        return controllerRepo.getController("TariffCostCtrlr","Currency").getvalue() ;
    }
}