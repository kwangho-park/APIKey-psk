package kr.com.dreamsecurity.hotp.systemController;

import kr.com.dreamsecurity.hotp.controller.BaseController;
import kr.com.dreamsecurity.hotp.service.DeviceService;
import kr.com.dreamsecurity.hotp.vo.DeviceVO;
import kr.com.dreamsecurity.hotp.vo.SearchDeviceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Hashtable;

/**
 * HOTP web console 이외에 third-party app 에서 HOTP 서버에 접근 및 데이터를 활용하는 경우에 제공하는 web API <br>
 * HOTP server 에서 발급한 pre-shared key 로 구간암호화하여 전송 <br>
 *
 * @author Park_Kwang_Ho
 */
@Controller
@RequestMapping(value = { "/api/sys/device" })
public class DeviceSysController extends BaseController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
    	DeviceService deviceService;

	/**
	 * device list 전체 조회 또는 특정 디바이스 <br>
	 *
	 * @param SearchDeviceDTO 디바이스 조회를 위한 데이터
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/list")
	@ResponseBody
	public Hashtable<String, Object> getDeviceList(@Valid @RequestBody SearchDeviceDTO searchDeviceDTO ) {

		Hashtable<String, Object> result = null;


		try {
			result = deviceService.getDeviceList(searchDeviceDTO);
			logger.debug("device 조회 또는 검색 결과 : "+result.get("result"));
			logger.debug("device list :" +result.get("list").toString());

		} catch (Exception e) {

			logger.debug("device 조회 또는 검색 결과 : "+result.get("result"));
			result = new Hashtable<String, Object>();
			logger.error(e.getMessage(), e);
			putErrorResult(e, result);
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/add")
	@ResponseBody
	public Hashtable<String, Object> addDevice(@RequestBody DeviceVO vo) {
		Hashtable<String, Object> result = null;
		
		try {
			result = deviceService.addDevice(vo);
		} catch (Exception e) {
			result = new Hashtable<String, Object>();
			logger.error(e.getMessage(), e);
			putErrorResult(e, result);
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/delete")
	@ResponseBody
	public Hashtable<String, Object> deleteDevice(@RequestBody DeviceVO vo) {
		Hashtable<String, Object> result = null;
		
		try {
			result = deviceService.removeDevice(vo);
		} catch (Exception e) {
			result = new Hashtable<String, Object>();
			logger.error(e.getMessage(), e);
			putErrorResult(e, result);
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/reset")
	@ResponseBody
	public Hashtable<String, Object> resetDevice(@RequestBody DeviceVO vo) {
		Hashtable<String, Object> result = null;
		
		try {
			result = deviceService.resetDevice(vo);
		} catch (Exception e) {
			result = new Hashtable<String, Object>();
			logger.error(e.getMessage(), e);
			putErrorResult(e, result);
		}
		
		return result;
	}

}
