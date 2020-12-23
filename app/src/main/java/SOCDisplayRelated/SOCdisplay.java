package SOCDisplayRelated;


import android.content.Intent;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView ;
import android.view.View ;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chargergui.DisplayMessageState;
import com.example.chargergui.ImageSetBattery;
import com.example.chargergui.R;
import com.example.chargergui.UserInput;

import java.util.List;

import Controller_Components.ControllerRepo;
import DisplayMessagesRelated.MessageStateEnumType;

public class SOCdisplay extends AppCompatActivity {
    TextView Batterytype;
    TextView Charge ;
    TextView EnergyinKWh ;
    TextView dateTime ;
    TextView Recommendation ;
    Button Input ;

    ImageView battery ;
    String chargeValue ;
    public float standardrate = 4;


    ControllerRepo controllerRepo ;
    String currency ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_socdisplay);
        Batterytype = findViewById(R.id.batterytype);
        Charge = findViewById(R.id.charge);
        EnergyinKWh = findViewById(R.id.energy);
        battery = findViewById(R.id.tickorcrossIMAGE);

        Recommendation = findViewById(R.id.recommend);
        Input = findViewById(R.id.input);




        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);


        SocViewModel socViewModel = new ViewModelProvider(this).get(SocViewModel.class) ;

        socViewModel.getSoc().observe(this, new Observer<SocEntity>() {
            @Override
            public void onChanged(SocEntity socEntity) {
                Charge.setText(String.valueOf(socEntity.getInitialSOC())) ;
                float energy = (socEntity.getBatteryCapacity()*socEntity.getInitialSOC())/100 ;
                EnergyinKWh.setText(String.valueOf(energy));
                Batterytype.setText(socEntity.getBatteryType());
                new ImageSetBattery(socEntity.getInitialSOC(), battery);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Recommendation.setText(R.string.hh);

    }

    public void onClickChargingInput(View view) {

        Intent i = new Intent(SOCdisplay.this, UserInput.class);

        // currentContext.startActivity(activityChangeIntent);
        startActivity(i);
    }



    @Override
    protected void onStop() {
        super.onStop();

    }
}
