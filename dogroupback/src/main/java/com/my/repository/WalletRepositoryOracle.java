package com.my.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.my.dto.WalletDTO;
import com.my.exception.FindException;
import com.my.sql.MyConnection;

public class WalletRepositoryOracle implements WalletRepository {
	private Connection conn = null;
	private PreparedStatement preStmt = null;
	private ResultSet rs = null;
	/**
	 * 사용자의 지갑 목록을 반환한다.
	 */
	@Override
	public List<WalletDTO> selectWallet(String email) throws FindException {
		List<WalletDTO> list = new ArrayList<>();
		try {
			conn = MyConnection.getConnection();
			String selectWalletSQL = "SELECT * FROM WALLET WHERE user_email= ?";
			preStmt = conn.prepareStatement(selectWalletSQL);
			preStmt.setString(1, email);
			rs = preStmt.executeQuery();
			
			while(rs.next()) {
				int transactionNo = rs.getInt("TRANSCATION_NO");
				String userEmail = rs.getString("USER_EMAIL");
				int walletBalance = rs.getInt("WALLET_BALANCE");
				Date transcationDate = rs.getDate("TRANSCATION_DATE");
				int studyId = rs.getInt("STUDY_ID");
				String transactionUser = rs.getString("TRANSCATION_USER");
				int transactionCategory = rs.getInt("TRANSCATION_CATEGORY");
				int transactionMoney = rs.getInt("TRANSACTION_MONEY");
				
				WalletDTO wallet = new WalletDTO(transactionNo, userEmail, walletBalance, transcationDate, studyId, transactionUser, transactionCategory, transactionMoney);
				list.add(wallet);
			}
			if(list.size() == 0) {
				throw new FindException("값을 찾지 못했습니다");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new FindException("값을 찾지 못했습니다: " + e.getMessage());
		}
		return list;
	}

}
