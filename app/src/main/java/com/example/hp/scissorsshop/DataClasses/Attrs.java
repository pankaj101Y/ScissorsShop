package com.example.hp.scissorsshop.DataClasses;

/**
 * Created by PANKAJ KUMAR on 4/6/2018.
 */

public interface Attrs {

    interface Verification {
        String PHONE_NUMBER="mobile";
        String OTP="otp";
    }

    interface Shop{
        String TYPE="shopType";
        String SEX="shopSex";
        String ADDRESS="shopAddress";
        String LAT="lat";
        String LOG="log";
        String NAME="shopName";
        String ID="shopId";
        String OWNER = "owner";
        String IMAGES = "images";
    }

    interface Service{
        String SERVER_ID="_id";
        String DOMAIN="domain";
        String NAME="service";
        String PRICE="price";
        String SERVICE_ID="serviceId";
        String NEW_PRICE="newPrice";
    }

    interface Package{
         String SERVER_ID ="_id";
         String PRICE="price";
         String DOMAIN="domain";
         String SERVICE="service";
         String SERVICES="services";
        String TYPE = "type";
        String PACKAGE_ID="packageId";
    }

    interface Accounts{
        String Account="account";
        String KEY="key";
        String INVALID = "invalid";
        String SHOP_REGISTRATION = "shopRegistration";
        String PASSWORD = "password";
        String LOG_IN = "log-in";
        String OWNER="owner";
        String DONE = "done";
    }

    interface PackagesChangesHolder{
        String ADDITIONS="additions";
        String REMOVALS="removals";
    }

}
