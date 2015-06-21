package com.ap.steven.digipolis;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steven on 2015-04-24.
 */
public class CustomJsonRequest extends JsonObjectRequest{

    public CustomJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

/*    public CustomJsonRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String user, String passw) {
        super(url, listener, errorListener);
        if (user != null && passw != null) {
            String loginEncoded = new String(Base64.encode((user + ":" + passw).getBytes(), Base64.NO_WRAP));
            this.headers.put("Authorization", "Basic " + loginEncoded);
        }
    }*/

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String auth = "Basic "
                + Base64.encodeToString(("DEV\\WhiteList.O" + ":" + "8laCk_1i5T").getBytes(), //"DEV\\WhiteList.O" + ":" + "8laCk_1i5T"
                Base64.DEFAULT);
        Log.e("auth", auth);
        headers.put("Authorization", auth);
        return headers;
    }
}
