package com.my.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Properties;

import com.my.dto.UserDTO;
import com.my.exception.FindException;
import com.my.repository.UserRepository;

public class UserService {
	private UserRepository repository;

	public UserService(String propertiesFileName) {
		Properties env = new Properties();
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("user");
			Class<?> clazz = Class.forName(className);
			Object obj = clazz.getDeclaredConstructor().newInstance();
			repository = (UserRepository) obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * email로 아이디 중복체크한다
	 * 아이디가 이미 존재하는 경우에 FindException
	 * @param email
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void idDuplicateCheck(String email) throws SQLException, Exception {
		repository.selectUserByEmail(email);
	}

	/**
	 * 회원을 저장소에 추가한다.
	 * 
	 * @param inputUser 회원의 가입 정보
	 * @return 회원 정보
	 */
	public UserDTO signUp(UserDTO inputUser) {
		repository.insertUser(inputUser);
		return inputUser;
	}
	
	/**
	 * 회원의 개인정보를 확인한다 .
	 * @param email 회원의 정보
	 * @return 회원 정보
	 * @throws FindException 회원을 찾지못하면 FindException 발생한다.
	 */
	public UserDTO searchUserInfo(String email) throws FindException {

		try {
			return repository.selectUserByEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}

	}

}
