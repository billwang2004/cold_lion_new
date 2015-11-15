package com.coldlion.mobilenew.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ab.activity.AbActivity;
import com.ab.soap.AbSoapClient;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.coldlion.mobilenew.R;
import com.coldlion.mobilenew.model.UserItem;
import com.coldlion.mobilenew.type.ConstValue;
import com.coldlion.mobilenew.utils.CryptoLib;
import com.coldlion.mobilenew.utils.NetService;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;


public class LoginActivity extends AbActivity implements ConstValue {

    @AbIocView(id = R.id.edUserName)
    EditText edUser;

    @AbIocView(id = R.id.edPassword)
    EditText edPassward;

    @AbIocView(id = R.id.btnLogin, click = "widgetClick")
    Button btLogin;

    @AbIocView(id = R.id.btnCancel, click = "widgetClick")
    Button btCancel;

    @AbIocView(id = R.id.cbCheckUserName)
    CheckBox cbRememberUser;

    @AbIocView(id = R.id.cbCheckPassword)
    CheckBox cbRememberPassword;

    AlertDialog mDialog = null;

    NetService mNetService = null;
    String connBeanId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mNetService = NetService.getInstance(this);

        connBeanId = AbSharedUtil.getString(this, KEY_CONN_BEAN_ID);
        if(connBeanId == null){
            mNetService.getSi("", new NetService.ResponseListener() {
                @Override
                public void onSuccess(int i, SoapObject soapObject) {
                    super.onSuccess(i, soapObject);
                    connBeanId =soapObject.getPropertyAsString("return");
                }
            });
        }

        String user = AbSharedUtil.getString(this, KEY_USER);
        if(!AbStrUtil.isEmpty(user)){
            edUser.setText(user);
            cbRememberUser.setChecked(true);
        }

        String password = AbSharedUtil.getString(this, KEY_PASSWORD);
        if(!AbStrUtil.isEmpty(password)){
            edPassward.setText(password);
            cbRememberPassword.setChecked(true);
        }

    }

    public void widgetClick(View v) {
          switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }


    void login(){
        //Params.put("arg1", "lchen");
        //Params.put("arg2", "bbbbbbbb");
        final String user = edUser.getText().toString();
        final String password = edPassward.getText().toString();

        mNetService.login(connBeanId, user, password, new NetService.ResponseListener() {
            @Override
            public void onSuccess(int i, SoapObject soapObject) {
                super.onSuccess(i, soapObject);
                String responseValue = soapObject.getPropertyAsString("return");
                UserItem userItem = (UserItem) AbJsonUtil.fromJson(responseValue, UserItem.class);

                AbSharedUtil.putString(getApplicationContext(), KEY_CONN_BEAN_ID, connBeanId);

                if (userItem.getUserId().toLowerCase().equals(user.toLowerCase())) {
                    if (cbRememberUser.isChecked()) {
                        AbSharedUtil.putString(getApplicationContext(), KEY_USER, user);
                    } else {
                        AbSharedUtil.putString(getApplicationContext(), KEY_USER, "");
                    }

                    if (cbRememberPassword.isChecked()) {
                        AbSharedUtil.putString(getApplicationContext(), KEY_PASSWORD, password);
                    } else {
                        AbSharedUtil.putString(getApplicationContext(), KEY_PASSWORD, "");
                    }

                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    intent.putExtra(KEY_USER, user);
                    intent.putExtra(KEY_CONN_BEAN_ID, connBeanId);
                    startActivity(intent);
                    finish();
                } else {
                    AbToastUtil.showToast(LoginActivity.this, getString(R.string.login_error));
                }
            }

        });


    }


}
