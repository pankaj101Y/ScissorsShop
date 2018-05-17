package com.example.hp.scissorsshop.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hp.scissorsshop.Networking.NetworkRequestObject;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class OTPVerification extends AppCompatActivity {
    EditText OTPText;
    Button buttonVerify;
    private String phone=null;
    ProgressBar p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        phone=getIntent().getStringExtra(Attrs.Verification.PHONE_NUMBER);
        OTPText=findViewById(R.id.OTPText);
        buttonVerify=findViewById(R.id.buttonVerify);
        p=Utility.getProgressBar(this);
        addContentView(p,Utility.progressBarParams());

        buttonVerify.setEnabled(false);
        OTPText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==6)
                    buttonVerify.setEnabled(true);
                else
                    buttonVerify.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void verify(View view) {
        String otp=OTPText.getText().toString();
        otp=otp.trim();
        if (!Utility.isInternetAvailable(this))
            Utility.showConnectivityError(this);
        else {
            setRequestedOrientation(getResources().getConfiguration().orientation);
            showProgress();
            NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.VERIFICATION, Headers.Query.VERIFY_OTP, Activities.OTP_VERIFICATION_ACTIVITY,
                    new NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            hideProgress();
                            afterOtpResponse(jsonObject);
                        }
                    });
            task.execute(new NetworkRequestObject(new String[]{Attrs.Verification.PHONE_NUMBER,Attrs.Verification.OTP},new String[]{phone,otp}));
        }
    }

    private void afterOtpResponse(JSONObject j){
        String status;
        try {
             status=j.getString(Responses.STATUS);
            if (status.equals(Responses.SUCCESS)){
                String id=j.getString(Attrs.Shop.ID);
                Intent passwordIntent=new Intent(this,InputActivity.class);
                passwordIntent.putExtra(Resolve.INPUT_ACTIVITY,Attrs.Accounts.PASSWORD);
                passwordIntent.putExtra(Attrs.Shop.ID,id);
                passwordIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(passwordIntent);
            }
        } catch (JSONException e) {
            status=Responses.FAILED;
        }
        if (status.equals(Responses.FAILED))
            Toast.makeText(this,"Verification Failed Please Try Again",Toast.LENGTH_LONG).show();
    }

    private void showProgress(){
        p.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        p.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    @Override
    protected void onPause() {
        ResponseHandler.clearCallbacks(Activities.OTP_VERIFICATION_ACTIVITY);
        hideProgress();
        super.onPause();
    }
}
