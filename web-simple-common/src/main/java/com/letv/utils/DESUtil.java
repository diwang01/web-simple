package com.letv.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

public class DESUtil {

    private static final Logger log = LoggerFactory.getLogger(DESUtil.class);
    Key key ;

    public DESUtil() {

    }

    public DESUtil(String str) {
        setKey(str); // 生成密匙
    }

    public Key getKey() {
        return key ;
    }

    public void setKey(Key key) {
        this . key = key;
    }

    /**
     * 根据参数生成 KEY
     */
    public void setKey(String strKey) {
        try {
//            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//            secureRandom.setSeed(strKey.getBytes());
//            KeyGenerator _generator = KeyGenerator.getInstance("DES");
//            _generator.init(secureRandom);
//            this.key = _generator.generateKey();
//            _generator = null;

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            DESKeySpec keySpec = new DESKeySpec(strKey.getBytes());
            this.key = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {

            log.error("[参数错误]key="+strKey,e);
        }

    }

    /**
     * 加密 String 明文输入 ,String 密文输出
     */
    public String encryptStr(String strMing) {
        byte [] byteMi = null ;
        byte [] byteMing = null ;
        String strMi = "" ;

        try {
            byteMing = strMing.getBytes("UTF8");
            byteMi = this .encryptByte(byteMing);
            strMi = Base64.encodeBase64URLSafeString(byteMi);
        } catch (Exception e) {
            log.error("[密文输出]key="+strMing,e);
        }
        return strMi;
    }

    /**
     * 解密 以 String 密文输入 ,String 明文输出
     *
     * @param strMi
     */
    public String decryptStr(String strMi) {
        byte [] byteMing = null ;
        byte [] byteMi = null ;
        String strMing = "" ;
        try {
            byteMi = Base64.decodeBase64(strMi);
            byteMing = this.decryptByte(byteMi);
            if (byteMing==null)return "";
            strMing = new String(byteMing, "UTF8");
        } catch (Exception e) {
            log.error("[密文输出]key="+strMi,e);
            log.error("{}",e.getMessage());
        }
        return strMing;
    }

    /**
     * 加密以 byte[] 明文输入 ,byte[] 密文输出
     *
     * @param byteS
     */
    private byte [] encryptByte( byte [] byteS) {
        byte [] byteFina = null ;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance ( "DES" );
            cipher.init(Cipher. ENCRYPT_MODE , key );
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            log.error("{}",e);
        } finally {
            cipher = null ;
        }
        return byteFina;
    }

    /**
     * 解密以 byte[] 密文输入 , 以 byte[] 明文输出
     *
     * @param byteD
     *
     */
    private byte [] decryptByte( byte [] byteD) {
        Cipher cipher;
        byte [] byteFina = null ;
        try {
            cipher = Cipher.getInstance ( "DES" );
            cipher.init(Cipher. DECRYPT_MODE , key );

            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            log.error("{}",e.getMessage());
            return null;
        } finally {
            cipher = null ;
        }
        return byteFina;
    }

    public static void main(String[] args) throws Exception {
//        DESUtil des = new DESUtil( "12345678" );
        // DES 加密文件
        // des.encryptFile("G:/test.doc", "G:/ 加密 test.doc");
        // DES 解密文件
        // des.decryptFile("G:/ 加密 test.doc", "G:/ 解密 test.doc");
//        byte[] str1 = "1231".getBytes("utf-8");

        // DES 加密字符串
        byte[] str2 = {44,55,-110,-69,-85,118,1,94};
        DESUtil des2 = new DESUtil("12345679");
        // DES 解密字符串
        byte[] deStr = des2.decryptByte(str2);
//        System. out .println( " 加密前： " + ArrayUtils.toString(str1));
//        System. out .println( " 加密后： " + ArrayUtils.toString(str2));
        System. out .println( " 解密后： " + ArrayUtils.toString(deStr));
    }
}