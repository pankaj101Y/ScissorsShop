package com.example.hp.scissorsshop.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hp.scissorsshop.DataClasses.Accounts;
import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.DataClasses.Shop;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Utility.Utility;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopRegistration extends AppCompatActivity {
    private EditText shopName,shopAddress;
    private Spinner shopType,shopSex;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_PERMISSION=1;
    private ArrayAdapter<String> typeAdapter,sexAdapter;
    private ProgressBar p;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_registration);
        shopName = findViewById(R.id.shopName);
        shopType = findViewById(R.id.shopType);
        shopAddress=findViewById(R.id.shopAddress);
        shopSex=findViewById(R.id.shopSex);

        p=Utility.getProgressBar(this);
        addContentView(p,Utility.progressBarParams());

         typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{
                "SHOP TYPE", "SALOON", "SPA", "SALOON + SPA"
        });

         sexAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{
                "Male", "Female", "Unisex"
        });

        shopSex.setAdapter(sexAdapter);
        shopType.setAdapter(typeAdapter);

        locationRequest=new LocationRequest();
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000*4);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
      }

    @SuppressLint("MissingPermission")
    public void callCurrentLocation() {
        final Location[] currentLocation = new Location[1];
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    currentLocation[0] = locationResult.getLastLocation();

                    String result = "Current Location Latitude is " +
                            currentLocation[0].getLatitude() + "\n" +
                            "Current location Longitude is " + currentLocation[0].getLongitude();
                    registerShop(shopName.getText().toString().trim(),shopAddress.getText().toString().trim(),
                            sexAdapter.getItem(shopSex.getSelectedItemPosition()),
                            typeAdapter.getItem(shopType.getSelectedItemPosition()),
                            currentLocation[0].getLatitude(),
                            currentLocation[0].getLongitude()
                            );

                    Toast.makeText(ShopRegistration.this,result,Toast.LENGTH_LONG).show();
                    mFusedLocationClient.removeLocationUpdates(this);
                }
            }, Looper.myLooper());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registerShop(String name, String address, String sex, String type, double lat, double log) {
        Shop shop=new Shop(name,type,address,sex, null, lat,log);
        setRequestedOrientation(getResources().getConfiguration().orientation);
        p.setVisibility(View.VISIBLE);
        NetworkTask<Shop>task=new NetworkTask<>(Headers.Scope.REGISTRATION, Headers.Query.SHOP_REGISTER, Activities.SHOP_REGISTRATION_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                p.setVisibility(View.GONE);
                afterResponse(jsonObject);
            }
        });
        task.execute(shop);
    }

    private void afterResponse(JSONObject j){
        String status;
        try {
            status=j.getString(Responses.STATUS);
            if (status.equals(Responses.SUCCESS)){
                Accounts.registerShop(this);
                registerOwner();
            }
        } catch (JSONException e){
            status=Responses.FAILED;
        }
        if (status.equals(Responses.FAILED))
            Toast.makeText(this,"Registration Failed Set Failed",Toast.LENGTH_LONG).show();
    }

    private void registerOwner() {
        Intent ownerIntent=new Intent(this, InputActivity.class);
        ownerIntent.putExtra(Resolve.INPUT_ACTIVITY, Attrs.Shop.OWNER);
        ownerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ownerIntent);
    }

    public void requestShopRegister(View view) {
        if (!isAllFieldsProvided())
            Snackbar.make(shopName, "Please Provide All Fields", BaseTransientBottomBar.LENGTH_LONG).show();
        else {
         showLocationDialog();
        }
    }

    private boolean isAllFieldsProvided() {
        return !TextUtils.isEmpty(shopName.getText().toString().trim())
                && !TextUtils.isEmpty(shopAddress.getText().toString().trim())
                && shopType.getSelectedItemPosition() != 0;
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage("Your Current Location Will Be Treated As Your Shop Location." +
                " So Make Sure Your Are In Your Shop." +
                "Press OK To Continue Else Press BACK");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getLocationPermissions();
            }
        });

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    private void getLocationPermissions() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
            }else checkForLocationSettings();
        } else {
            checkForLocationSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode== LOCATION_PERMISSION){
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForLocationSettings();
                } else {
                    Utility.showMessage(this,"Cannot Get Your Location","Error","BACK");
                }
        }
    }

    public void checkForLocationSettings() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            callCurrentLocation();
                        }
                    })
                    .addOnFailureListener(ShopRegistration.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(ShopRegistration.this, LOCATION_PERMISSION);
                                    } catch (IntentSender.SendIntentException sie) {
                                        sie.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(ShopRegistration.this, "Setting change is not available.Try in another device.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==LOCATION_PERMISSION&&resultCode==RESULT_OK)
            callCurrentLocation();
    }

    @Override
    protected void onPause() {
        p.setVisibility(View.GONE);
        super.onPause();
    }
}
