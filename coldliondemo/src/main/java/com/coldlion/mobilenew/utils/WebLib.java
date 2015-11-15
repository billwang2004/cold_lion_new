/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.coldlion.mobilenew.utils;

import android.util.Log;

import com.ab.util.AbBase64;
import com.ab.util.AbStrUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * 
 */
public class WebLib {

    /**
     *
     * @param pObject
     * @return
     */
    public static String writeNZipObjectToString(Object pObject) {
        String str = "";
        byte[] bytes = writeNZipObject(pObject);
        if (bytes != null) {
            str = AbBase64.encode(bytes);
        }
        return str;
    }

    /**
     *
     * @param str
     * @return
     */
    public static Object unzipNReadObjectFromString(String str) {
        if (!AbStrUtil.isEmpty(str)) {
            byte[] dec = AbBase64.decode(str);
            //String strObj = new String(dec);
            return unzipNReadObject(dec);
        }
        return null;
    }

    /**
     *
     * @param pObject
     * @return
     */
    public static byte[] writeNZipObject(Object pObject) {
        GZIPOutputStream gzipOut = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            gzipOut = new GZIPOutputStream(baos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
            objectOut.writeObject(pObject);
            objectOut.close();
            byte[] bytes = baos.toByteArray();
            baos.close();
            return bytes;
        } catch (IOException excep) {
            //LogUtils.error(WebLib.class, Em.getString("engine.error.serializationError"), excep);
        } finally {
            try {
                gzipOut.close();
            } catch (IOException excep) {
                Logger.getLogger(WebLib.class.getName()).log(Level.SEVERE, null, excep);
            }
        }
        return null;
    }

    /**
     *
     * @param objBytes
     * @return
     */
    public static Object unzipNReadObject(byte[] objBytes) {
        GZIPInputStream gzipIn = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
            gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            Object myObj1 = objectIn.readObject();
            objectIn.close();
            bais.close();
            return myObj1;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("test", ex.toString());
            //LogUtils.error(WebLib.class, Em.getString("engine.error.deserializationError"), ex);
        } finally {
            try {
                gzipIn.close();
            } catch (IOException excep) {
                Logger.getLogger(WebLib.class.getName()).log(Level.SEVERE, null, excep);
            }
        }
        return null;
    }

    /**
     *
//     * @param pDatatable
     */
//    public static void adjustUnspecifiedTimezone(DataTable pDatatable) {
//        //LogUtils.info(WebLib.class, "");
//    }

    private WebLib() {
    }
}
