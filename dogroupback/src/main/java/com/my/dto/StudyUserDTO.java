package com.my.dto;

import java.util.List;

/**
 * 스터디 User DTO
 * @author Chanmin Sung
 *
 */
public class StudyUserDTO extends UserDTO {
	private int studyId;					//스터디 ID
	private List<HomeworkDTO> HomeworkList;	//과제 리스트(제출한 과제)
	
	
	public StudyUserDTO(int studyId, List<HomeworkDTO> homeworkList) {
		super();
		this.studyId = studyId;
		HomeworkList = homeworkList;
	}
	public int getStudyId() {
		return studyId;
	}
	public void setStudyId(int stduyId) {
		this.studyId = stduyId;
	}
	public List<HomeworkDTO> getHomeworkList() {
		return HomeworkList;
	}
	public void setHomeworkList(List<HomeworkDTO> homeworkList) {
		HomeworkList = homeworkList;
	}
}
