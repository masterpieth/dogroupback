package com.my.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.dto.HomeworkDTO;
import com.my.dto.PageBean;
import com.my.dto.StudyDTO;

import com.my.dto.StudyUserDTO;
import com.my.dto.UserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.RemoveException;
import com.my.repository.StudyRepository;


public class StudyService {
	private StudyRepository repository;
	private UserService userService;
	
	/**
	 * 서비스 생성자
	 */
	public StudyService(String propertiesFileName) {
		Properties env = new Properties();
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("study");	//클래스이름을 String 타입으로 찾아온것 
			Class<?> clazz = Class.forName(className); 		//객체생성 반환형이 object
			Object obj = clazz.getDeclaredConstructor().newInstance();
			repository = (StudyRepository)obj;
		} catch (IOException e) {
			e.printStackTrace();
		}	//연결된 자원을 읽는다. key = value  
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시연용 StudyService 생성자(추후삭제)
	 * @param propertiesFileName
	 */
	public StudyService(String propertiesFileName, UserService userService) {
		this.userService = userService;
		Properties env = new Properties();
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("study");	//클래스이름을 String 타입으로 찾아온것 
			Class<?> clazz = Class.forName(className); 		//객체생성 반환형이 object
			Object obj = clazz.getDeclaredConstructor().newInstance();
			repository = (StudyRepository)obj;
		} catch (IOException e) {
			e.printStackTrace();
		}	//연결된 자원을 읽는다. key = value  
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 스터디의 정보를 반환한다.
	 * @param studyId
	 * @return스터디 스터디의 기본 정보를 반환한다. 스터디의 현재 참여자수 + 스터디장의 성실도 + 스터디의 과목 정보
	 * @throws FindException
	 */
	public StudyDTO searchStudyInfo(int studyId) throws FindException {
		return repository.selectStudyByStudyId(studyId);
	}

	/**
	 * 스터디원의 상세정보를 조회한다.
	 * @param email   회원 email
	 * @param studyId 스터디 id
	 * @return StudyUserDTO(스터디 id, 과제 전체 제출 내역, 주차별 출석 인정 정보)
	 * @throws FindException
	 */
	public StudyUserDTO searchMyStudyUserInfo(String email, int studyId) throws FindException {
		StudyUserDTO user = new StudyUserDTO(studyId, repository.selectUserHomeworkByEmail(email, studyId), null);
		user = searchStudyUserHomeworkState(user);
		return user;
	}

	/**
	 * 
	 * 진행중인 스터디를 검색한다.
	 * 
	 * @param email 스터디ID
	 * @return 스터디 목록
	 * @throws FindException 진행중인 스터디를 찾지못하면 FindException발생한다.
	 */
	public List<StudyDTO> searchMyStudy(String email) throws FindException {
		return repository.selectStudyByEmail(email);
	}

	/**
	 * 스터디를 개설한다. 돈이 없는 경우 개설에 실패한다.
	 * 
	 * @param study
	 */
	public void openStudy(StudyDTO study) throws AddException {
		if (compareUserBalanceWithStudyFee(study.getStudyFee(), study.getUserEmail())) {
			repository.insertStudy(study);
		} else {
			throw new AddException("스터디를 개설하는데 실패했습니다.");
		}
	}

	/**
	 * 스터디에 참여신청한다. 돈이 없는 경우 또는 성실도가 커트라인보다 낮은 경우 참여에 실패한다.
	 * @param user
	 * @param study
	 * @throws AddException
	 */
	public void joinStudy(UserDTO user, StudyDTO study) throws AddException {
		if (compareUserBalanceWithStudyFee(study.getStudyFee(), user.getEmail())
				&& compareUserDiligenceWithStudyDiligenceCutline(study.getStudyDiligenceCutline(), user.getEmail())) {
			repository.insertStudyUser(study, user.getEmail());
		} else {
			throw new AddException("스터디 참여에 실패했습니다");
		}
	}

	/**
	 * 스터디 참여를 취소한다. 스터디 유저가 삭제되며 환불처리도 함께 진행된다.
	 * 
	 * @param study
	 * @param email
	 * @throws RemoveException
	 */
	public void leaveStudy(StudyDTO study, String email) throws RemoveException {
		repository.deleteStudyUser(study, email);
	}

