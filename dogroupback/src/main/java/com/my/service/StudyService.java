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
		String propertiesFileName = "repository.properties";	//3차수정을 외부파일에서 한다
		Properties env = new Properties();					
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("study");	//클래스이름을 String 타입으로 찾아온것 
			Class.forName(className); 	//JVM메모리에 로드한다
			Class clazz = Class.forName(className); 		//객체생성 반환형이 object
			Object obj = clazz.getDeclaredConstructor().newInstance();	
			repository = (StudyRepository)obj;
			
		} catch (IOException e) {
			e.printStackTrace();
		}	//연결된 자원을 읽는다. key = value  
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
	
	public List<StudyDTO> searchMyStudy(String email) throws FindException {
		return repository.selectStudyByEmail(email);
	}
}}
