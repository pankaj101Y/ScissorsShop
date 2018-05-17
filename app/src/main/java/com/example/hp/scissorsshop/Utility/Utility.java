package com.example.hp.scissorsshop.Utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.hp.scissorsshop.UI.ContentViewerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by HP on 3/25/2018.
 */

public class Utility {

    public static JSONObject getJsonFromFile(String file, Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getListFromJsonArray(JSONArray a,Class<?> c){
        int l=a.length();
        Constructor<?> constructors= null;
        try {
            constructors = c.getDeclaredConstructor(JSONObject.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        ArrayList<Object> arrayList=new ArrayList<>();
        for (int i=0;i<l;i++){
            try {
                arrayList.add(constructors != null ? constructors.newInstance(a.getJSONObject(i)) : null);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public static boolean isInternetAvailable(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo!=null&&networkInfo.isConnected();
    }

    public static void showMessage(Context context, String message,String title,String pos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showConnectivityError(Context context){
        Utility.showMessage(context,"No Internet Connection Found.Please Enable Internet.","ERROR","Back");
    }

    public static ProgressDialog getProgressDialog(Context context, String message, String title){
        ProgressDialog p=new ProgressDialog(context);
        p.setMessage(message);
        p.setCancelable(false);
        p.setTitle(title);
        p.setIndeterminate(true);
        return p;
    }

    public static int pixFromDp(Context context,int dp){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int)(.5+px);
    }

    public static ProgressBar getProgressBar(Context context){
        ProgressBar progressBar;
        progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        return progressBar;
    }

    public static ConstraintLayout.LayoutParams progressBarParams(){
        return new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
