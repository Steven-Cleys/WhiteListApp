package com.ap.steven.digipolis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 2/16/2015.
 * In deze klasse worden de network calls naar de server beheert
 */
public class NetworkHandler {

    //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()); //om de localstorage te kunnen gebruiken hebben we de context van de app nodig
    SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(
            "stuff", Context.MODE_PRIVATE);
    //String json = "{ \"contacten\": [ { \"naam\": \"James Bond\", \"nummer\": \"999-000-0007\" },{ \"naam\": \"Gert Bouwens\", \"nummer\": \"0484618128\" },{ \"naam\": \"Steven Cleys\", \"nummer\": \"0473377765\" },{ \"naam\": \"Jeanne d'Arc\", \"nummer\": \"999-000-0001\" }, { \"naam\": \"John Doe\", \"nummer\": \"999-000-0002\" }, { \"naam\": \"Jane Doe\", \"nummer\": \"999-000-0003\" }, { \"naam\": \"Harrison Ford\", \"nummer\": \"999-000-0000\" },{ \"naam\": \"Abraham Lincoln\", \"nummer\": \"999-000-0004\" }, { \"naam\": \"Mona Lisa\", \"nummer\": \"999-000-0005\" }, { \"naam\": \"Isaac Newton\", \"nummer\": \"999-000-0006\" },  { \"naam\": \"Will Smith\", \"nummer\": \"999-000-0009\" },{ \"naam\": \"Bruce Willis\", \"nummer\": \"999-000-0008\" } ] }";
    //temp json for testing purposes
    private RequestQueue mRequestQueue; //needed for volley request
    String DBUrl = "https://app1.o.esb.local/whitelist/contacten/";    //database url for networkcall
    //String DBUrl = "https://stevencleys.cloudant.com/whitelist/contacten";


    static NetworkHandler myNetworkH = null;

    private NetworkHandler() {
        //loadData();
    }

    public static NetworkHandler getNetworkHandler() { //singleton

        if (myNetworkH == null)
        {
            myNetworkH = new NetworkHandler();

        }
        return myNetworkH;

    }

/*
    public List<Contact> getContacts() {
        Log.d("getc",contactjes.toString());
        return contactjes;
    }
*/

    public void getData() {  //get data from server and put it in sharedprefs to store them intern
        Log.e("NR", "server request");
        TelephonyManager telemamanger = (TelephonyManager)MyApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String getSimSerialNumber = telemamanger.getSimSerialNumber();
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());

        JSONObject wtf = null;
        CustomJsonRequest jr = new CustomJsonRequest(Request.Method.GET, DBUrl+getSimSerialNumber, wtf , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { //get database
                try {
                    Log.d("volley response", response.toString());
                    prefs.edit().putString("stuff", response.toString()).apply();
                    Log.d("responsecache", prefs.getString("stuff", ""));
                    String filename = "myfile";
                    FileOutputStream outputStream;
                    try {
                        outputStream = MyApplication.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(response.toString().getBytes());
                        outputStream.close();
                        Log.d("out", readFromFile());
                        MyApplication.getAppContext().getContentResolver().notifyChange(Uri.parse("https://app1.o.esb.local/whitelist/contacten/"), null, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null)
                Log.e("com.rommel.steven", error.getMessage());
            }
        });

        mRequestQueue.add(jr);

                //loadData();
    }

    public List<Contact> loadData() { //from sharedpreferences to a list of contacts required for the application
        List<Contact> contactjes = new ArrayList<>();
        String cachedata = readFromFile();
        Log.d("cache", cachedata);
        Contact contact;
        try {
            JSONObject obj = new JSONObject(cachedata);
            JSONArray arr = obj.getJSONArray("contacten");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                Log.i("json", (arr.getString(i)));
                contact = new Contact(item.getString("naam"), item.getString("nummer")); //naam / nummer
                Log.d("contact",contact.toString());
                contactjes.add(contact);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactjes;
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = MyApplication.getAppContext().openFileInput("myfile");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
