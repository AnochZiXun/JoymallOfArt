package tw.com.joymall.controlpanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.validator.routines.EmailValidator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tw.com.joymall.Utils;
import tw.com.joymall.entity.AccessLog;
import tw.com.joymall.entity.Packet;
import tw.com.joymall.entity.PacketStatus;
import tw.com.joymall.entity.Regular;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.AccessLogRepository;
import tw.com.joymall.repository.PacketRepository;
import tw.com.joymall.repository.PacketStatusRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.Services;

/**
 * 控制臺首頁
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel")
public class DefaultController {

	@Autowired
	private AccessLogRepository accessLogRepository;

	@Autowired
	private PacketRepository packetRepository;

	@Autowired
	private PacketStatusRepository packetStatusRepository;

	@Autowired
	private StaffRepository staffRepository;

	final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

	@Autowired
	private Services services;

	/**
	 * 列表(工作人員：存取記錄；攤商：所有訂單)
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
		if (myself == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//找嘸郎
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		if (myself.isInternal()) {
			/*
			 分頁
			 */
			Element elementPagination = Utils.createElementWithAttribute("pagination", documentElement, "action", requestURI);
			if (size < 1) {
				size = 10;//每頁幾筆
			}
			if (number < 0) {
				number = 0;//第幾頁
			}
			Pageable pageRequest = new PageRequest(number, size, Sort.Direction.DESC, "timestamp");
			Page<AccessLog> pageOfEntities;
			pageOfEntities = accessLogRepository.findAll(pageRequest);
			number = pageOfEntities.getNumber();
			Integer totalPages = pageOfEntities.getTotalPages();
			Long totalElements = pageOfEntities.getTotalElements();
			if (pageOfEntities.hasPrevious()) {
				elementPagination.setAttribute("previous", Integer.toString(number - 1));
				if (!pageOfEntities.isFirst()) {
					elementPagination.setAttribute("first", "0");
				}
			}
			elementPagination.setAttribute("size", size.toString());
			elementPagination.setAttribute("number", number.toString());
			for (Integer i = 0; i < totalPages; i++) {
				Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementPagination, Integer.toString(i + 1), "value", i.toString());
				if (number.equals(i)) {
					elementOption.setAttribute("selected", null);
				}
			}
			elementPagination.setAttribute("totalPages", totalPages.toString());
			elementPagination.setAttribute("totalElements", totalElements.toString());
			if (pageOfEntities.hasNext()) {
				elementPagination.setAttribute("next", Integer.toString(number + 1));
				if (!pageOfEntities.isLast()) {
					elementPagination.setAttribute("last", Integer.toString(totalPages - 1));
				}
			}

			/*
			 列表
			 */
			Element elementList = Utils.createElement("list", documentElement);
			for (AccessLog entity : pageOfEntities.getContent()) {
				String remoteHost = entity.getRemoteHost(), login = entity.getUserName(), method = entity.getMethod(), query = entity.getQuery(), userAgent = entity.getUserAgent();
				Date timestamp = entity.getTimestamp();
				Short status = entity.getStatus();
				Long bytes = entity.getBytes();
				String userName = null;
				if (login != null) {
					userName = staffRepository.findOneByLogin(login).getName();
				}

				Element elementRow = Utils.createElement("row", elementList);
				Utils.createElementWithTextContent("remoteHost", elementRow, remoteHost == null ? "" : remoteHost);
				Utils.createElementWithTextContent("userName", elementRow, userName == null ? "" : userName);
				Utils.createElementWithTextContent("timestamp", elementRow, timestamp == null ? "" : simpleDateFormat.format(timestamp));
				Utils.createElementWithTextContent("method", elementRow, method == null ? "" : method);
				Utils.createElementWithTextContent("query", elementRow, query == null ? "" : query);
				Utils.createElementWithTextContent("status", elementRow, status == null ? "" : status.toString());
				Utils.createElementWithTextContent("bytes", elementRow, bytes.toString());
				Utils.createElementWithTextContent("userAgent", elementRow, userAgent == null ? "" : userAgent);
			}

			ModelAndView modelAndView = new ModelAndView("cPanel/welcomeStaff");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		/*
		 分頁
		 */
		Element elementPagination = Utils.createElementWithAttribute("pagination", documentElement, "action", requestURI);
		if (size < 1) {
			size = 10;//每頁幾筆
		}
		if (number < 0) {
			number = 0;//第幾頁
		}
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.DESC, "merchantTradeDate");
		Page<Packet> pageOfEntities;
		pageOfEntities = packetRepository.findByBoothOrderByMerchantTradeDateDesc(myself, pageRequest);
		number = pageOfEntities.getNumber();
		Integer totalPages = pageOfEntities.getTotalPages();
		Long totalElements = pageOfEntities.getTotalElements();
		if (pageOfEntities.hasPrevious()) {
			elementPagination.setAttribute("previous", Integer.toString(number - 1));
			if (!pageOfEntities.isFirst()) {
				elementPagination.setAttribute("first", "0");
			}
		}
		elementPagination.setAttribute("size", size.toString());
		elementPagination.setAttribute("number", number.toString());
		for (Integer i = 0; i < totalPages; i++) {
			Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementPagination, Integer.toString(i + 1), "value", i.toString());
			if (number.equals(i)) {
				elementOption.setAttribute("selected", null);
			}
		}
		elementPagination.setAttribute("totalPages", totalPages.toString());
		elementPagination.setAttribute("totalElements", totalElements.toString());
		if (pageOfEntities.hasNext()) {
			elementPagination.setAttribute("next", Integer.toString(number + 1));
			if (!pageOfEntities.isLast()) {
				elementPagination.setAttribute("last", Integer.toString(totalPages - 1));
			}
		}

		/*
		 列表
		 */
		Element elementList = Utils.createElement("list", documentElement);
		for (Packet entity : pageOfEntities.getContent()) {
			Regular regular = entity.getRegular();
			PacketStatus packetStatus = entity.getPacketStatus();
			String merchantTradeNo = entity.getMerchantTradeNo();
			Date merchantTradeDate = entity.getMerchantTradeDate();
			Integer totalAmount = entity.getTotalAmount();

			Element elementRow = Utils.createElement("row", elementList);
			Utils.createElementWithTextContentAndAttribute("regular", elementRow, regular.getEmail(), "fullname", regular.getLastname().concat(regular.getFirstname()));
			Utils.createElementWithTextContent("merchantTradeNo", elementRow, merchantTradeNo);
			Utils.createElementWithTextContent("merchantTradeDate", elementRow, simpleDateFormat.format(merchantTradeDate));
			Utils.createElementWithTextContent("totalAmount", elementRow, totalAmount.toString());
			Utils.createElementWithTextContent("status", elementRow, packetStatus == null ? "" : packetStatus.getId().toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/welcomeBooth");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 變更密碼
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/shadow.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView shadow(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//找嘸郎
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		ModelAndView modelAndView = new ModelAndView("cPanel/shadow");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 變更密碼
	 *
	 * @param shadow1 新密碼
	 * @param shadow2 確認新密碼
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/shadow.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView shadow(@RequestParam(defaultValue = "") String shadow1, @RequestParam(defaultValue = "") String shadow2, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//找嘸郎
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
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			myself.setShadow(org.apache.catalina.realm.RealmBase.Digest(shadow1, "MD5", "UTF-8"));
			staffRepository.saveAndFlush(myself);

			errorMessage = "已成功變更密碼！";
		}

		elementForm.setAttribute("error", errorMessage);

		ModelAndView modelAndView = new ModelAndView("cPanel/shadow");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 基本資料(攤商)
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/booth.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView booth(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		byte[] logo = myself.getLogo();
		String html = myself.getHtml(), address = myself.getAddress(), cellular = myself.getCellular(), phone = myself.getPhone(), representative = myself.getRepresentative(), merchantID = myself.getMerchantID(), hashKey = myself.getHashKey(), hashIV = myself.getHashIV();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		Utils.createElementWithTextContent("login", elementForm, myself.getLogin());
		Utils.createElementWithTextContent("name", elementForm, myself.getName());
		Utils.createElementWithTextContent("logo", elementForm, Boolean.toString(logo == null || logo.length == 0));
		Utils.createElementWithTextContent("html", elementForm, html == null ? "" : html);
		Utils.createElementWithTextContent("address", elementForm, address == null ? "" : address);
		Utils.createElementWithTextContent("cellular", elementForm, cellular == null ? "" : cellular);
		Utils.createElementWithTextContent("phone", elementForm, phone == null ? "" : phone);
		Utils.createElementWithTextContent("representative", elementForm, representative == null ? "" : representative);
		Utils.createElementWithTextContent("merchantID", elementForm, merchantID == null ? "" : merchantID);
		Utils.createElementWithTextContent("hashKey", elementForm, hashKey == null ? "" : hashKey);
		Utils.createElementWithTextContent("hashIV", elementForm, hashIV == null ? "" : hashIV);

		ModelAndView modelAndView = new ModelAndView("cPanel/iAmBooth");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改基本資料(攤商)
	 *
	 * @param login
	 * @param name
	 * @param multipartFile
	 * @param html
	 * @param address
	 * @param cellular
	 * @param phone
	 * @param representative
	 * @param merchantID
	 * @param hashKey
	 * @param hashIV
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/booth.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	@SuppressWarnings({"ResultOfObjectAllocationIgnored", "UseSpecificCatch"})
	private ModelAndView booth(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String name, @RequestParam(required = false, value = "logo") MultipartFile multipartFile, @RequestParam(defaultValue = "") String html, @RequestParam(defaultValue = "") String address, @RequestParam(defaultValue = "") String cellular, @RequestParam(defaultValue = "") String phone, @RequestParam(defaultValue = "") String representative, @RequestParam(defaultValue = "") String merchantID, @RequestParam(defaultValue = "") String hashKey, @RequestParam(defaultValue = "") String hashIV, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		String errorMessage = null;//錯誤訊息

		try {
			login = login.trim().toLowerCase();
			if (login.isEmpty()) {
				throw new NullPointerException();
			}

			if (!EmailValidator.getInstance(false, false).isValid(login)) {
				errorMessage = "錯誤的帳號(電子郵件)格式！";
			}

			if (staffRepository.countByLoginAndIdNot(login, myself.getId()) > 0) {
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
			errorMessage = "店家抬頭為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		byte[] myLogo = myself.getLogo(), logo = null;
		boolean isMultipartFileEmpty = multipartFile.isEmpty(), isMyLogoEmpty = myLogo == null || myLogo.length == 0;
		if (isMultipartFileEmpty && isMyLogoEmpty) {
			errorMessage = "店家圖像為必選！";
		} else if (!isMultipartFileEmpty) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				logo = multipartFile.getBytes();

				BufferedImage bufferedImage = Utils.rescaleImage(logo, 120, 120);
				if (ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream)) {
					logo = byteArrayOutputStream.toByteArray();
				} else {
					errorMessage = "處理店家圖像時發生不明的錯誤！";
				}
			} catch (IOException ioException) {
				errorMessage = ioException.getLocalizedMessage();
			} finally {
				IOUtils.closeQuietly(byteArrayOutputStream);
			}
		}

		try {
			html = html.trim();
			if (html.isEmpty()) {
				html = null;
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			address = address.trim();
			if (address.isEmpty()) {
				address = null;
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			cellular = cellular.trim();
			if (cellular.isEmpty()) {
				cellular = null;
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			phone = phone.trim();
			if (phone.isEmpty()) {
				phone = null;
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			representative = representative.trim();
			if (representative.isEmpty()) {
				representative = null;
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			merchantID = merchantID.trim();
			if (merchantID.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "歐付寶特店編號為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			hashKey = hashKey.trim();
			if (hashKey.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "HashKey 為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			hashIV = hashIV.trim();
			if (hashIV.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "HashIV 為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		/*
		 if (merchantID != null && (hashKey == null || hashIV == null)) {
		 errorMessage = "如欲啟用歐付寶線上付款功能則「歐付寶特店編號、歐付寶AIO介接的HashKey、歐付寶AIO介接的HashIV」三個欄位缺一不可！";
		 }
		 if (hashKey != null && (merchantID == null || hashIV == null)) {
		 errorMessage = "如欲啟用歐付寶線上付款功能則「歐付寶特店編號、歐付寶AIO介接的HashKey、歐付寶AIO介接的HashIV」三個欄位缺一不可！";
		 }
		 if (hashIV != null && (merchantID == null || hashKey == null)) {
		 errorMessage = "如欲啟用歐付寶線上付款功能則「歐付寶特店編號、歐付寶AIO介接的HashKey、歐付寶AIO介接的HashIV」三個欄位缺一不可！";
		 }
		 */

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			boolean isSameLogin = false;
			if (Objects.equals(login, myself.getLogin())) {
				isSameLogin = true;
			} else {
				myself.setLogin(login);
			}
			myself.setName(name);
			if (!isMultipartFileEmpty) {
				myself.setLogo(logo);
			}
			myself.setHtml(html);
			myself.setAddress(address);
			myself.setCellular(cellular);
			myself.setPhone(phone);
			myself.setRepresentative(representative);
			myself.setMerchantID(merchantID);
			myself.setHashKey(hashKey);
			myself.setHashIV(hashIV);
			staffRepository.saveAndFlush(myself);

			if (isSameLogin) {
				return new ModelAndView("redirect:/cPanel/booth.asp");
			} else {
				session.invalidate();
				return new ModelAndView("redirect:/cPanel/");
			}
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("error", errorMessage);
		Utils.createElementWithTextContent("login", elementForm, login);
		Utils.createElementWithTextContent("name", elementForm, name);
		Utils.createElementWithTextContent("logo", elementForm, Boolean.toString(isMyLogoEmpty));
		Utils.createElementWithTextContent("html", elementForm, html == null ? "" : html);
		Utils.createElementWithTextContent("address", elementForm, address == null ? "" : address);
		Utils.createElementWithTextContent("cellular", elementForm, cellular == null ? "" : cellular);
		Utils.createElementWithTextContent("phone", elementForm, phone == null ? "" : phone);
		Utils.createElementWithTextContent("representative", elementForm, representative == null ? "" : representative);
		Utils.createElementWithTextContent("merchantID", elementForm, merchantID == null ? "" : merchantID);
		Utils.createElementWithTextContent("hashKey", elementForm, hashKey == null ? "" : hashKey);
		Utils.createElementWithTextContent("hashIV", elementForm, hashIV == null ? "" : hashIV);

		ModelAndView modelAndView = new ModelAndView("cPanel/iAmBooth");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 基本資料(工作人員)
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/staff.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView staff(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		Utils.createElementWithTextContent("login", elementForm, myself.getLogin());
		Utils.createElementWithTextContent("name", elementForm, myself.getName());

		ModelAndView modelAndView = new ModelAndView("cPanel/iAmStaff");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改基本資料(工作人員)
	 *
	 * @param login 帳號
	 * @param name 工作人員暱稱
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/staff.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	@SuppressWarnings("UseSpecificCatch")
	private ModelAndView staff(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String name, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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

		String errorMessage = null;//錯誤訊息

		try {
			login = login.trim().toLowerCase();
			if (login.isEmpty()) {
				throw new NullPointerException();
			}

			if (!EmailValidator.getInstance(false, false).isValid(login)) {
				errorMessage = "錯誤的帳號(電子郵件)格式！";
			}

			if (staffRepository.countByLoginAndIdNot(login, myself.getId()) > 0) {
				errorMessage = "已存在的帳號(電子郵件)！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "帳號為必填！";
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

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			boolean isSameLogin = false;
			if (Objects.equals(login, myself.getLogin())) {
				isSameLogin = true;
			} else {
				myself.setLogin(login);
			}
			myself.setName(name);
			staffRepository.saveAndFlush(myself);

			if (isSameLogin) {
				return new ModelAndView("redirect:/cPanel/staff.asp");
			} else {
				session.invalidate();
				return new ModelAndView("redirect:/cPanel/");
			}
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("error", errorMessage);
		Utils.createElementWithTextContent("login", elementForm, login);
		Utils.createElementWithTextContent("name", elementForm, name);

		ModelAndView modelAndView = new ModelAndView("cPanel/iAmStaff");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 登出
	 *
	 * @param session
	 * @return 重導向返回控制臺首頁
	 */
	@RequestMapping(value = "/logOut.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView logOut(HttpSession session) throws ParserConfigurationException {
		session.invalidate();
		return new ModelAndView("redirect:/cPanel/");
	}

	/**
	 * 列表(備貨中)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/preparing/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView preparingPacket(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("title", "控制臺 &#187; 備貨中");
		documentElement.setAttribute("breadcrumb", "備貨中");
		services.buildAsideElement(document, request);

		/*
		 分頁
		 */
		Element elementPagination = Utils.createElementWithAttribute("pagination", documentElement, "action", request.getRequestURI());
		if (size < 1) {
			size = 10;//每頁幾筆
		}
		if (number < 0) {
			number = 0;//第幾頁
		}
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.DESC, "merchantTradeDate");
		Page<Packet> pageOfEntities;
		pageOfEntities = packetRepository.findByBoothAndPacketStatusOrderByMerchantTradeDateDesc(myself, packetStatusRepository.findOne((short) 1), pageRequest);
		number = pageOfEntities.getNumber();
		Integer totalPages = pageOfEntities.getTotalPages();
		Long totalElements = pageOfEntities.getTotalElements();
		if (pageOfEntities.hasPrevious()) {
			elementPagination.setAttribute("previous", Integer.toString(number - 1));
			if (!pageOfEntities.isFirst()) {
				elementPagination.setAttribute("first", "0");
			}
		}
		elementPagination.setAttribute("size", size.toString());
		elementPagination.setAttribute("number", number.toString());
		for (Integer i = 0; i < totalPages; i++) {
			Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementPagination, Integer.toString(i + 1), "value", i.toString());
			if (number.equals(i)) {
				elementOption.setAttribute("selected", null);
			}
		}
		elementPagination.setAttribute("totalPages", totalPages.toString());
		elementPagination.setAttribute("totalElements", totalElements.toString());
		if (pageOfEntities.hasNext()) {
			elementPagination.setAttribute("next", Integer.toString(number + 1));
			if (!pageOfEntities.isLast()) {
				elementPagination.setAttribute("last", Integer.toString(totalPages - 1));
			}
		}

		/*
		 列表
		 */
		Element elementList = Utils.createElement("list", documentElement);
		for (Packet entity : pageOfEntities.getContent()) {
			Regular regular = entity.getRegular();
			PacketStatus packetStatus = entity.getPacketStatus();
			String merchantTradeNo = entity.getMerchantTradeNo();
			Date merchantTradeDate = entity.getMerchantTradeDate();
			Integer totalAmount = entity.getTotalAmount();

			Element elementRow = Utils.createElementWithAttribute("row", elementList, "id", entity.getId().toString());
			Utils.createElementWithTextContent("regular", elementRow, regular.getEmail());
			Utils.createElementWithTextContent("fullname", elementRow, regular.getLastname().concat(regular.getFirstname()));
			Utils.createElementWithTextContent("merchantTradeNo", elementRow, merchantTradeNo);
			Utils.createElementWithTextContent("merchantTradeDate", elementRow, simpleDateFormat.format(merchantTradeDate));
			Utils.createElementWithTextContent("totalAmount", elementRow, totalAmount.toString());
			Utils.createElementWithTextContent("status", elementRow, packetStatus == null ? "" : packetStatus.getId().toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/packets");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 出貨
	 *
	 * @param id 訂單的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/preparing/{id:\\d+}.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String prepared(@PathVariable Long id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		Packet packet = packetRepository.findOne(id);
		if (packet == null) {
			return jsonObject.put("reason", "找不到訂單！").toString();
		}

		packet.setPacketStatus(packetStatusRepository.findOne((short) 2));
		packetRepository.saveAndFlush(packet);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 列表(出貨中)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/delivering/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView deliveringPacket(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("title", "控制臺 &#187; 出貨中");
		documentElement.setAttribute("breadcrumb", "出貨中");
		services.buildAsideElement(document, request);

		/*
		 分頁
		 */
		Element elementPagination = Utils.createElementWithAttribute("pagination", documentElement, "action", request.getRequestURI());
		if (size < 1) {
			size = 10;//每頁幾筆
		}
		if (number < 0) {
			number = 0;//第幾頁
		}
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.DESC, "merchantTradeDate");
		Page<Packet> pageOfEntities;
		pageOfEntities = packetRepository.findByBoothAndPacketStatusOrderByMerchantTradeDateDesc(myself, packetStatusRepository.findOne((short) 2), pageRequest);
		number = pageOfEntities.getNumber();
		Integer totalPages = pageOfEntities.getTotalPages();
		Long totalElements = pageOfEntities.getTotalElements();
		if (pageOfEntities.hasPrevious()) {
			elementPagination.setAttribute("previous", Integer.toString(number - 1));
			if (!pageOfEntities.isFirst()) {
				elementPagination.setAttribute("first", "0");
			}
		}
		elementPagination.setAttribute("size", size.toString());
		elementPagination.setAttribute("number", number.toString());
		for (Integer i = 0; i < totalPages; i++) {
			Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementPagination, Integer.toString(i + 1), "value", i.toString());
			if (number.equals(i)) {
				elementOption.setAttribute("selected", null);
			}
		}
		elementPagination.setAttribute("totalPages", totalPages.toString());
		elementPagination.setAttribute("totalElements", totalElements.toString());
		if (pageOfEntities.hasNext()) {
			elementPagination.setAttribute("next", Integer.toString(number + 1));
			if (!pageOfEntities.isLast()) {
				elementPagination.setAttribute("last", Integer.toString(totalPages - 1));
			}
		}

		/*
		 列表
		 */
		Element elementList = Utils.createElement("list", documentElement);
		for (Packet entity : pageOfEntities.getContent()) {
			Regular regular = entity.getRegular();
			PacketStatus packetStatus = entity.getPacketStatus();
			String merchantTradeNo = entity.getMerchantTradeNo();
			Date merchantTradeDate = entity.getMerchantTradeDate();
			Integer totalAmount = entity.getTotalAmount();

			Element elementRow = Utils.createElementWithAttribute("row", elementList, "id", entity.getId().toString());
			Utils.createElementWithTextContent("regular", elementRow, regular.getEmail());
			Utils.createElementWithTextContent("fullname", elementRow, regular.getLastname().concat(regular.getFirstname()));
			Utils.createElementWithTextContent("merchantTradeNo", elementRow, merchantTradeNo);
			Utils.createElementWithTextContent("merchantTradeDate", elementRow, simpleDateFormat.format(merchantTradeDate));
			Utils.createElementWithTextContent("totalAmount", elementRow, totalAmount.toString());
			Utils.createElementWithTextContent("status", elementRow, packetStatus == null ? "" : packetStatus.getId().toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/packets");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 結單
	 *
	 * @param id 訂單的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/delivering/{id:\\d+}.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String delivered(@PathVariable Long id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		Packet packet = packetRepository.findOne(id);
		if (packet == null) {
			return jsonObject.put("reason", "找不到訂單！").toString();
		}

		packet.setPacketStatus(packetStatusRepository.findOne((short) 3));
		packetRepository.saveAndFlush(packet);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 列表(已結單)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/closed/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView closedPacket(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("title", "控制臺 &#187; 已結單");
		documentElement.setAttribute("breadcrumb", "已結單");
		services.buildAsideElement(document, request);

		/*
		 分頁
		 */
		Element elementPagination = Utils.createElementWithAttribute("pagination", documentElement, "action", request.getRequestURI());
		if (size < 1) {
			size = 10;//每頁幾筆
		}
		if (number < 0) {
			number = 0;//第幾頁
		}
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.DESC, "merchantTradeDate");
		Page<Packet> pageOfEntities;
		pageOfEntities = packetRepository.findByBoothAndPacketStatusOrderByMerchantTradeDateDesc(myself, packetStatusRepository.findOne((short) 3), pageRequest);
		number = pageOfEntities.getNumber();
		Integer totalPages = pageOfEntities.getTotalPages();
		Long totalElements = pageOfEntities.getTotalElements();
		if (pageOfEntities.hasPrevious()) {
			elementPagination.setAttribute("previous", Integer.toString(number - 1));
			if (!pageOfEntities.isFirst()) {
				elementPagination.setAttribute("first", "0");
			}
		}
		elementPagination.setAttribute("size", size.toString());
		elementPagination.setAttribute("number", number.toString());
		for (Integer i = 0; i < totalPages; i++) {
			Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementPagination, Integer.toString(i + 1), "value", i.toString());
			if (number.equals(i)) {
				elementOption.setAttribute("selected", null);
			}
		}
		elementPagination.setAttribute("totalPages", totalPages.toString());
		elementPagination.setAttribute("totalElements", totalElements.toString());
		if (pageOfEntities.hasNext()) {
			elementPagination.setAttribute("next", Integer.toString(number + 1));
			if (!pageOfEntities.isLast()) {
				elementPagination.setAttribute("last", Integer.toString(totalPages - 1));
			}
		}

		/*
		 列表
		 */
		Element elementList = Utils.createElement("list", documentElement);
		for (Packet entity : pageOfEntities.getContent()) {
			Regular regular = entity.getRegular();
			PacketStatus packetStatus = entity.getPacketStatus();
			String merchantTradeNo = entity.getMerchantTradeNo();
			Date merchantTradeDate = entity.getMerchantTradeDate();
			Integer totalAmount = entity.getTotalAmount();

			Element elementRow = Utils.createElementWithAttribute("row", elementList, "id", entity.getId().toString());
			Utils.createElementWithTextContent("regular", elementRow, regular.getEmail());
			Utils.createElementWithTextContent("fullname", elementRow, regular.getLastname().concat(regular.getFirstname()));
			Utils.createElementWithTextContent("merchantTradeNo", elementRow, merchantTradeNo);
			Utils.createElementWithTextContent("merchantTradeDate", elementRow, simpleDateFormat.format(merchantTradeDate));
			Utils.createElementWithTextContent("totalAmount", elementRow, totalAmount.toString());
			Utils.createElementWithTextContent("status", elementRow, packetStatus == null ? "" : packetStatus.getId().toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/packets");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}
}
