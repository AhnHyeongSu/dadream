package com.dd.dealing.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dd.dealing.service.DealingService;
import com.dd.dealing.vo.BoardVO;
import com.dd.dealing.vo.DealingVO;
import com.dd.dealing.vo.JjimVO;
import com.dd.dealing.vo.KakaoLoginVO;
import com.dd.dealing.vo.MemberVO;
import com.dd.dealing.vo.NoticeVO;
import com.dd.dealing.vo.ReplyVO;
import com.dd.dealing.vo.ReportVO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.nurigo.java_sdk.api.Message;

@Controller("dealingController")
public class DealingControllerImpl implements DealingController {

	private static final String DEALING_IMAGE_REPO = "E:\\spring\\ahnhyeongsu\\dadream\\src\\main\\resources\\static\\dealing";
	private static final String BOARD_IMAGE_REPO = "E:\\spring\\ahnhyeongsu\\dadream\\src\\main\\resources\\static\\board";
	private static final String NOTICE_IMAGE_REPO = "E:\\spring\\ahnhyeongsu\\dadream\\src\\main\\resources\\static\\notice";

	@Autowired
	private DealingService dealingService;
	@Autowired
	private MemberVO memberVO;
	@Autowired
	private KakaoLoginVO kakaoLoginVO;
	@Autowired
	private DealingVO dealingVO;

	/* ??????????????? */
	@Override
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String MAIN(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "/dealingmain";
	}

	/* ???????????? ?????? */
	@Override
	@RequestMapping(value = "/userjoin.do", method = RequestMethod.GET)
	public String userjoin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		return viewName;
	}

	/* ???????????? ??? */
	@Override
	@RequestMapping(value = "/userform.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView userform(@RequestParam("memberjoin") String memberjoin, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String viewName = (String) request.getAttribute("viewName");
		mav.addObject("memberjoin", memberjoin);
		mav.setViewName(viewName);
		return mav;
	}

//	????????? ????????????
	@Override
	@ResponseBody
	@RequestMapping(value = "/idcheck.do", method = { RequestMethod.POST })
	public Map<String, Object> idcheck(@RequestBody Map<String, Object> body) throws Exception {
		int result = 0;
		result = dealingService.idcheck((String) body.get("user_Id"));
		System.out.println((String) body.get("user_Id"));
		Map<String, Object> map = new HashMap<>();
		System.out.println(result);
		map.put("resultCode", result);

		return map;
	}

//	???????????? ?????? 
	@Override
	@RequestMapping(value = "/addMember.do", method = RequestMethod.POST)
	public ModelAndView addMember(MemberVO member, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		int result = 0;
		result = dealingService.addMember(member);
		ModelAndView mav = new ModelAndView("redirect:/formcheck.do");
		return mav;
	}

//	???????????? ??????
	@Override
	@RequestMapping(value = "/formcheck.do", method = RequestMethod.GET)
	public String uformcheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return viewName;
	}

