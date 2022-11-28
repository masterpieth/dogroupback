package com.my.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyConnection {
	public static Connection getConnection() throws Exception {
		// 1)JDBC드라이버 로드
		Class.forName("oracle.jdbc.driver.OracleDriver");
		System.out.println("JDBC드라이버 로드 성공");
		// 2)Connection
		Connection conn = null;
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String user = "hr";
		String password = "hr";

		conn = DriverManager.getConnection(url, user, password);
		System.out.println("DB와 연결 성공");
		return conn;
	}

	public static void close(Statement stmt, Connection conn) {
		close(null, stmt, conn);
	}
	
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
