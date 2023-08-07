package kr.com.dreamsecurity.hotp.filter;

import kr.com.dreamsecurity.hotp.constants.RESULT;
import kr.com.dreamsecurity.hotp.crypto.AES256Cipher;
import kr.com.dreamsecurity.hotp.service.SystemService;
import kr.com.dreamsecurity.hotp.vo.SystemInfoDTO;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.DecoderException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;


/**
 * http body 데이터 인증을 위한 wrapper 클래스 <br>
 * description : body 복호화, http header hash 검증, system ID/PW 인증
 */
public class RequestBodyWrapper extends HttpServletRequestWrapper {

    private final String requestDecryptBody;
    private RESULT result;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public RequestBodyWrapper(HttpServletRequest request) throws IOException, DecoderException {

        super(request);

        String decryptBody = "";        // AES256 복호화된 http body

        try {

            // system 정보 조회 //
            String systemId = request.getHeader("System-ID");

            SystemService systemService = new SystemService();
            Hashtable<String, Object> systemInfoMap = systemService.getSystem(systemId);

            SystemInfoDTO systemInfoDTO = (SystemInfoDTO) systemInfoMap.get("systemInfo");

            String secretKey = systemInfoDTO.getSecretKey();
            String secretIv = systemInfoDTO.getSecretIv();

            if(systemInfoMap.get("result") == RESULT.SUCCESS){

                // http boby 복호화 (pre-shard key : AES256) //
                String encryptBody = getRequestBody(request);
                logger.debug("<encrypt> HTTP body : " + encryptBody);

                decryptBody = AES256Cipher.AES256Decode(encryptBody, secretKey, secretIv);      // 복호화 불가능 예외처리 예정
                logger.debug("<decrypt> - HTTP body  :" + decryptBody);


                // auth sign 검증 (HMAC) //
                String authSign = request.getHeader("Auth-Sign");
                logger.debug("<HTTP header> " + authSign);

                String authSignCheck = AES256Cipher.hmacSha256(decryptBody, secretKey);

                if (authSign.equals(authSignCheck)) {
                    this.result = RESULT.SUCCESS;
                } else {
                    this.result = RESULT.FAIL;
                    logger.info("<fail> Auth-sign 검증 실패 (secret key 또는 secret iv 불일치)");
                    return;
                }

                // system ID/pw 인증 //
                JSONObject jsonObject = new JSONObject(decryptBody);

                HashMap<String, String> jsonMap = new HashMap<>();

                for (String key : jsonObject.keySet()) {
                    jsonMap.put(key, jsonObject.getString(key));
                }
                String bodySystemId = jsonMap.get("systemId");
                String bodySystemPw = jsonMap.get("systemPw");

                if(bodySystemId.equals(systemInfoDTO.getSystemId()) && bodySystemPw.equals(systemInfoDTO.getSystemPW()) ){
                    logger.info("<success> system id/pw 인증 성공");
                    this.result = RESULT.SUCCESS;
                }else{
                    logger.info("<fail> system id/pw 불일치");
                    this.result = RESULT.FAIL;
                    return;
                }

            }else{
                logger.info("<fail> system id 조회 실패");
                this.result = RESULT.FAIL;
                return;
            }

        }catch(Exception e){
            this.result = RESULT.FAIL;
            e.printStackTrace();

        }finally {
            requestDecryptBody =  decryptBody;
        }

    }


    @Override
    public ServletInputStream getInputStream() {

        final ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(requestDecryptBody.getBytes(StandardCharsets.UTF_8));

        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }


    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        byte[] rawData = new byte[128];
        InputStream inputStream = request.getInputStream();
        rawData = IOUtils.toByteArray(inputStream);
        return new String(rawData);
    }

    public RESULT getResult(){
        return this.result;
    }

}