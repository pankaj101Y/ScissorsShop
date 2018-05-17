package com.example.hp.scissorsshop.Networking;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.example.hp.scissorsshop.Utility.Activities;

import org.json.JSONObject;

/**
 * Created by PANKAJ KUMAR on 4/7/2018.
 */

public class ResponseHandler {

    private static SparseArray<SparseArray<NetworkCallback>>callbacks=new SparseArray<>();

    static void handle(String scope, String query,int requester,int requestNo, JSONObject result){
        NetworkCallback callback=getCallback(requester,requestNo);
        switch (scope){
            case Headers.Scope.VERIFICATION:
                handleVerification(query,result,callback);
                break;
            case Headers.Scope.REGISTRATION:
                handleShopRegistration(query,result,callback);
                break;
            case Headers.Scope.SERVICE:
                handleService(query,result,callback);
                break;
            case Headers.Scope.PACKAGE:
                handlePackage(query,result,callback);
                break;
            case Headers.Scope.SHOP_PROFILE:
                handleShopProfile(query,result,callback);
                break;

        }
        callbacks.get(requester).remove(requestNo);
    }

    private static void handleVerification(String query, JSONObject result,NetworkCallback callback) {
        // TODO: 4/7/2018 here check for query and and do work if needed

        if (callback!=null){
            callback.onResponse(result);
        }
    }

    private static void handleShopRegistration(String query, JSONObject result,NetworkCallback callback) {
        // TODO: 4/7/2018 here check for query and and do work if needed

        if (callback!=null)
            callback.onResponse(result);
    }

    private static void handleService(String query, JSONObject result, NetworkCallback callback) {
        // TODO: 4/7/2018 here check for query and and do work if needed

        if (callback!=null)
            callback.onResponse(result);
    }

    private static void handlePackage(String query, JSONObject result, NetworkCallback callback) {
        // TODO: 4/7/2018 here check for query and and do work if needed

        if (callback!=null)
            callback.onResponse(result);
    }

    private static void handleShopProfile(String query, JSONObject result, NetworkCallback callback) {
        // TODO: 4/7/2018 here check for query and and do work if needed
        if (callback!=null)
            callback.onResponse(result);
    }


    static int addCallback(int requester, NetworkCallback callback){
        int callbackNo;
        if (callbacks.get(requester)==null) callbacks.put(requester,new SparseArray<NetworkCallback>());
        callbackNo=callbacks.get(requester).size();
        callbacks.get(requester).put(callbackNo,callback);
        return callbackNo;
    }

    public static void addImageCallback(int requester,int callbackNo, NetworkCallback callback){
        if (callbacks.get(requester)==null) callbacks.put(requester,new SparseArray<NetworkCallback>());
        if (callbacks.get(requester).size()==0)
            callbacks.get(requester).put(callbackNo,callback);
    }

    private static NetworkCallback getCallback(int requester,int callbackNo){
        if (isValidRequester(requester)) {
            if (callbacks.get(requester)==null)
                return null;
            return callbacks.get(requester).get(callbackNo);
        }
        return null;
    }

    private static boolean isValidRequester(int requester){
        return requester >= Activities.MAIN_ACTIVITY && requester <= Activities.PROFILE_ACTIVITY;
    }

    public static void clearCallbacks(int of){
        if (callbacks.get(of)!=null)
            callbacks.get(of).clear();
    }
}
