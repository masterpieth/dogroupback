package com.my.dto;

/**
 * 스터디 User DTO
 * @author Chanmin Sung
 *
 */
public class StudyUserDTO extends UserDTO {
	private int studyId;					//스터디 ID
	private HomeworkDTO homework;			//과제
	
	public StudyUserDTO() {						
		super();
	}
	public StudyUserDTO(int studyId, HomeworkDTO homework) { 
		super();
		this.studyId = studyId;
		this.homework = homework;
	}
	
	public StudyUserDTO(String email, String name, String password, int diligence, int userBalance, int status) {
		super(email, name, password, diligence, userBalance, status);
		// TODO Auto-generated constructor stub
	}

	public int getStudyId() {
		return studyId;
	}
	public void setStudyId(int stduyId) {
		this.studyId = stduyId;
	}
	public HomeworkDTO getHomework() {
		return homework;
	}
	public void setHomework (HomeworkDTO homework) {
		this.homework = homework;
	}
}
