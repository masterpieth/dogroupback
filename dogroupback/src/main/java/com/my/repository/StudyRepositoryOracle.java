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

import com.my.dto.StudySubjectDTO;
import com.my.dto.StudyUserDTO;
import com.my.dto.SubjectDTO;

import com.my.dto.UserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.sql.MyConnection;

public class StudyRepositoryOracle implements StudyRepository {
	
	/**
	 * 회원의 이메일로 진행된 모든 스터디 정보를 반환한다. 
	 */
	@Override
	public List<StudyDTO> selectStudyByEmail(String email) throws FindException {
		List<StudyDTO> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		try {
			conn = MyConnection.getConnection();
			String selectStudyByEmailSQL = "SELECT st.*, s.subject_name, s.subject_parent_code "
										+ "FROM STUDY st JOIN study_subject ss ON st.study_id = ss.study_id "
										+ "JOIN subject s ON  ss.subject_code = s.subject_code "
										+ "WHERE st.user_email = ?";
			preStmt = conn.prepareStatement(selectStudyByEmailSQL);
			preStmt.setString(1, email);
			rs = preStmt.executeQuery();

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
				int studyHomeworkPerWeek = rs.getInt("study_homework_per_week");
				int studyPaid = rs.getInt("study_paid");
				int studyGatheredSize = rs.getInt("study_gathered_size");
				Clob studyContent = rs.getClob("study_content");
				
				StudyDTO study = new StudyDTO(studyId, userEmail, studyTitle, studySize, studyFee,
						studyCertification, studyDiligenceCutline, studyPostDate, studyStartDate, studyEndDate,
						studyHomeworkPerWeek, studyPaid, studyGatheredSize, studyContent);
				
				List<StudySubjectDTO> studySubjectList = new ArrayList<>(); //스터디 과목 정보를 저장할 LIST
				StudySubjectDTO studySubject = new StudySubjectDTO(studyId, null); //스터디 과목 정보를 저장할 객체
				
				SubjectDTO subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
				SubjectDTO parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
				subject.setSubjectParent(parentSubject);
				studySubject.setSubject(subject);
				studySubjectList.add(studySubject);
				
				while (rs.next()) {
					studySubject = new StudySubjectDTO(studyId, null);
					subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
					parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
					subject.setSubjectParent(parentSubject);
					studySubject.setSubject(subject);
					studySubjectList.add(studySubject);
				}
				study.setSubjects(studySubjectList);
				
				list.add(study);
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
	 * 스터디의 현재 정보를 반환한다.
	 */
	@Override
	public StudyDTO selectStudyByStudyId(int studyId) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		try {
			String selectStudySQL = "SELECT st.*, --스터디\r\n" 
					+ "       ss.subject_code, --스터디과목 코드 \r\n"
					+ "       s.subject_name, --스터디 과목명\r\n"
					+ "       (SELECT user_diligence FROM users WHERE user_email = st.user_email) diligence --스터디장 성실도\r\n"
					+ "		  FROM STUDY st JOIN study_subject ss ON st.study_id = ss.study_id\r\n"
					+ "             JOIN subject s ON  ss.subject_code = s.subject_code             \r\n"
					+ "		  WHERE st.study_id= ?";
			
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(selectStudySQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();

			StudyDTO study = null;
			if (rs.next()) {
				study = new StudyDTO();
				study.setStudyId(rs.getInt("study_id"));
				study.setUserEmail(rs.getString("user_email"));
				study.setStudyTitle(rs.getString("study_title"));
				study.setStudyCertification(rs.getInt("study_certification"));
				study.setStudySize(rs.getInt("study_size"));
				study.setStudyFee(rs.getInt("study_fee"));
				study.setStudyDiligenceCutline(rs.getInt("study_diligence_cutline"));
				study.setStudyHomeworkPerWeek(rs.getInt("study_homework_per_week"));
				study.setStudyPostDate(rs.getDate("study_post_date"));
				study.setStudyStartDate(rs.getDate("study_start_date"));
				study.setStudyEndDate(rs.getDate("study_end_date"));
				study.setStudyContent(rs.getClob("study_content"));
				study.setStudyPaid(rs.getInt("study_paid"));
				study.setStudyGatheredSize(rs.getInt("study_gathered_size"));

				UserDTO u = new UserDTO();
				u.setDiligence(rs.getInt("diligence"));
				u.setEmail(rs.getString("user_email"));
				study.setStudyLeader(u);
				
				List<StudySubjectDTO> studySubjectList = new ArrayList<>(); //스터디 과목 정보를 저장할 LIST
				StudySubjectDTO studySubject = new StudySubjectDTO(studyId, null); //스터디 과목 정보를 저장할 객체
				
				SubjectDTO subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
				SubjectDTO parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
				subject.setSubjectParent(parentSubject);
				studySubject.setSubject(subject);
				studySubjectList.add(studySubject);
				
				while (rs.next()) {
					studySubject = new StudySubjectDTO(studyId, null);
					subject = new SubjectDTO(rs.getString("subject_code"), rs.getString("subject_name"), null);
					parentSubject = new SubjectDTO(rs.getString("subject_parent_code"), null, null);
					subject.setSubjectParent(parentSubject);
					studySubject.setSubject(subject);
					studySubjectList.add(studySubject);
				}
				study.setSubjects(studySubjectList);
				return study;
			}
			throw new FindException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
 
	/**
	 * 스터디원 목록과 스터디원의 회원 정보를 반환한다.
	 */
	public List<StudyUserDTO> studyUsers(int studyId) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		
		String selectStudyUserSQL = "select * from study_users join users using (user_email) where study_id = ?";		
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(selectStudyUserSQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();
			List<StudyUserDTO> studyUserList = new ArrayList<>();
			StudyUserDTO user;
			while (rs.next()) {
				user = new StudyUserDTO();
				user.setStudyId(studyId);
				user.setDiligence(rs.getInt("user_diligence"));
				user.setEmail(rs.getString("user_email"));
				user.setName(rs.getString("user_name"));
				user.setPassword(rs.getString("user_password"));
				user.setStatus(rs.getInt("user_status"));
				user.setUserBalance(rs.getInt("user_balance"));
				studyUserList.add(user);
			}
			return studyUserList;
      }
  }

	/**
	 * 스터디의 모든 과제 제출 내역을 리스트로 반환한다. selectStudy스터디 목록조회(회원용) study, study_users,
	 * study_subject, subject테이블사용 현재 해당 스터디에 참가중인 회원수를 포함하여 보여줌 하나의 studyId로 검색했을때
	 * 서로다른 스터디과목명은 3개가 온다.
	 */
	@Override
	public List<HomeworkDTO> selectHomeworkByStudyId(int studyId) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<HomeworkDTO> homeworkList = new ArrayList<>();
		String homeworListSQL = "select * from homework where " + "study_id = ? order by user_email, study_submit_dt";
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(homeworListSQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();

			while (rs.next()) {
				String userEmail = rs.getString("user_email");
				Date studySubmitDt = rs.getDate("study_submit_dt");
				HomeworkDTO homework = new HomeworkDTO();
				homework.setUserEmail(userEmail);
				homework.setStudySubmitDt(studySubmitDt);
				homeworkList.add(homework);
			}
			return homeworkList;
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
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
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
	 * 스터디의 모든 과제 제출 내역을 리스트로 반환한다. selectStudy스터디 목록조회(회원용) study, study_users,
	 * study_subject, subject테이블사용 현재 해당 스터디에 참가중인 회원수를 포함하여 보여줌 하나의 studyId로 검색했을때
	 * 서로다른 스터디과목명은 3개가 온다.
	 */
	@Override
	public List<HomeworkDTO> selectHomeworkByStudyId(int studyId) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<HomeworkDTO> homeworkList = new ArrayList<>();
		String homeworListSQL = "select * from homework where " + "study_id = ? order by user_email, study_submit_dt";
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(homeworListSQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();

			while (rs.next()) {
				String userEmail = rs.getString("user_email");
				Date studySubmitDt = rs.getDate("study_submit_dt");
				HomeworkDTO homework = new HomeworkDTO();
				homework.setUserEmail(userEmail);
				homework.setStudySubmitDt(studySubmitDt);
				homeworkList.add(homework);
			}
			return homeworkList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}

	/**
	 * 스터디 유저의(개인) 모든 과제 제출 내역을 반환한다.
	 * 
	 * @parama userEmail 유저 email
	 * @param studyId 스터디 ID
	 * @return 과제 리스트
	 */
	@Override
	public List<HomeworkDTO> selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<HomeworkDTO> homeworkList = new ArrayList<>();
		HomeworkDTO homework;
		String homeworListSQL = "select * from homework where study_id = ? and user_email = ?";
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(homeworListSQL);
			preStmt.setInt(1, studyId);
			preStmt.setString(2, userEmail);
			rs = preStmt.executeQuery();

			while (rs.next()) {
				homework = new HomeworkDTO();
				Date studySubmitDt = rs.getDate("study_submit_dt");
				homework.setStduyID(studyId);
				homework.setStudySubmitDt(studySubmitDt);
				homework.setUserEmail(userEmail);
				homeworkList.add(homework);
			}
			return homeworkList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}

	/**
	 * 스터디를 Insert 한다. Study와 StudyUser(스터디장)가 한 트랜잭션에 insert되고, 실패시 롤백한다.
	 */
	@Override
	public void insertStudy(StudyDTO study) {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			String insertStudySQL = "INSERT INTO STUDY VALUES(study_seq.nextval, ?, ?, ?, ?, ?, ?, sysdate, ?, ?, ?, 0, ?, 0)";
			String insertedSeqSQL = "SELECT study_seq.currval as CURVAL FROM dual";
			
			//기본값 셋팅
			preStmt = conn.prepareStatement(insertStudySQL);
			preStmt.setString(1, study.getUserEmail());
			preStmt.setString(2, study.getStudyTitle());
			preStmt.setInt(3, study.getStudySize());
			preStmt.setInt(4, study.getStudyFee());
			preStmt.setInt(5, study.getStudyCertification());
			preStmt.setDouble(6, study.getStudyDiligenceCutline());
			preStmt.setDate(7, new java.sql.Date(study.getStudyStartDate().getTime()));
			preStmt.setDate(8, new java.sql.Date(study.getStudyEndDate().getTime()));
			preStmt.setInt(9, study.getStudyHomeworkPerWeek());
			preStmt.setClob(10, study.getStudyContent());
			preStmt.executeUpdate();
			preStmt.close();
			//스터디 번호 SELECT
			preStmt = conn.prepareStatement(insertedSeqSQL);
			rs = preStmt.executeQuery();
			if (rs.next()) {
				study.setStudyId(rs.getInt("CURVAL"));
			}
			// 스터디장 insert
			insertStudyUserLeader(study, study.getUserEmail(), conn);
			// 스터디 과목 insert
			insertStudySubject(study, study.getSubjects(), conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}

	/**
	 * 스터디의 내용을 update한다.
	 */
	@Override
	public void updateStudy(StudyDTO study) throws ModifyException {
	}

	/**
	 * 스터디장을 insert 한다 - insertStudy에서 connection을 받아서 한 트랜잭션에 있도록 한다.
	 * 
	 */
	@Override
	public void insertStudyUserLeader(StudyDTO study, String email, Connection conn) throws AddException {
		CallableStatement calStmt = null;
		ResultSet rs = null;
		try {
			// 지갑 관련 프로시저
			String procSQL = "{ call proc_wallet(?, ?, ?, ?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setInt(1, 1);
			calStmt.setString(2, email);
			calStmt.setInt(3, study.getStudyId());
			calStmt.setString(4, null);
			calStmt.setInt(5, 2);
			calStmt.setInt(6, study.getStudyFee());
			calStmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			try {
				e.printStackTrace();
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			MyConnection.close(rs, calStmt, null);
		}
	}

	/**
	 * 스터디원을 Insert 한다 - 지갑 관련 추가-삭제 등을 담당하는 프로시저를 호출한다. flag 1: 스터디원 추가 flag 0:
	 * 스터디원 삭제
	 */
	@Override
	public void insertStudyUser(StudyDTO study, String email) throws AddException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		CallableStatement calStmt = null;
		ResultSet rs = null;
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			// 지갑 관련 프로시저
			String procSQL = "{ call proc_wallet(?, ?, ?, ?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setInt(1, 1);
			calStmt.setString(2, email);
			calStmt.setInt(3, study.getStudyId());
			calStmt.setString(4, null);
			calStmt.setInt(5, 2);
			calStmt.setInt(6, study.getStudyFee());
			calStmt.executeUpdate();
			// 유저 insert
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
		}
	}

	/**
	 * 스터디에서 탈퇴한다 - StudyUser 테이블에서 정보를 제거한다.
	 */
	@Override
	public void deleteStudyUser(StudyDTO study, String email) throws RemoveException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		CallableStatement calStmt = null;
		ResultSet rs = null;
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			// 지갑 관련 프로시저
			String procSQL = "{ call proc_wallet(?, ?, ?, ?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setInt(1, 0);
			calStmt.setString(2, email);
			calStmt.setInt(3, study.getStudyId());
			calStmt.setString(4, null);
			calStmt.setInt(5, 5);
			calStmt.setInt(6, study.getStudyFee());
			calStmt.executeUpdate();
			// 스터디 유저를 삭제한다.
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
			if (calStmt != null) {
				try {
					calStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 스터디 과목을 insert 한다 - insertStudy에서 connection을 받아서 한 트랜잭션에 있도록 한다.
	 */
	@Override
	public void insertStudySubject(StudyDTO study, List<StudySubjectDTO> subjects, Connection conn)
			throws AddException {
		PreparedStatement preStmt = null;
		try {
			// 스터디 과목 insert
			String insertStudySubjectSQL = "INSERT INTO STUDY_SUBJECT VALUES(?, ?)";
			preStmt = conn.prepareStatement(insertStudySubjectSQL);
			for (StudySubjectDTO subject : subjects) {
				preStmt.setInt(1, study.getStudyId());
				preStmt.setString(2, subject.getSubject().getSubjectCode());
				preStmt.executeUpdate();
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			MyConnection.close(null, preStmt, null);
		}
	}
	
	/**
	 * 검색 조건에 맞는 스터디 개수를 카운트하여 반환한다. (조건 : 타이틀명, 스터디 정원)
	 */
	@Override
	public int studyCount(String studyTitle, int studySize) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String studyStudyCountSQL = null;
		try {
			conn = MyConnection.getConnection();
			if (studyTitle == null)
				studyTitle = "";
			if (studySize != 0) {
				studyStudyCountSQL = "SELECT count(*) FROM study WHERE study_title Like ? and study_size = ?";
				preStmt = conn.prepareStatement(studyStudyCountSQL);
				preStmt.setString(1, "%" + studyTitle + "%");
				preStmt.setInt(2, studySize);
			} else {
				studyStudyCountSQL = "SELECT count(*) FROM study WHERE study_title Like ? ";
				preStmt = conn.prepareStatement(studyStudyCountSQL);
				preStmt.setString(1, "%" + studyTitle + "%");
			}
			rs = preStmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}

	/**
	 * 검색 조건에 맞는 스터디 리스트를 반환한다. (조건 : 현재 페이지, 페이지당 개수, 타이틀명, 스터디 정원)
	 */
	@Override
	public List<StudyDTO> selectStudy(int currentPage, int cntPerPage, String studyTitle, int studySize) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		List<StudyDTO> list = new ArrayList<>();
		String studyStudySQL = null;
		int startRow = currentPage * cntPerPage - cntPerPage + 1;
		int endRow = currentPage * cntPerPage;
		try {
			conn = MyConnection.getConnection();
			if (studyTitle == null)
				studyTitle = "";
			if (studySize != 0) {
				studyStudySQL = "SELECT * FROM  ( SELECT rownum rn, a.* FROM (SELECT * FROM study WHERE study_title Like ? and study_size = ? order by study_id) a )WHERE rn BETWEEN ? AND ?";
				preStmt = conn.prepareStatement(studyStudySQL);
				preStmt.setString(1, "%" + studyTitle + "%");
				preStmt.setInt(2, studySize);
				preStmt.setInt(3, startRow);
				preStmt.setInt(4, endRow);
			} else {
				studyStudySQL = "SELECT * FROM  ( SELECT rownum rn, a.* FROM (SELECT * FROM study WHERE study_title Like ? order by study_id) a )WHERE rn BETWEEN ? AND ?";
				preStmt = conn.prepareStatement(studyStudySQL);
				preStmt.setString(1, "%" + studyTitle + "%");
				preStmt.setInt(2, startRow);
				preStmt.setInt(3, endRow);
			}
			rs = preStmt.executeQuery();
			while (rs.next()) {
				int studyId = rs.getInt("study_id");
				String userEmail = rs.getString("user_email");
				studyTitle = rs.getString("study_title");
				studySize = rs.getInt("study_size");
				int studyFee = rs.getInt("study_fee");
				int studyCertification = rs.getInt("study_certification");
				int studyDiligenceCutline = rs.getInt("study_diligence_cutline");
				Date studyPostDate = rs.getDate("study_post_date");
				Date studyStartDate = rs.getDate("study_start_date");
				Date studyEndDate = rs.getDate("study_end_date");
				int studyHomeworkPerWeek = rs.getInt("STUDY_HOMEWORK_PER_WEEK");
				int studyPaid = rs.getInt("STUDY_PAID");
				int studyGatheredSize = rs.getInt("STUDY_GATHERED_SIZE");
				Clob studyContent = rs.getClob("STUDY_CONTENT");
				
				StudyDTO studyAll = new StudyDTO(studyId, userEmail, studyTitle, studySize, studyFee,
						studyCertification, studyDiligenceCutline, studyPostDate, studyStartDate, studyEndDate,
						studyHomeworkPerWeek, studyPaid, studyGatheredSize, studyContent);
				
				list.add(studyAll);
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
	 * 회원의 성실도를 반환한다.
	 * @throws FindException 
	 */
	@Override
	public int searchUserDeligence(String email) throws FindException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String studyStudySQL = null;
		try {
			conn = MyConnection.getConnection();
			studyStudySQL = "SELECT user_diligence FROM users WHERE user_email = ?";
			preStmt = conn.prepareStatement(studyStudySQL);
			preStmt.setString(1, email);
			rs = preStmt.executeQuery();
			rs.next();
			return rs.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	
	/**
	 * 회원의 성실도를 반영한다. (스터디 종료 성실도 결과 반영)
	 * @throws FindException 
	 */
	@Override
	public void setUserDeligence(StudyUserDTO studyUser) throws ModifyException {
		Connection conn = null;
		PreparedStatement preStmt = null;
		String studyStudySQL = null;
		try {
			conn = MyConnection.getConnection();
			studyStudySQL = "UPDATE users SET user_diligence = ? WHERE user_email = ?";
			preStmt = conn.prepareStatement(studyStudySQL);
			preStmt.setDouble(1, studyUser.getDiligence());
			preStmt.setString(2, studyUser.getEmail());
			preStmt.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModifyException(e.getMessage());
		} finally {
			MyConnection.close(preStmt, conn);
		}
	}
	
	/**
	 * 회원에게 스터디 종료에 따른 금액을 환급한다.
	 */
	@Override
	public void refundToUser(int studyId, String email, int prize) {
		Connection conn = null;
		PreparedStatement preStmt = null;
		CallableStatement calStmt = null;
		ResultSet rs = null;
		try {
			conn = MyConnection.getConnection();
			conn.setAutoCommit(false);
			// 지갑 관련 프로시저 스터디원에게 환급 실시   
			String procSQL = "{ call proc_wallet_prize(?, ?, ?) }";
			calStmt = conn.prepareCall(procSQL);
			calStmt.setString(1, email);
			calStmt.setInt(2, prize);
			calStmt.setInt(3, studyId);
			calStmt.executeUpdate();
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
			if (calStmt != null) {
				try {
					calStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
