package com.my.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.dto.StudySubjectDTOBomi;
import com.my.dto.SubjectDTOBomi;
import com.my.dto.UserDTO;
import com.my.dto.StudyUserDTO;
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
	
	/**
	 * 스터디의 모든 과제 제출 내역을 리스트로 반환한다.
	 */
	@Override
	public List<HomeworkDTO> selectHomeworkByStudyId(int studyId) throws FindException {
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

	/** 스터디의 모든 정보를 반환한다.
	 * 해당 스터디 기본 정보 + 스터디의 현재 참여자수 + 스터디장의 성실도 + 스터디의 과목 정보 + 참여중인 스터디원들의 정보(유저 기본 정보 + 과제 제출)를 반환한다. 
	 */
	@Override
	public StudyDTOBomi selectStudy(int studyId) throws FindException {

		try {
			conn = MyConnection.getConnection();
			//------- 스터디 기본 정보, 스터디원 인원수, 스터디장의 성실도, 스터디의 과목 목록 SEARCH START -------
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

			StudyDTOBomi study;
			List<StudySubjectDTOBomi> studySubjectList = new ArrayList<>(); 
			
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
				SubjectDTOBomi subjectDToBomi = new SubjectDTOBomi(rs.getString("subject_code"), rs.getString("subject_name"), null);
				studySubjectDtoBomi.setSubject(subjectDToBomi);
				studySubjectList.add(studySubjectDtoBomi);

				while (rs.next()) {
					studySubjectDtoBomi = new StudySubjectDTOBomi();
					subjectDToBomi = new SubjectDTOBomi(rs.getString("subject_code"), rs.getString("subject_name"), null);
					studySubjectDtoBomi.setSubject(subjectDToBomi);
					studySubjectList.add(studySubjectDtoBomi);
				}
				study.setSubjects(studySubjectList);
				//------- 스터디 기본 정보, 스터디원 인원수, 스터디장의 성실도, 스터디의 과목 목록 SEARCH START -------
				
				//------- 스터디원 목록(+성실도) SEARCH START -------
				String selectStudyUserSQL = "select * from study_users join users using (user_email) where study_id = ?";
				preStmt = conn.prepareStatement(selectStudyUserSQL);
				preStmt.setInt(1, studyId);
				rs = preStmt.executeQuery();
				
				List<StudyUserDTO> studyUserList = new ArrayList<>();
				StudyUserDTO user;
				while(rs.next()) {
					user = new StudyUserDTO(studyId, selectUserHomeworkByEmail(rs.getString("user_email"), studyId), null);
					user.setDiligence(rs.getInt("user_diligence"));
					user.setEmail(rs.getString("user_email"));
					user.setName(rs.getString("user_name"));
					user.setPassword(rs.getString("user_password"));
					user.setStatus(rs.getInt("user_stauts"));
					user.setUserBalance(rs.getInt("user_balance"));
					studyUserList.add(user);
				}
				study.setStudyUsers(studyUserList);
				//------- 스터디원 목록(+성실도) SEARCH END -------
				return study;
			}
			throw new FindException();

		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}
	}
	
	/**
	 * 스터디 정보를 반환한다.
	 * @param studyId
	 * @return studyDTO 스터디 기본 정보, 스터디 유저 목록
	 * @throws FindException
	 */
	@Override
	public StudyDTO selectStudyByStudyId(int studyId) throws FindException {
		try {
			conn = MyConnection.getConnection();
			String selectStudyByEmailSQL = "SELECT * FROM study WHERE study_id= ?";

			preStmt = conn.prepareStatement(selectStudyByEmailSQL);
			preStmt.setInt(1, studyId);
			rs = preStmt.executeQuery();
			
			StudyDTO study;
			if (rs.next()) {
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

				study = new StudyDTO(studyId, userEmail, studyTitle, studySize, studyFee,
						studyCertification, studyDiligenceCutline, studyPostDate, studyStartDate, studyEndDate,
						studyHomeworkPerWeek, studyPaid, studyContent);

				return study;
			} else {
				throw new FindException("해당 스터디가 존재하지 않습니다.");
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	
	/* 
	 * 스터디 유저의(개인) 모든 과제 제출 내역을 반환한다.
	 * @parama userEmail 유저 email
	 * @param studyId 스터디 ID
	 * @return 과제 리스트
	 */
	@Override
	public List<HomeworkDTO> selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException {
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
			for(HomeworkDTO h : homeworkList) {
				System.out.println(h);
			}
			return homeworkList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		} finally {
			MyConnection.close(rs, preStmt, conn);
		}
	}
	
}
