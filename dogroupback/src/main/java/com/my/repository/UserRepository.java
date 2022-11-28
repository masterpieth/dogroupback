package com.my.repository;

import java.sql.SQLException;

import com.my.dto.UserDTO;
import com.my.exception.FindException;

public interface UserRepository {
	/**
	 * 회원을 저장소에 추가한다 매개변수로 회원이 전달된다
	 * 
	 * @throws SQLException
	 * @throws Exception
	 * 
	 * 
	 */

	UserDTO selectUserByEmail(String email) throws FindException, SQLException, Exception;

}
