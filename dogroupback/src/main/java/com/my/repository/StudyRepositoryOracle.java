package com.my.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.my.dto.StudyDTO;
import com.my.exception.FindException;
import com.my.sql.MyConnection;

public class StudyRepositoryOracle implements StudyRepository {

	public List<StudyDTO> selectStudyByEmail(String email) throws FindException {
		List<StudyDTO> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = MyConnection.getConnection();
			String selectStudyByEmailSQL = "SELECT * FROM STUDY";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		try {
			pstmt = conn.prepareStatement(selectStudyByEmailSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	
	
}

