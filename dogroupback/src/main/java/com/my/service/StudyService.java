package com.my.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.dto.StudyUserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.repository.StudyRepository;
import com.my.repository.StudyRepositoryOracle;

public class StudyService {
	private StudyRepository repository;

	public StudyService() {
		repository = new StudyRepositoryOracle();
//		String propertiesFileName = "repository.properties";	//3차수정을 외부파일에서 한다
//		Properties env = new Properties();					
//		try {
//			env.load(new FileInputStream(propertiesFileName));
//			String className = env.getProperty("study");	//클래스이름을 String 타입으로 찾아온것 
//			Class<?> clazz = Class.forName(className); 		//객체생성 반환형이 object
//			Object obj = clazz.getDeclaredConstructor().newInstance();	
//			repository = (StudyRepository)obj;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	//연결된 자원을 읽는다. key = value  
//		catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		}
	}

	public List<StudyDTO> searchMyStudy(String email) throws FindException {
		return repository.selectStudyByEmail(email);
	}

	/**
	 * 스터디의 모든 정보를 반환한다.
	 * @param studyId
	 * @return스터디 기본 정보 + 스터디의 현재 참여자수 + 스터디장의 성실도 + 스터디의 과목 정보 + 참여중인 스터디원들의 정보(유저
	 *            기본 정보 + 과제 제출 + 유효 출석일)
	 * @throws FindException
	 */
	public StudyDTOBomi searchStudyInfo(int studyId) throws FindException {
		return repository.selectStudy(studyId);
	}

	/**
	 * 스터디원의 상세정보를 조회한다.
	 * @param email		회원 email
	 * @param studyId	스터디 id
	 * @return StudyUserDTO(스터디 id, 과제 전체 제출 내역, 주차별 출석 인정 정보)
	 * @throws FindException
	 */
    public StudyUserDTO searchMyStudyUserInfo(String email, int studyId) throws FindException {
    	StudyUserDTO user = new StudyUserDTO(studyId, repository.selectUserHomeworkByEmail(email, studyId), null);
    	user = searchStudyUserHomeworkState(user);
		return user;
	}

	/**
	 * Github 과제를 체크한다.
	 * 
	 * @param email
	 * @param studyId
	 * @throws AddException
	 */
	public void checkMyGithubCommit(String email, int studyId) throws AddException {
		try {
			java.util.Date date = getGithubEventsDate(email);
			Date created_at = new Date(date.getTime());
			repository.insertHomeworkByEmail(email, studyId, created_at);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("시스템 오류가 발생했습니다");
		}
	}

	/**
	 * GithubEvent를 읽어온다.
	 * 
	 * @param email //User의 이메일 정보
	 * @return //과제날짜
	 * @throws Exception
	 */
	private java.util.Date getGithubEventsDate(String email) throws Exception {
		String[] emailArr = email.split("@");
		String userId = emailArr[0];
		String userUrl = "https://api.github.com/users/" + userId + "/events/public";
		URL url;
		try {
			// 요청 연결
			url = new URL(userUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// 정보 읽어오기
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				sb.append(inputLine);
			}

			// JSON 정보 파싱
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> map = mapper.readValue(sb.toString(),
					new TypeReference<List<Map<String, Object>>>() {
					});
			System.out.println();
			int arrSize = map.size();

			// 오늘 날짜의 PushEvent, PullRequestEvent 를 검색하고, 없는 경우 AddException을 터뜨린다.
			for (int i = 0; i < arrSize; i++) {
				Map<String, Object> object = map.get(i);
				String type = (String) object.get("type");

				switch (type) {
				case "PushEvent":
				case "PullRequestEvent":
					String created_at = (String) object.get("created_at");
					created_at = created_at.replace("T", " ");
					created_at = created_at.replace("Z", "");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date eventUtilDate = formatter.parse(created_at);
					java.util.Date todayUtilDate = formatter.parse(created_at);

					if (eventUtilDate.equals(todayUtilDate)) {
						return eventUtilDate;
					}
				}
			}
			throw new AddException("이벤트를 찾을 수 없습니다.");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		}
	}

