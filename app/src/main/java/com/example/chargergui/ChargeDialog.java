package com.example.chargergui;

import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class ChargeDialog extends AppCompatDialogFragment {
    private EditText editcharge;
    private ChargeDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        assert getArguments() != null;
        final String currentsoc = getArguments().getString("currentsoc");//Get pass data with its key value

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_charge,null);

        editcharge.setHint(currentsoc+"% < Input Charge < 100%");
        builder.setView(view)
                .setTitle("Enter Charge Value")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float j = Float.parseFloat(editcharge.getText().toString());
                        UserInput u = new UserInput();

                        assert currentsoc != null;
                        if(j<=100  & j > Float.parseFloat(currentsoc)) {
                            Target.SOC = j ;
                            listener.applyTexts2(j);
                            UserInput userInput = new UserInput();
                            userInput.startcharging.setEnabled(true);
                        }
                        else {
                            listener.applyTextsinvalid() ;
                        }
                    }
                });

        editcharge = view.findViewById(R.id.InCharge);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ChargeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ChargeDialogListener");
        }
    }

    public interface ChargeDialogListener{
        void applyTexts2(float j) ;
        void applyTextsinvalid();
    }
}
