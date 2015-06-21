package com.ap.steven.digipolis;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends Activity {

    public static ListAdapter myAdapter;
    private ListView list;
    private EditText input;
    private ContentObserver mObserver;
    public static final String AUTHORITY = "com.company.content";
    public static final String ACCOUNT_TYPE = "default_account";
    public static final String ACCOUNT = "stub_account";
    public static final long SECONDS_PER_MINUTE = 2L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    ContentResolver mResolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listView);
        input = (EditText) findViewById(R.id.editText);

        //NukeSSLCerts.nuke();
        Account dummyaccount = CreateSyncAccount(this);

        mResolver = getContentResolver();
        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { //refresh list adapter
            public void onChange(boolean selfChange) {

                Log.d("onchange","data changed");
                NetworkHandler handler = NetworkHandler.getNetworkHandler();
                myAdapter.fullContactlist = handler.loadData();
                myAdapter.contactlist = myAdapter.fullContactlist;
                myAdapter.notifyDataSetChanged();
                Log.d("",handler.loadData().toString());
            }
        };
        getContentResolver().registerContentObserver(Uri.parse("https://app1.o.esb.local/whitelist/contacten/"),false, mObserver);

/*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.setIsSyncable(dummyaccount,AUTHORITY,1);
        ContentResolver.setSyncAutomatically(dummyaccount,AUTHORITY,true);
        ContentResolver.addPeriodicSync(dummyaccount,AUTHORITY,Bundle.EMPTY,20); //time

        myAdapter = new ListAdapter();

        list.setAdapter(myAdapter);
        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {

                Contact clickedContact = myAdapter.getItem(pos);

                Toast.makeText(MainActivity.this, clickedContact.name, Toast.LENGTH_LONG).show();
                LaunchCall(clickedContact.tel);
            }
        });

        //testMethod();

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                // When user changed the Text
                MainActivity.this.myAdapter.filter(cs);
                Log.e("", cs.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            Contact selectedContact = myAdapter.getItem(info.position);
            menu.setHeaderTitle(selectedContact.name);
            Log.d("",info.position + "");
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void LaunchDialer(String number) {
        Log.e("", "launching dialer");
        String numberToDial = "tel:" + number;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(numberToDial)));
    }

    // Launch the phone dialer

    public void LaunchCall(String number) { //pass number to default android call launcher to initiate call

        String numberToDial = "tel:" + number;

        Log.e("", "launching call to " + numberToDial);
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial)));
    }

    public void testMethod() { //temp method
        TelephonyManager tMgr =(TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String getSimSerialNumber = telemamanger.getSimSerialNumber();

        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String EMEI = mngr.getDeviceId();

        //Log.d("phonenumber" , mPhoneNumber);
        //Log.d("sim serial" , getSimSerialNumber);
        //Log.d("emei" , EMEI);
        Toast.makeText(MainActivity.this, "phonemuber "+ mPhoneNumber, Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this,"sim serial "+ getSimSerialNumber, Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, "emei "+ EMEI, Toast.LENGTH_LONG).show();
    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return newAccount;
    }

   /* public List<Contact> loadData() { //from sharedpreferences to a list of contacts required for the application

        preferences = this. getSharedPreferences("json",Context.MODE_MULTI_PROCESS);
        ArrayList<Contact> contactjes = new ArrayList<>();
        String cachedata = preferences.getString("json","");
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
    }*/
    

    public static class MyOutgoingCallHandler extends BroadcastReceiver { //the receiver


        @Override
        public void onReceive(Context context, Intent intent) {
/*            Log.d("resultdata", getResultCode() + "");
            setResultData("5555");
            setResult(0, null, null);
            abortBroadcast();
            MainActivity main = new MainActivity();
            //main.LaunchCall("");*/
            // Toast.makeText(context, intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER), Toast.LENGTH_LONG).show();
            Log.e("",intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)); //log phonenumber for outgoing call
            boolean check = false;

            NetworkHandler handler = NetworkHandler.getNetworkHandler();
            List<Contact> fullContacts = handler.loadData();

            mainLoop:
            for (Contact c : fullContacts) {
                Log.i("tel",c.tel.replace("-", "") + " " + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                if (intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER).equals(c.tel.replace("-", ""))) {
                    Log.e("hmm", intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
                    check = true;
                    break mainLoop; //break out of for loop
                }
            }


            if(!check) { //check for phonenumber
                Log.e("","Call Blocked");
                Toast.makeText(context, R.string.out_wrong_number, Toast.LENGTH_LONG).show();
                setResultData(null); //cancel the call request
            }
        }
    }

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }
}