	/**
	 * 스터디원 모두의 주차별 출석(유효 과제 상태)을 조회한다.
	 * @param studyId 스터디 ID
	 * @return List<StudyUserDTO> (studyId, email, checkHomework 정보)
	 * @throws FindException
	 */
	public List<StudyUserDTO> searchMyStudyHomeworkState(int studyId) throws FindException {
		try {
			StudyDTO study = repository.selectStudyByStudyId(studyId);
			Date strStartDt = study.getStudyStartDate(); // 스터디시작일
			Date strEndDt = study.getStudyEndDate(); // 스터디종료일
			java.util.Date now = new java.util.Date(); // 현재일자
			java.util.Date standardDate = (now.before(strEndDt)) ? now : strEndDt; // 기준일자
			int HomeworkPerWeek = study.getStudyHomeworkPerWeek(); // 주당 과제 제출 횟수
			long diff = (standardDate.getTime() - strStartDt.getTime()); // 기준일자 스터디 시작일의 시간차이
			TimeUnit time = TimeUnit.DAYS; // 시간차이를 날짜로 변환
			double studyDays = (double) time.convert(diff, TimeUnit.MILLISECONDS);
			int totalHomework = (int) Math.ceil(studyDays / 7); // 현시점에 총 제출해야할 횟수
			//System.out.println("제출해야 할 횟수:" + totalHomework);

			List<StudyUserDTO> homeworkTotalList = new ArrayList<>(); // 위의 맵을 리스트로 같는다.
			StudyUserDTO user = null;
			String old_user_email = "";
			List<HomeworkDTO> homeworkList = repository.selectHomeworkByStudyId(studyId); // 스터디의 전체 과제 내역을 가져온다.

			for (HomeworkDTO hw : homeworkList) {
				String hwEmail = hw.getUserEmail();
				if (!old_user_email.equals(hwEmail)) {
					user = new StudyUserDTO(studyId, null, new int[totalHomework]);
					user.setEmail(hwEmail);
					homeworkTotalList.add(user);
					old_user_email = hwEmail;
				}
				int[] arr = user.getCheckHomework();
				Date submitDt = hw.getStudySubmitDt(); // 제출일
				long diff1 = (submitDt.getTime() - strStartDt.getTime()); // 과제 제출일과 스터디 시작일의 시간차이
				TimeUnit time1 = TimeUnit.DAYS;
				int index = (int) time1.convert(diff1, TimeUnit.MILLISECONDS) / 7;
				arr[index]++;
			}

			for (StudyUserDTO User : homeworkTotalList) {
				int[] Arr = User.getCheckHomework();
				for (int i = 0; i < Arr.length; i++) {
					Arr[i] = (Arr[i] >= HomeworkPerWeek) ? 1 : 0; 
					//System.out.print(User.getEmail() + "님 : " + (i + 1) + "주차] : " + ((Arr[i] != 0) ? "출석 인정" : "출석 불인정")  + "\t");
				}
				//System.out.println();
			}
			return homeworkTotalList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}
	}

	/**
	 * 스터디원 개인의 주차별 출석(유효 과제 상태)을 조회한다.
	 * @param studyId
	 * @param email
	 * @return StudyUserDTO
	 * @throws FindException
	 */
	public StudyUserDTO searchStudyUserHomeworkState(StudyUserDTO user) throws FindException{
		try {
			int studyId = user.getStudyId();
			StudyDTO study = repository.selectStudyByStudyId(studyId);
			Date strStartDt = study.getStudyStartDate(); // 스터디시작일
			Date strEndDt = study.getStudyEndDate(); // 스터디종료일
			java.util.Date now = new java.util.Date(); // 현재일자
			java.util.Date standardDate = (now.before(strEndDt)) ? now : strEndDt; // 기준일자
			int HomeworkPerWeek = study.getStudyHomeworkPerWeek(); // 주당 과제 제출 횟수
			long diff = (standardDate.getTime() - strStartDt.getTime()); // 기준일자 스터디 시작일의 시간차이
			TimeUnit time = TimeUnit.DAYS; // 시간차이를 날짜로 변환
			double studyDays = (double) time.convert(diff, TimeUnit.MILLISECONDS);
			int totalHomework = (int) Math.ceil(studyDays / 7); // 현시점에 총 제출해야할 횟수
			//System.out.println("제출해야 할 횟수:" + totalHomework);

			List<HomeworkDTO> homeworkList = user.getHomeworkList(); // 스터디의 전체 과제 내역을 가져온다.

			user.setCheckHomework(new int[totalHomework]);
			int[] arr = user.getCheckHomework();
			for (HomeworkDTO hw : homeworkList) {
				Date submitDt = hw.getStudySubmitDt(); // 제출일
				long diff1 = (submitDt.getTime() - strStartDt.getTime()); // 과제 제출일과 스터디 시작일의 시간차이
				TimeUnit time1 = TimeUnit.DAYS;
				int index = (int) time1.convert(diff1, TimeUnit.MILLISECONDS) / 7;
				arr[index]++;
			}
			
			for (int i = 0; i < arr.length; i++) {
				arr[i] = (arr[i] >= HomeworkPerWeek) ? 1 : 0; 
				//System.out.print(User.getEmail() + "님 : " + (i + 1) + "주차] : " + ((Arr[i] != 0) ? "출석 인정" : "출석 불인정")  + "\t");
			}

			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws FindException {
		StudyService service = new StudyService();
	}
}