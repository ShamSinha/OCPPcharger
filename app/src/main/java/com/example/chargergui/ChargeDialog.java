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

import SOCDisplayRelated.InputRepo;


public class ChargeDialog extends AppCompatDialogFragment {
    private EditText editCharge;
    private ChargeDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        assert getArguments() != null;
        final double InitialSoc = Math.ceil(getArguments().getDouble("Initial_SOC"));//Get pass data with its key value


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_charge,null);

        final InputRepo inputRepo = new InputRepo(getContext());

        editCharge.setHint(InitialSoc + "% < Input Charge < 100%");
        builder.setView(view)
                .setTitle("Enter Charge")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        double inputCharge = Double.parseDouble(editCharge.getText().toString());

                        if(inputCharge <=100.0  && inputCharge > InitialSoc) {
                            listener.applyTextCharge(inputCharge);
                            UserInput userInput = new UserInput();
                            userInput.startCharging.setEnabled(true);
                            
                        }
                        else {
                            listener.applyTextInvalid() ;
                        }
                    }
                });

        editCharge = view.findViewById(R.id.InCharge);
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
        void applyTextCharge(double input) ;
        void applyTextInvalid();
    }
}