//	????????? ??????
	@Override
	@RequestMapping(value = "/phoneCheck.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> phoneCheck(HttpServletRequest req, @RequestBody String phone) throws Exception {
//		???????????? ????????? api ??? 
		String api_Key = "NCS1HCKPZLWBLHYF";
		String api_Secret = "K36DHC51VMA3TXP9QANEU6CTUYPFKX9Q";
		Message coolsms = new Message(api_Key, api_Secret);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(phone);
		String phoneNum = (String) jsonObject.get("phone");
		System.out.println("phone " + phoneNum);

		HashMap<String, String> set = new HashMap<String, String>();
//		????????????
		String random = "";
		for (int i = 0; i < 4; i++) {
			int randomnum = (int) Math.floor(Math.random() * 10) + 0;
			random += randomnum;
		}
		System.out.println(random);

		set.put("to", phoneNum);// ????????????
		set.put("from", "01097402124"); // ????????????
		set.put("type", "SMS"); // ????????????
		set.put("text", "DaDream ?????????. ??????????????? [ " + random + " ] ?????????");// ????????????

		JSONObject result = (JSONObject) coolsms.send(set);// ?????????&??????????????????
		System.out.println(result);

		Map<String, Object> map = new HashMap<>();
		map.put("res", random);
		return map;
	}

	/* ???????????? */
	@Override
	@RequestMapping(value = "/removeMember.do", method = RequestMethod.GET)
	public ModelAndView removeMem(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		MemberVO member = (MemberVO) session.getAttribute("member");
		String user_Id = member.getUser_Id();
		System.out.println("????????? ??????????????? : " + user_Id);
		ModelAndView mav = new ModelAndView();
		dealingService.removeMem(user_Id);
		session.removeAttribute("member");
		session.removeAttribute("isLogOn");
		mav.setViewName("redirect:/dealingmain.do");

		return mav;
	}

	/* ??????????????? ???????????? ?????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/infoCheck.do", method = RequestMethod.POST)
	public Map<String, Object> infoCheck(@RequestBody MemberVO member, HttpServletRequest request) throws Exception {

		int result = 0;
		result = dealingService.infoCheck(member);
		System.out.println("????????? : " + result);
		Map<String, Object> map = new HashMap<>();
		map.put("userCheck", result);

		return map;
	}

	/* ???????????? ?????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/memberMod.do", method = RequestMethod.POST)
	public Map<String, Object> memberMod(@RequestBody MemberVO member, HttpServletRequest request) throws Exception {
		int result = 0;
		result = dealingService.memberMod(member);
		System.out.println("????????? : " + result);
		Map<String, Object> map = new HashMap<>();
		map.put("check", result);

		return map;
	}

	/* ???????????? ?????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/pwdCheck.do", method = RequestMethod.POST)
	public Map<String, Object> pwdCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String user_Id = request.getParameter("user_Id");
		String user_Pwd = request.getParameter("pwdCheck");

		System.out.println("user_Pwd : " + user_Pwd);
		System.out.println("user_Id : " + user_Id);

		int result = 0;
		result = dealingService.pwdCheck(user_Pwd, user_Id);
		System.out.println("????????? : " + result);
		Map<String, Object> map = new HashMap<>();
		map.put("userCheck", result);

		return map;
	}

	/* ????????? */
	@Override
	@RequestMapping(value = "/login.do", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return viewName;
	}

	// ??????????????????
	@Override
	@RequestMapping(value = "/kakaologin.do", method = RequestMethod.GET)
	public String kakaologin(@RequestParam("code") String token, RedirectAttributes rAttr, Model model,
			HttpServletRequest request) throws IOException, ParseException, Exception {
		System.out.println("??????: " + token);

		Map<String, String> kakaoreq = new HashMap<>();
		kakaoreq.put("grant_type", "authorization_code");
		kakaoreq.put("client_id", "35aa1216a2a526e324dd20cbbf64bc06");
		kakaoreq.put("redirect_uri", "http://localhost:8080/kakaologin.do");
		kakaoreq.put("code", token);
//			==================================
//				1?????? ?????? http?????? ???????????? ?????????

		String kakaoURL = "https://kauth.kakao.com/oauth/token";
		URL url = new URL(kakaoURL);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST"); // post ??????????????? true/ ???????????? ?????????????????? ???
		con.setDoOutput(true);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

		StringBuilder sb = new StringBuilder();
		sb.append("grant_type=authorization_code"); // ???????????? ???
		sb.append("&client_id=35aa1216a2a526e324dd20cbbf64bc06");
		sb.append("&redirect_uri=http://localhost:8080/kakaologin.do");
		sb.append("&code=" + token);

		bw.write(sb.toString());// ??????
		bw.flush();// ?????? ??????
		int responsecode = con.getResponseCode();
		System.out.println("responsecode :" + responsecode);

		// ?????? ?????? ??????????????? JSON ?????? ?????????
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = "";
		String result = "";

		while ((line = br.readLine()) != null) {

			result += line;
		}
		System.out.println("responseBody : " + result);

		// ?????? ????????? Gson ??????
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(result);
		System.out.println("?????? ????????? : " + element);

		String access_token = element.getAsJsonObject().get("access_token").getAsString();
		String refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();
		System.out.println("??????????????? : " + access_token);
		System.out.println("?????????????????? : " + refresh_token);

		br.close();
		bw.close();

//			==========?????? ?????? =================
		String userInfo = "https://kapi.kakao.com/v2/user/me";
		Map<String, Object> user = new HashMap<>();
//			=========================================
//			/* 2???????????? Jsoup "url" ?????? HTMl ??? ???????????? */
//			==========================================
		Response response = Jsoup.connect(userInfo).method(Method.GET)
				.header("Authorization", " Bearer " + access_token).ignoreHttpErrors(true).ignoreContentType(true)
				.execute();
		System.out.println(response.body());
//			==========================================
//			?????? ?????????  Gson, json-simple ??????
		JsonParser parser1 = new JsonParser();
		JsonElement element1 = parser.parse(response.body());
		System.out.println("?????? ?????? ????????? : " + element1);
		JsonObject properties = element1.getAsJsonObject().get("properties").getAsJsonObject();
		JsonObject kakao_account = element1.getAsJsonObject().get("kakao_account").getAsJsonObject();
		String id = element1.getAsJsonObject().get("id").getAsString();
		System.out.println("?????? ID : " + id);
		System.out.println("?????? properties :" + properties);
		System.out.println("?????? kakao_account :" + kakao_account);

		String nickname = properties.getAsJsonObject().get("nickname").getAsString();
//		String email = kakao_account.getAsJsonObject().get("email").getAsString();
		String Id = element1.getAsJsonObject().get("id").getAsString();
		System.out.println("ID :" + Id);
		System.out.println("nickname : " + nickname);
//		System.out.println("email : " + Id);
		user.put("user_Id", Id);
		user.put("user_Level", 4);
		user.put("user_Name", nickname);
//			????????? ???????????? ?????? ?????? 
		int info = 0;
		info = dealingService.kakaologin(user);

		kakaoLoginVO = dealingService.kakaoresult(user);
//		String user_Name = email;
		if (kakaoLoginVO != null) {
			HttpSession session = request.getSession();
			session.setAttribute("member", kakaoLoginVO);
			session.setAttribute("isLogOn", true);
			return ("redirect:/dealingmain.do");
		}
		rAttr.addAttribute("result", "loginFailed");
		return ("redirect:/login.do");

	}

