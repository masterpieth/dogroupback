package com.my.dto;

/**
 * Study 상세 DTO
 * @author NYK
 *
 */
public class StudySubjectDTOBomi {
	private int studyId;
	private SubjectDTOBomi subject;
	public StudySubjectDTOBomi() {
		super();
	}
	public StudySubjectDTOBomi(int studyId, SubjectDTOBomi subject) {
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
	public SubjectDTOBomi getSubject() {
		return subject;
	}
	public void setSubject(SubjectDTOBomi subject) {
		this.subject = subject;
	}
	
}
