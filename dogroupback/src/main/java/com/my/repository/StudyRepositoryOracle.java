package com.my.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.my.dto.StudyDTO;
import com.my.exception.FindException;
import com.my.sql.MyConnection;

public class StudyRepositoryOracle implements StudyRepository {
	private Connection conn = null;
	private PreparedStatement preStmt = null;
	private ResultSet rs = null;

	@Override
	public List<StudyDTO> selectStudyByEmail(String email) throws FindException {
		List<StudyDTO> list = new ArrayList<>();
		
		try {
			conn = MyConnection.getConnection();
			String selectStudyByEmailSQL = "SELECT * FROM study WHERE user_email= ? ";
				
			preStmt = conn.prepareStatement(selectStudyByEmailSQL);
			preStmt.setString(1, email);
			rs = preStmt.executeQuery();
			
			System.out.println("연결되었음");
			while(rs.next()) {
			int studyId = rs.getInt("study_id");				
			String userEmail = rs.getString("user_email");			
			String studyTitle = rs.getString("study_title"); 			
			int studySize = rs.getInt("study_size");				
			int studyFee = rs.getInt("study_fee");				
			int studyCertification = rs.getInt("study_certification");
			int studyDiligenceCutline = rs.getInt("study_diligence_cutline");
			Date studyPostDate = rs.getDate("study_post_date");		
			Date studyStartDate = rs.getDate("study_start_date");	
			Date studyEndDate = rs.getDate("study_end_date"); 		
			int studyHomeworkPerWeek= rs.getInt("STUDY_HOMEWORK_PER_WEEK");	
			int studyPaid = rs.getInt("STUDY_PAID");				
			String studyContent = rs.getString("STUDY_CONTENT");	
			
			StudyDTO studyall = new StudyDTO(studyId, userEmail, studyTitle, studySize, studyFee, 
					 studyCertification, studyDiligenceCutline ,
					 studyPostDate, studyStartDate, studyEndDate,	
					 studyHomeworkPerWeek, studyPaid, studyContent);
			
			list.add(studyall);
			}
			return list;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			MyConnection.close(rs, preStmt, conn);
		}
	
	}	
	
	public static void main(String[] args) {
		StudyRepository repository = new StudyRepositoryOracle();
		List<StudyDTO> list;
		try {
			list = repository.selectStudyByEmail("user18@gmail.com");
			for(StudyDTO i :list) {
				
				System.out.println(i.getStudyId());
			}
		} catch (FindException e) {
			e.printStackTrace();
		}
		
	}
}

