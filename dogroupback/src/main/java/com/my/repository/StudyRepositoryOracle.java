package com.my.repository;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.dto.StudySubjectDTOBomi;
import com.my.dto.SubjectDTOBomi;
import com.my.dto.UserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.RemoveException;
import com.my.sql.MyConnection;

public class StudyRepositoryOracle implements StudyRepository {
	private Connection conn = null;
	private PreparedStatement preStmt = null;
	private CallableStatement calStmt = null;
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
			while (rs.next()) {
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
				int studyHomeworkPerWeek = rs.getInt("STUDY_HOMEWORK_PER_WEEK");
				int studyPaid = rs.getInt("STUDY_PAID");
				String studyContent = rs.getString("STUDY_CONTENT");

				StudyDTO studyall = new StudyDTO(studyId, userEmail, studyTitle, studySize, studyFee,
						studyCertification, studyDiligenceCutline, studyPostDate, studyStartDate, studyEndDate,
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
	 * 스터디회원의 과제를 insert한다
	 */
	@Override
	public void insertHomeworkByEmail(String email, int studyId, Date created_at) throws AddException {
		try {
			conn = MyConnection.getConnection();
			String insertHomeworkByEmailSQL = "INSERT INTO HOMEWORK VALUES(?, ?, ?)";
			preStmt = conn.prepareStatement(insertHomeworkByEmailSQL);
			preStmt.setDate(1, new java.sql.Date(created_at.getTime()));
			preStmt.setInt(2, studyId);
			preStmt.setString(3, email);
			preStmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("HomeWork Insert 실패");
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	/**
	 * selectStudy스터디 목록조회(회원용) study, study_users, study_subject, subject테이블사용 현재
	 * 해당 스터디에 참가중인 회원수를 포함하여 보여줌
	 * 하나의 studyId로 검색했을때 서로다른 스터디과목명은 3개가 온다.
	 */
	@Override
	public StudyDTOBomi selectStudy(int studyId) throws FindException {

		try {
			conn = MyConnection.getConnection();
			String selectStudySQL = "SELECT st.*, --스터디\r\n" + "       ss.subject_code, --스터디과목 코드 \r\n"
					+ "       s.subject_name, --스터디 과목명\r\n"
					+ "       (SELECT COUNT(*) FROM study_users WHERE study_id=st.study_id) cnt, --스터디 참여자수 \r\n"
					+ "       (SELECT user_diligence FROM users WHERE user_email = st.user_email) diligence --스터디장 성실도\r\n"
					+ "		  FROM STUDY st JOIN study_subject ss ON st.study_id = ss.study_id\r\n"
					+ "             JOIN subject s ON  ss.subject_code = s.subject_code             \r\n"
					+ "		  WHERE st.study_id= ?";
			preStmt = conn.prepareStatement(selectStudySQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();
			System.out.println("db연결되었음");

			List<StudySubjectDTOBomi> studySubjectList = new ArrayList<>(); 

			StudyDTOBomi study;
			if (rs.next()) {
				study = new StudyDTOBomi();
				study.setStudyTitle(rs.getString("study_title"));
				study.setStudyCertification(rs.getInt("study_certification"));
				study.setStudySize(rs.getInt("study_size"));
				study.setStudyFee(rs.getInt("study_fee"));
				study.setStudyDiligenceCutline(rs.getInt("study_diligence_cutline"));
				study.setStudyHomeworkPerWeek(rs.getInt("STUDY_HOMEWORK_PER_WEEK"));
				study.setStudyStartDate(rs.getDate("study_start_date"));
				study.setStudyEndDate(rs.getDate("study_end_date"));
				study.setStudyContent(rs.getClob("study_content"));
				study.setStudyUsers(rs.getInt("cnt"));// 해당스터디 현재 참가인원수
				
				UserDTO u = new UserDTO();
				u.setDiligence(rs.getInt("diligence"));			
				u.setEmail(rs.getString("user_email"));
				study.setStudyLeader(u);
				
				
				StudySubjectDTOBomi studySubjectDtoBomi = new StudySubjectDTOBomi();

				SubjectDTOBomi subjectDToBomi = new SubjectDTOBomi(rs.getString("subject_code"),
						rs.getString("subject_name"), null);
				studySubjectDtoBomi.setSubject(subjectDToBomi);
				studySubjectList.add(studySubjectDtoBomi);

				while (rs.next()) {
					studySubjectDtoBomi = new StudySubjectDTOBomi();

					subjectDToBomi = new SubjectDTOBomi(rs.getString("subject_code"), rs.getString("subject_name"),
							null);
					studySubjectDtoBomi.setSubject(subjectDToBomi);
					studySubjectList.add(studySubjectDtoBomi);

				}
				study.setSubjects(studySubjectList);
				return study;
			}
			throw new FindException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}
	}
	/**
	 * 스터디를 Insert 한다. Study와 StudyUser(스터디장)가 한 트랜잭션에 insert되고, 실패시 롤백한다.
	 */
	@Override
	public void  insertStudy(StudyDTO study) {
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			String insertStudySQL = "INSERT INTO STUDY VALUES(study_seq.nextval, ?, ?, ?, ?, ?, ?, sysdate, ?, ?, ?, 0, ?)";
			String insertedSeqSQL = "SELECT study_seq.currval as CURVAL FROM dual";
			
			preStmt = conn.prepareStatement(insertStudySQL);
			//기본값 세팅
			preStmt.setString(1, study.getUserEmail());
			preStmt.setString(2, study.getStudyTitle());
			preStmt.setInt(3, study.getStudySize());
			preStmt.setInt(4, study.getStudyFee());
			preStmt.setInt(5, study.getStudyCertification());
			preStmt.setInt(6, study.getStudyDiligenceCutline());
			preStmt.setDate(7, new java.sql.Date(study.getStudyStartDate().getTime()));
			preStmt.setDate(8, new java.sql.Date(study.getStudyEndDate().getTime()));
			preStmt.setInt(9, study.getStudyHomeworkPerWeek());
			//content Clob 세팅
			Clob clob = conn.createClob();
			clob.setString(1, study.getStudyContent());
			preStmt.setClob(10, clob);
			preStmt.executeUpdate();
			
			preStmt = conn.prepareStatement(insertedSeqSQL);
			rs = preStmt.executeQuery();
			if(rs.next()) {
				study.setStudyId(rs.getInt("CURVAL"));
			}
			//스터디장 insert
			insertStudyUserLeader(study, study.getUserEmail(), conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	/**
	 * 스터디장을 insert 한다 - insertStudy에서 connection을 받아서 한 트랜잭션에 있도록 한다.
	 * 
	 */
	@Override
	public void insertStudyUserLeader(StudyDTO study, String email, Connection conn) throws AddException {
		try {
			//지갑 관련 프로시저
			String procSQL = "{ call proc_wallet(?, ?, ?, ?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setInt(1, 1);
			calStmt.setString(2, email);
			calStmt.setInt(3, study.getStudyId());
			calStmt.setString(4, null);
			calStmt.setInt(5, 2);
			calStmt.setInt(6, study.getStudyFee());
			calStmt.executeUpdate();
			//유저 insert
			String insertStudyUserSQL = "INSERT INTO STUDY_USERS VALUES(?, ?)";
			preStmt = conn.prepareStatement(insertStudyUserSQL);
			preStmt.setInt(1, study.getStudyId());
			preStmt.setString(2, email);
			conn.commit();
		} catch (Exception e) {
			try {
				e.printStackTrace();
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			MyConnection.close(rs, preStmt, conn);
			if(calStmt != null) {
				try {
					calStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 스터디원을 Insert 한다 - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다.
	 * flag 1: 스터디원 추가
	 * flag 0: 스터디원 삭제
	 */
	@Override
	public void insertStudyUser(StudyDTO study, String email) throws AddException {
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			//지갑 관련 프로시저
			String procSQL = "{ call proc_wallet(?, ?, ?, ?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setInt(1, 1);
			calStmt.setString(2, email);
			calStmt.setInt(3, study.getStudyId());
			calStmt.setString(4, null);
			calStmt.setInt(5, 2);
			calStmt.setInt(6, study.getStudyFee());
			calStmt.executeUpdate();
			//유저 insert
			String insertStudyUserSQL = "INSERT INTO STUDY_USERS VALUES(?, ?)";
			preStmt = conn.prepareStatement(insertStudyUserSQL);
			preStmt.setInt(1, study.getStudyId());
			preStmt.setString(2, email);
			conn.commit();
		} catch (Exception e) {
			try {
				e.printStackTrace();
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			MyConnection.close(rs, preStmt, conn);
			if(calStmt != null) {
				try {
					calStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
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
	/**
	 * 스터디에서 탈퇴한다 - StudyUser 테이블에서 정보를 제거한다.
	 */
	@Override
	public void deleteStudyUser(StudyDTO study, String email) throws RemoveException {
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			//지갑 관련 프로시저
			String procSQL = "{ call proc_wallet(?, ?, ?, ?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setInt(1, 0);
			calStmt.setString(2, email);
			calStmt.setInt(3, study.getStudyId());
			calStmt.setString(4, null);
			calStmt.setInt(5, 5);
			calStmt.setInt(6, study.getStudyFee());
			calStmt.executeUpdate();
			//스터디 유저를 삭제한다.
			String deleteStudyUserSQL = "DELETE FROM study_users WHERE study_id = ? AND user_email = ?";
			preStmt = conn.prepareStatement(deleteStudyUserSQL);
			preStmt.setInt(1, study.getStudyId());
			preStmt.setString(2, email);
			conn.commit();
		} catch (Exception e) {
			try {
				e.printStackTrace();
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			MyConnection.close(rs, preStmt, conn);
			if(calStmt != null) {
				try {
					calStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
