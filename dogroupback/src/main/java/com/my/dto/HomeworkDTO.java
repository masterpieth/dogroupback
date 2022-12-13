package com.my.dto;

import java.util.Date;
import java.util.List;

/**
 * 과제 관련 DTO
 * @author Chanmin Sung
 *
 */
public class HomeworkDTO {
    private int    stduyID;         //스터디 ID
    private String userEmail;       //회원 ID
    private Date studySubmitDt; 	//제출 날짜
	
    public HomeworkDTO() {
		super();
	}
	public HomeworkDTO(int stduyID, String userEmail, Date studySubmitDt) {
		super();
		this.stduyID = stduyID;
		this.userEmail = userEmail;
		this.studySubmitDt = studySubmitDt;
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
	public Date getStudySubmitDt() {
		return studySubmitDt;
	}
	public void setStudySubmitDt(Date studySubmitDt) {
		this.studySubmitDt = studySubmitDt;
	}
    

}
