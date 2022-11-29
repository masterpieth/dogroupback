package com.my.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import com.my.dto.WalletDTO;
import com.my.exception.FindException;
import com.my.repository.WalletRepository;

public class WalletService {
	private WalletRepository repository;

	public WalletService(String propertiesFileName) {
		Properties env = new Properties();
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("wallet");
			Class<?> clazz = Class.forName(className);
			Object obj = clazz.getDeclaredConstructor().newInstance();
			repository = (WalletRepository) obj;
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
	 * 사용자의 지갑 목록을 반환한다.
	 * @param email				//사용자 ID
	 * @return 지갑목록
	 * @throws FindException
	 */
	public List<WalletDTO> searchTransactionListAll(String email) throws FindException {
		return repository.selectWallet(email);
	}
}
