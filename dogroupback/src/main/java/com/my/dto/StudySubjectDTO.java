package com.my.dto;

/**
 * 스터디 과목 DTO
 * @author bomi
 *
 */
public class StudySubjectDTO {
	private int studyId;
	private SubjectDTO subject;

	public StudySubjectDTO() {
		super();
	}

	public StudySubjectDTO(int studyId, SubjectDTO subject) {
		super();
		this.studyId = studyId;
		this.subject = subject;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public SubjectDTO getSubject() {
		return subject;
	}

	public void setSubject(SubjectDTO subject) {
		this.subject = subject;
	}

}
