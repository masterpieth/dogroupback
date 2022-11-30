package com.my.repository;

import java.sql.Connection;
import java.util.List;

import com.my.dto.WalletDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;

public interface WalletRepository {
	/**
	 * 사용자의 지갑 목록을 반환한다.
	 * @return 지갑 내역
	 * @throws FindException 
	 */
	List<WalletDTO> selectWallet(String email) throws FindException;
	
	/**
	 * 
	 * @param email에 해당하는  사용자의 잔액을 확인함
	 * @return 
	 * @throws FindException
	 */
		//	void selectUserBalance(String email) throws FindException;
	

	/**
	 * 
	 * @param Wallet 지갑에 돈이 충전됨(거래내역생성, 현재날짜로그, 거래자분류, 카테고리분류, 금액 내역 생성)
	 * @throws AddException
	 */
	void updateUserBalance(String email,  WalletDTO wallet) throws Exception;
	
	/**
	 * @param Wallet 지갑에 돈이 충전되는 프로시저를 호출함(새로운 거래내역생성)
	 * @throws AddException
	 * @throws Exception 
	 */

	void insertWallet(WalletDTO Wallet, String email) throws  Exception;

	/**
	 * @param email에  해당하는 사용자의 지갑에 돈을 충전함
	 * @param balance 잔액을 업데이트함
	 * @throws Exception 
	 */



}
