package com.my.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import com.my.dto.StudyDTO;
import com.my.exception.FindException;
import com.my.repository.StudyRepository;
import com.my.repository.StudyRepositoryOracle;

public class StudyService {
	private StudyRepository repository;
	public StudyService() {
		repository = new StudyRepositoryOracle();
		String propertiesFileName = "repository.properties";	
		Properties env = new Properties();					
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("study");	
			Class.forName(className); 	
			Class clazz = Class.forName(className); 		
			Object obj = clazz.getDeclaredConstructor().newInstance();	
			repository = (StudyRepository)obj;
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		catch (ClassNotFoundException e) {
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
	 * 진행중인 스터디를 검색한다.
	 * @param email 스터디ID
	 * @return 스터디 목록
	 * @throws FindException 진행중인 스터디를 찾지못하면 FindException발생한다.
	 */
	public List<StudyDTO> searchMyStudy(String email) throws FindException {
		return repository.selectStudyByEmail(email);
	}
}
