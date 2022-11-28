package com.my.vo;

import java.sql.Date;

/**
 * 과제 관련 DTO
 * @author Chanmin Sung
 *
 */
public class HomeworkDTO {
	private Date stduySubmitDt;		//과제 제출일
	private int	stduyID;			//스터디 ID
	private String userEmail;		//회원 ID
	
	public Date getStduySubmitDt() {
		return stduySubmitDt;
	}
	public void setStduySubmitDt(Date stduySubmitDt) {
		this.stduySubmitDt = stduySubmitDt;
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
