package com.my.repository;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.RemoveException;

public interface StudyRepository {

	/**
	 * 과제를 테이블에 추가한다. 오늘날짜의 과제가 없으면 AddException을 터뜨린다.
	 * 
	 * @param email      회원Email
	 * @param studyId    스터디Id
	 * @param created_at 이벤트생성일자
	 * @throws AddException 데이터를 넣을 수 없을 때 발생하는 예외
	 */
	void insertHomeworkByEmail(String email, int studyId, Date created_at) throws AddException;

	/**
	 * 진행중인 스터디를 검색한다.
	 * 
	 * @param email 회원 Email
	 * @return 스터디 검색 결과 리스트
	 * @throws FindException
	 */
	List<StudyDTO> selectStudyByEmail(String email) throws FindException;

	/**
	 * 선택한 스터디원의 email로 스터디원 상세 조회
	 * 
	 * @param email
	 * @return 스터디원의 과제 리스트
	 * @throws FindException 상세 조회중 오류시 발생
	 */
	HomeworkDTO selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException;

	/**
	 * 스터디 상세페이지를 조회한다(회원용)
	 * 
	 * @param studyId
	 * @return Study의 상세내용을 반환
	 * @throws FindException 상세 조회중 오류시 발생
	 */
	StudyDTOBomi selectStudy(int studyId) throws FindException;
	/**
	 * 스터디를 Insert 한다
	 * @param study
	 */
	void insertStudy(StudyDTO study);

	/**
	 * 스터디원을 Insert 한다 - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다.
	 * flag 1: 스터디원 추가
	 * flag 0: 스터디원 삭제
	 */
	void insertStudyUser(StudyDTO study, String email) throws AddException;

	/**
	 * 스터디장을 insert 한다 - insertStudy에서 connection을 받아서 한 트랜잭션에 있도록 한다.
	 * flag 1: 스터디원 추가
	 * flag 0: 스터디원 삭제
	 */
	void insertStudyUserLeader(StudyDTO study, String email, Connection conn) throws AddException;
	
	/**
	 * 스터디에서 탈퇴한다 - StudyUser 테이블에서 정보를 제거한다.
	 * @param study				스터디정보
	 * @param email				사용자이메일
	 * @throws RemoveException
	 */
	void deleteStudyUser(StudyDTO study, String email) throws RemoveException;
}
