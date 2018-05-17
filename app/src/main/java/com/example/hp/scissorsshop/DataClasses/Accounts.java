package com.example.hp.scissorsshop.DataClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.hp.scissorsshop.MainActivity;

/**
 * Created by PANKAJ KUMAR on 4/7/2018.
 */

public class Accounts {
    private static String shopId=null;

    public static void init(Context context){
        shopId= getField(Attrs.Accounts.KEY,context);
    }

    public static void LogIn(String accountKey, Context context){
        setField(Attrs.Accounts.KEY,accountKey,context);
    }

    public static String getAccountKey(){
        return shopId;
    }

    public static boolean LoggedIn(Context context){
       return !getField(Attrs.Accounts.KEY,context).equals(Attrs.Accounts.INVALID);
    }

    public static void registerShop(Context context){
        setField(Attrs.Accounts.SHOP_REGISTRATION,Attrs.Accounts.DONE,context);
    }

    public static boolean isShopRegister(Context context){
        return !getField(Attrs.Accounts.SHOP_REGISTRATION,context).equals(Attrs.Accounts.INVALID);
    }

    public static void setOwner(Context context){
        setField(Attrs.Accounts.OWNER,Attrs.Accounts.DONE,context);
    }

    public static boolean isOwner(Context context){
        return !getField(Attrs.Accounts.OWNER,context).equals(Attrs.Accounts.INVALID);
    }


    private static String getField(String fieldName, Context context){
        String defaultValue=Attrs.Accounts.INVALID;

        SharedPreferences sharedPreferences=context.getSharedPreferences(Attrs.Accounts.Account,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(fieldName,defaultValue);
    }

    private static void setField(String fieldName,String fieldValue,Context context){
        SharedPreferences.Editor e=context
                .getSharedPreferences(Attrs.Accounts.Account,Context.MODE_PRIVATE).
                        edit();
        e.putString(fieldName,fieldValue);
        e.apply();
    }

    public static void signOut(Context context) {
        SharedPreferences.Editor e=context.getSharedPreferences(Attrs.Accounts.Account,Context.MODE_PRIVATE).edit();
        e.clear();
        e.apply();
    }
}
