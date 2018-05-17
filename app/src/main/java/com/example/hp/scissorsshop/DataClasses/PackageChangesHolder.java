package com.example.hp.scissorsshop.DataClasses;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PackageChangesHolder implements Json<PackageChangesHolder>{
    private ArrayList<Service>additions;
    private ArrayList<Service>removals;

    public PackageChangesHolder(){
        additions=new ArrayList<>();
        removals=new ArrayList<>();
    }

    public void addNewService(Service service){
        additions.add(service);
    }

    public void addServiceToRemove(Service service) {
        removals.add(service);
    }

    public ArrayList<Service> getAdditions() {
        return additions;
    }

    @Override
    public JSONObject toJson(){
        JSONArray a=new JSONArray();
        JSONArray r=new JSONArray();

        int l=removals.size();

        try {
            for (int i=0;i<l;i++){
                JSONObject temp=new JSONObject();
                temp.put(Attrs.Service.SERVER_ID,removals.get(i).getServerId());
                r.put(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




        l=additions.size();
        JSONObject j=new JSONObject();
        try {
            for (int i=0;i<l;i++){
                 JSONObject temp=new JSONObject();
                temp.put(Attrs.Service.DOMAIN,additions.get(i).getDomain());
                temp.put(Attrs.Service.NAME,additions.get(i).getName());
                a.put(temp);
            }
            j.put(Attrs.PackagesChangesHolder.ADDITIONS,a);
            j.put(Attrs.PackagesChangesHolder.REMOVALS,r);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return j;
    }

    @Override
    public void fromJson(JSONObject j){
    }

    public void clearChanges(){
        additions.clear();
        removals.clear();
    }
}
