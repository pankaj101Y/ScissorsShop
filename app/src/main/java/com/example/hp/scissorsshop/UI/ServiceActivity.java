package com.example.hp.scissorsshop.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hp.scissorsshop.DataClasses.Values;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.DataClasses.Service;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Utility.Tasks;
import com.example.hp.scissorsshop.Views.CustomAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.view.View.GONE;

public class ServiceActivity extends AppCompatActivity implements
        CustomAlertDialog.AlertDialogInteraction {
    private static final String ADDITION_CONFIRMATION_DIALOG = "addition-confirmation";
    EditText priceText;
    String hairServices[],nailsServices[],bodyServices[],domains[];
    String selectedService=null,price,serviceType=null;
    private Spinner domainsSpinner, servicesSpinner;
    int taskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_addition);

        domainsSpinner = findViewById(R.id.serviceDomain);
        servicesSpinner = findViewById(R.id.services);
        priceText=findViewById(R.id.price);
        domains=getResources().getStringArray(R.array.domain);
        hairServices=getResources().getStringArray(R.array.hair_services);

        taskType =getIntent().getIntExtra(Tasks.SERVICE, Tasks.ADD_SERVICE);
        if (taskType == Tasks.ADD_PACKAGE_SERVICE)
            SetupForPackage();

            final ArrayAdapter<CharSequence> domainAdapter =new ArrayAdapter<CharSequence>(this,
                    android.R.layout.simple_spinner_item,domains);
        domainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        domainsSpinner.setAdapter(domainAdapter);


        final ArrayAdapter<String>serviceAdapter= new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<String>(Arrays.asList(hairServices)));

        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicesSpinner.setAdapter(serviceAdapter);

        domainsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        serviceType= Values.ServicesType.HAIR;
                        serviceAdapter.clear();
                        if (hairServices==null||hairServices.length==0)
                            hairServices=getResources().getStringArray(R.array.hair_services);
                        serviceAdapter.addAll(new ArrayList<>(Arrays.asList(hairServices)));
                        serviceAdapter.notifyDataSetChanged();
                        selectedService =serviceAdapter.getItem(0);
                        break;

                    case 1:
                        serviceType= Values.ServicesType.NAILS;
                        serviceAdapter.clear();
                        if (nailsServices==null||nailsServices.length==0)
                            nailsServices=getResources().getStringArray(R.array.nails_services);
                        serviceAdapter.addAll(new ArrayList<>(Arrays.asList(nailsServices)));
                        serviceAdapter.notifyDataSetChanged();
                        selectedService =serviceAdapter.getItem(0);
                        break;

                    case 2:
                        serviceType= Values.ServicesType.BODY;
                        serviceAdapter.clear();
                        if (bodyServices==null||bodyServices.length==0)
                            bodyServices=getResources().getStringArray(R.array.body_services);
                        serviceAdapter.addAll(new ArrayList<>(Arrays.asList(bodyServices)));
                        serviceAdapter.notifyDataSetChanged();
                        selectedService =serviceAdapter.getItem(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedService =serviceAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void SetupForPackage() {
        priceText.setVisibility(GONE);
    }

    public void addService(View view) {
        if (selectedService==null)
            Snackbar.make(priceText,"Please Select A Service",Snackbar.LENGTH_LONG).show();
        else if (taskType == Tasks.ADD_SERVICE)
            tryToGetPrice();
        else addServiceToPackage();
    }

    private void addServiceToPackage() {
        Intent resultIntent =new Intent();
        resultIntent.putExtra(Resolve.SERVICE_NAME,selectedService);
        resultIntent.putExtra(Resolve.SERVICE_DOMAIN,
                domains[domainsSpinner.getSelectedItemPosition()].toLowerCase());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void tryToGetPrice() {
        price=priceText.getText().toString().trim();
        if (TextUtils.isEmpty(price))
            Snackbar.make(priceText,"Please Enter Price",Snackbar.LENGTH_LONG).show();
        else {
            FragmentManager fm = getSupportFragmentManager();
            CustomAlertDialog alertDialog = CustomAlertDialog.newInstance("Confirmation",
                    createServiceConMessage(),"YES","NO");
            alertDialog.show(fm, ADDITION_CONFIRMATION_DIALOG);

        }
    }

    private String createServiceConMessage() {
        return "Do You Want To Add \n" + "Service: " + selectedService + "\n" + "Price: " + price;
    }

    @Override
    public void positiveResponse() {
        Service service=new Service(null,domains[domainsSpinner.getSelectedItemPosition()].toLowerCase(),
                selectedService,price);
        NetworkTask<Service>task=new NetworkTask<>(Headers.Scope.SERVICE, Headers.Query.ADD, Activities.SERVICE_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        showToast("Service Added");
                        finish();
                    }else
                        showToast("Service Not Added. Please Try Again");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute(service);
    }

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void negativeResponse() {

    }

    @Override
    public void onBackPressed() {
        Intent resultIntent =new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ResponseHandler.clearCallbacks(Activities.SERVICE_ACTIVITY);
    }
}
