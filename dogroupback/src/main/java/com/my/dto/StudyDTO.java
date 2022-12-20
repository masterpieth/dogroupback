package com.my.dto;

import java.sql.Clob;
import java.util.Date;
import java.util.List;

/**
 * 스터디 관련 DTO
 * @author Chanmin, Bomi
 *
 */
public class StudyDTO {
	private int studyId;					//스터디 ID
	private String userEmail;				//스터디장
	private String studyTitle;				//스터디명
	private int studySize;					//스터디 모집목표 인원
	private int studyFee;					//스터디 입장료
	private int studyCertification;			//스터디 인증방식
	private double studyDiligenceCutline;	//스터디 성실도 기준
	private Date studyPostDate;				//스터디 게시일자
	private Date studyStartDate;			//스터디 시작일자
	private Date studyEndDate;				//스터디 종료일자
	private int studyHomeworkPerWeek;		//스터디 주당 과제 횟수(1~7)
	private int studyPaid;					//스터디 정산여부(최종 정산)
	private int studyGatheredSize;			//스터디 현재모집 인원
	private Clob studyContent;				//스터디 내용(게시글)
	
	private List<StudySubjectDTO> subjects;	//스터디 과목 리스트(최대 3과목)
	private UserDTO studyLeader; 			//스터티장 정보
	private List<StudyUserDTO> studyUsers; 	//스터디원 정보
	
	public StudyDTO() {
		super();
	}


	public StudyDTO(int studyId, String userEmail, String studyTitle, int studySize, int studyFee,
			int studyCertification, double studyDiligenceCutline, Date studyPostDate, Date studyStartDate,
			Date studyEndDate, int studyHomeworkPerWeek, int studyPaid, int studyGatheredSize, Clob studyContent) {
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
		this.studyGatheredSize = studyGatheredSize;
		this.studyContent = studyContent;
	}

	public StudyDTO(List<StudySubjectDTO> subjects, UserDTO studyLeader, List<StudyUserDTO> studyUsers) {
		super();
		this.subjects = subjects;
		this.studyLeader = studyLeader;
		this.studyUsers = studyUsers;
	}
	
	public StudyDTO(int studyId, String userEmail, String studyTitle, int studySize, int studyFee,
			int studyCertification, double studyDiligenceCutline, Date studyPostDate, Date studyStartDate,
			Date studyEndDate, int studyHomeworkPerWeek, int studyPaid, int studyGatheredSize, Clob studyContent,
			List<StudySubjectDTO> subjects, UserDTO studyLeader, List<StudyUserDTO> studyUsers) {
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
		this.studyGatheredSize = studyGatheredSize;
		this.studyContent = studyContent;
		this.subjects = subjects;
		this.studyLeader = studyLeader;
		this.studyUsers = studyUsers;
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

	public double getStudyDiligenceCutline() {
		return studyDiligenceCutline;
	}

	public void setStudyDiligenceCutline(double studyDiligenceCutline) {
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

	public int getStudyGatheredSize() {
		return studyGatheredSize;
	}

	public void setStudyGatheredSize(int studyGatheredSize) {
		this.studyGatheredSize = studyGatheredSize;
	}

	public Clob getStudyContent() {
		return studyContent;
	}

	public void setStudyContent(Clob studyContent) {
		this.studyContent = studyContent;
	}

	
	public List<StudySubjectDTO> getSubjects() {
		return subjects;
	}

	
	public void setSubjects(List<StudySubjectDTO> subjects) {
		this.subjects = subjects;
	}

	
	public UserDTO getStudyLeader() {
		return studyLeader;
	}

	
	public void setStudyLeader(UserDTO studyLeader) {
		this.studyLeader = studyLeader;
	}

	
	public List<StudyUserDTO> getStudyUsers() {
		return studyUsers;
	}

	public void setStudyUsers(List<StudyUserDTO> studyUsers) {
		this.studyUsers = studyUsers;
	}
}
