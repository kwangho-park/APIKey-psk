package SystemController;

import kr.com.dreamsecurity.hotp.crypto.AES256Cipher;
import kr.com.dreamsecurity.hotp.util.json.JSONObject;
import kr.com.dreamsecurity.hotp.util.json.parser.JSONParser;
import kr.com.dreamsecurity.hotp.util.json.parser.ParseException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SystemControllerTest {


    /**
     *
     * RESTful API 요청 테스트 (pre-shared key 적용된 요청 )
     * request body (json string) 를 AES로 양방향 암호화 (인증을 위한 value)
     * request body 를 hmac으로 단방향 암호화 (데이터 무결성 검증을 위한 hash value)
     *
     */
    @Test
    public void getDeviceList() throws IOException, ParseException {

        // given : private key , iv
        String secretKey = "3MYH2UYDM73441C3PPN179F089R5Y27G";   // 32byte
        String secretIv = "87W1M4S8009730GK";                    // 16byte

        String encryptJson = "";
        String authSign = "";                 // 변조 방지를 위한 sign 값 (replay attack 방지)
        String systemId = "stealth";

        int responseCode = 0;

        try {

            // when
            String plainJson = "{"
                    + "\"searchType\":\"device_name\","
                    + "\"searchValue\":\"VM-VPNserver\","
                    + "\"systemId\":\"stealth\","
                    + "\"systemPw\":\"stealth123\""
                    + "}";

            // json string 암호화 (AES256) //
            System.out.println("[START] device list request API (in test code)");
            System.out.println("[HTTP body] jsonString (plain data) : "+plainJson);

            encryptJson = AES256Cipher.AES256Encode(plainJson, secretKey, secretIv);

            System.out.println("[HTTP body] jsonString (encrypt data)  :" +encryptJson);

            // replay attack 방지 (hamcSHA256) //
            authSign = AES256Cipher.hmacSha256(plainJson, secretKey);

            System.out.println("[HTTP header] auth sign : "+authSign);

            // http request (self call)
            URL url = new URL("http://127.0.0.1:8080/hotp/api/sys/device/list");

            // httpURLConnection : http client 역할을 수행하는 java lib
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();     // https 를 요청하는경우 인증서 검증 비활성화 설정 필요

            // http header 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Auth-Sign", authSign);
            connection.setRequestProperty("System-ID", systemId);
            connection.setDoOutput(true);



            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(encryptJson);         // AES256 암호화된 json string
            outputStream.flush();
            outputStream.close();


            responseCode = connection.getResponseCode();

            System.out.println("responseCode : " + responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){



                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String inputLine = "";


                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuffer.append(inputLine);
                }
                bufferedReader.close();


                // then : 디바이스 목록 반환
                String response = stringBuffer.toString();

                System.out.println(response);

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response);

                JSONObject jsonObj = (JSONObject) obj;

                if (jsonObj.get("result") == null) {
                    System.out.println("error message : " + (String) jsonObj.get("msg"));
                } else {

                    String result = (String) jsonObj.get("result");
                    List<Map<String, String>> list = (List<Map<String, String>>) jsonObj.get("list");

                    long totalSize = (Long) jsonObj.get("totalSize");       // long type
                    int totalSizeInt = Long.valueOf(totalSize).intValue();

                    System.out.println("result : " + result);
                    System.out.println("list (type : list<Man<String,String>) : " + list);
                    System.out.println("totalSize (type : int) : " + totalSizeInt);
                }

                /////////////////// http code : 200 아닌경우 ////////////////////
            }else{
                System.out.println("[error] responseCode : "+responseCode);
            }



            System.out.println("[END] device list request API (in test code)");

        }catch(IOException e){
            e.printStackTrace();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
