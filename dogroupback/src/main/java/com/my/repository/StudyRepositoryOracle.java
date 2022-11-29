package com.my.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.exception.AddException;
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
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	/**
	 * 
	 */
	@Override
	public void insertHomeworkByEmail(String email, int studyId, Date created_at) throws AddException {
		try {
			conn = MyConnection.getConnection();
			String insertHomeworkByEmailSQL = "INSERT INTO HOMEWORK VALUES(?, ?, ?)";
			preStmt = conn.prepareStatement(insertHomeworkByEmailSQL);
			preStmt.setDate(1, created_at);
			preStmt.setInt(2, studyId);
			preStmt.setString(3, "user1@gmail.com");
			preStmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("HomeWork Insert 실패");
		}
	}

	@Override
	public StudyDTOBomi selectStudy(int studyId) throws FindException {
		
		try {
			conn = MyConnection.getConnection();
			String selectStudySQL = "SELECT st.*, --스터디\r\n"
					+ "       ss.subject_code, --스터디과목 코드 \r\n"
					+ "       s.subject_name, --스터디 과목명\r\n"
					+ "       (SELECT COUNT(*) FROM study_users WHERE study_id=st.study_id) cnt, --스터디 참여자수 \r\n"
					+ "       (SELECT user_diligence FROM users WHERE user_email = st.user_email) diligence --스터디장 성실도\r\n"
					+ "		  FROM STUDY st JOIN study_subject ss ON st.study_id = ss.study_id\r\n"
					+ "             JOIN subject s ON  ss.subject_code = s.subject_code             \r\n"
					+ "		 WHERE st.study_id=71;";
			preStmt = conn.prepareStatement(selectStudySQL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	@Override
	public StudyDTOBomi selectStudy(int studyId) throws FindException {
		
		try {
			conn = MyConnection.getConnection();
			String selectStudySQL = "SELECT st.*, --스터디\r\n"
					+ "       ss.subject_code, --스터디과목 코드 \r\n"
					+ "       s.subject_name, --스터디 과목명\r\n"
					+ "       (SELECT COUNT(*) FROM study_users WHERE study_id=st.study_id) cnt, --스터디 참여자수 \r\n"
					+ "       (SELECT user_diligence FROM users WHERE user_email = st.user_email) diligence --스터디장 성실도\r\n"
					+ "		  FROM STUDY st JOIN study_subject ss ON st.study_id = ss.study_id\r\n"
					+ "             JOIN subject s ON  ss.subject_code = s.subject_code             \r\n"
					+ "		 WHERE st.study_id=71;";
			preStmt = conn.prepareStatement(selectStudySQL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
      
  @Override
  public HomeworkDTO selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException {
      List<Date> homeworkList = new ArrayList<Date>();
      String homeworListSQL = "select * from homework where "    
                              + "study_id = ? and user_email = ? "
                              + "and study_submit_dt >= (select study_start_date from study where study_id = ?)";
      try {
          conn = MyConnection.getConnection();
          preStmt = conn.prepareStatement(homeworListSQL);
          preStmt.setInt(1, studyId);
          preStmt.setString(2, userEmail);
          preStmt.setInt(3, studyId);
          rs = preStmt.executeQuery(); //선택한 유저의 스터디 시작일 이후의 제출 과제 내역을 모두 가져온다.

          while (rs.next()) {
              Date studySubmitDt = rs.getDate("study_submit_dt");
              homeworkList.add(studySubmitDt);
          }
          return new HomeworkDTO(homeworkList, studyId, userEmail);
      } catch (Exception e) {
          e.printStackTrace();
          throw new FindException(e.getMessage());
      } finally {
          MyConnection.close(rs, preStmt, conn);
      }
  }
}

