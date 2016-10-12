package com.cognizant.app.beacondetectorapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WelcomeOfferActivity extends Activity {
    EditText password,userName;
    Button login;
    Button pushNotification;
    ProgressBar progressBar;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_offer);
        password=(EditText) findViewById(R.id.editText2);
        userName=(EditText) findViewById(R.id.editText1);
        login=(Button) findViewById(R.id.button1);
        pushNotification =(Button) findViewById(R.id.button2);

        progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);


        login.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String s1=userName.getText().toString();
                String s2=password.getText().toString();
                new ExecuteTask().execute(s1,s2);

            }
        });

        pushNotification.setOnClickListener(new OnClickListener (){

            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                new ExecuteTask().execute();
            }
        });
    }

    class ExecuteTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String[] paramValues=params;
            String name=null;
            String password=null;

            if(paramValues !=null && paramValues.length!=0) {
                name = paramValues[0];
                password = paramValues[1];
            }

            if(name !=null && password !=null) {
                return postLoginData(params);
            }else{
                return postPushNotificationData();
            }
        }

        @SuppressLint("ShowToast")
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            System.out.println("Webservice Response:::::"+result);

        }

    }

    public String postLoginData(String[] values) {

        String responseData="";
        boolean result = false;
        HttpClient hc = new DefaultHttpClient();
        String message;

        HttpPost httpPost = new HttpPost("http://10.0.2.2:8080/booking-engine/api/booking/login");
        JSONObject object = new JSONObject();

        try {

            object.put("username", values[0]);
            object.put("password", values[1]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            message = object.toString();
            httpPost.setEntity(new StringEntity(message, "UTF8"));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Booking-API-Key","1Q7VbsO93CPDMxTyzYp0ADnXG56gaRymAsp3vRiTcfw");
            httpPost.setHeader("appPlatform","android");

            HttpResponse resp = hc.execute(httpPost);

            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == 204)
                    result = true;
            }

            responseData= readResponse(resp);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return responseData;
    }


    public String postPushNotificationData() {

        String responseData="";
        boolean result = false;
        HttpClient hc = new DefaultHttpClient();
        String message;

        HttpPost httpPost = new HttpPost("http://10.0.2.2:8080/booking-engine/api/booking/notify/info");
        JSONObject object = new JSONObject();

        try {

            object.put("memberId", "123456");
            object.put("deviceId", "1234");
            object.put("latitude", "65.7");
            object.put("longitude", "87.6");
            object.put("identifier", "Car_Rental");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            message = object.toString();
            httpPost.setEntity(new StringEntity(message, "UTF8"));
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Booking-API-Key","1Q7VbsO93CPDMxTyzYp0ADnXG56gaRymAsp3vRiTcfw");
            httpPost.setHeader("appPlatform","android");

            HttpResponse resp = hc.execute(httpPost);

            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == 204)
                    result = true;
            }

            System.out.println("Status line:  " + resp.getStatusLine().getStatusCode());
            responseData= readResponse(resp);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return responseData;


    }


    public String readResponse(HttpResponse res) {

        InputStream is=null;
        String returnText="";

        try {

            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();

            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }

            returnText=sb.toString();

        } catch (Exception e){

            e.printStackTrace();
        }

        return returnText;

    }

}