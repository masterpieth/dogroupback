package com.my.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Properties;

import com.my.dto.UserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.repository.UserRepository;
import com.my.repository.UserRepositoryOracle;

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
	 * 아이디가 없으면 만들수 있다는 의미로 true를 반환하고, 아이디를 발견하면 새로 만들수 없다는 의미로 false를 반환한다.
	 * @param email
	 * @throws FindException 
	 */
	public boolean idDuplicateCheck(String email) {
		try {
			repository.selectUserByEmail(email);
		} catch (FindException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	/**
	 * 회원을 저장소에 추가한다.
	 * 
	 * @param inputUser 회원의 가입 정보
	 * @return 회원 정보
	 */
	public UserDTO signUp(UserDTO inputUser) throws AddException{
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
