package com.my.repository;

import com.my.dto.UserDTO;

public interface UserRepository {
	
	/**
	 * 회원을 저장소에 추가한다.
	 * @param inputUser 회원의 가입 정보
	 */
	void insertUser(UserDTO inputUser);

}
