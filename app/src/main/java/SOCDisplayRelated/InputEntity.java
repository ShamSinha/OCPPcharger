package SOCDisplayRelated;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InputEntity {

    @PrimaryKey(autoGenerate = true)
    private int id ;

    private final int InputAmount ;
    private final double TargetCharge ;

    public InputEntity(int inputAmount, double targetCharge) {
        InputAmount = inputAmount;
        TargetCharge = targetCharge;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getInputAmount() {
        return InputAmount;
    }

    public double getTargetCharge() {
        return TargetCharge;
    }
}
