package com.my.repository;

import java.sql.Date;
import java.util.List;

import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.dto.StudyUserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;

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
	 * 스터디원의 email로 스터디원의 List<HomeworkDTO> 리스트 반환한다.
	 * @param email
	 * @param sstudyId
	 * @return 스터디원의 과제 리스트
	 * @throws FindException 상세 조회중 오류시 발생
	 */
	List<HomeworkDTO> selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException;

	/**
	 * 스터디의 모든 정보를 반환한다.
	 * @param studyId
	 * @return 스터디 기본 정보 + 스터디의 현재 참여자수 + 스터디장의 성실도 + 스터디의 과목 정보 + 참여중인 스터디원들의 정보(유저 기본 정보 + 과제 제출 + 유효 출석일)
	 * @throws FindException 상세 조회중 오류시 발생
	 */
	StudyDTOBomi selectStudy(int studyId) throws FindException;
	
	/**
	 * 스터디에 해당하는 제출한 과제를 반환한다.
	 * @param studyId 스터디아이디
	 * @return 
	 * @throws FindException
	 */
	List<HomeworkDTO> selectHomeworkByStudyId(int studyId) throws FindException;
	
	/**
	 * 스터디ID로 스터디의 기본정보를 반환한다.
	 * @param studyId
	 * @return 스터디 기본정보
	 * @throws FindException
	 */
	StudyDTO selectStudyByStudyId(int studyId) throws FindException;
}

