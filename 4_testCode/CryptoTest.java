package crypto;

import kr.com.dreamsecurity.hotp.crypto.AES256Cipher;
import org.junit.Test;

/**
 * java crypto lib 테스트 코드
 * HOTP 연동을 위한 RESTful API 암복호화를 위한 테스트 코드 (in host side)
 *
 * @author Park_Kwang_Ho
 */
public class CryptoTest {

    // generate key and iv
    @Test
    public void generateValue(){

        // given

        // when
        AES256Cipher aes256Cipher = AES256Cipher.getInstance();

        System.out.println(aes256Cipher.generateKey());
        System.out.println(aes256Cipher.generateIv());

        // then

    }


    // java cryto encryte/decryte test
    @Test
    public void AES256CipherTest(){

        // given
        String secretKey="3MYH2UYDM73441C3PPN179F089R5Y27G";
        String secretIv="87W1M4S8009730GK";

        String plainData = "pkh";
        String encryteData ="";
        String decryteData ="";

        try{


//            secretKey = AES256Cipher.getInstance().generateKey();
//            secretIv = AES256Cipher.getInstance().generateIv();

            // when
            encryteData = AES256Cipher.AES256Encode(plainData,secretKey, secretIv);
            decryteData = AES256Cipher.AES256Decode(encryteData, secretKey, secretIv);


            // then
            System.out.println("plainText : " + plainData);
            System.out.println("secretKey : " + secretKey);
            System.out.println("secretIv  : " + secretIv);
            System.out.println("encryteData : " + encryteData);
            System.out.println("decryteDate : "+ decryteData);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void hmacSha256Test(){

        // given
        String secretKey="";
        String plainData="pkh";
        String encrytData="";
        try{

            // when
            secretKey = AES256Cipher.getInstance().generateKey();
            encrytData = AES256Cipher.hmacSha256(plainData, secretKey);

            // then
            System.out.println("encrytData (HmacSha256+base64) : "+encrytData);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * POST man 으로 RESTful API key 테스트를 위한 Auth-Sign, http body 암호화 데이터 생성<br>
     * (by JAVA security lib, AES256, HMAC) <br>
     *
     * API 종류 : 디바이스 조회/등록/삭제, HOTP 리셋, HOTP 서버 설정 <br>
     *
     */
    @Test
    public void APIkeyTest(){

        // given : private key , iv //
        String secretKey = "3MYH2UYDM73441C3PPN179F089R5Y27G";   // 32byte
        String secretIv = "87W1M4S8009730GK";                    // 16byte

        // http plain body
        String plainList = "{"
                + "\"searchType\":\"device_name\","
                + "\"searchValue\":\"VM-VPNserver\","
                + "\"systemId\":\"stealth\","
                + "\"systemPw\":\"stealth123\""
                + "}";

        String plainAdd = "{"
                + "\"device_name\":\"pkh_device2\","
                + "\"device_id\":\"23-34-56-78-9A-BC\","
                + "\"secret_key\":\"magicsdp\","
                + "\"event_counter\":\"0\","
                + "\"systemId\":\"stealth\","
                + "\"systemPw\":\"stealth123\""
                + "}";


        String plainDelete = "{"
                + "\"device_id\":\"12-34-56-78-9A-BC\","
                + "\"systemId\":\"stealth\","
                + "\"systemPw\":\"stealth123\""
                + "}";

        String plainHOTPreset = "{"
                + "\"device_id\":\"12-34-56-78-9A-BC\","
                + "\"systemId\":\"stealth\","
                + "\"systemPw\":\"stealth123\""
                + "}";


        // hotp 서버 설정 조회
        String plainConfig1 = "{"
                + "\"systemId\":\"stealth\","
                + "\"systemPw\":\"stealth123\""
                + "}";


        // hotp 서버 설정 업데이트
        String plainConfig2 = "{"
                + "\"otp_scope\":\"40\","
                + "\"otp_length\":\"20\","
                + "\"systemId\":\"stealth\","
                + "\"systemPw\":\"stealth123\""
                + "}";


        try{
            // when
            // http header 의 Auth-Sign 생성 및 http body 암호화


            // then
            System.out.println("암호화 데이터");
            System.out.println("plainList authSign (Hmac) : "+AES256Cipher.hmacSha256(plainList, secretKey));
            System.out.println("plainList body (AES256) : "+AES256Cipher.AES256Encode(plainList, secretKey, secretIv));

            System.out.println("plainAdd authSign (Hmac) : "+AES256Cipher.hmacSha256(plainAdd, secretKey));
            System.out.println("plainAdd body (AES256) : "+AES256Cipher.AES256Encode(plainAdd, secretKey, secretIv));

            System.out.println("plainDelete authSign (Hmac) : "+AES256Cipher.hmacSha256(plainDelete, secretKey));
            System.out.println("plainDelete body (AES256) : "+AES256Cipher.AES256Encode(plainDelete, secretKey, secretIv));

            System.out.println("plainHOTPreset authSign (Hmac) : "+AES256Cipher.hmacSha256(plainHOTPreset, secretKey));
            System.out.println("plainHOTPreset body (AES256) : "+AES256Cipher.AES256Encode(plainHOTPreset, secretKey, secretIv));

            System.out.println("plainConfig1 authSign (Hmac) : "+AES256Cipher.hmacSha256(plainConfig1, secretKey));
            System.out.println("plainConfig1 body (AES256) : "+AES256Cipher.AES256Encode(plainConfig1, secretKey, secretIv));

            System.out.println("plainConfig2 authSign (Hmac) : "+AES256Cipher.hmacSha256(plainConfig2, secretKey));
            System.out.println("plainConfig2 body (AES256) : "+AES256Cipher.AES256Encode(plainConfig2, secretKey, secretIv));

        }catch(Exception e){
            e.printStackTrace();
        }

    }


}
