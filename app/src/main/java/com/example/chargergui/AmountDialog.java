package com.example.chargergui;

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
import androidx.appcompat.app.AppCompatDialogFragment;



public class AmountDialog extends AppCompatDialogFragment {
    private EditText editAmount;
    private AmountDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        assert getArguments() != null;
        final int maximumCost = getArguments().getInt("MAX_COST");
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_amount,null);
        editAmount.setHint("Input Amount < "+ maximumCost);
        builder.setView(view)
                .setTitle("Enter Amount")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = Integer.parseInt(editAmount.getText().toString());
                        if(i <= maximumCost) listener.setAmount(i);
                        else listener.setAmount(maximumCost);
                    }
                });

        editAmount = view.findViewById(R.id.InRupees);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AmountDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AmountDialogListener");
        }
    }

    public interface AmountDialogListener{
        void setAmount(int i) ;
    }
}
