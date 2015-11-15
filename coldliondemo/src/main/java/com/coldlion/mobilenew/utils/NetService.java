package com.coldlion.mobilenew.utils;

import android.content.Context;
import android.util.Log;

import com.ab.soap.AbSoapClient;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.coldlion.mobilenew.type.ConstValue;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by Bill Wang on 2015/10/11.
 */
public class NetService  {

    final String url = "http://52.6.66.43:8080/CLDevWebserver/EngineServer";
    final String nameSpace = "http://ws.coldlion.com/";

    AbSoapClient soapClient = null;
    private Context mContext = null;

    private static NetService s_Instance = null;

    static  public class ResponseListener  extends AbSoapListener{

        @Override
        public void onSuccess(int i, SoapObject soapObject) {
            Log.i(ConstValue.APP_TAG, soapObject.toString());
        }

        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            Log.i(ConstValue.APP_TAG, s);
        }

        @Override
        public void onFailure(int i, SoapFault soapFault) {
            Log.i(ConstValue.APP_TAG, soapFault.toString());
        }
    }


    private NetService(Context context) {
        // 创建Http工具类
        mContext = context;
        soapClient = new AbSoapClient(context);
        soapClient.setDotNet(false);
    }

    public static NetService getInstance(Context context) {
        if(s_Instance == null){
            s_Instance = new NetService(context);
        }else{
            s_Instance.mContext = context;
        }
        return s_Instance;
    }


    /**
     *  获取connect bean id
     * @param userId
     */
    public void getSi(String userId, ResponseListener listener){
        AbSoapParams Params = new AbSoapParams();

        Params.put("UserId", userId);
        Params.put("Key", CryptoLib.getInstance().encrypt("@Loglogistics"));

        soapClient.call(url, nameSpace, "getsi", Params, listener);
    }

    /**
     * 登陆
     * @param connBeanId
     * @param user
     * @param password
     * @param listener
     */
   public void login(String connBeanId, String user, String password, ResponseListener listener){
        AbSoapParams Params = new AbSoapParams();
        Params.put("arg0", connBeanId);
        Params.put("arg1", user);
        Params.put("arg2", password);
        Params.put("arg3", " ");

        soapClient.call(url, nameSpace, "ln2", Params, listener);
    }

    /**
     * 获取menu
     * @param connBeanId
     * @param listener
     */
    public void getMenu(String connBeanId, ResponseListener listener) {
        AbSoapParams Params = new AbSoapParams();
        Params.put("arg0", connBeanId);
        soapClient.call(url, nameSpace, "mobileGum", Params, listener);
    }

    /**
     * 获取页面内容
     * @param connBeanId
     * @param menuName
     * @param listener
     */
    public void getPage(String connBeanId, String menuName, ResponseListener listener){
        AbSoapParams Params = new AbSoapParams();
        Params.put("arg0", connBeanId);
        Params.put("arg1", menuName);
        soapClient.call(url, nameSpace, "mobileOdp", Params, listener);
    }


}
