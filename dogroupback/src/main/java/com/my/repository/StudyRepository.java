package com.my.repository;

import java.util.List;

import com.my.dto.StudyDTO;
import com.my.exception.FindException;

public interface StudyRepository {
	
	/**
	 * @param email 스터디 ID
	 * @return 진행중인 스터디 목록
	 * @throws FindException 진행중인 스터디를 찾지못하면 FindException발생한다.
	 */
	List<StudyDTO> selectStudyByEmail(String email) throws FindException;
	
}



