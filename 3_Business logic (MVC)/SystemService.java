package kr.com.dreamsecurity.hotp.service;

import kr.com.dreamsecurity.hotp.constants.RESULT;
import kr.com.dreamsecurity.hotp.dao.SystemDAO;
import kr.com.dreamsecurity.hotp.vo.SystemInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Hashtable;


/**
 * system_info 테이블 관련 서비스
 */
public class SystemService {


	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());


	public Hashtable<String, Object> getSystem(String systemId) {

		Hashtable<String, Object> result = new Hashtable<String, Object>();
		SystemDAO systemDAO = new SystemDAO();

		try {

			SystemInfoDTO systemInfoDTO = systemDAO.selectSystem(systemId);


			result.put("result", RESULT.SUCCESS);
			result.put("systemInfo", systemInfoDTO);

		} catch (SQLException e) {
			result.put("result", RESULT.FAIL);
			e.printStackTrace();

		} catch (Exception e) {
			result.put("result", RESULT.FAIL);
			e.printStackTrace();
		}
		
		return result;
	}


	
}
