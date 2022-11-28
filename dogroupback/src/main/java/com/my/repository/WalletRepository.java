package com.my.repository;

import java.util.List;

import com.my.dto.WalletDTO;
import com.my.exception.FindException;

public interface WalletRepository {
	/**
	 * 사용자의 지갑 목록을 반환한다.
	 * @return 지갑 내역
	 * @throws FindException 
	 */
	List<WalletDTO> selectWallet(String email) throws FindException;
}
