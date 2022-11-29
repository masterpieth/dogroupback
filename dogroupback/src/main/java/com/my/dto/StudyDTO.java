package com.my.dto;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;

/**
 * 스터디 관련 DTO
 * @author Chanmin Sung
 *
 */
public class StudyDTO {
	private int studyId;				//스터디 ID
	private String userEmail;			//스터디장
	private String studyTitle;			//스터디명
	private int studySize;				//스터디 인원
	private int studyFee;				//스터디 입장료
	private int studyCertification;		//스터디 인증방식
	private int studyDiligenceCutline;	//스터디 성실도 기준
	private Date studyPostDate;			//스터디 게시일자
	private Date studyStartDate;		//스터디 시작일자
	private Date studyEndDate;			//스터디 종료일자
	private int studyHomeworkPerWeek;	//스터디 주당 과제 횟수(1~7)
	private int studyPaid;				//스터디 정산여부(최종 정산)
	private String studyContent;		//스터디 내용(게시글)
	
	public StudyDTO() {
		super();
	}

	public StudyDTO(int studyId, String userEmail, String studyTitle, int studySize, int studyFee,
			int studyCertification, int studyDiligenceCutline, Date studyPostDate, Date studyStartDate,
			Date studyEndDate, int studyHomeworkPerWeek, int studyPaid, String studyContnet) {
		super();
		this.studyId = studyId;
		this.userEmail = userEmail;
		this.studyTitle = studyTitle;
		this.studySize = studySize;
		this.studyFee = studyFee;
		this.studyCertification = studyCertification;
		this.studyDiligenceCutline = studyDiligenceCutline;
		this.studyPostDate = studyPostDate;
		this.studyStartDate = studyStartDate;
		this.studyEndDate = studyEndDate;
		this.studyHomeworkPerWeek = studyHomeworkPerWeek;
		this.studyPaid = studyPaid;
		this.studyContent = studyContent;
	}
	
	
	public int getStudyId() {
		return studyId;
	}
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getStudyTitle() {
		return studyTitle;
	}
	public void setStudyTitle(String studyTitle) {
		this.studyTitle = studyTitle;
	}
	public int getStudySize() {
		return studySize;
	}
	public void setStudySize(int studySize) {
		this.studySize = studySize;
	}
	public int getStudyFee() {
		return studyFee;
	}
	public void setStudyFee(int studyFee) {
		this.studyFee = studyFee;
	}
	public int getStudyCertification() {
		return studyCertification;
	}
	public void setStudyCertification(int studyCertification) {
		this.studyCertification = studyCertification;
	}
	public int getStudyDiligenceCutline() {
		return studyDiligenceCutline;
	}
	public void setStudyDiligenceCutline(int studyDiligenceCutline) {
		this.studyDiligenceCutline = studyDiligenceCutline;
	}
	public Date getStudyPostDate() {
		return studyPostDate;
	}
	public void setStudyPostDate(Date studyPostDate) {
		this.studyPostDate = studyPostDate;
	}
	public Date getStudyStartDate() {
		return studyStartDate;
	}
	public void setStudyStartDate(Date studyStartDate) {
		this.studyStartDate = studyStartDate;
	}
	public Date getStudyEndDate() {
		return studyEndDate;
	}
	public void setStudyEndDate(Date studyEndDate) {
		this.studyEndDate = studyEndDate;
	}
	public int getStudyHomeworkPerWeek() {
		return studyHomeworkPerWeek;
	}
	public void setStudyHomeworkPerWeek(int studyHomeworkPerWeek) {
		this.studyHomeworkPerWeek = studyHomeworkPerWeek;
	}
	public int getStudyPaid() {
		return studyPaid;
	}
	public void setStudyPaid(int studyPaid) {
		this.studyPaid = studyPaid;
	}
	public String getStudyContnet() {
		return studyContent;
	}
	public void setStudyContnet(Clob clob) throws IOException, SQLException {
		 this.studyContent = clobToStr(clob);
	}
	
	private String clobToStr(Clob clob) throws IOException, SQLException{
		BufferedReader contentReader = new BufferedReader(clob.getCharacterStream());
		StringBuffer out = new StringBuffer();
		String aux;
		while ((aux=contentReader.readLine())!=null) {
		out.append(aux);
		out.append("<br>");
		}
		return out.toString();
	}
}
