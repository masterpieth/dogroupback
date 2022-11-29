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

	@Override
	public HomeworkDTO selectUserHomeworkByEmail(String userEmail, int studyId) throws FindException {
		List<Date> homeworkList = new ArrayList<Date>();
		String homeworListSQL = "select * from homework where " + "study_id = ? and user_email = ? "
				+ "and study_submit_dt >= (select study_start_date from study where study_id = ?)";
		try {
			conn = MyConnection.getConnection();
			preStmt = conn.prepareStatement(homeworListSQL);
			preStmt.setInt(1, studyId);
			preStmt.setString(2, userEmail);
			preStmt.setInt(3, studyId);
			rs = preStmt.executeQuery(); // 선택한 유저의 스터디 시작일 이후의 제출 과제 내역을 모두 가져온다.

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
