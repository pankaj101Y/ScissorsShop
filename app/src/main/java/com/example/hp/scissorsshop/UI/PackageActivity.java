package com.example.hp.scissorsshop.UI;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.Networking.NetworkRequestObject;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.DataClasses.Package;
import com.example.hp.scissorsshop.DataClasses.PackageChangesHolder;
import com.example.hp.scissorsshop.DataClasses.Service;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Utility.Tasks;
import com.example.hp.scissorsshop.Views.InputDialog;
import com.example.hp.scissorsshop.Views.ContentView;

import org.json.JSONException;
import org.json.JSONObject;

public class PackageActivity extends AppCompatActivity implements InputDialog.InputDialogInteraction {
    private static final String PRICE_INPUT_DIALOG = "price-input-dialog";
    private LinearLayout packageLayout;
    private Button addButton=null;
    private Package currentPackage;
    private boolean editing =false;
    private static PackageChangesHolder changesHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        packageLayout=findViewById(R.id.packageLayout);

        int task=getIntent().getIntExtra(Tasks.PACKAGE,Tasks.ADD_PACKAGE);
        currentPackage= getIntent().getParcelableExtra(Resolve.CONTENT_TYPE_PACKAGE);
        if (task==Tasks.EDIT)
            editing =true;

        if (addButton==null)
            addButton=getAddButton();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServiceToPackage();
            }
        });
        packageLayout.addView(addButton);

        FloatingActionButton fab=findViewById(R.id.requestAddPackage);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPackage())
                    promptPriceInputBox(-1, currentPackage.getPrice());
                else showToast("Package Is Not Valid");
            }
        });
        setUp();
    }

    private void setUp() {
        if (editing &&changesHolder==null){
            changesHolder=new PackageChangesHolder();
        }
        layoutPackageServices();
    }

    private void layoutPackageServices() {
        if (currentPackage==null||currentPackage.getServices().size()==0)
            return;
        for (int i=0;i<currentPackage.getServices().size();i++)
            addServiceToLayout(currentPackage.getServices().get(i).getName());
    }

    private void promptPriceInputBox(int position,String price) {
        FragmentManager fm = getSupportFragmentManager();
         InputDialog.newInstance(position,price).show(fm, PRICE_INPUT_DIALOG);
    }

    public void addServiceToPackage() {
        Intent addServiceToPackageIntent=new Intent(this,ServiceActivity.class);
        addServiceToPackageIntent.putExtra(Tasks.SERVICE, Tasks.ADD_PACKAGE_SERVICE);
        startActivityForResult(addServiceToPackageIntent,Tasks.ADD_PACKAGE_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode== Tasks.ADD_PACKAGE_SERVICE&&resultCode==RESULT_OK){
            String service =data.getStringExtra(Resolve.SERVICE_NAME);
            String domain=data.getStringExtra(Resolve.SERVICE_DOMAIN);
            addServiceToLayout(service);
            Service s=new Service(null,domain,service,null);
            currentPackage.addService(s);

            if (editing)
                changesHolder.addNewService(s);
        }
    }

    private void addServiceToLayout(String service){
        final ContentView serviceView=new ContentView(this);
        serviceView.getServiceName().setText(service);
        serviceView.getDeleteIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteServiceFromPackage(packageLayout.indexOfChild(serviceView));
            }
        });
        packageLayout.removeViewAt(packageLayout.getChildCount()-1);
        packageLayout.addView(serviceView);
        packageLayout.addView(addButton);
    }

    private void deleteServiceFromPackage(int layoutPosition) {
        if (editing){
            if (layoutPosition>=currentPackage.getServices().size()){
                changesHolder.getAdditions().remove(layoutPosition-currentPackage.getServices().size());
            }else{
                changesHolder.addServiceToRemove(currentPackage.getServices().get(layoutPosition));
            }
        }else currentPackage.getServices().remove(layoutPosition);

        packageLayout.removeViewAt(layoutPosition);
    }

    private Button getAddButton(){
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.ic_add_circle);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
        , ViewGroup.LayoutParams.WRAP_CONTENT);

        Resources resources=getResources();
        float pixPerDp= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, resources.getDisplayMetrics());
        layoutParams.width = (int)(0.5+pixPerDp*64);
        layoutParams.height = (int)(0.5+pixPerDp*64);
        layoutParams.gravity = Gravity.CENTER;
        button.setLayoutParams(layoutParams);
        return button;
    }

    @Override
    public void onInput(String price,int position) {
        currentPackage.setPrice(price);
        if (editing)
            editPackage(price);
        else addPackage();
    }

    private void addPackage() {
        NetworkTask<Package>task=new NetworkTask<>(Headers.Scope.PACKAGE, Headers.Query.ADD, Activities.PACKAGE_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        showToast("Package Added");
                        finish();
                    }else
                        showToast("Package Not Added. Please Try Again");
                } catch (JSONException ignored) {
                    showToast("Package Not Added. Please Try Again");
                }
            }
        });
        task.execute(currentPackage);
    }

    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    private void editPackage(String price) {
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.PACKAGE,
                Headers.Query.EDIT, Activities.PACKAGE_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        changesHolder.clearChanges();
                        showToast("Package Edited");
                    }else showToast("Package Not Edited Try Again");
                } catch (JSONException ignored) {
                    showToast("Package Not Edited Try Again");
                }
            }
        });

        NetworkRequestObject networkRequestObject= new NetworkRequestObject(changesHolder.toJson());
        networkRequestObject.add(Attrs.Package.PRICE,price);
        networkRequestObject.add(Attrs.Package.PACKAGE_ID,currentPackage.getServerId());
        task.execute(networkRequestObject);
    }

    @Override
    public void onCancel() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        ResponseHandler.clearCallbacks(Activities.PACKAGE_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (changesHolder!=null)
            changesHolder.clearChanges();
    }

    public boolean isValidPackage() {
        boolean isValidPackage;
        if (!editing)
            isValidPackage=currentPackage.getServices().size()>1;
        else {
            isValidPackage=currentPackage.getServices().size()+changesHolder.getAdditions().size()>1;
        }
        return isValidPackage;
    }
}
