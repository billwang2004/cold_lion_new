/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coldlion.mobilenew.utils;

import com.ab.util.AbBase64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author PC04User
 */
public class CryptoLib {

    private static CryptoLib crypto;

    /**
     *
     * @return
     */
    public static CryptoLib getInstance() {
        //Singleton
        if (crypto == null) {
            crypto = new CryptoLib();
        }

        return crypto;
    }

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    
    private CryptoLib() {
        try {
            String algo = "DESede";            
            String ltrans = "DESede/CBC/PKCS5Padding";

            byte[] bytes = new byte[]{
                80, 51, 53, 74, 57,
                66, 66, 50, 122, 55,
                122, 108, 115, 53, 88,
                48, 48, 52, 77, 121,
                111, 46, 87, 87};

            DESedeKeySpec keySpec = new DESedeKeySpec(bytes);
            SecretKey secKey = SecretKeyFactory.getInstance(algo).generateSecret(keySpec);
            IvParameterSpec ivps = new IvParameterSpec(new byte[8]);
            encryptCipher = Cipher.getInstance(ltrans);
            decryptCipher = Cipher.getInstance(ltrans);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secKey, ivps);
            decryptCipher.init(Cipher.DECRYPT_MODE, secKey, ivps);

        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            //LogUtils.error(CryptoLib.class, Em.getString("engine.error.cryptoError"), e);
        }
    }

    /**
     *
     * @param pString
     * @return
     */
    @SuppressWarnings({"restriction", "hiding"})
    public String encrypt(String pString) {
        try {

            byte[] bytes = pString.getBytes("UTF8");

            byte[] retByte = encryptCipher.doFinal(bytes);
            return AbBase64.encode(retByte);

        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
           // LogUtils.error(CryptoLib.class, Em.getString("engine.error.encryptException") + pString, e);
        }
        return null;
    }



}
