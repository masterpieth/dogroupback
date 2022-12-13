package com.my.dto;

import java.util.List;

/**
 * 스터디 User DTO
 * @author Chanmin Sung
 *
 */
public class StudyUserDTO extends UserDTO {
	private int studyId;					//스터디 ID
	private List<HomeworkDTO> HomeworkList;	//제출한 과제 전체 리스트
	private	int[] checkHomework;			//주차별 유효 과제 체크 리스트	
	
	public StudyUserDTO(int studyId, List<HomeworkDTO> homeworkList, int[] checkHomework) {
		super();
		this.studyId = studyId;
		HomeworkList = homeworkList;
		this.checkHomework = checkHomework;
	}
	public StudyUserDTO() {
		super();
	}
	public StudyUserDTO(String email, String name, String password, int diligence, int userBalance, int status) {
		super(email, name, password, diligence, userBalance, status);
	}
	public int getStudyId() {
		return studyId;
	}
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
	public List<HomeworkDTO> getHomeworkList() {
		return HomeworkList;
	}
	public void setHomeworkList(List<HomeworkDTO> homeworkList) {
		HomeworkList = homeworkList;
	}
	public int[] getCheckHomework() {
		return checkHomework;
	}
	public void setCheckHomework(int[] checkHomework) {
		this.checkHomework = checkHomework;
	}
	

}
