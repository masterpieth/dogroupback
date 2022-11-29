package com.my.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.dto.HomeworkDTO;
import com.my.dto.StudyDTO;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.repository.StudyRepository;

public class StudyService {
	private StudyRepository repository;

	public StudyService(String propertiesFileName) {
		Properties env = new Properties();
		try {
			env.load(new FileInputStream(propertiesFileName));
			String className = env.getProperty("study");
			Class clazz = Class.forName(className);
			Object obj = clazz.getDeclaredConstructor().newInstance();
			repository = (StudyRepository) obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
			java.util.Date todayUtilDate = formatter.parse(formatter.format(new java.util.Date()));
			Date created_at = new Date(todayUtilDate.getTime());
			repository.insertHomeworkByEmail(email, studyId, created_at);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException("시스템 오류가 발생했습니다");
		}
	}

	/**
	 * Github 과제를 체크한다. Insert 가 안될 경우 예외를 발생시킨다.
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
					java.util.Date todayUtilDate = formatter.parse(formatter.format(new java.util.Date()));

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

	public HomeworkDTO searchMyStudyUserInfo(String email, int studyId) throws FindException {
		return repository.selectUserHomeworkByEmail(email, studyId);
	}
}
