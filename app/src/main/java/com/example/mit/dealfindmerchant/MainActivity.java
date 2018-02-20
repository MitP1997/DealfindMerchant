package com.example.mit.dealfindmerchant;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnqr, btncheck;
    JSONObject json;
    EditText txt;
    String url_all_orders = "http://couponfind.in/get_orders.php";
    String url_dealbyid="http://couponfind.in/get_dealbyid.php";
    Boolean launched=false;
    JSONParser jsonParser = new JSONParser();
    JSONArray orders,deals;
    String[] verify;
    String[] check;
    String[] order_id;
    String[] no_deals,deal_id;
    IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new allOrders().execute();
        btnqr = (Button) findViewById(R.id.btn);
        btncheck = (Button) findViewById(R.id.check);
        txt = (EditText) findViewById(R.id.edit);
        qrScan=new IntentIntegrator(this);



        btncheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String val = txt.getText().toString();
                boolean existance=false,verification=false;
                if (val.equalsIgnoreCase(""))
                {
                    txt.setError("Please enter in this field");
                }

                else {
                        String order=null,j=null,d=null;
                        for(int i=0;i<orders.length();i++)
                        {
                            if(val.equalsIgnoreCase(check[i]))
                            {
                                existance=true;
                                if(verify[i].equals("1"))
                                    verification=true;
                                order=order_id[i];
                                j=no_deals[i];
                                d=deal_id[i];
                                break;
                            }
                        }

                        if(existance)
                        {
                            launched=true;
                            new dealbyid(verification,order,j,d).execute();
                        }
                        else
                            txt.setError("Invalid code");
                }
            }
        });

        btnqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

    }

    public String coder(String deal_id,String contactnumber){
        String qr="DLFD";
        for(int i=0;i<10;i+=2)
        {
            qr=qr+contactnumber.charAt(i);
            if(i%3==0)
                qr+=deal_id.charAt(0);
        }
        return qr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                //if qrcode has nothing in it
                if (result.getContents() == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    //if qr contains data
                    try {
                        JSONObject obj = new JSONObject(result.getContents());
                        String val = obj.toString();
                        Boolean verification = false;
                        String order = null;
                        Toast.makeText(getApplicationContext(), "Hurray " + val, Toast.LENGTH_SHORT).show();
                        Boolean existance = false;
                        String j=null,d=null;
                        for (int i = 0; i < orders.length(); i++) {
                            if (val.equalsIgnoreCase(check[i])) {
                                existance = true;
                                if (verify[i].equals("1"))
                                    verification = true;
                                order = order_id[i];
                                j=no_deals[i];
                                d=deal_id[i];
                                break;
                            }
                        }

                        if (existance) {
                            launched = true;
                            new dealbyid(verification,order,j,d).execute();
                        } else
                            Toast.makeText(getApplicationContext(), "Wrong QR code", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Boolean verification = false;
                        String order = null;
                        String val = result.getContents();
                        Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT).show();
                        Boolean existance = false;
                        String j=null,d=null;
                        for (int i = 0; i < orders.length(); i++) {
                            if (val.equalsIgnoreCase(check[i])) {
                                existance = true;
                                if (verify[i].equals("1"))
                                    verification = true;
                                order = order_id[i];
                                j=no_deals[i];
                                d=deal_id[i];
                                break;
                            }
                        }

                        if (existance) {
                            launched = true;
                            new dealbyid(verification,order,j,d).execute();
                        } else
                            Toast.makeText(getApplicationContext(), "Wrong QR code", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
    }


    public void newActivity(Boolean v,String o,String j,String i)
    {
        Intent it = new Intent(MainActivity.this, Main2Activity.class);
        it.putExtra("verification", v);
        it.putExtra("order_id", o);
        it.putExtra("no_deals",j);
        it.putExtra("itemname",i);
        startActivity(it);
    }

    public class dealbyid extends AsyncTask<String,String,String>
    {
        String order,j,deal_id,itemname;
        Boolean verification;
        public dealbyid(Boolean v,String o,String i,String d)
        {
            verification=v;
            order=o;
            j=i;
            deal_id=d;
        }


        @Override
        protected String doInBackground(String... param) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("deal_id",deal_id));
            // getting JSON string from URL
            json = jsonParser.makeHttpRequest(url_dealbyid, "GET", params);
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    deals = json.getJSONArray("deals");
                    JSONObject c=deals.getJSONObject(0);

                    itemname=c.getString("itemname");
                    newActivity(verification,order,j,itemname);
                }
            }catch(Exception e){}



            return null;
        }

    }

    @Override
    public void onBackPressed() {
        /*finish();
        System.gc();
        System.exit(0);*/

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    public class allOrders extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... param) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            json = jsonParser.makeHttpRequest(url_all_orders, "GET", params);
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    orders = json.getJSONArray("orders");
                    verify=new String[orders.length()];
                    check=new String[orders.length()];
                    order_id=new String[orders.length()];
                    no_deals=new String[orders.length()];
                    deal_id=new String[orders.length()];
                    for (int i = 0; i < orders.length(); i++) {
                        JSONObject c = orders.getJSONObject(i);
                        String deal = c.getString("deal_id");
                        deal_id[i]=deal;
                        String number = c.getString("number");
                        order_id[i]=c.getString("order_id");
                        verify[i]=c.getString("verification");
                        check[i]=coder(deal,number);
                        no_deals[i]=c.getString("no_deals");
                        Log.d("Check[i]",check[i]);
                    }
                }


            } catch (Exception e) {
                Log.d("caught","");
                e.printStackTrace();
            }
            return null;
        }
    }
}
