package com.example.bill.coldliontest;

import android.content.ContentValues;
import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Set;

/**
* Description:
===========================
* Author：           Bill Wang
* Create Date：  2014/11/26
*/

public class WebService {
    final static String NAMESPACE = "http://tempuri.org/";

    String mNameSpace = NAMESPACE;
    String mWsdl;
    boolean mDotNet = true;
    boolean mDebug = true;

    public  interface  WebServiceCallBack{
        void success(SoapObject response);
        void fail(String message);
    }

    public WebService(String wsdl) {
        mWsdl = wsdl;
    }

    public WebService(String namespace, String wsdl) {
        mNameSpace = namespace;
        mWsdl = wsdl;
    }

    void setDebugMode(boolean debug){
        mDebug = debug;
    }

    void setDotNet(boolean dotNet){
        mDotNet = dotNet;
    }

    private Object executeInner(final String methodName, final ContentValues params) {

        // 指定webservice的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(mNameSpace, methodName);
     //   KJLoger.debug(methodName +":" + params);

        // 设置调用方法的参数值，如果没有参数，可以省略
        Set<String> keys = params.keySet();

        for (String key : keys) {
           String v = params.getAsString(key);
            rpc.addProperty(key, v);
        }
        // 创建HttpTransportsSE对象。通过HttpTransportsSE类的构造方法可以指定WebService的WSDL文档的URL
        HttpTransportSE ht = new HttpTransportSE(mWsdl);
        ht.debug = mDebug;

        // 生成调用Webservice方法的SOAP请求信息
        SoapSerializationEnvelope  envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = rpc;
        envelope.dotNet = mDotNet;
        envelope.setOutputSoapObject(rpc);

        String xml = envelope.toString();
        Object object;

        try {
            // 使用call方法调用WebService方法
            ht.call(null, envelope);
            object = envelope.bodyIn;
         //   KJLoger.debug("result:" +  params);
        } catch (Exception e){
            object = e;
            e.printStackTrace();
        }

        return object;
    }

    public  void execute(final String methodName, final ContentValues cvParams, final WebServiceCallBack callback){

        AsyncTask task =   new AsyncTask() {

            @Override
            protected void onPreExecute() {
                //super.onPreExecute();
             //   dialog =  ViewInject.getprogress(mActivity, "Loading...",  false);
            }

            @Override
            protected Object doInBackground(Object[] params) {
                 return executeInner(methodName, cvParams);
            }

            @Override
            protected void onPostExecute(Object o) {

               // dialog.cancel();

                if (o instanceof SoapObject) {
                    callback.success((SoapObject) o);
                } else {
                    callback.fail(o.toString());
                }
            }
        };

        task.execute();

    }

}
