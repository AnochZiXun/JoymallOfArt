package tw.com.joymall.controlpanel;

import java.sql.SQLException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tw.com.joymall.Utils;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.Services;

/**
 * 工作人員
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/staff")
public class StaffController {

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private Services services;

	/**
	 * 列表
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止攤商存取
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		/*
		 分頁
		 */
		Element paginationElement = Utils.createElementWithAttribute("pagination", documentElement, "action", request.getRequestURI());
		if (size < 1) {
			size = 10;//每頁幾筆
		}
		if (number < 0) {
			number = 0;//第幾頁
		}
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.ASC, "login");
		Page<Staff> pageOfEntities;
		pageOfEntities = staffRepository.findByInternalTrueAndIdNot(myself.getId(), pageRequest);
		number = pageOfEntities.getNumber();
		Integer totalPages = pageOfEntities.getTotalPages();
		Long totalElements = pageOfEntities.getTotalElements();
		if (pageOfEntities.hasPrevious()) {
			paginationElement.setAttribute("previous", Integer.toString(number - 1));
			if (!pageOfEntities.isFirst()) {
				paginationElement.setAttribute("first", "0");
			}
		}
		paginationElement.setAttribute("size", size.toString());
		paginationElement.setAttribute("number", number.toString());
		for (Integer i = 0; i < totalPages; i++) {
			Element optionElement = Utils.createElementWithTextContentAndAttribute("option", paginationElement, Integer.toString(i + 1), "value", i.toString());
			if (number.equals(i)) {
				optionElement.setAttribute("selected", null);
			}
		}
		paginationElement.setAttribute("totalPages", totalPages.toString());
		paginationElement.setAttribute("totalElements", totalElements.toString());
		if (pageOfEntities.hasNext()) {
			paginationElement.setAttribute("next", Integer.toString(number + 1));
			if (!pageOfEntities.isLast()) {
				paginationElement.setAttribute("last", Integer.toString(totalPages - 1));
			}
		}

		/*
		 列表
		 */
		Element listElement = Utils.createElement("list", documentElement);
		for (Staff entity : pageOfEntities.getContent()) {
			Element rowElement = Utils.createElement("row", listElement);
			Utils.createElementWithTextContent("id", rowElement, entity.getId().toString());
			Utils.createElementWithTextContent("login", rowElement, entity.getLogin());
			Utils.createElementWithTextContent("name", rowElement, entity.getName());
			Utils.createElementWithTextContent("available", rowElement, Boolean.toString(entity.getShadow().matches("^[a-z0-9]{32}$")));
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/staff/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 變更密碼
	 *
	 * @param id 工作人員的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/shadow.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView shadow(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止攤商存取
			return null;
		}
		Staff staff = staffRepository.findOne(id);
		if (staff == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("title", "控制臺 &#187; 變更" + staff.getName() + "的密碼");
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "變更" + staff.getName() + "的密碼");

		ModelAndView modelAndView = new ModelAndView("cPanel/shadow");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 變更密碼
	 *
	 * @param shadow1 新密碼
	 * @param shadow2 確認密碼
	 * @param id 工作人員的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/shadow.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView shadow(@RequestParam(defaultValue = "") String shadow1, @RequestParam(defaultValue = "") String shadow2, @PathVariable Integer id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止攤商存取
			return null;
		}
		Staff staff = staffRepository.findOne(id);
		if (staff == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}

		String errorMessage = null;//錯誤訊息

		try {
			if (shadow1.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "新密碼為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			if (shadow2.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "確認新密碼為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		if (!Objects.equals(shadow1, shadow2)) {
			errorMessage = "新密碼及確認新密碼不符！";
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("title", "控制臺 &#187; 變更" + staff.getName() + "的密碼");
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "變更" + staff.getName() + "的密碼");

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			staff.setShadow(org.apache.catalina.realm.RealmBase.Digest(shadow1, "MD5", "UTF-8"));
			staffRepository.saveAndFlush(staff);
			return new ModelAndView("redirect:/cPanel/staff/");
		}

		elementForm.setAttribute("error", errorMessage);

		ModelAndView modelAndView = new ModelAndView("cPanel/shadow");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * (停用|啟用)工作人員帳號
	 *
	 * @param id 工作人員的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/available.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String available(@PathVariable Integer id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			return jsonObject.put("reason", "找不到工作人員或您為店家！").toString();
		}
		Staff staff = staffRepository.findOne(id);
		if (staff == null || !staff.isInternal()) {
			return jsonObject.put("reason", "此為店家或找不到工作人員！").toString();
		}

		String shadow = staff.getShadow(), stringReversed = new StringBuilder(shadow).reverse().toString();
		boolean isAvailable = shadow.matches("^[a-z0-9]{32}$");
		staff.setShadow(isAvailable ? stringReversed.toUpperCase() : stringReversed.toLowerCase());
		staffRepository.saveAndFlush(staff);

		return jsonObject.put("response", true).toString();
	}

	/**
	 * 新增工作人員
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView create(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "新增工作人員");

		ModelAndView modelAndView = new ModelAndView("cPanel/staff/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增工作人員
	 *
	 * @param login 帳號
	 * @param name 工作人員暱稱
	 * @param shadow 密碼
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	@SuppressWarnings("UseSpecificCatch")
	private ModelAndView create(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String shadow, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		String errorMessage = null;//錯誤訊息

		try {
			login = login.trim().toLowerCase();
			if (login.isEmpty()) {
				throw new NullPointerException();
			}

			if (!org.apache.commons.validator.routines.EmailValidator.getInstance(false, false).isValid(login)) {
				errorMessage = "錯誤的帳號(電子郵件)格式！";
			}

			if (staffRepository.countByLogin(login) > 0) {
				errorMessage = "已存在的帳號(電子郵件)！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "帳號(電子郵件)為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			name = name.trim();
			if (name.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "工作人員暱稱為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			if (shadow.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "密碼為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		if (errorMessage == null) {
			staffRepository.saveAndFlush(new Staff(true, login, org.apache.catalina.realm.RealmBase.Digest(shadow, "MD5", "UTF-8"), name));
			return new ModelAndView("redirect:/cPanel/staff/");
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "新增工作人員");
		elementForm.setAttribute("error", errorMessage);
		Utils.createElementWithTextContent("login", elementForm, login);
		Utils.createElementWithTextContent("name", elementForm, name);

		ModelAndView modelAndView = new ModelAndView("cPanel/staff/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}
}
