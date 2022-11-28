package com.my.repository;

import java.util.List;

import com.my.dto.StudyDTO;
import com.my.exception.FindException;

public interface StudyRepository {
	
	/**
	 * @param email 스터디 id
	 * @return 진행중인 스터디를 검색한다.
	 * @throws FindException
	 */
	List<StudyDTO> selectStudyByEmail(String email) throws FindException;
	
}



