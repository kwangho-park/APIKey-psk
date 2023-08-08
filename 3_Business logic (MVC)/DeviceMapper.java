package kr.com.dreamsecurity.hotp.mapper;

import kr.com.dreamsecurity.hotp.vo.DeviceVO;
import kr.com.dreamsecurity.hotp.vo.SearchDeviceDTO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository(value = "deviceMapper")
public interface DeviceMapper {

	/**
	 * 디바이스 목록 조회
	 * @param vo
	 * @return List<DeviceVO>
	 * @throws SQLException
	 */
	public List<DeviceVO> selectDeviceList(SearchDeviceDTO vo) throws SQLException;

	/**
	 * 디바이스 목록 카운트 조회
	 * @param vo
	 * @return int
	 * @throws SQLException
	 */
	public int selectDeviceListCount(SearchDeviceDTO vo) throws SQLException;
	
	/**
	 * 디바이스 조회
	 * @param vo
	 * @return DeviceVO
	 * @throws SQLException
	 */
	public DeviceVO selectDevice(DeviceVO vo) throws SQLException;
	
	/**
	 * 디바이스 추가
	 * @param vo
	 * @throws SQLException
	 */
	public void insertDevice(DeviceVO vo) throws SQLException;
	
	/**
	 *
	 * 디바이스 삭제
	 * @param vo
	 * @throws SQLException
	 */
	public void deleteDevice(DeviceVO vo) throws SQLException;

	/**
	 * 디바이스 업데이트
	 * @param vo
	 * @throws SQLException
	 */
	public void updateDevice(DeviceVO vo) throws SQLException;
	
	/**
	 * 디바이스 초기화
	 * @param vo
	 * @throws SQLException
	 */
	public void resetDevice(DeviceVO vo) throws SQLException;
}
