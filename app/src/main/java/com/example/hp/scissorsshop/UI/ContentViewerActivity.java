package com.example.hp.scissorsshop.UI;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hp.scissorsshop.Adapters.ContentActionListener;
import com.example.hp.scissorsshop.Adapters.ContentAdapter;
import com.example.hp.scissorsshop.DataClasses.Accounts;
import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.DataClasses.Package;
import com.example.hp.scissorsshop.DataClasses.Service;
import com.example.hp.scissorsshop.Networking.Headers;
import com.example.hp.scissorsshop.Networking.NetworkCallback;
import com.example.hp.scissorsshop.Networking.NetworkRequestObject;
import com.example.hp.scissorsshop.Networking.NetworkTask;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.R;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Utility.Tasks;
import com.example.hp.scissorsshop.Utility.Utility;
import com.example.hp.scissorsshop.Views.InputDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentViewerActivity extends AppCompatActivity implements
        ContentActionListener,InputDialog.InputDialogInteraction{
    private static final String PRICE_INPUT_DIALOG = "input-price";
    private ArrayList<Object>services=new ArrayList<>();
    private ArrayList<Object>packages=new ArrayList<>();
    private String resolve=null;
    private ContentAdapter adapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_viewer);

        resolve = getIntent().getStringExtra(Resolve.CONTENT_TYPE);
        RecyclerView recyclerView = findViewById(R.id.contentRecyclerView);
        progressBar=Utility.getProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);
        addContentView(progressBar,Utility.progressBarParams());

        if (resolve.equals(Resolve.CONTENT_TYPE_SERVICE))
            adapter = new ContentAdapter(getServices(), Resolve.CONTENT_TYPE_SERVICE,this);
        else adapter = new ContentAdapter(getPackages(), Resolve.CONTENT_TYPE_PACKAGE,this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }



    public ArrayList<Object> getServices() {
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.SERVICE,
                Headers.Query.MY_SERVICES, Activities.CONTENT_VIEWER_ACTIVITY,
                new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String status;
                try {
                    status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        progressBar.setVisibility(View.GONE);
                        int start=services.size();
                        services.addAll(Utility.getListFromJsonArray(jsonObject.getJSONArray(Responses.SERVICES), Service.class));
                        adapter.notifyItemChanged(start,services.size());
                    }
                } catch (JSONException e) {
                    status=Responses.FAILED;
                }
                if (status.equals(Responses.FAILED)){
                    finish();
                    showToast("Something Went Wrong");
                }
            }
        });
        task.execute(new NetworkRequestObject(Attrs.Shop.ID, Accounts.getAccountKey()));
        return services;
    }

    public ArrayList<Object> getPackages() {
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.PACKAGE,
                Headers.Query.MY_PACKAGES, Activities.CONTENT_VIEWER_ACTIVITY,
                new NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        String status;
                        try {
                            status=jsonObject.getString(Responses.STATUS);
                            if (status.equals(Responses.SUCCESS)){
                                progressBar.setVisibility(View.GONE);
                                int start=packages.size();
                                packages.addAll(Utility.getListFromJsonArray(jsonObject.getJSONArray(Responses.PACKAGES), Package.class));
                                adapter.notifyItemChanged(start,packages.size());
                            }
                        } catch (JSONException e) {
                            status=Responses.FAILED;
                        }
                        if (status.equals(Responses.FAILED)){
                            finish();
                            showToast("Something Went Wrong");
                        }
                    }
                });
        task.execute(new NetworkRequestObject(Attrs.Shop.ID, Accounts.getAccountKey()));
        return packages;
    }

    @Override
    public void edit(int position) {
        if (resolve.equals(Resolve.CONTENT_TYPE_SERVICE))
            editService(position);
        else editPackage(position);
    }


    @Override
    public void delete(int position) {
        if (resolve.equals(Resolve.CONTENT_TYPE_SERVICE))
            deleteService(position);
        else deletePackage(position);
    }

    private void deletePackage(final int position) {
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.PACKAGE,
                Headers.Query.DELETE, Activities.CONTENT_VIEWER_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);
                        showToast("Package Deleted");
                    }else showToast("Package Not Deleted Try Again");
                } catch (JSONException ignored) {
                    showToast("Package Not Deleted Try Again");
                }
            }
        });

        NetworkRequestObject networkRequestObject=new NetworkRequestObject(
                new String[]{Attrs.Package.PACKAGE_ID,Attrs.Shop.ID},
                new String[]{((Package)adapter.getItem(position)).getServerId(),Accounts.getAccountKey()});
        task.execute(networkRequestObject);
        showToast("Deleting...");
    }

    private void deleteService(final int position) {
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.SERVICE,
                Headers.Query.DELETE, Activities.CONTENT_VIEWER_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);
                        showToast("Service Deleted");
                    }else showToast("Service Not Service Deleted Try Again");
                } catch (JSONException ignored) {
                    showToast("Service Not Service Deleted Try Again");
                }
            }
        });

        NetworkRequestObject networkRequestObject=new NetworkRequestObject(
                new String[]{Attrs.Service.SERVICE_ID,Attrs.Shop.ID},
                new String[]{((Service)adapter.getItem(position)).getServerId(),Accounts.getAccountKey()});
        task.execute(networkRequestObject);
        showToast("Deleting...");
    }

    private void editPackage(int position) {
        Intent packageEditIntent=new Intent(this,PackageActivity.class);
        packageEditIntent.putExtra(Tasks.PACKAGE,Tasks.EDIT)
                .putExtra(Resolve.CONTENT_TYPE_PACKAGE,(Package)adapter.getItem(position));
        startActivity(packageEditIntent);
    }

    private void editService(int position) {
        promptPriceInputBox(position,((Service)services.get(position)).getPrice());
    }

    private void promptPriceInputBox(int position,String  price) {
        FragmentManager fm = getSupportFragmentManager();
        InputDialog.newInstance(position,price).show(fm, PRICE_INPUT_DIALOG);
    }

    @Override
    public void onInput(final String newPrice, final int position) {
        NetworkTask<NetworkRequestObject>task=new NetworkTask<>(Headers.Scope.SERVICE, Headers.Query.EDIT,
                Activities.CONTENT_VIEWER_ACTIVITY, new NetworkCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String status=jsonObject.getString(Responses.STATUS);
                    if (status.equals(Responses.SUCCESS)){
                        ((Service)adapter.getItem(position)).setPrice(newPrice);
                        adapter.notifyItemChanged(position);
                        showToast("Service Edited");
                    }else showToast("Service Not Edited Try Again");
                } catch (JSONException ignored) {
                    showToast("Service Not Edited Try Again");
                }
            }
        });

        NetworkRequestObject networkRequestObject=new NetworkRequestObject(
                new String[]{Attrs.Service.SERVICE_ID,Attrs.Service.NEW_PRICE},
                new String[]{((Service)adapter.getItem(position)).getServerId(),newPrice}
                );
        task.execute(networkRequestObject);
        showToast("Editing...");
    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        ResponseHandler.clearCallbacks(Activities.CONTENT_VIEWER_ACTIVITY);
    }

}
