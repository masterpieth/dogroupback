package com.my.dto;

/**
 * 과목 분류 DTO
 * @author BOMI
 *
 */
public class SubjectDTO {
	private String subjectCode;
	private String subjectName;
	private SubjectDTO subjectParent;
	
	public SubjectDTO() {
		super();
	}
	
	public SubjectDTO(String subjectCode, String subjectName, SubjectDTO subjectParent) {
		super();
		this.subjectCode = subjectCode;
		this.subjectName = subjectName;
		this.subjectParent = subjectParent;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public SubjectDTO getSubjectParent() {
		return subjectParent;
	}
	public void setSubjectParent(SubjectDTO subjectParent) {
		this.subjectParent = subjectParent;
	}
}
