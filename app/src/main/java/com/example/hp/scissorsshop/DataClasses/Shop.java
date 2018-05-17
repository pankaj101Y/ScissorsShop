package com.example.hp.scissorsshop.DataClasses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Shop implements Json<Shop> {
    private String name;
    private String type;
    private String address;
    private String sex;
    private String owner;
    private String[] images;
    private double lat;
    private double log;

    public Shop(String name, String type, String address, String sex, String owner, double lat, double log) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.sex = sex;
        this.owner = owner;
        this.lat = lat;
        this.log = log;
    }

    public Shop(JSONObject j){
        fromJson(j);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getSex() {
        return sex;
    }

    public String getOwner() {
        return owner;
    }

    public String[] getImages() {
        return images;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject j=new JSONObject();
        j.put(Attrs.Shop.ID,Accounts.getAccountKey());
        j.put(Attrs.Shop.NAME,name);
        j.put(Attrs.Shop.TYPE,type);
        j.put(Attrs.Shop.ADDRESS,address);
        j.put(Attrs.Shop.SEX,sex);
        j.put(Attrs.Shop.OWNER,owner);
        j.put(Attrs.Shop.LAT,lat);
        j.put(Attrs.Shop.LOG,log);
        j.put(Attrs.Shop.IMAGES,images);
        return j;
    }

    @Override
    public void fromJson(JSONObject j){
        try {
            name=j.getString(Attrs.Shop.NAME);
            type=j.getString(Attrs.Shop.TYPE);
            address=j.getString(Attrs.Shop.ADDRESS);
            sex=j.getString(Attrs.Shop.SEX);
            owner=j.getString(Attrs.Shop.OWNER);
            JSONArray t=j.getJSONArray(Attrs.Shop.IMAGES);
            int l=t.length();
            images=new String[l];
            for (int i=0;i<l;i++)
                images[i]=t.getString(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
