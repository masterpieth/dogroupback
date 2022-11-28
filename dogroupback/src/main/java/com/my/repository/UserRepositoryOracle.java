package com.my.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.my.dto.UserDTO;
import com.my.sql.MyConnection;

public class UserRepositoryOracle implements UserRepository {
	private Connection conn = null;
	private PreparedStatement preStmt = null;
	private ResultSet rs = null;
	
	@Override
	public void insertUser(UserDTO inputUser) {
		//기본 성실도:50, 잔액:0, 상태:1
		String insertUserSQL = "insert into users(user_email, user_name, user_password, user_diligence, user_balance, user_status)"
							+ " values(?, ?, ?, 50, 0, 1)";
		try {
			conn =  MyConnection.getConnection();
			preStmt = conn.prepareStatement(insertUserSQL);
			preStmt.setString(1, inputUser.getEmail());
			preStmt.setString(2, inputUser.getName());
			preStmt.setString(3, inputUser.getPassword());
			int row = preStmt.executeUpdate();
			if(row == 1) {
				System.out.println("성공적으로 DB에 회원 정보가 등록되었습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(preStmt, conn);
		}
		
	}

}
