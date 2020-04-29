package com.example.chargergui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.TransactionEventRequest;
import DataType.IdTokenInfoType;
import DataType.IdTokenType;
import DataType.TransactionType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.IdTokenEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class PINauthorizeDialog extends AppCompatDialogFragment {
    private EditText PIN;
    private TextView UseYourPIN ;

    private PINauthorizeDialogListener listener ;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_pindialog, null);

        builder.setView(view)
                .setCustomTitle(UseYourPIN)

                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("AUTHORIZE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SendRequestToCSMS toCSMS2 = new SendRequestToCSMS();
                        IdTokenType.setType(IdTokenEnumType.KeyCode);
                        IdTokenType.setIdToken(PIN.getText().toString());

                        try {
                            toCSMS2.sendAuthorizeRequest();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (EncodeException e) {
                            e.printStackTrace();
                        }

                        if(IdTokenInfoType.status == AuthorizationStatusEnumType.Accepted ){

                            TransactionEventRequest.triggerReason = TriggerReasonEnumType.Deauthorized ;
                            TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
                            TransactionType.chargingState = ChargingStateEnumType.EVConnected;

                            try {
                                toCSMS2.sendTransactionEventRequest();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (EncodeException e) {
                                e.printStackTrace();
                            }
                            listener.applyTexts("PIN "+IdTokenInfoType.status.toString() );

                        }
                        else {
                            listener.applyTexts("PIN "+IdTokenInfoType.status.toString() );
                        }

                    }
                });

        UseYourPIN = view.findViewById(R.id.textView13) ;
        PIN = view.findViewById(R.id.editText);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (PINauthorizeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AmountDialogListener");
        }
    }

    public interface PINauthorizeDialogListener{
        void applyTexts(String s) ;
    }

}
