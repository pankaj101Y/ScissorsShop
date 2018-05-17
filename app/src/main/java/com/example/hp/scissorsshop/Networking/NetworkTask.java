package com.example.hp.scissorsshop.Networking;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.hp.scissorsshop.DataClasses.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkTask<I> extends AsyncTask<I,Void,JSONObject>{
    private String scope,query;
    private int requestNo,requester;
    private boolean DEBUG=true;
    private String TAG="networkTask";

    public NetworkTask(String scope, String query,int requester,NetworkCallback callback){
        this.scope=scope;
        this.query=query;
        requestNo = ResponseHandler.addCallback(requester,callback);
        this.requester=requester;
    }

    @Override
    protected JSONObject doInBackground(I[] is) {
        try {
            URL url=new URL(NetworkDetails.DATABASE_REQUEST);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Headers.Properties.METHOD);
            conn.setRequestProperty(Headers.Properties.CONTENT_TYPE,Headers.Properties.JSON);
            conn.setConnectTimeout(Headers.Properties.TIME_OUT);

            if (DEBUG)
                Log.e(TAG,"connected to server");

            JSONObject request=new JSONObject();
            request.put(Headers.Keys.SCOPE,scope);
            request.put(Headers.Keys.QUERY,query);
            request.put(Headers.Keys.REQUESTER,requester);
            request.put(Headers.Keys.REQUEST_NO, requestNo);
            request.put(Headers.Keys.CONTENT,((Json)is[0]).toJson());
            if (DEBUG)
                Log.e(TAG,request.toString());


            OutputStream os=conn.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(request.toString());

            writer.close();
            os.close();
            if (DEBUG)
                Log.e(TAG,"finished writing");
            InputStream in=conn.getInputStream();
            InputStreamReader reader=new InputStreamReader(in,"ISO-8859-1");
            BufferedReader bReader=new BufferedReader(reader);


            StringBuilder builder=new StringBuilder();
            String temp;
            while ((temp=bReader.readLine())!=null){
                builder.append(temp);
            }

            bReader.close();
            reader.close();
            in.close();

            if (DEBUG)
                Log.e(TAG,builder.toString());
            return new JSONObject(builder.toString());


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject j) {
        ResponseHandler.handle(scope,query,requester,requestNo,j);
    }

    public int getRequestNo(){
        return requestNo;
    }
}
