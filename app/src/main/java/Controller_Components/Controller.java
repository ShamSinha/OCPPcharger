package Controller_Components;


import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity
public class Controller {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public String componentName ;
    public String variableName ;
    public String mutability ;
    public String unit ;
    public String dataType ;
    public String value ;

    public Controller(String componentName, String variableName, String mutability, String unit, String dataType, String value) {
        this.componentName = componentName;
        this.variableName = variableName;
        this.mutability = mutability;
        this.unit = unit;
        this.dataType = dataType;
        this.value = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getMutability() {
        return mutability;
    }

    public String getUnit() {
        return unit;
    }

    public String getDataType() {
        return dataType;
    }

    public String getvalue() {
        return value;
    }
}
