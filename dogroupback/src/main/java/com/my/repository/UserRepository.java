package com.my.repository;

import com.my.dto.UserDTO;
import com.my.exception.FindException;

public interface UserRepository {
	
	/**
	 * 회원을 저장소에 추가한다.
	 * @param inputUser 회원의 가입 정보
	 */
	void insertUser(UserDTO inputUser);

	/**
	 * 내 정보를 조회한다.
	 * @param email 회원의 이메일
	 * @return 회원 정보
	 * @throws FindException 정보가 조회되지않으면 FindException 발생한다.
	 */
	UserDTO selectUserByEmail(String email) throws FindException;
	
}
