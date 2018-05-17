package com.example.hp.scissorsshop.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hp.scissorsshop.DataClasses.Package;
import com.example.hp.scissorsshop.DataClasses.Values;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.UI.PackageActivity;
import com.example.hp.scissorsshop.UI.ServiceActivity;
import com.example.hp.scissorsshop.Utility.Resolve;


public class SelectionDialog extends DialogFragment {

    public SelectionDialog(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.choices, container);

        v.findViewById(R.id.addService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addService=new Intent(getContext(),ServiceActivity.class);
                startActivity(addService);
                dismiss();
            }
        });

        v.findViewById(R.id.addSimplePackage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSimplePackageIntent=new Intent(getContext(),PackageActivity.class);
                addSimplePackageIntent.putExtra(Resolve.CONTENT_TYPE_PACKAGE,
                        new Package(Values.Packages.SIMPLE_PACKAGE));
                startActivity(addSimplePackageIntent);
                dismiss();
            }
        });

        v.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addHomePackageIntent=new Intent(getContext(),PackageActivity.class);
                addHomePackageIntent.putExtra(Resolve.CONTENT_TYPE_PACKAGE,
                        new Package(Values.Packages.Home_PACKAGE));
                startActivity(addHomePackageIntent);
                dismiss();
            }
        });

        getDialog().setTitle("ADD");
        return v;
    }
}
