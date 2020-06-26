package SOCDisplayRelated;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


public class SocViewModel extends AndroidViewModel {

    private SocRepo repo ;
    private LiveData<List<SocEntities>> allSoc ;
    public SocViewModel(@NonNull Application application) {
        super(application);
        repo = new SocRepo(application);
        allSoc = repo.getAllSoc();
    }

    public void insert(SocEntities Soc){
        repo.insert(Soc);
    }
    public void update(SocEntities Soc){
        repo.update(Soc);
    }

    public LiveData<List<SocEntities>> getAllSoc() {
        return allSoc;
    }

    public void deleteAll(){
         repo.deleteAll();
    }
}
