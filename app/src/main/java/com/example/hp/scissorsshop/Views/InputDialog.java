package com.example.hp.scissorsshop.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.hp.scissorsshop.R;

public class InputDialog extends DialogFragment {
    private InputDialogInteraction inputDialogInteraction=null;
    private String price;
    private int itemPosition;
    private String oldPrice;
    private static final String ITEM_POSITION="item-position";
    private static final String OLD_PRICE="old-price";
    private static final int NO_ITEM=-1;
    private static final String NO_PRICE = "";

    public InputDialog(){}

    public static InputDialog newInstance(int position,String oldPrice){
        InputDialog dialog=new InputDialog();
        Bundle args=new Bundle();
        args.putInt(ITEM_POSITION,position);
        args.putString(OLD_PRICE,oldPrice);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        inputDialogInteraction=(InputDialogInteraction) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments()!=null){
            itemPosition=getArguments().getInt(ITEM_POSITION,NO_ITEM);
            oldPrice=getArguments().getString(OLD_PRICE,NO_PRICE);
        }
        final View view=inflater.inflate(R.layout.input_dialog,container,false);

        final EditText priceText=(EditText)view.findViewById(R.id.priceInput);
        priceText.setText(oldPrice);
        priceText.setSelection(0,oldPrice.length());
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               price= (priceText).getText().toString().trim();
               if (!TextUtils.isEmpty(price)){
                   inputDialogInteraction.onInput(price,itemPosition);
                   dismiss();
               }
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialogInteraction.onCancel();
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Enter");
        getDialog().setCancelable(false);
    }

    @Override
    public void onDestroy() {
        inputDialogInteraction=null;
        super.onDestroy();
    }

    public interface InputDialogInteraction{
        void onInput(String price,int itePosition);
        void onCancel();
    }
}
