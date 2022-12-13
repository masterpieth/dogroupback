package com.my.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSetInternal;
import com.my.dto.WalletDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.sql.MyConnection;

public class WalletRepositoryOracle implements WalletRepository {

	/**
	 * 
	 * 사용자의 지갑 목록을 반환한다.
	 */
	@Override
	public List<WalletDTO> selectWallet(String email) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<WalletDTO> list = new ArrayList<>();
		try {
			conn = MyConnection.getConnection();
			String selectWalletSQL = "SELECT * FROM WALLET WHERE user_email= ?";
			preStmt = conn.prepareStatement(selectWalletSQL);
			preStmt.setString(1, email);
			rs = preStmt.executeQuery();

			while (rs.next()) {
				int transactionNo = rs.getInt("TRANSCATION_NO");
				String userEmail = rs.getString("USER_EMAIL");
				int walletBalance = rs.getInt("WALLET_BALANCE");
				Date transcationDate = rs.getDate("TRANSCATION_DATE");
				int studyId = rs.getInt("STUDY_ID");
				String transactionUser = rs.getString("TRANSCATION_USER");
				int transactionCategory = rs.getInt("TRANSCATION_CATEGORY");
				int transactionMoney = rs.getInt("TRANSACTION_MONEY");

				WalletDTO wallet = new WalletDTO(transactionNo, userEmail, walletBalance, transcationDate, studyId,
						transactionUser, transactionCategory, transactionMoney);
				list.add(wallet);
			}
			if (list.size() == 0) {
				throw new FindException("값을 찾지 못했습니다");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException("값을 찾지 못했습니다: " + e.getMessage());
		}
		return list;
	}

	/**
	 * @param email에 해당하는 사용자의 잔액을 확인함
	 * @return
	 * @throws FindException
	 */
	/*
	 * @Override public void selectUserBalance(String email) throws FindException {
	 * try { conn = MyConnection.getConnection(); String selectUserBalanceSQL =
	 * "SELECT user_balance FROM users WHERE user_email= ?"; preStmt =
	 * conn.prepareStatement(selectUserBalanceSQL); preStmt.setString(1, email); rs
	 * = preStmt.executeQuery();
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	/**
	 * @param email에  해당하는 사용자의 지갑에 돈을 충전하는 프로시저호출함
	 * @param balance 잔액을 업데이트함
	 * @throws Exception
	 */
	@Override
	public void updateUserBalance(WalletDTO wallet, int flag ) throws Exception {
		Connection conn = null;
		CallableStatement calStmt = null;
		// 지갑 충전 프로시저
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			String procUserWalletSQL = "{call proc_userwallet(?, ?, ?, ?, ?)}";
			calStmt = conn.prepareCall(procUserWalletSQL);
			calStmt.setInt(1, flag);
			calStmt.setString(2, wallet.getEmail());
			calStmt.setString(3, wallet.getTransactionUser());
			calStmt.setInt(4, 3);
			calStmt.setInt(5, wallet.getTransactionMoney());
			calStmt.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			MyConnection.close(null, calStmt, conn);

		}

	}

	
}
