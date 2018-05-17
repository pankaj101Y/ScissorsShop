package com.example.hp.scissorsshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;

import com.example.hp.scissorsshop.DataClasses.Accounts;
import com.example.hp.scissorsshop.DataClasses.Attrs;
import com.example.hp.scissorsshop.UI.ReviewActivity;
import com.example.hp.scissorsshop.UI.ShopActivity;
import com.example.hp.scissorsshop.UI.ShopRegistration;
import com.example.hp.scissorsshop.Utility.Activities;
import com.example.hp.scissorsshop.Networking.ResponseHandler;
import com.example.hp.scissorsshop.UI.ContentViewerActivity;
import com.example.hp.scissorsshop.UI.InputActivity;
import com.example.hp.scissorsshop.Utility.Resolve;
import com.example.hp.scissorsshop.Views.SelectionDialog;

public class
MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CHOICES = "input choices";
    private int selectedItem=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoicesDialog();
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (selectedItem!=-1)
                    handleItemClicked();

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        startUpChecks();
    }

    private void handleItemClicked() {
        switch (selectedItem){
            case R.id.myServices:
                Intent showServicesIntent=new Intent(this, ContentViewerActivity.class);
                showServicesIntent.putExtra(Resolve.CONTENT_TYPE, Resolve.CONTENT_TYPE_SERVICE);
                startActivity(showServicesIntent);
                break;

            case R.id.myPackages:
                Intent showPackagesIntent=new Intent(this, ContentViewerActivity.class);
                showPackagesIntent.putExtra(Resolve.CONTENT_TYPE, Resolve.CONTENT_TYPE_PACKAGE);
                startActivity(showPackagesIntent);
                break;

            case R.id.signOut:
                Accounts.signOut(this);
                startUpChecks();
                break;

            case R.id.reviews:
                Intent reviews=new Intent(this, ReviewActivity.class);
                startActivity(reviews);
                break;

            case R.id.myShop:
                Intent myShopIntent=new Intent(this, ShopActivity.class);
                startActivity(myShopIntent);
                break;
        }
        selectedItem=-1;
    }

    private void startUpChecks() {
        if (!Accounts.LoggedIn(this)){
            Intent verifyIntent=new Intent(this, InputActivity.class);
            verifyIntent.putExtra(Resolve.INPUT_ACTIVITY, Attrs.Accounts.LOG_IN);
            startActivity(verifyIntent);
            finish();
        }else {
            Accounts.init(this);
            if (!Accounts.isShopRegister(this)){
                registerShop();
                finish();
            }
            else if (!Accounts.isOwner(this)){
                registerOwner();
                finish();
            }
        }
    }

    private void registerShop(){
        Intent registrationIntent=new Intent(this, ShopRegistration.class);
        startActivity(registrationIntent);
    }

    private void registerOwner(){
        Intent ownerIntent=new Intent(this, InputActivity.class);
        ownerIntent.putExtra(Resolve.INPUT_ACTIVITY,Attrs.Shop.OWNER);
        startActivity(ownerIntent);
    }

    private void showChoicesDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SelectionDialog editNameDialogFragment = new SelectionDialog();
        editNameDialogFragment.show(fm, CHOICES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ResponseHandler.clearCallbacks(Activities.MAIN_ACTIVITY);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectedItem= item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        selectedItem=-1;
        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
