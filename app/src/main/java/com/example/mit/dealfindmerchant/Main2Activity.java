package com.example.mit.dealfindmerchant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    TextView txt,no_deals;
    ImageView img;
    JSONParser jsonParser=new JSONParser();
    String url_update_order="http://couponfind.in/update_order.php";
    JSONObject json;
    String order_id,noDeal,itemname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        txt=(TextView)findViewById(R.id.text);
        img=(ImageView)findViewById(R.id.imagee);
        no_deals=(TextView)findViewById(R.id.no_deals);
        Boolean verification=getIntent().getExtras().getBoolean("verification");
        order_id=getIntent().getExtras().getString("order_id");
        noDeal=getIntent().getExtras().getString("no_deals");
        itemname=getIntent().getExtras().getString("itemname");
        if(verification)
        {
            txt.setText("Already Verified");
            no_deals.setText("");
            img.setImageResource((R.drawable.error));
        }
        else
        {
            Log.d("Verification will be","updated "+noDeal);

            no_deals.setText(""+noDeal+" "+itemname+" purchase has been verified");
            new updateOrder().execute();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(Main2Activity.this,MainActivity.class);
        startActivity(i);
    }

    public class updateOrder extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... param) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("verification","1"));
            params.add(new BasicNameValuePair("order_id",order_id));
            // getting JSON string from URL
            Log.d("url","");
            json = jsonParser.makeHttpRequest(url_update_order, "POST", params);
            Log.d("To string "+json.toString(),"");
                        return null;
        }

    }
}
