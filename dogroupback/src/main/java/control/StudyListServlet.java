package control;

import java.io.IOException;
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
import com.my.dto.StudyDTO;
import com.my.exception.FindException;
import com.my.service.StudyService;

/**
 * 스터디 리스트를 반환하는 Servlet
 * @author NYK
 *
 */
@WebServlet("/studylist")
public class StudyListServlet extends HttpServlet {
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
		
		//JSON문자열 얻기
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object>map = new HashMap<>();
		try {
			List<StudyDTO> result = service.searchMyStudy("user1@gmail.com");
			map.put("status", 1);
			map.put("myStudyList", result);
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
