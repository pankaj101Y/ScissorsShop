package com.example.hp.scissorsshop.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.scissorsshop.DataClasses.Accounts;
import com.example.hp.scissorsshop.MainActivity;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkRequestObject;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class InputActivity extends AppCompatActivity {
    private EditText inputText1,inputText2;
    private Button button;
    private TextView logInView;
    boolean gettingMobile =false,loggingIn=false, gettingPassword =false,gettingOwner=false;
    private String shopId;
    private ProgressBar p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        inputText1 =findViewById(R.id.inputText1);
        inputText2 =findViewById(R.id.inputText2);
        button = findViewById(R.id.button);
        logInView = findViewById(R.id.logInView);
        p=Utility.getProgressBar(this);
        addContentView(p,Utility.progressBarParams());

        String task=getIntent().getStringExtra(Resolve.INPUT_ACTIVITY);
        switch (task) {
            case Attrs.Accounts.LOG_IN:
                setUpForLogin();
                break;
            case Attrs.Verification.PHONE_NUMBER:
                setUpForMobile();
                break;
            case Attrs.Accounts.PASSWORD:
                setupForPassword();
                break;
            case Attrs.Shop.OWNER:
                setUpForOwner();
                break;
        }
    }

    private void setUpForMobile() {
        inputText1.setHint("Enter Mobile Number");
        inputText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputText2.setVisibility(View.GONE);
        button.setText("Send OTP");
        gettingMobile=true;
    }

    private void setupForPassword() {
        inputText1.setHint("Enter Password");
        inputText1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputText2.setHint("Confirm Password");
        inputText2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        button.setText("Set Password");
        gettingPassword =true;
        shopId=getIntent().getStringExtra(Attrs.Shop.ID);
    }

    private void setUpForOwner() {
        inputText1.setHint("Owners Name");
        inputText2.setVisibility(View.GONE);
        gettingOwner=true;
        button.setText("Finish");
    }

    private void setUpForLogin() {
        inputText1.setHint("ID");
        inputText2.setHint("Password");
        button.setText("LOG IN");
        logInView.setVisibility(View.VISIBLE);
        loggingIn=true;
    }

    public void onInputsEntered(View view) {
        if (gettingMobile)
            requestOTP();
        else if (gettingPassword)setPassword();
        else if (gettingOwner)setOwner();
        else if (loggingIn&& canLogIn()) attemptLogIn();;
    }

    public void requestOTP() {
        if (connected()&&mobileValid()){
            String phone= inputText1.getText().toString().trim();
            NetworkTask<NetworkRequestObject> task=new NetworkTask<>(Headers.Scope.VERIFICATION, Headers.Query.sendOTP,
                    Activities.INPUT_ACTIVITY, null);
            task.execute(new NetworkRequestObject(Attrs.Verification.PHONE_NUMBER,phone));

            Intent verifyIntent=new Intent(this,OTPVerification.class);
            verifyIntent.putExtra(Attrs.Verification.PHONE_NUMBER,phone);
            startActivity(verifyIntent);
        }
    }
    
    

    private void setPassword() {
        if(connected()&&passWordValid()){
            setRequestedOrientation(getResources().getConfiguration().orientation);
            showProgress();
            String pass= inputText1.getText().toString().trim();
            NetworkTask<NetworkRequestObject> task=new NetworkTask<>(Headers.Scope.VERIFICATION, Headers.Query.SET_PASSWORD,
                    Activities.INPUT_ACTIVITY, new NetworkCallback() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    hideProgress();
                    afterPasswordResponse(jsonObject);
                }
            });
            NetworkRequestObject networkRequestObject=new NetworkRequestObject(Attrs.Accounts.PASSWORD,pass);
            networkRequestObject.add(Attrs.Shop.ID,shopId);
            task.execute(networkRequestObject);
        }
    }


    private void afterPasswordResponse(JSONObject j){
        String status;
        try {
            status=j.getString(Responses.STATUS);
            if (status.equals(Responses.SUCCESS)){
                String id=j.getString(Attrs.Shop.ID);
                Accounts.LogIn(id,this);
                Accounts.init(this);
                registerShop();
            }
        } catch (JSONException e){
            status=Responses.FAILED;
        }
        if (status.equals(Responses.FAILED))
            Toast.makeText(this,"Password Set Failed",Toast.LENGTH_LONG).show();
    }



    private void setOwner() {
        if (connected()&&validOwnerName()){
            setRequestedOrientation(getResources().getConfiguration().orientation);
            showProgress();
            String owner= inputText1.getText().toString().trim();
            NetworkTask<NetworkRequestObject> task=new NetworkTask<>(Headers.Scope.REGISTRATION, Headers.Query.SET_OWNER,
                    Activities.INPUT_ACTIVITY, new NetworkCallback() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    hideProgress();
                    afterOwnerResponse(jsonObject);
                }
            });
            NetworkRequestObject networkRequestObject=new NetworkRequestObject(Attrs.Shop.OWNER,owner);
            networkRequestObject.add(Attrs.Shop.ID,Accounts.getAccountKey());
            task.execute(networkRequestObject);
        }
    }

    private void afterOwnerResponse(JSONObject j) {
        String status;
        try {
            status=j.getString(Responses.STATUS);
            if (status.equals(Responses.SUCCESS)){
                Accounts.setOwner(this);
                Intent mainIntent=new Intent(this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        } catch (JSONException e) {
            status=Responses.FAILED;
        }
        if (status.equals(Responses.FAILED))
            Toast.makeText(this,"Failed Please Try Again",Toast.LENGTH_LONG).show();
    }




    private boolean canLogIn() {
        if (!Utility.isInternetAvailable(this)){
            Utility.showMessage(this,"Please Connect To Internet","Message","OK");
            return false;
        }
        else if (mobileValid()&&validOwnerName()){
            return true;
        }else
            Toast.makeText(this,"Please Enter Valid Credentials",Toast.LENGTH_LONG).show();
        return false;
    }

    private void attemptLogIn(){
        setRequestedOrientation(getResources().getConfiguration().orientation);
        showProgress();
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.VERIFICATION, Headers.Query.LOG_IN,
                Activities.MAIN_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideProgress();
                afterLoginAttempt(jsonObject);
            }
        });

        String mobile=inputText1.getText().toString();
        String password=inputText2.getText().toString();
        NetworkRequestObject networkRequestObject=new NetworkRequestObject(Attrs.Verification.PHONE_NUMBER,mobile);
        networkRequestObject.add(Attrs.Accounts.PASSWORD,password);
        task.execute(networkRequestObject);
    }

    private void afterLoginAttempt(JSONObject j){
        String status,id;
        try {
            status=j.getString(Responses.STATUS);
            if (status.equals(Responses.SUCCESS)){
                id=j.getString(Attrs.Shop.ID);
                Accounts.LogIn(id,this);
                Accounts.init(this);
                String undone=j.getString(Responses.UNDONE);
                switch (undone) {
                    case Attrs.Accounts.SHOP_REGISTRATION:
                        registerShop();
                        break;
                    case Attrs.Shop.OWNER:
                        registerOwner();
                        break;
                    case Responses.NOTHING:
                        Accounts.registerShop(this);
                        Accounts.setOwner(this);
                        gotoMainActivity();
                        break;
                }
            }
        } catch (JSONException e) {
            status=Responses.FAILED;
        }
        if (status.equals(Responses.FAILED)){
            Toast.makeText(this,"Something Went Wrong",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoMainActivity() {
        Intent mainActivity=new Intent(this,MainActivity.class);
        startActivity(mainActivity);
        finish();
    }



    private void registerOwner() {
        Intent ownerIntent=new Intent(this, InputActivity.class);
        ownerIntent.putExtra(Resolve.INPUT_ACTIVITY, Attrs.Shop.OWNER);
        ownerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ownerIntent);
    }

    private void registerShop() {
        Intent registrationIntent=new Intent(this,ShopRegistration.class);
        registrationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registrationIntent);
    }


    private boolean mobileValid() {
        String number= inputText1.getText().toString().trim();
        return number.length()==10;
    }

    private boolean passWordValid(){
        String pass= inputText1.getText().toString();
        return pass.length() > 6;
    }

    private boolean validOwnerName() {
        return inputText1.getText().toString().trim().length()>2;
    }

    private boolean connected(){
        if (!Utility.isInternetAvailable(this)){
            Utility.showConnectivityError(this);
            return false;
        }
        return true;
    }

    public void requestSignUP(View view) {
        Intent verifyIntent=new Intent(this, InputActivity.class);
        verifyIntent.putExtra(Resolve.INPUT_ACTIVITY, Attrs.Verification.PHONE_NUMBER);
        startActivity(verifyIntent);
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
        hideProgress();
        ResponseHandler.clearCallbacks(Activities.INPUT_ACTIVITY);
        super.onPause();
    }
}
