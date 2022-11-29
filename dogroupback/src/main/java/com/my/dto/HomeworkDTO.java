package com.my.dto;

import java.sql.Date;
import java.util.List;

/**
 * 과제 관련 DTO
 * @author Chanmin Sung
 *
 */
public class HomeworkDTO {
	private List<Date> stduySubmitDtList;	//과제 제출일 리스트
	private int	stduyID;					//스터디 ID
	private String userEmail;				//회원 ID
	
	public HomeworkDTO() {
		super();
	}

	public HomeworkDTO(List<Date> stduySubmitDtList, int stduyID, String userEmail) {
		super();
		this.stduySubmitDtList = stduySubmitDtList;
		this.stduyID = stduyID;
		this.userEmail = userEmail;
	}

	public List<Date> getStduySubmitDtList() {
		return stduySubmitDtList;
	}

	public void setStduySubmitDtList(List<Date> stduySubmitDtList) {
		this.stduySubmitDtList = stduySubmitDtList;
	}

	public int getStduyID() {
		return stduyID;
	}

	public void setStduyID(int stduyID) {
		this.stduyID = stduyID;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
