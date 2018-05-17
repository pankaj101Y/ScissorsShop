package com.example.hp.scissorsshop.Networking;

/**
 * Created by PANKAJ KUMAR on 4/6/2018.
 */

public interface Headers {

    interface Keys{
        String SCOPE="scope";
        String QUERY="query";
        String CONTENT ="content";
        String REQUESTER = "requester";
        String REQUEST_NO = "requestNo";
    }

    interface Scope{
        String VERIFICATION="verification";
        String REGISTRATION = "shopRegistration";
        String SERVICE = "service";
        String PACKAGE = "package";
        String SHOP_PROFILE = "shopProfile";
    }

    interface Query{
        String sendOTP="sendOTP";
        String VERIFY_OTP = "verifyOTP";
        String SHOP_REGISTER = "registershop";
        String ADD="add";
        String MY_SERVICES = "myservices";
        String MY_PACKAGES = "mypackages";
        String EDIT = "edit";
        String DELETE = "deleteImage";
        String SET_PASSWORD = "setPassword";
        String SET_OWNER = "setOwner";
        String CHECK = "check";
        String LOG_IN = "login";
        String SHOP_DETAILS = "getShopDetails";
        String OWNER_DETAILS = "getOwnerDetails";
        String ADD_IMAGE = "addImage";
    }

    interface Properties{
        String METHOD="POST";
        String CONTENT_TYPE="content-type";
        String JSON="application/json";
        int TIME_OUT=4000;
    }
}
