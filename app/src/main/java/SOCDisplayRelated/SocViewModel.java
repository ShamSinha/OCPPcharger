package SOCDisplayRelated;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Controller_Components.ControllerRepo;


public class SocViewModel extends AndroidViewModel {

    private SocRepo repo ;

    private LiveData<SocEntity>Soc ;
    public SocViewModel(@NonNull Application application) {
        super(application);
        repo = new SocRepo(application);
        Soc = repo.getSoc();
    }

    public void insert(SocEntity Soc){
        repo.insert(Soc);
    }
    public void update(SocEntity Soc){
        repo.update(Soc);
    }

    public LiveData<SocEntity> getSoc() {
        return Soc;
    }

    public void deleteAll(){
         repo.deleteAll();
    }


}
