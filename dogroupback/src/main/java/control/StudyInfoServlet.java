package control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.dto.StudyDTOBomi;
import com.my.exception.FindException;
import com.my.service.StudyService;

/**
 * 스터디 상세정보를 반환하는 Servlet
 * @author NYK
 *
 */
@WebServlet("/studyInfo")
public class StudyInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StudyService service;

	@Override
	public void init() throws ServletException {
		super.init();
		ServletContext sc = getServletContext();
		String realPath = sc.getRealPath("/WEB-INF/repository.properties");
		service = new StudyService(realPath);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//CORS해결방법
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		//요청전달데이터 얻기
		int studyId = Integer.parseInt(request.getParameter("studyId"));
		
		//JSON문자열 얻기
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object>map = new HashMap<>();
		try {
			StudyDTOBomi study = service.searchStudyInfo(studyId);
			map.put("status", 1);
			map.put("studyInfo", study);
		} catch (FindException e) {
			e.printStackTrace();
			map.put("status", 0);
			map.put("msg", e.getMessage());
		}
		String jsonStr = mapper.writeValueAsString(map);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().print(jsonStr);
	}

}
