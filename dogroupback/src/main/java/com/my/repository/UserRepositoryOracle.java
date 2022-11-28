package com.my.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.my.dto.UserDTO;
import com.my.exception.FindException;
import com.my.sql.MyConnection;

public class UserRepositoryOracle implements UserRepository {

	private Connection conn = null;
	private PreparedStatement preStmt = null;
	private ResultSet rs = null;

	@Override
	public UserDTO selectUserByEmail(String email) throws FindException, SQLException {
		UserDTO user;
		String selectSQL = "SELECT * FROM users WHERE user_email=?";
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(selectSQL);
			preStmt.setString(1, email);
			rs = preStmt.executeQuery();
			if (rs.next()) {
			return new UserDTO(rs.getString("user_email"), rs.getString("user_password"), rs.getString("user_name"));
			}else {
			throw new FindException("아이디에 해당하는 고객이없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
		return null;
	}
}