//	????????? ??????
	@Override
	@RequestMapping(value = "/logincheck.do", method = RequestMethod.POST)
	public ModelAndView logincheck(@ModelAttribute("member") MemberVO member, RedirectAttributes rAttr,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		memberVO = dealingService.login(member);
		if (memberVO != null) {
			HttpSession session = request.getSession();

			session.setAttribute("member", memberVO);
			session.setAttribute("isLogOn", true);
			mav.setViewName("redirect:/dealingmain.do");
		} else {
			rAttr.addAttribute("result", "loginFailed");
			mav.setViewName("redirect:/login.do");
		}
		return mav;
	}

//	????????????
	@Override
	@RequestMapping(value = "/logout.do", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("member");
		session.removeAttribute("isLogOn");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/dealingmain.do");
		return mav;
	}

	/* ??????????????? */
	@Override
	@RequestMapping(value = "/mypage.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView mypage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		String viewName = (String) request.getAttribute("viewName");
		MemberVO member = (MemberVO) session.getAttribute("member");
		String user_Id = member.getUser_Id();
		String user_Pwd = member.getUser_Pwd();
		System.out.println("??????????????? : " + user_Id);
		MemberVO members = dealingService.members(user_Id);
		List<ReportVO> myReport = new ArrayList<ReportVO>();
		List<DealingVO> myDealing = new ArrayList<DealingVO>();
		List<JjimVO> myJjim = new ArrayList<JjimVO>();
		List<BoardVO> myboardList = new ArrayList<BoardVO>();
		myReport = dealingService.myReport(user_Id);
		myDealing = dealingService.myDealing(user_Id);
		myJjim = dealingService.myJjim(user_Id);
		myboardList = dealingService.myboardList(user_Id);
		System.out.println("??? ?????? : " + myJjim);
		mav.addObject("myReport", myReport);
		mav.addObject("myDealing", myDealing);
		mav.addObject("myJjim", myJjim);
		mav.addObject("user_Pwd", user_Pwd);
		mav.addObject("user_Id", user_Id);
		mav.addObject("members", members);
		mav.addObject("myboardList", myboardList);
		mav.setViewName(viewName);
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return mav;
	}

	/* ????????? ???????????? */
	@Override
	@RequestMapping(value = "/dealingmain.do", method = RequestMethod.GET)
	public String dealingmain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return viewName;
	}

	/* ???????????? */
	@Override
	@RequestMapping(value = "/dealing/gongsilCenter.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String gongsilCenter(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("??????" + viewName);
		return viewName;
	}

	/* ?????? ?????? */
	@Override
	@RequestMapping(value = "/gongsilSearch.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public List<DealingVO> gongsilSearch(@RequestParam(required = false) List<String> dl_Form,
			@RequestParam(required = false, defaultValue = "") String dl_Address, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (dl_Form != null) {
			for (String str : dl_Form) {
				System.out.println(str);
			}
		}
		System.out.println(dl_Form.size());

		Map<String, Object> gsMap = new HashMap<String, Object>();
		gsMap.put("gsList", dl_Form);
		gsMap.put("dl_Address", dl_Address);

		List<DealingVO> gsResult = dealingService.gongsilSearch(gsMap);
		return gsResult;
	}

	/* ???????????? ??? ???????????? */
	@Override
	@RequestMapping(value = "/dealing/modDealing.do", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView modDealing(@RequestParam("modDl_Num") int dl_Num, @RequestParam("modDlUser_Id") String user_Id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("????????? ??? ?????? : " + dl_Num + ", " + user_Id);
		Map<String, Object> modDlmap = new HashMap<String, Object>();
		modDlmap.put("user_Id", user_Id);
		modDlmap.put("dl_Num", dl_Num);
		DealingVO modDealingVO = dealingService.modDealing(modDlmap);
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.addObject("modDealing", modDealingVO);
		mav.setViewName(viewName);

		return mav;
	}

	/* ???????????? */
	@Override
	@RequestMapping(value = "/dealing/modDealing2.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modDealing2(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		Map<String, Object> dealingMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			dealingMap.put(name, value);
		}
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		String imageFileName = up(multipartRequest);
		dealingMap.put("dl_Image", imageFileName);
		String dl_Num = (String) dealingMap.get("dl_Num");
		String message = null;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		try {
			dealingService.modDealing2(dealingMap);
//			if (imageFileName != null && imageFileName.length() != 0) {
//				File srcFile = new File(DEALING_IMAGE_REPO + "\\" + user_Id + "\\" + imageFileName);
//				File destDir = new File(DEALING_IMAGE_REPO + "\\" + dl_Num);
//				FileUtils.moveFileToDirectory(srcFile, destDir, true);

			// if (srcFile != null) {
			String originalFileName = (String) dealingMap.get("originalFileName");
			File oldFile = new File(DEALING_IMAGE_REPO + "\\" + user_Id + "\\" + originalFileName);
			oldFile.delete();
			// }
			// }
			message = "<script>";
			message += "alert('??????????????? ?????????????????????.');";
			message += "location.href='" + multipartRequest.getContextPath() + "/dealingmain.do'";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(DEALING_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();

			message = "<script>";
			message += "alert('??????????????? ??????');";
			message += "location.href='" + multipartRequest.getContextPath() + "/dealingmain.do'";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	/* ???????????? */
	@Override
	@RequestMapping(value = "/dealing/deleteDealing.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity deleteDealing(@RequestParam("delDl_Num") int dl_Num,
			@RequestParam("delDlUser_Id") String user_Id, @RequestParam("delDl_Image") String dl_Image,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("????????? ??? ?????? : " + dl_Num + ", " + user_Id);
		HttpSession session = request.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String verify_Id = memberVO.getUser_Id();
		response.setContentType("text/html; charset=utf-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			if (verify_Id.equals(user_Id)) {
				dealingService.deleteDealing(dl_Num);
				File destDir = new File(DEALING_IMAGE_REPO + "\\" + user_Id + "\\" + dl_Image);
				destDir.delete();

				message = "<script>";
				message += "alert('??????????????? ?????????????????????.');";
				message += "location.href='" + request.getContextPath() + "/dealingmain.do'";
				message += "</script>";
				resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			message = "<script>";
			message += "alert('?????? ????????? ????????????.');";
			message += "location.href='" + request.getContextPath() + "/dealingmain.do'";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	/* ???????????? */
	@Override
	@RequestMapping(value = "/dealingform.do", method = RequestMethod.GET)
	public String dealingform(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return viewName;
	}

	/* ????????? ?????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/jjimCheck.do", method = { RequestMethod.GET, RequestMethod.POST })
	public int jjimCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		MemberVO member = (MemberVO) session.getAttribute("member");
		String user_Id = member.getUser_Id();
		String dl_Num1 = request.getParameter("dl_Num");
		int dl_Num = Integer.parseInt(dl_Num1);
		System.out.println("??? ?????? : " + user_Id + ", " + dl_Num);
		Map<String, Object> jjimMap = new HashMap<String, Object>();
		jjimMap.put("user_Id", user_Id);
		jjimMap.put("dl_Num", dl_Num);
		int result = 0;
		result = dealingService.jjimCheck(jjimMap);

		return result;

	}

	/* ????????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/jjim.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String jjim(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		MemberVO member = (MemberVO) session.getAttribute("member");
		String user_Id = member.getUser_Id();
		String dl_Num1 = request.getParameter("dl_Num");
		int dl_Num = Integer.parseInt(dl_Num1);
		System.out.println("??? ?????? : " + user_Id + ", " + dl_Num);
		Map<String, Object> jjimMap = new HashMap<String, Object>();
		jjimMap.put("user_Id", user_Id);
		jjimMap.put("dl_Num", dl_Num);
		dealingService.jjim(jjimMap);

		return null;

	}

	/* ??? ?????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/jjimRemove.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String jjimRemove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		MemberVO member = (MemberVO) session.getAttribute("member");
		String user_Id = member.getUser_Id();
		String dl_Num1 = request.getParameter("dl_Num");
		int dl_Num = Integer.parseInt(dl_Num1);
		System.out.println("??? ?????? : " + user_Id + ", " + dl_Num);
		Map<String, Object> jjimMap = new HashMap<String, Object>();
		jjimMap.put("user_Id", user_Id);
		jjimMap.put("dl_Num", dl_Num);
		dealingService.jjimRemove(jjimMap);

		return null;

	}

	/* ???????????? DB?????? */
	@Override
	@RequestMapping(value = "/addNewdealing.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity dealingpost(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> dealingMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
//			System.out.println(name);
			String value = multipartRequest.getParameter(name);
//			System.out.println(value);
			dealingMap.put(name, value);
		}

		String imageFileName = up(multipartRequest);
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		System.out.println("????????? ?????? ?????? : " + imageFileName);
		dealingMap.put("user_Id", user_Id);
		dealingMap.put("dl_Image", imageFileName);
		dealingMap.put("dl_Views", 1);
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-Type", "text/html;charset=utf-8");

		try {
			int result = dealingService.addNewdealing(dealingMap);
//			if (imageFileName != null && imageFileName.length() != 0) {
//				File srcFile = new File(PRODUCT_IMAGE_REPO + "\\" + imageFileName);
//				File desDir = new File(PRODUCT_IMAGE_REPO);
//			????????? ?????? ????????? ??????????????? ?????? moveFileToDirectory
//				FileUtils.moveFileToDirectory(srcFile, desDir, true);
//			}
			message = "<script>";
			message += "alert('??????????????? ?????????????????????.');";
			message += "location.href='" + multipartRequest.getContextPath() + "/dealingmain.do'";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(DEALING_IMAGE_REPO + "\\" + "imageFileName");
			srcFile.delete();

			message = "<script>";
			message += "alert('?????? ?????? ?????????');";
//			message += "location.href='" + multipartRequest.getContextPath() + "location.reload();'";
			message += "";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;

	}

	/* ?????? ???????????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/dealingview.do", method = RequestMethod.GET)
	public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dl_Num1 = request.getParameter("dl_Num");
		int dl_Num = Integer.parseInt(dl_Num1);
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("dl_Num : " + dl_Num);
		dealingService.viewCount(dl_Num);
		ModelAndView mav = new ModelAndView();
		DealingVO DealingContents = dealingService.getDealingContents(dl_Num);
		mav.addObject("DealingContents", DealingContents);
		mav.setViewName(viewName);
		return mav;
	}

	/* ????????? ????????? */
	@Override
	@RequestMapping("/call.do")
	public String call(Model model) {
		return "/dealing/call";
	}

	/* ???????????? */
	@Override
	@RequestMapping(value = "/dealingmod.do", method = RequestMethod.GET)
	public String dealingmod(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return viewName;
	}

	/* ?????? ??????,?????? */
	@Override
	@RequestMapping("/modanddel.do")
	public String modanddel(Model model) {
		return "/dealing/modanddel";
	}

	/* ????????? */
	@Override
	@RequestMapping(value = "/map.do", method = RequestMethod.GET)
	public ModelAndView map(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		String dl_Form = (String) request.getParameter("dl_Form");
		String dl_Address = (String) request.getParameter("dl_Address");
		// ?????? ?????? ????????? ??????
		System.out.println("dl_Address : " + dl_Address);
		// List<DealingVO> allListdealing = dealingService.allListdealing(dl_Form);
		ModelAndView mav = new ModelAndView();
		mav.addObject("dl_Address", dl_Address);
		mav.addObject("dl_Form", dl_Form);
		// mav.addObject("allListdealing", allListdealing);
		mav.setViewName(viewName);
		return mav;
	}

	/* ?????? ??? ?????? */
	@Override
	@RequestMapping(value = "/showMap.do", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity showMap(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		// String dl_Lat = request.getParameter("dl_Lat");
		// String dl_Lng = request.getParameter("dl_Lng");
		String dl_Form = request.getParameter("dl_Form");
		String dl_Address = request.getParameter("dl_Address");

		// System.out.println("dl_Lat : " + dl_Lat);
		// System.out.println("dl_Lng : " + dl_Lng);
		System.out.println("dl_Form : " + dl_Form);
		System.out.println("dl_Address : " + dl_Address);
		Map<String, Object> dlSearch = new HashMap<String, Object>();
		List<DealingVO> dlMap = new ArrayList<DealingVO>();
		if (dl_Form != null && dl_Form != "") {
			// ???????????? ??????
			dlSearch.put("dl_Form", dl_Form);
			dlMap = dealingService.selectMap(dlSearch);
		} else if (dl_Address != null && dl_Address != "") {
			// ??????????????? ??????
			dlSearch.put("dl_Address", dl_Address);
			dlMap = dealingService.selectMap3(dlSearch);
		}
		System.out.println("dlMap : " + dlMap);

		return new ResponseEntity(dlMap, HttpStatus.OK);
	}

	/* ??????????????? ?????? */
	@Override
	@RequestMapping(value = "/showMap2.do", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity showMap2(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> dlMap = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = request.getParameter(name);
			dlMap.put(name, value);
		}
		List<DealingVO> dlReq = new ArrayList<DealingVO>();
		dlReq = dealingService.selectMap2(dlMap);

		System.out.println("dlReq : " + dlReq);
		return new ResponseEntity(dlReq, HttpStatus.OK);

	}

	/* ?????????????????? ???????????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/hereMe.do", method = RequestMethod.GET)
	public ResponseEntity hereMe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String strLat = request.getParameter("lat");
		String strLng = request.getParameter("lng");
		double lat = Double.parseDouble(strLat);
		double lng = Double.parseDouble(strLng);
		System.out.println("lat : " + lat);
		System.out.println("lng : " + lng);
		Map<String, Object> hereMap = new HashMap<String, Object>();
		List<DealingVO> hereList = new ArrayList<DealingVO>();
		hereMap.put("lat", lat);
		hereMap.put("lng", lng);
		hereList = dealingService.hereMe(hereMap);
		System.out.println("??????????????? : " + hereList);
		return new ResponseEntity(hereList, HttpStatus.OK);

	}
	/* ?????????????????? ?????? */
	/* ???????????????????????? ???????????? ???????????? */
//	@ResponseBody
//	@RequestMapping(value = "/dlLocalStorage.do", method = RequestMethod.GET)
//	public ResponseEntity dlMain2(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		Map<String, Object> mainMap = new HashMap<String, Object>();
//		Enumeration enu = request.getParameterNames();
//		while (enu.hasMoreElements()) {
//			String name = (String) enu.nextElement();
//			String value = request.getParameter(name);
//			mainMap.put(name, value);
//		}
//		List<DealingVO> dlMain = new ArrayList<DealingVO>();
//		dlMain = dealingService.dlMainMap(mainMap);
//
//		return new ResponseEntity(dlMain, HttpStatus.OK);
//	}

	// ????????????????????? ?????????
	@Override
	@RequestMapping(value = "/inteboardform.do", method = { RequestMethod.GET })
	public String addboard(BoardVO board, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		return "/dealing/addboard";
	}

	// ????????????????????? ??????
	@Override
	@RequestMapping(value = "/addinteboard.do", method = RequestMethod.POST)

	public ResponseEntity boardpost(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		Map<String, Object> boardMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
//						System.out.println(name);
			String value = multipartRequest.getParameter(name);
//						System.out.println(value);
			boardMap.put(name, value);

		}
		String imageFileName1 = up1(multipartRequest);
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		System.out.println("????????? ?????? ?????? : " + imageFileName1);
		boardMap.put("user_Id", user_Id);
		boardMap.put("inte_Image", imageFileName1);
		boardMap.put("viewCounts", 1);
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-Type", "text/html;charset=utf-8");
		try {
			int result = dealingService.insertboard(boardMap);
//						if (imageFileName != null && imageFileName.length() != 0) {
//							File srcFile = new File(PRODUCT_IMAGE_REPO + "\\" + imageFileName);
//							File desDir = new File(PRODUCT_IMAGE_REPO);
//						????????? ?????? ????????? ??????????????? ?????? moveFileToDirectory
//							FileUtils.moveFileToDirectory(srcFile, desDir, true);
//						}
			message = "<script>";
			message += "alert('????????? ????????? ?????????????????????.');";
			message += "location.href='" + multipartRequest.getContextPath() + "/inteboardlist.do'";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(BOARD_IMAGE_REPO + "\\" + "imageFileName1");
			srcFile.delete();

			message = "<script>";
			message += "alert('?????? ??????????????????');";
//						message += "location.href='" + multipartRequest.getContextPath() + "location.reload();'";
			message += "";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;

	}

	// ??????????????????????????????
	@Override
	@RequestMapping(value = "/inteboardlist.do", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public ModelAndView inteboardlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");

		ModelAndView mav = new ModelAndView();
		mav.setViewName("/dealing/inteboardlist");
		List<BoardVO> inteboardlist = dealingService.listArticles();
		mav.addObject("inteboardlist", inteboardlist);
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return mav;
	}

	// ???????????? ????????? ????????????
	@Override
	@RequestMapping(value = "/board/read", method = RequestMethod.GET)
	public String read(BoardVO boardVO, @RequestParam("inte_Num") int inte_Num, Model model, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		dealingService.updateView(inte_Num);// ????????? ??????
		BoardVO boardContents = dealingService.getBoardContents(inte_Num);
		HttpSession session = req.getSession();

		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		if (memberVO == null) {
			model.addAttribute("user_Id", "guest");

		} else {
			String user_Id = memberVO.getUser_Id();
			model.addAttribute("user_Id", user_Id);
		}
//			Cookie[] cookies = req.getCookies();
//			String user_Id = null;
//			
//			for(Cookie cookie : cookies) {
//				System.out.println(cookie.getName());
//				if(cookie.getName().equals("user_Id")) {
//					user_Id !==1; 
//				}
//			}

		model.addAttribute("boardContents", boardContents);

		return "/dealing/boardview";
	}

	// ???????????? ??? ????????????
	@Override
	@RequestMapping(value = "/board/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("boardVO") BoardVO boardVO, @RequestParam("inte_Num") int inte_Num,
			Model model) throws Exception {

		BoardVO boardContents = dealingService.getBoardContents(inte_Num);
		model.addAttribute("boardContents", boardContents);
		return "/dealing/boardupdate";
	}

	// ???????????? ??? ??????
	@Override
	@RequestMapping(value = "/board/update_action", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		Map<String, Object> boardMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			boardMap.put(name, value);
		}
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();

		String imageFileName1 = up1(multipartRequest);
		boardMap.put("inte_Image", imageFileName1);
		String inte_Num = (String) boardMap.get("inte_Num");
		String message = null;
		ResponseEntity resEnt = null;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html;charset=utf-8");

		try {
			dealingService.modArticle(boardMap);
//				if (imageFileName1 != null && imageFileName1.length() != 0) {
//					File srcFile = new File(BOARD_IMAGE_REPO + "\\" + "user_Id" + "\\" + imageFileName1);
//					File destDir = new File(BOARD_IMAGE_REPO + "\\" + user_Id);
//					destDir.mkdirs();
//					FileUtils.moveFileToDirectory(srcFile, destDir, true);

			String originalFileName = (String) boardMap.get("originalFileName");
			File oldFile = new File(BOARD_IMAGE_REPO + "\\" + user_Id + "\\" + originalFileName);
			oldFile.delete();

//			}
			message = "<script>";
			message += "alert('Success');";
			message += "location.href='" + multipartRequest.getContextPath() + "/board/read?inte_Num=" + inte_Num
					+ "';";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			File srcFile = new File(BOARD_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName1);
			srcFile.delete();
			message = "<script>";
			message += "alert('Error');";
			message += "location.href='" + multipartRequest.getContextPath() + "/board/read?inte_Num=" + inte_Num
					+ "';";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	// ???????????? ??? ?????? ????????????
	@Override
	@RequestMapping(value = "/board/removeArticle.do", method = RequestMethod.POST)

	public ResponseEntity removeArticle(@RequestParam("inte_Num") int inte_Num, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html;charset=utf-8");
		try {
			dealingService.removeArticle(inte_Num);
			File destDir = new File(BOARD_IMAGE_REPO + "\\" + inte_Num);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += "alert('?????????????????????.');";
			message += "location.href='" + request.getContextPath() + "/inteboardlist.do';";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "<script>";
			message += "alert('???????????????.');";
			message += "location.href='" + request.getContextPath() + "/board/{inte_Num}';";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// ???????????? ???????????????
	@Override
	@RequestMapping(value = "/noticelist.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView noticelist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/dealing/noticelist");
		List<NoticeVO> noticlesList = dealingService.listNoticles();
		mav.addObject("notic", noticlesList);
		System.out.println("notice" + noticlesList);
		System.out.println("interceptor?????? ??? viewName:" + viewName);
		return mav;
	}

	// ???????????? ?????????
	@Override
	@RequestMapping(value = "/noticeform.do", method = RequestMethod.GET)
	public String addnotice(NoticeVO notice, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		return "/dealing/addnotice";
	}

//		// ???????????? ??? ??????
//		@RequestMapping(value = "/board/create_action", method = RequestMethod.POST)
//		public String create_action(@ModelAttribute("noticeVO") NoticeVO noticeVO, RedirectAttributes redirect)
//				throws Exception {
	//
//			dealingService.insertnotice(noticeVO);
//			redirect.addFlashAttribute(noticeVO.getNotice_Num());
//			return "/dealing/list";
//		}

	// ???????????? ??? ??????
	@Override
	@RequestMapping(value = "/addnotice.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity noticepost(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		Map<String, Object> noticeMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			System.out.println(name);
			String value = multipartRequest.getParameter(name);
			System.out.println(value);
			noticeMap.put(name, value);
		}
//			String imageFileName2 = up2(multipartRequest);
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
//			System.out.println("????????? ?????? ?????? : " + imageFileName2);
		noticeMap.put("user_Id", user_Id);
//			noticeMap.put("Notice_Image", imageFileName2);
		noticeMap.put("viewCnt", 1);
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-Type", "text/html;charset=utf-8");
		try {
			int result = dealingService.insertnotice(noticeMap);
//					if (imageFileName != null && imageFileName.length() != 0) {
//						File srcFile = new File(PRODUCT_IMAGE_REPO + "\\" + imageFileName);
//						File desDir = new File(PRODUCT_IMAGE_REPO);
//					????????? ?????? ????????? ??????????????? ?????? moveFileToDirectory
//						FileUtils.moveFileToDirectory(srcFile, desDir, true);
//					}
			message = "<script>";
			message += "alert('????????? ????????? ?????????????????????.');";
			message += "location.href='" + multipartRequest.getContextPath() + "/noticelist.do'";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(NOTICE_IMAGE_REPO + "\\" + "imageFileName2");
			srcFile.delete();

			message = "<script>";
			message += "alert('?????? ??????????????????');";
//					message += "location.href='" + multipartRequest.getContextPath() + "location.reload();'";
			message += "";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	/* ???????????? ??? ???????????? */
	@Override
	@RequestMapping(value = "/notice/read", method = RequestMethod.GET)
	public String noticeread(NoticeVO noticeVO, @RequestParam("notice_Num") int notice_Num, Model model,
			HttpServletRequest req) throws Exception {
		dealingService.updateViewCnt(notice_Num);
		NoticeVO noticeContents = dealingService.getNoticeContents(notice_Num);
		HttpSession session = req.getSession();

		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		if (memberVO == null) {
			model.addAttribute("user_Id", "guest");
		} else {
			String user_Id = memberVO.getUser_Id();
			model.addAttribute("user_Id", user_Id);
		}
		model.addAttribute("noticeContents", noticeContents);

		return "/dealing/noticeview";
	}

	/* ???????????? ??? ???????????? */
	@Override
	@RequestMapping(value = "/notice/update", method = { RequestMethod.POST, RequestMethod.GET })
	public String noticeupdate(@ModelAttribute("noticeVO") NoticeVO noticeVO,
			@RequestParam("notice_Num") int notice_Num, Model model) throws Exception {

		NoticeVO noticeContents = dealingService.getNoticeContents(notice_Num);
		model.addAttribute("noticeContents", noticeContents);
		return "/dealing/noticeupdate";
	}

	// ???????????? ??? ?????? ????????????
	@Override
	@RequestMapping(value = "/notice/update_action", method = RequestMethod.POST)
	public String notice_update_action(@ModelAttribute("noticeVO") NoticeVO noticeVO, HttpServletRequest request,
			RedirectAttributes redirect, Model model) {
		try {
			dealingService.updateNotice(noticeVO);
			redirect.addFlashAttribute("redirect", noticeVO.getNotice_Num());
			redirect.addFlashAttribute("msg", "????????? ?????????????????????.");
		} catch (Exception e) {
			redirect.addFlashAttribute("msg", "????????? ?????????????????????.");
		}
		return "redirect:/notice/read?notice_Num=" + noticeVO.getNotice_Num();
	}

	// ???????????? ??? ?????? ??????
	@Override
	@RequestMapping(value = "/notice/modNoticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modNoticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		Map<String, Object> noticeMap = new HashMap<String, Object>();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			noticeMap.put(name, value);
		}
//			String notice_Image = up2(multipartRequest);

		HttpSession session = multipartRequest.getSession();
		session.setAttribute("user_Id", "testId");

		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getUser_Id();
		System.out.println("user_Id:" + id);
		noticeMap.put("user_Id", id);
//			noticeMap.put("notice_Image", notice_Image);
		String notice_Num = (String) noticeMap.get("notice_Num");
		System.out.println("notice_Num :" + notice_Num);
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html;charset=utf-8");

		return resEnt;
	}

	// ???????????? ??? ??????
	@Override
	@ResponseBody
	@RequestMapping(value = "/notice/removeNoticle", method = RequestMethod.POST)
	public ResponseEntity removeNoticle(@RequestParam("notice_Num") int notice_Num, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=UTF-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html;charset=utf-8");
		try {
			dealingService.removeNoticle(notice_Num);
			File destDir = new File(NOTICE_IMAGE_REPO + "\\" + notice_Num);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += "alert('?????????????????????.');";
			message += "location.href='" + request.getContextPath() + "/noticelist.do';";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "<script>";
			message += "alert('???????????????.');";
			message += "location.href='" + request.getContextPath() + "/noticelist.do';";
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	/////////////////////////////////////// ??????
	// ?????? ??????
	@Override
	@RequestMapping(value = "/replies/new", method = RequestMethod.POST)
	public ResponseEntity<String> replywrite(@RequestBody ReplyVO reply, HttpServletRequest req) throws Exception {

		HttpSession session = req.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		reply.setUser_Id(user_Id);

		// insert??? ??????????????? result????????? 1??????
		// insert??? ??????????????? result????????? 0??????
		// ?????? MV?????? ??????
		System.out.println(reply);
		int result = dealingService.rewrite(reply);

		// result??? 1?????? HTTP.OK
		// result??? 0?????? HTTP.INTERNAL_SEVER_ERROR

		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// ?????? ??????
	@Override
	@RequestMapping(value = "/replies/{inte_Num}", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<ReplyVO>> getlist(@PathVariable int inte_Num) throws Exception {
//				System.out.println(inte_Num);

		return new ResponseEntity<>(dealingService.list(inte_Num), HttpStatus.OK);
	}

	// ?????? ??????
	@Override
	@RequestMapping(value = "/replies/modify", method = RequestMethod.PUT, consumes = "application/json", produces = "text/plain;charset=utf-8")
	public ResponseEntity<String> replymodify(@RequestBody ReplyVO reply, HttpServletRequest req, Model model)
			throws Exception {
		HttpSession session = req.getSession();
//				reply.setUser_Id(user_Id);

		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		if (memberVO == null) {
			model.addAttribute("user_Id", "guest");

		} else {
			String user_Id = memberVO.getUser_Id();
			model.addAttribute("user_Id", user_Id);
		}

		dealingService.modify(reply);

		return dealingService.modify(reply) == 1
				? new ResponseEntity<>(new String("success".getBytes(), "UTF-8"), HttpStatus.OK) // 200
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500
	}

	// ?????? ??????
//			@JsonProperty("reply")
//			@RequestMapping(value = "/replies/remove/{reply_Num}", method = RequestMethod.DELETE)
	@Override
	@DeleteMapping(value = "/replies/{reply_Num}", produces = { MediaType.TEXT_PLAIN_VALUE })
	public ResponseEntity<String> replyremove(@PathVariable("reply_Num") int reply_Num, HttpServletRequest req,
			ReplyVO reply) throws Exception {
		HttpSession session = req.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();

		reply.setUser_Id(user_Id);

		return dealingService.remove(reply_Num) == 1
				? new ResponseEntity<>(new String("success".getBytes(), "UTF-8"), HttpStatus.OK) // 200
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500
	}

	/* ???????????? */
	@Override
	@ResponseBody
	@RequestMapping(value = "/report.do", method = { RequestMethod.POST })
	public Map<String, Object> report(@RequestBody Map<String, Object> report, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("rep" + report);
		HttpSession session = request.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		report.put("user_Id", user_Id);
		int result = 0;
		result = dealingService.addReport(report);
		Map<String, Object> check = new HashMap<>();
		check.put("result", result);

		return check;
	}

	/* ?????? ????????? */
	@Override
	@RequestMapping("/rpPopUp.do")
	public String reportPop(HttpServletRequest request, Model model) {
		String dl_Num1 = request.getParameter("dl_Num");
		int dl_Num = Integer.parseInt(dl_Num1);
		model.addAttribute("dl_Num", dl_Num);
		return "/dealing/report";
	}

//	????????? ?????????
	@Override
	public String up(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		int count = 0;
//		while (fileNames.hasNext()) {
		String fileName = fileNames.next();
		MultipartFile mFile = multipartRequest.getFile(fileName);
		imageFileName = mFile.getOriginalFilename();
		System.out.println("imaage :" + imageFileName);
		File file = new File(DEALING_IMAGE_REPO + "\\" + user_Id + "\\" + imageFileName);
		if (mFile.getSize() != 0) {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				mFile.transferTo(new File(DEALING_IMAGE_REPO + "\\" + user_Id + "\\" + imageFileName));
			}
		}
//		}
		return imageFileName;
	}

	// ????????? ????????? ?????????
	@Override
	public String up1(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName1 = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String user_Id = memberVO.getUser_Id();
		int count = 0;
		// while (fileNames.hasNext()) {
		String fileName = fileNames.next();
		MultipartFile mFile = multipartRequest.getFile(fileName);
		imageFileName1 = mFile.getOriginalFilename();
		System.out.println("imaage :" + imageFileName1);
		File file = new File(BOARD_IMAGE_REPO + "\\" + user_Id + "\\" + imageFileName1);
		if (mFile.getSize() != 0) {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				mFile.transferTo(new File(BOARD_IMAGE_REPO + "\\" + user_Id + "\\" + imageFileName1));
			}
		}
//				}
		return imageFileName1;
	}

	@ResponseBody
	@RequestMapping(value = "/dlorder.do", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> dlorder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
		int result = 0;

		Map<String, Object> map = new HashMap<>();
		result = dealingService.dlorder(body);
		String dl_Num = (String) body.get("dl_Num");
		dealingService.dlsold(dl_Num);
		map.put("result", result);

		return map;
	}

}