	/**
	 * 사용자의 잔액과 입장료를 비교하여 입장을 할 수 있는지 없는지를 반환한다.
	 * 
	 * @return 스터디 입장료보다 잔액이 많으면 true / 스터디 입장료보다 잔액이 적어서 입장을 할 수 없으면 false
	 */
	private boolean compareUserBalanceWithStudyFee(int studyFee, String email) {
		try {
			UserDTO user = userService.searchUserInfo(email);
			int userBalance = user.getUserBalance();
			if (userBalance > studyFee) {
				return true;
			}
		} catch (FindException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 사용자의 성실도와 스터디의 성실도 커트라인을 비교하여 입장을 할 수 있는지 없는지를 반환한다.
	 * @return 스터디의 성실도 커트라인보다 사용자의 성실도가 높으면 true / 낮으면 false를 반환한다.
	 */
	private boolean compareUserDiligenceWithStudyDiligenceCutline(double studyDiligenceCutline, String email) {
		try {
			UserDTO user = userService.searchUserInfo(email);
			double userDiligence = user.getDiligence();
			if (studyDiligenceCutline < userDiligence) {
				return true;
			}
		} catch (FindException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 버튼형 출석 과제를 체크한다. Insert 가 안될 경우 예외를 발생시킨다.
	 * 
	 * @param email
	 * @param studyId
	 * @throws AddException
	 */
	public void checkTodayHomework(String email, int studyId) throws AddException {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date todayUtilDate = (Date) formatter.parse(new Date().toString());
			Date created_at = new Date(todayUtilDate.getTime());
			repository.insertHomeworkByEmail(email, studyId, created_at);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("시스템 오류가 발생했습니다");
		}
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
			Date created_at = getGithubEventsDate(email);
			repository.insertHomeworkByEmail(email, studyId, created_at);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("시스템 오류가 발생했습니다");
		}
	}

	/**
	 * GithubEvent를 읽어온다.
	 * 
	 * @param email User의 이메일 정보
	 * @return Date 과제날짜
	 * @throws Exception
	 */
	private Date getGithubEventsDate(String email) throws FindException {
		String[] emailArr = email.split("@");
		String userId = emailArr[0];
		// username을 받아오기 위한 searchuser url
		String searchUserUrl = "https://api.github.com/search/users?q=" + userId;
		try {
			// username이 담겨있는 json data를 받아온다
			String searchUserResult = getGithubJsonDataToString(searchUserUrl);
			// JSON 정보 파싱
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> searchUsermap = mapper.readValue(searchUserResult,
					new TypeReference<Map<String, Object>>() {
					});
			// username이 담겨있는 list
			List<Object> items = (List) searchUsermap.get("items");
			// username
			HashMap<String, Object> loginInfo = (HashMap<String, Object>) items.get(0);
			String username = (String) loginInfo.get("login");

			// username을 가지고 event를 받아오기 위한 url
			String userEventUrl = "https://api.github.com/users/" + username + "/events/public";
			// username의 event가 담겨있는 json data를 받아온다.
			String userEventResult = getGithubJsonDataToString(userEventUrl);
			List<Map<String, Object>> userEventmapList = mapper.readValue(userEventResult,
					new TypeReference<List<Map<String, Object>>>() {
					});
			int arrSize = userEventmapList.size();
			// 오늘 날짜의 PushEvent, PullRequestEvent 를 검색하고, 없는 경우 AddException을 터뜨린다.
			for (int i = 0; i < arrSize; i++) {
				Map<String, Object> object = userEventmapList.get(i);
				String type = (String) object.get("type");

				switch (type) {
				case "PushEvent":
				case "PullRequestEvent":
					String created_at = (String) object.get("created_at");
					created_at = created_at.replace("T", " ");
					created_at = created_at.replace("Z", "");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date eventUtilDate = (Date) formatter.parse(created_at);
					Date todayUtilDate = (Date) formatter.parse(new Date().toString());

					if (eventUtilDate.equals(todayUtilDate)) {
						return eventUtilDate;
					}
				}
			}
			throw new FindException("이벤트를 찾을 수 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException("시스템 오류가 발생했습니다.");
		}
	}

	/**
	 * github Api 에 요청을 보내서 결과로 받아온 Json을 String 형태로 반환한다.
	 * 
	 * @param url 요청을 보낼 주소
	 * @return api 결과값 String
	 * @throws FindException
	 */
	private String getGithubJsonDataToString(String callUrl) throws FindException {
		URL url;
		try {
			// 요청 연결
			url = new URL(callUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// 정보 읽어오기
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				sb.append(inputLine);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException("시스템 오류가 발생했습니다.");
		}
	}

	/**
	 * 스터디원 모두의 주차별 출석(유효 과제 상태)을 조회한다.
	 * 
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

			List<StudyUserDTO> homeworkTotalList = new ArrayList<>(); // 위의 맵을 리스트로 갖는다.

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
					// System.out.print(User.getEmail() + "님 : " + (i + 1) + "주차] : " + ((Arr[i] !=
					// 0) ? "출석 인정" : "출석 불인정") + "\t");
				}
				// System.out.println();
			}
			return homeworkTotalList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}
	}

	/**
	 * 스터디원 개인의 주차별 출석(유효 과제 상태)을 조회한다.
	 * 
	 * @param studyId
	 * @param email
	 * @return StudyUserDTO
	 * @throws FindException
	 */
	public StudyUserDTO searchStudyUserHomeworkState(StudyUserDTO user) throws FindException {
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
			int totalWeek = (int) Math.ceil(studyDays / 7); // 현시점에 총 제출해야할 횟수
			// System.out.println("제출해야 할 횟수:" + totalHomework);

			List<HomeworkDTO> homeworkList = user.getHomeworkList(); // 스터디의 전체 과제 내역을 가져온다.

			user.setCheckHomework(new int[totalWeek]);
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
				// System.out.print(User.getEmail() + "님 : " + (i + 1) + "주차] : " + ((Arr[i] !=
				// 0) ? "출석 인정" : "출석 불인정") + "\t");
			}
			user.setCheckHomework(arr);
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}
	}
	
	/**
	 * 검색 조건에 맞는 스터디의 리스트와 페이징버튼정보를 페이지빈으로 반환한다.
	 * @param currentPage 현재 페이지
	 * @param studyTitle 검색할 스터디 타이틀명
	 * @param studySize 검색할 스터디 정원
	 * @return PageBean<StudyDTO>
	 * @throws FindException
	 */
	public PageBean<StudyDTO> getPageBean(int currentPage, String studyTitle, int studySize) throws FindException {
		List<StudyDTO> list = searchStudy(currentPage, PageBean.CNT_PER_PAGE, studyTitle, studySize);
		int totalCnt = repository.studyCount(studyTitle, studySize);
		PageBean<StudyDTO> pageBean = new PageBean<>(currentPage, list, totalCnt);
		return pageBean;
	}
	
	/**
	 * 검색 조건에 맞는 스터디 리스트를 반환한다.
	 * @param currentPage 현재 페이지
	 * @param cntPerPage 페이당 목록 개수
	 * @param studyTitle 검색할 스터디 타이틀명
	 * @param studySize 검색할 스터디 정원
	 * @return List<StudyDTO>
	 * @throws FindException
	 */
	public List<StudyDTO> searchStudy(int currentPage, int cntPerPage, String studyTitle, int studySize) throws FindException {
		return repository.selectStudy(currentPage, cntPerPage, studyTitle, studySize);
	}

	/**
	 * 스터디 종료시 상금을 분배(일괄 정산)한다.
	 * @param study
	 * @throws FindException 
	 */
	public void distributePrizeMoney(int studyId) throws FindException {
		//0. 스터디 정보 가져오기 기본 정보 + 스터디장 userId
		StudyDTO study = repository.selectStudyByStudyId(studyId);
		//1. 스터디원 모두의 주차별 출석(유효 과제 상태)을 조회한다.
		List<StudyUserDTO> studyUsers = searchMyStudyHomeworkState(studyId);
		
		//2. 스터디의 진행 기간과 입장금액 정보를 가져온다.
		int studyFee = study.getStudyFee(); //현재 스터디의 입장 요금은 얼마인가?
		int howLongStudyWeek = study.getStudyFee(); //스터디의 입장요금은?
		
		//3. 스터디원의 달성률 계산 후 개인의 1차 환금액 스터디 누적 환금액를 구한다.
		int studyGatheredSize = study.getStudyGatheredSize(); //스터디 진행 인원 수는?
		int studyPrize = studyFee * studyGatheredSize; //우선 스터디의 누적 벌금은 스터디 입장금액 * 스터디 인원수로 계산한 후 스터디원의 1차 환급액을 참가하는 방식으로 계산한다.
		Map<String, Integer> studyUserRefundInfo = new HashMap<>(); //1차 환금액 정보를 담을 Map
		
		for(StudyUserDTO studyUser : studyUsers) {
			int[] checkHomework = studyUser.getCheckHomework(); //스터디원의 주차별 유효과제 제출 리스트를 가져온다.
			int validHomeworkCnt = Collections.frequency(Arrays.asList(checkHomework), 1); //스터디원의 유효 과제 제출 개수를 계산한다.
			
			double studyAchievementRate; //스터디원의 스터디 달성률을 계산한다. (유효과제 개수 / 진행 주차) 0~1 사이값
			if(validHomeworkCnt == 0 ) studyAchievementRate = 0;
			else studyAchievementRate = (double)validHomeworkCnt / howLongStudyWeek;	
			
			//금액의 최소 단위는 10원으로 한다.
			double studyUserRefund = (double)studyFee * (1-studyAchievementRate);//1차 환금액 : 1차로 스터디원의 벌금을 제외한 환금 금액을 계산한다.
			studyUserRefund = (int)(studyUserRefund + 9) / 10 * 10; // 1의 자릿수에서 올림
			studyPrize -= studyUserRefund; // 전체 studyPenalty에서 스터디원 개개인의 1차 환급액을 차감하여 공동 상금을 구한다.
			studyUserRefundInfo.put(studyUser.getEmail(), (int)studyUserRefund); //스터디원 email을 key로, 1차 환금액을 value로 저장한다.
		}
		//4. 1차 환금액과 스터디 누적 환금액을 n등분하여 스터디 유저에게 환급한다. 스터디의 STUDY_PAID 값을 TRUE(1)로 준다.
		String studyLeaderEmail = study.getStudyLeader().getEmail();
		int userPrize = (int)studyPrize/studyGatheredSize; //스터디 전체 상금을 스터디 인원으로 나눈 값(정수)
		int prizeMod = studyPrize%studyGatheredSize; //스터디 전체 상금을 나누고 남은 금액(정수)
		int userPrizeRound = (userPrize/10)*10;	//유저상금 1의자리 버림 연산
		for(StudyUserDTO studyUser : studyUsers) {
			String email = studyUser.getEmail();
			if(studyUser.getEmail().equals(studyLeaderEmail)) {
				//스터디장인 경우 자투리 금액을 추가로 가져간다. n등분한 유저상금 + 자투리 금액 + 스터디원들의 1의자리 버림 금액 액수
				userPrizeRound = userPrizeRound + prizeMod + (userPrize - userPrizeRound) * studyGatheredSize;
			}
			repository.refundToUser(studyId, email, studyUserRefundInfo.get(email) + userPrizeRound);
			//환금액의 소수점 계산을 어떻게 처리할 것인가? -> 스터디장이 자투리 금액을 가져간다.
		}
	}
	
	/**
	 * 스터디 종료시 스터디원들의 개인의 달성률에 따른 성실도를 반영한다.
	 * @param study
	 * @throws Exception 
	 */
	public void updateStudyUserDiligence(int studyId) throws Exception {
		//1. 스터디원 모두의 주차별 출석(유효 과제 상태)을 조회한다.
		List<StudyUserDTO> studyUsers = searchMyStudyHomeworkState(studyId);
		
		//2. 스터디의 성실도 정보를 가져온다.(최대 성실도 보상 값)
		int howLongStudyWeek = studyUsers.get(0).getHomeworkList().size(); //현재 스터디가 몇주 차인가?
		
		int studyDeligence; //스터디의 주차를 기준으로 보상 성실도 최대값 부여(6주 단위로 성실도 2추가, 최대 성실도는 10)
		if(howLongStudyWeek>=25) studyDeligence = 10;
		else if(howLongStudyWeek == 1) studyDeligence = 2;
		else studyDeligence = (howLongStudyWeek-1/6)*2 + 2;
		
		//3. 스터디원의 달성률 계산 후 성실도를 계산한다.
		for(StudyUserDTO studyUser : studyUsers) {
			String email = studyUser.getEmail();
			int[] checkHomework = studyUser.getCheckHomework(); //스터디원의 주차별 유효과제 제출 리스트를 가져온다.
			int validHomeworkCnt = Collections.frequency(Arrays.asList(checkHomework), 1); //스터디원의 유효 과제 제출 개수를 계산한다.
			
			double studyAchievementRate; //스터디원의 스터디 달성률을 계산한다. (유효과제 개수 / 진행 주차) 0~1 사이값
			if(validHomeworkCnt == 0 ) studyAchievementRate = 0;
			else studyAchievementRate = (double)validHomeworkCnt / howLongStudyWeek;	
			
			//스터디 기간(주차) 			w
			//스터디 성실도 보상 			d
			//스터디원의 스터디 달성율 		p
			//스터디 종료 시 성실도 보상 식 	d * (log(w){w*p)-0.5)
			double updateDeligeence = studyDeligence * ((Math.log(howLongStudyWeek * studyAchievementRate) / (double)Math.log(howLongStudyWeek)) - 0.5);
			int deligence = repository.searchUserDeligence(email); //현재 스터디원의 성실도 정보 가져오기
			
		//4. 스터디원의 성실도 부여
			studyUser.setDiligence(deligence + updateDeligeence); //현재 성실도에 스터디 종료 결과 성실도 보상 반영
			repository.setUserDeligence(studyUser);
		}
	}
	
	/**
	 * 스터디 종료시 필요한 작업을 수행한다.(상금과 성실도를 반영)
	 * @param study
	 * @throws Exception 
	 */
	public void endStudy(int studyId) throws Exception {
		distributePrizeMoney(studyId);
		updateStudyUserDiligence(studyId);
	}

}