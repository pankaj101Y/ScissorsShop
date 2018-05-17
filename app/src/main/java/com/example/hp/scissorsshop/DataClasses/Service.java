package com.example.hp.scissorsshop.DataClasses;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Service  implements Json<Service>,Parcelable{
    private String serverId,domain,name,price;

    public Service(String serverId,String domain, String name, String price) {
        this.serverId=serverId;
        this.domain = domain;
        this.name = name;
        this.price = price;
    }

    public Service(JSONObject j){
        fromJson(j);
    }


    private Service(Parcel in) {
        serverId = in.readString();
        domain = in.readString();
        name = in.readString();
        price = in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public String getDomain() {
        return domain;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append(getName()).append("\n");
        if (price!=null)
            builder.append(getPrice()).append("\n");
        return builder.toString();
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serverId);
        dest.writeString(domain);
        dest.writeString(name);
        dest.writeString(price);
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject j=new JSONObject();
        j.put(Attrs.Shop.ID,Accounts.getAccountKey());
        j.put(Attrs.Service.SERVER_ID,serverId);
        j.put(Attrs.Service.NAME,name);
        j.put(Attrs.Service.DOMAIN,domain);
        j.put(Attrs.Service.PRICE,price);
        return j;
    }

    @Override
    public void fromJson(JSONObject j){
        try {
            name=j.getString(Attrs.Service.NAME);
            serverId=j.getString(Attrs.Service.SERVER_ID);
            price=j.getString(Attrs.Service.PRICE);
        } catch (JSONException ignored) {}
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
