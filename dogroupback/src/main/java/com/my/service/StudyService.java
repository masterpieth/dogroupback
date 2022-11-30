package com.my.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.dto.StudyDTOBomi;
import com.my.dto.UserDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.RemoveException;
import com.my.repository.StudyRepository;
import com.my.repository.StudyRepositoryOracle;

public class StudyService {
	private StudyRepository repository;
	private UserService userService;
	
	public StudyService() {
		repository = new StudyRepositoryOracle();
	}
	
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
	 * 진행중인 스터디를 검색한다.
	 * 
	 * @param email 스터디ID
	 * @return 스터디 목록
	 * @throws FindException 진행중인 스터디를 찾지못하면 FindException발생한다.
	 */
	public List<StudyDTO> searchMyStudy(String email) throws FindException {
		return repository.selectStudyByEmail(email);
	}
    public HomeworkDTO searchMyStudyUserInfo(String email, int studyId) throws FindException {
        return repository.selectUserHomeworkByEmail(email, studyId);
    }
    
    
    public StudyDTOBomi searchStudyInfo(int studyId)throws FindException{
		return repository.selectStudy(studyId);
    	
    }
	/**
	 * 스터디를 개설한다. 돈이 없는 경우 개설에 실패한다.
	 * @param study
	 */
	public void openStudy(StudyDTO study) throws AddException {
		if(compareUserBalanceWithStudyFee(study.getStudyFee(), study.getUserEmail())) {
			repository.insertStudy(study);
		}
		throw new AddException("스터디를 개설하는데 실패했습니다.");
	}
	/**
	 * 스터디에 참여신청한다. 돈이 없는 경우 또는 성실도가 커트라인보다 낮은 경우 참여에 실패한다.
	 * @param user
	 * @param study
	 * @throws AddException
	 */
	public void joinStudy(UserDTO user, StudyDTO study) throws AddException {
		if(compareUserBalanceWithStudyFee(study.getStudyFee(), user.getEmail())
				&& compareUserDiligenceWithStudyDiligenceCutline(study.getStudyDiligenceCutline(), user.getEmail())) {
			repository.insertStudyUser(study, user.getEmail());
		}
		throw new AddException("스터디 참여에 실패했습니다");
	}
	/**
	 * 스터디 참여를 취소한다. 스터디 유저가 삭제되며 환불처리도 함께 진행된다.
	 * @param study
	 * @param email
	 * @throws RemoveException
	 */
	public void leaveStudy(StudyDTO study, String email) throws RemoveException {
		repository.deleteStudyUser(study, email);
	}
	/**
	 * 사용자의 잔액과 입장료를 비교하여 입장을 할 수 있는지 없는지를 반환한다.
	 * @return 스터디 입장료보다 잔액이 많으면 true / 스터디 입장료보다 잔액이 적어서 입장을 할 수 없으면 false
	 */
	private boolean compareUserBalanceWithStudyFee(int studyFee, String email) {
		try {
			userService = new UserService("repository.properties");
			UserDTO user = userService.searchUserInfo(email);
			int userBalance = user.getUserBalance();
			if(userBalance > studyFee) {
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
	private boolean compareUserDiligenceWithStudyDiligenceCutline(int studyDiligenceCutline, String email) {
		try {
			userService = new UserService("repository.properties");
			UserDTO user = userService.searchUserInfo(email);
			int userDiligence = user.getDiligence();
			if(studyDiligenceCutline < userDiligence) {
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
			Date todayUtilDate = (Date)formatter.parse(new Date().toString());
			Date created_at = new Date(todayUtilDate.getTime());
			repository.insertHomeworkByEmail(email, studyId, created_at);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("시스템 오류가 발생했습니다");
		}
	}
	/**
	 * Github 과제를 체크한다.
	 * @param email
	 * @param studyId
	 * @throws AddException
	 */
	public void checkMyGithubCommit(String email, int studyId) throws AddException {
		try {
			Date date = getGithubEventsDate(email);
			Date created_at = new Date(date.getTime());
			repository.insertHomeworkByEmail(email, studyId, created_at);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("시스템 오류가 발생했습니다");
		}
	}
	/**
	 * GithubEvent를 읽어온다.
	 * @param email			User의 이메일 정보
	 * @return Date			과제날짜
	 * @throws Exception
	 */
	private Date getGithubEventsDate(String email) throws FindException {
		String[] emailArr = email.split("@");
		String userId = emailArr[0];
		//username을 받아오기 위한 searchuser url
		String searchUserUrl = "https://api.github.com/search/users?q=" + userId;
		try {
			//username이 담겨있는 json data를 받아온다
			String searchUserResult = getGithubJsonDataToString(searchUserUrl);
			//JSON 정보 파싱
			ObjectMapper mapper = new ObjectMapper();
			Map<String,Object> searchUsermap = mapper.readValue(searchUserResult, new TypeReference<Map<String,Object>>(){});
			//username이 담겨있는 list
			List<Object> items = (List) searchUsermap.get("items");
			//username
			HashMap<String, Object> loginInfo = (HashMap<String, Object>) items.get(0);
			String username = (String) loginInfo.get("login");
			
			//username을 가지고 event를 받아오기 위한 url
			String userEventUrl = "https://api.github.com/users/" + username + "/events/public";
			//username의 event가 담겨있는 json data를 받아온다.
			String userEventResult = getGithubJsonDataToString(userEventUrl);
			List<Map<String,Object>> userEventmapList = mapper.readValue(userEventResult, new TypeReference<List<Map<String,Object>>>(){});
			int arrSize = userEventmapList.size();
			//오늘 날짜의 PushEvent, PullRequestEvent 를 검색하고, 없는 경우 AddException을 터뜨린다.
			for(int i=0; i<arrSize; i++) {
				Map<String, Object> object = userEventmapList.get(i);
				String type = (String) object.get("type");
				
				switch(type) {
				case "PushEvent":
				case "PullRequestEvent":
					String created_at = (String) object.get("created_at");
					created_at = created_at.replace("T", " ");
					created_at = created_at.replace("Z", "");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date eventUtilDate = (Date) formatter.parse(created_at);
					Date todayUtilDate = (Date) formatter.parse(created_at);
					
					if(eventUtilDate.equals(todayUtilDate)) {
						return eventUtilDate;
					}
				}
			}
			throw new FindException("이벤트를 찾을 수 없습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			throw new FindException("시스템 오류가 발생했습니다.");
		}
	}
	/**
	 * github Api 에 요청을 보내서 결과로 받아온 Json을 String 형태로 반환한다.
	 * @param url			요청을 보낼 주소
	 * @return api			결과값 String
	 * @throws FindException 
	 */
	private String getGithubJsonDataToString(String callUrl) throws FindException {
		URL url;
		try {
			//요청 연결
			url = new URL(callUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			//정보 읽어오기
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while((inputLine = bufferedReader.readLine()) != null) {
				sb.append(inputLine);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FindException("시스템 오류가 발생했습니다.");
		}
	}
}
