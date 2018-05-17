package com.example.hp.scissorsshop.Views;

import android.app.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;



public class CustomAlertDialog extends DialogFragment {
    private static final String TITLE="title";
    private static final String MESSAGE="message";
    private static final String POSITIVE="positive";
    private static final String NEGATIVE="negative";

    private AlertDialogInteraction dialogInteraction=null;

    public CustomAlertDialog() {}

    @Override
    public void onAttach(Context context) {
        dialogInteraction=(AlertDialogInteraction)context;
        super.onAttach(context);
    }

    public static CustomAlertDialog newInstance(String title, String message, String pos, String neg) {
        CustomAlertDialog frag = new CustomAlertDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putString(POSITIVE, pos);
        args.putString(NEGATIVE,neg);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String message = getArguments().getString(MESSAGE);
        String pos = getArguments().getString(POSITIVE);
        String neg= getArguments().getString(NEGATIVE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        if (pos!=null)
        alertDialogBuilder.setPositiveButton(pos,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogInteraction.positiveResponse();
            }
        });
        if (neg!=null)
        alertDialogBuilder.setNegativeButton(neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialogInteraction.negativeResponse();
                    dialog.dismiss();
                }
            }

        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialogInteraction=null;
    }


    public interface AlertDialogInteraction {
        void positiveResponse();
        void negativeResponse();
    }
}
