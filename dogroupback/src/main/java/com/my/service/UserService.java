package com.my.service;

import java.sql.SQLException;

import com.my.exception.FindException;
import com.my.repository.UserRepository;

public class UserService {
	private UserRepository repository;

	
	
	
	public void  idDuplicateCheck(String email) throws Exception{
		
		repository.selectUserByEmail(email);
		
		
	}
	
	
}
