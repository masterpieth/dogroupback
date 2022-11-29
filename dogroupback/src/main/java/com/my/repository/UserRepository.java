package com.my.repository;

import com.my.dto.UserDTO;
import com.my.exception.FindException;
import com.my.exception.AddException;

public interface UserRepository {
	
	/**
	 * 회원을 저장소에 추가한다.
	 * @param inputUser 회원의 가입 정보
	 * @throws AddException 추가 도중 발생하는 오류
	 */
	void insertUser(UserDTO inputUser) throws AddException;

	/**
	 * 이메일로 회원아이디에 해당하는 고객을 반환한다
	 * @param email 아이디
	 * @return 고객
	 * @throws FindException 아이디에 해당하는 고객이 없으면 FindException발생한다
	 */
	UserDTO selectUserByEmail(String email) throws FindException;
}
