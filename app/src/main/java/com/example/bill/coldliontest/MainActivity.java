package com.example.bill.coldliontest;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ab.soap.AbSoapClient;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.example.bill.coldliontest.model.CLMenuItem;
import com.example.bill.coldliontest.model.UserItem;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    AbSoapClient soapClient;
    private List<CLMenuItem> mMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         soapClient = new AbSoapClient(this);
        soapClient.setDotNet(false);
        getSi("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final String url = "http://52.6.66.43:8080/CLDevWebserver/EngineServer";
    final String nameSpace = "http://ws.coldlion.com/";
    String connBeanId ;

    void getPage(){
        AbSoapParams Params = new AbSoapParams();
        Params.put("arg0", connBeanId);
        Params.put("arg1", "OrderHoldLeonTest");
        soapClient.call(url, nameSpace, "mobileOdp", Params, new AbSoapListener() {

            @Override
            public void onSuccess(int i, SoapObject soapObject) {
                Log.i("test", soapObject.toString());
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onFailure(int i, SoapFault soapFault) {

            }
        });

    }

    void getMenu() {
        AbSoapParams Params = new AbSoapParams();
        Params.put("arg0", connBeanId);
        soapClient.call(url, nameSpace, "mobileGum", Params, new AbSoapListener() {

            @Override
            public void onSuccess(int i, SoapObject soapObject) {
                Log.i("test", soapObject.toString());

                String retStr  =soapObject.getPropertyAsString("return");
                try {
                    JSONObject jsonObject = new JSONObject(retStr);
                    String menus = jsonObject.getString("menus");
                    TypeToken<List<CLMenuItem>> t = new TypeToken<List<CLMenuItem>>() {
                    };
                    mMenuList = (List<CLMenuItem>) AbJsonUtil.fromJson(menus, t);
                    getPage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //   String responseValue = soapObject.getPropertyAsString("return");

            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onFailure(int i, SoapFault soapFault) {

            }
        });
    }


    void getSi(String userId){
         AbSoapParams Params = new AbSoapParams();

         Params.put("UserId", userId);
        Params.put("Key", CryptoLib.getInstance().encrypt("@Loglogistics"));


        soapClient.call(url, nameSpace, "getsi", Params, new AbSoapListener() {

            @Override
            public void onSuccess(int i, SoapObject soapObject) {
                Log.i("test", soapObject.toString());

                connBeanId =soapObject.getPropertyAsString("return");
                login();

            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onFailure(int i, SoapFault soapFault) {

            }
        });

    }

    void login(){

        final String methodName ="ln2";
        AbSoapParams Params = new AbSoapParams();
        Params.put("arg0", connBeanId);
        Params.put("arg1", "user1");
        Params.put("arg2", "bbbbbbbb");
        Params.put("arg3", " ");


        soapClient.call(url, nameSpace, methodName, Params, new AbSoapListener() {
            @Override
            public void onSuccess(int i, SoapObject soapObject) {
                Log.i("test", soapObject.toString());


                String responseValue =soapObject.getPropertyAsString("return");
                    UserItem userItem = (UserItem) AbJsonUtil.fromJson(responseValue, UserItem.class);
                  getMenu();


            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                Log.i("test", s);
            }

            @Override
            public void onFailure(int i, SoapFault soapFault) {
                Log.i("test", soapFault.toString());
            }
        });


    }
}
