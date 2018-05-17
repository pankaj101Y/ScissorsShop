package com.example.hp.scissorsshop.Networking;

import com.example.hp.scissorsshop.DataClasses.Json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PANKAJ KUMAR on 4/7/2018.
 */

public class NetworkRequestObject implements Json<NetworkRequestObject>{
    private JSONObject j=new JSONObject();

    public NetworkRequestObject(String keys[], String values[]){
        int l=keys.length;
        if (l==values.length){
            for (int i=0;i<l;i++)
                try {
                    j.put(keys[i],values[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    public NetworkRequestObject(String key,String value){
        if (key!=null)
            try {
                j.put(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public NetworkRequestObject(JSONObject j){
        this.j=j;
    }

    public NetworkRequestObject add(String key,String value){
        try {
            j.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return j;
    }

    @Override
    public void fromJson(JSONObject j){
    }
}
