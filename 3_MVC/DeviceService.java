package kr.com.dreamsecurity.hotp.service;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import kr.com.dreamsecurity.hotp.crypto.SDPcryptoApi;
import kr.com.dreamsecurity.hotp.util.RandomUtils;
import kr.com.dreamsecurity.hotp.vo.DeviceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.com.dreamsecurity.hotp.mapper.DeviceMapper;
import kr.com.dreamsecurity.hotp.vo.SearchDeviceDTO;

@Service
public class DeviceService {

	@Autowired
	DeviceMapper deviceMapper;
	
	public Hashtable<String, Object> getDeviceList(SearchDeviceDTO vo) throws Exception {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		
		try {
			vo.setStart(vo.getPageNum() * vo.getPagePer());
			
			List<DeviceVO> deviceList = deviceMapper.selectDeviceList(vo);
			
			for(DeviceVO deviceVO : deviceList) {
				
				// secretKey 복호화
				String plainSecretKey = decryptData(deviceVO.getSecret_key());
				
				deviceVO.setSecret_key(plainSecretKey);
				
			}
			
			result.put("result", "success");
			result.put("list", deviceList);
			result.put("totalSize", deviceMapper.selectDeviceListCount(vo));
		} catch (SQLException e) {
			throw new Exception("데이터베이스 오류 발생");
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	public Hashtable<String, Object> addDevice(DeviceVO vo) throws Exception {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		
		try {
			if (deviceMapper.selectDevice(vo) != null) {
				throw new Exception("디바이스 아이디가 이미 존재합니다.");
			}
			if (vo.getSecret_key() == null || vo.getSecret_key().equals("")) {
				vo.setSecret_key(RandomUtils.getRandomSecretKey());
			} else {
				// secretKey 암호화
				String encryptionSecretKey = encryptData(vo.getSecret_key());
				
				vo.setSecret_key(encryptionSecretKey);

				deviceMapper.insertDevice(vo);
				
				// secretKey 복호화
				String plainSecretKey = decryptData(vo.getSecret_key());
				
				vo.setSecret_key(plainSecretKey);
			}
			
			
			result.put("result", "success");
			result.put("data", vo);
		} catch (SQLException e) {
			throw new Exception("데이터베이스 오류 발생");
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	public Hashtable<String, Object> removeDevice(DeviceVO vo) throws Exception {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		
		try {
			deviceMapper.deleteDevice(vo);
			result.put("result", "success");
		} catch (SQLException e) {
			throw new Exception("데이터베이스 오류 발생");
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	public Hashtable<String, Object> resetDevice(DeviceVO vo) throws Exception {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		
		try {
			deviceMapper.resetDevice(vo);
			result.put("result", "success");
		} catch (SQLException e) {
			throw new Exception("데이터베이스 오류 발생");
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
    // 암호화 (JCAOS)
    public String encryptData(String input) throws Exception{
        return SDPcryptoApi.getInstance().encryptSym(input.getBytes());
    }
    
    // 복호화 (JCAOS)
    public String decryptData(String input) throws Exception{
        
        byte[] decBytes = SDPcryptoApi.getInstance().decryptSym(input);
        
        return new String(decBytes);
    }
	
}
