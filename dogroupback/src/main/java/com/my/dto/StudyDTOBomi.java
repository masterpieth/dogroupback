package com.my.dto;

import java.util.List;

/**
 * 스터디 관련 DTO
 * @author Chanmin Sung
 *
 */
public class StudyDTOBomi extends StudyDTO{
	private List<StudySubjectDTOBomi> subjects;
	private UserDTO studyLeader; //스터티장
	private List<StudyUserDTO> studyUsers; //스터디원
	
	public StudyDTOBomi() {
		super();
	}

	public StudyDTOBomi( List<StudySubjectDTOBomi> subjects,
			UserDTO studyLeader, List<StudyUserDTO> studyUsers) {
		super();
		
		this.subjects = subjects;
		this.studyLeader = studyLeader;
		this.studyUsers = studyUsers;
	}

	
	public List<StudySubjectDTOBomi> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<StudySubjectDTOBomi> subjects) {
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
