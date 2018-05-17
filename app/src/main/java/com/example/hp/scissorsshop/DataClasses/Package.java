package com.example.hp.scissorsshop.DataClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.hp.scissorsshop.Networking.Responses;
import com.example.hp.scissorsshop.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Package implements Parcelable,Json<Package> {
    private String serverId;
    private String price=null;
    private String type;
    private ArrayList<Service>services=new ArrayList<>();

    public Package(String type){
        this.type=type;
    }

    public Package(JSONObject j){
        fromJson(j);
    }

    public Package(ArrayList<Service> services, String price, String id, String type) {
        this.services = services;
        this.serverId =id;
        this.type = type;
        this.price=price;
    }

    private Package(Parcel in) {
        serverId = in.readString();
        price = in.readString();
        type = in.readString();
        services = in.createTypedArrayList(Service.CREATOR);
    }

    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };

    public void addService(Service service){
        services.add(service);
    }

    public void removeService(int serviceNum){
        if (serviceNum<services.size())
            services.remove(serviceNum);
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public String getPrice() {
        return price;
    }

    public String getServerId() {
        return serverId;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        int l=services.size();
        for (int i=0;i<l;i++){
            builder.append(services.get(i).toString());
        }
        builder.append("\n").append("Price :").append(price);
        return builder.toString();
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject j=new JSONObject();
        j.put(Attrs.Shop.ID,Accounts.getAccountKey());
        j.put(Attrs.Package.SERVER_ID, serverId);
        j.put(Attrs.Package.PRICE,price);
        j.put(Attrs.Package.SERVICES, getServicesJSONArray());
        j.put(Attrs.Package.TYPE,type);
        return j;
    }


    @Override
    public void fromJson(JSONObject j){
        try {
            serverId =j.getString(Attrs.Package.SERVER_ID);
            price=j.getString(Attrs.Package.PRICE);
            // TODO: 4/7/2018 take care of this
           // type=j.getString(Attrs.Package.TYPE);
            services=Utility.getListFromJsonArray(j.getJSONArray(Responses.SERVICES),Service.class);
        } catch (JSONException ignored) {
        }
    }

    private JSONArray getServicesJSONArray() throws JSONException {
        int l=services.size();
        JSONArray a=new JSONArray();
        for (int i=0;i<l;i++){
            JSONObject j=new JSONObject();
            j.put(Attrs.Package.DOMAIN,services.get(i).getDomain());
            j.put(Attrs.Package.SERVICE,services.get(i).getName());
            a.put(j);
        }
        return a;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serverId);
        dest.writeString(price);
        dest.writeString(type);
        dest.writeTypedList(services);
    }
}
