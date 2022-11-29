package com.my.repository;

import java.sql.Date;
import java.util.List;

import com.my.dto.StudyDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;

public interface StudyRepository {
	
	/**
	 * @param email 스터디 ID
	 * @return 진행중인 스터디 목록
	 * @throws FindException 진행중인 스터디를 찾지못하면 FindException발생한다.
	 */
	List<StudyDTO> selectStudyByEmail(String email) throws FindException;
	/**
	 * 과제를 테이블에 추가한다. 오늘날짜의 과제가 없으면 AddException을 터뜨린다.
	 * @param email				//회원Email
	 * @param studyId			//스터디Id
	 * @param created_at 		//이벤트생성일자
	 * @throws AddException		//데이터를 넣을 수 없을 때 발생하는 예외
	 */
	void insertHomeworkByEmail(String email, int studyId, Date created_at) throws AddException;
}



