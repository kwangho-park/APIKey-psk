package kr.com.dreamsecurity.hotp.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchDeviceDTO extends SearchPagingDTO {

	@NotNull(message="searchType는 null 일 수 없습니다")
	private String searchType;

	@NotNull(message="searchValue는 null 일 수 없습니다")
	private String searchValue;

	@NotNull(message="systemId는 null 일 수 없습니다")
	@Pattern(regexp="[a-zA-Z0-9]{0,20}" , message = "systemId 는 20자 이하의 영문 또는 숫자입니다.")
	private String systemId;

	@NotNull(message="systemPw는 null 일 수 없습니다")
	@Pattern(regexp="[a-zA-Z0-9]{0,100}", message = "systemPw 는 100자 이하의 영문 또는 숫자입니다.")
	private String systemPw;

	// private int state;					// 미사용


}
