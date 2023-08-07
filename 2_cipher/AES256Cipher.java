package kr.com.dreamsecurity.hotp.crypto;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 
 * AES256, HMAC 암호화 (by javax lib) 
 * 
 * @author Park_Kwang_Ho
 *
 */
public class AES256Cipher {
    private static volatile AES256Cipher INSTANCE;  // main memory 할당 (volatile)

    static String IV = "";          // 16byte

    public static AES256Cipher getInstance() {
        if (INSTANCE == null) {
            synchronized (AES256Cipher.class) {
                if (INSTANCE == null)
                    INSTANCE = new AES256Cipher();
            }
        }
        return INSTANCE;
    }

    // random key 생성
    public String generateKey() {
    	char[] tmp = new char[32];
		for(int i=0; i<tmp.length; i++) {
			int div = (int) Math.floor( Math.random() * 2 );
			
			if(div == 0) {
				tmp[i] = (char) (Math.random() * 10 + '0') ;
			}else {
				tmp[i] = (char) (Math.random() * 26 + 'A') ;
			}
		}
    	return new String(tmp);
    }

    // random IV 생성
    public String generateIv() {
    	char[] tmp = new char[16];
		for(int i=0; i<tmp.length; i++) {
			int div = (int) Math.floor( Math.random() * 2 );
			
			if(div == 0) {
				tmp[i] = (char) (Math.random() * 10 + '0') ;
			}else {
				tmp[i] = (char) (Math.random() * 26 + 'A') ;
			}
		}
    	return new String(tmp);
    }

    // AES256 encrypt + base64 encode //
    public static String AES256Encode(String plainData, String secretKey, String secretIv)
            throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] keyData = secretKey.getBytes();
        IV = secretIv;

        SecretKey secureKey = new SecretKeySpec(keyData, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));

        byte[] encryptData = cipher.doFinal(plainData.getBytes("UTF-8"));
        System.out.println("AES256 encryptData : "+ Arrays.toString(encryptData));

        String encodeData = new String(Base64.encodeBase64(encryptData));
        System.out.println("base64 encodeData : "+encodeData);

        return encodeData;
    }


    /**
     * base64 decode 및  AES decrypt 함수 <br>
     * @param encodeData base64 인코딩 및 AES256 암호화된 데이터
     * @param secretKey AES256 복호화를 위해 필요한 private key (32byte)
     * @param secretIv AES256 복호화를 위해 필요한 iv (16byte)
     *
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String AES256Decode(String encodeData, String secretKey, String secretIv)
            throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] keyData = secretKey.getBytes();      // 32byte
        IV = secretIv;                              // 16byte
        
        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));

        byte[] decodeData = Base64.decodeBase64(encodeData.getBytes());
        System.out.println("AES256 decodeData : "+ Arrays.toString(decodeData));

        String decryptData = new String(cipher.doFinal(decodeData), "UTF-8");
        System.out.println("base64 decode : "+ decryptData);

        return decryptData;
    }

    // HmacSha256 + base64 encode //
    public static String hmacSha256(String plainData, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        hmacSha256.init(secretKeySpec);

        byte[] encryptData = hmacSha256.doFinal(plainData.getBytes());
        System.out.println("hmacSha256 encryptData : "+ Arrays.toString(encryptData));

        String encodeData = new String(Base64.encodeBase64(encryptData));
        System.out.println("base64 encodeData : "+ encodeData);

        return encodeData;
    }
}
