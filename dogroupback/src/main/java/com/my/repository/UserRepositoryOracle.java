package com.my.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.my.dto.UserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.sql.MyConnection;

public class UserRepositoryOracle implements UserRepository {
	
	@Override
	public void insertUser(UserDTO inputUser) throws AddException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		//기본 성실도:50, 잔액:0, 상태:1
		String insertUserSQL = "insert into users(user_email, user_name, user_password, user_diligence, user_balance, user_status)"
							+ " values(?, ?, ?, 50, 0, 1)";
		try {
			conn =  MyConnection.getConnection();
			preStmt = conn.prepareStatement(insertUserSQL);
			preStmt.setString(1, inputUser.getEmail());
			preStmt.setString(2, inputUser.getName());
			preStmt.setString(3, inputUser.getPassword());
			preStmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("회원 추가에 실패했습니다."); 
		} finally {
			MyConnection.close(preStmt, conn);
		}
		
	}

    @Override
    public UserDTO selectUserByEmail(String email) throws FindException {
    	Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
        String selectSQL = "SELECT * FROM users WHERE user_email= ? ";
        try {
            conn = MyConnection.getConnection();
            preStmt = conn.prepareStatement(selectSQL);
            preStmt.setString(1, email);
            rs = preStmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("user_name");
                String password = rs.getString("user_password");
                int diligence = rs.getInt("user_diligence");
                int userBalance = rs.getInt("user_balance");
                int status = rs.getInt("user_status");
                UserDTO userInfo = new UserDTO(email, name, password, diligence, userBalance, status);
                return userInfo;
            } else {
                throw new FindException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new FindException("정보를 찾을 수 없습니다.");
        } finally {
            MyConnection.close(rs, preStmt, conn);
        }
    }
}
