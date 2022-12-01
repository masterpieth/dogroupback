package control;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.dto.StudyDTOBomi;
import com.my.dto.StudySubjectDTOBomi;
import com.my.dto.SubjectDTOBomi;
import com.my.exception.AddException;
import com.my.service.StudyService;
import com.my.service.UserService;

/**
 * 스터디를 Insert 하는 서블릿
 * @author NYK
 *
 */
@WebServlet("/studyInsert")
public class StudyInsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StudyService service;
	private UserService userService;

	@Override
	public void init() throws ServletException {
		super.init();
		ServletContext sc = getServletContext();
		String realPath = sc.getRealPath("/WEB-INF/repository.properties");
		userService = new UserService(realPath);
		service = new StudyService(realPath, userService);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> map111 = request.getParameterMap();
		StudyDTOBomi study = new StudyDTOBomi();
		study.setUserEmail(request.getParameter("userEmail"));
		study.setStudyTitle(request.getParameter("studyTitle"));
		study.setStudyFee(Integer.parseInt(request.getParameter("studyFee")));
		study.setStudySize(Integer.parseInt(request.getParameter("studySize")));
		study.setStudyCertification(Integer.parseInt(request.getParameter("studyCertification")));
		study.setStudyHomeworkPerWeek(Integer.parseInt(request.getParameter("studyHomeworkPerWeek")));
		study.setStudyDiligenceCutline(Integer.parseInt(request.getParameter("studyDiligenceCutline")));
		study.setStudyPaid(0);
		study.setStudyPostDate(new Date());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			study.setStudyStartDate((Date) format.parse(request.getParameter("studyStartDate")));
			study.setStudyEndDate((Date) format.parse(request.getParameter("studyStartDate")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		study.setStudyContent(request.getParameter("studyContent"));
		String[] subject = request.getParameterValues("subject");
		List<StudySubjectDTOBomi> subjectList = new ArrayList<>();
		for(String s : subject) {
			StudySubjectDTOBomi ssDTO = new StudySubjectDTOBomi();
			ssDTO.setSubject(new SubjectDTOBomi(s, null, null));
			subjectList.add(ssDTO);
		}
		study.setSubjects(subjectList);
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		try {
			service.openStudy(study);
			map.put("status", 1);
		} catch (AddException e) {
			e.printStackTrace();
			map.put("status", 0);
			map.put("msg", e.getMessage());
		}
		String jsonStr = mapper.writeValueAsString(map);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().print(jsonStr);
	}

}
