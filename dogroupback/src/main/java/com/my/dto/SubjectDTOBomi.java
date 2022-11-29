package com.my.dto;

public class SubjectDTOBomi {
	private String subjectCode;
	private String subjectName;
	private SubjectDTOBomi subjectParent;
	
	public SubjectDTOBomi() {
		super();
	}
	
	public SubjectDTOBomi(String subjectCode, String subjectName, SubjectDTOBomi subjectParent) {
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
	public SubjectDTOBomi getSubjectParent() {
		return subjectParent;
	}
	public void setSubjectParent(SubjectDTOBomi subjectParent) {
		this.subjectParent = subjectParent;
	}
	
	
}
