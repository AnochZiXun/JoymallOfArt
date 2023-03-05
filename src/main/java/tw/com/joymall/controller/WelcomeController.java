package tw.com.joymall.controller;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import static org.apache.catalina.realm.RealmBase.Digest;
import org.apache.commons.mail.EmailException;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tw.com.joymall.Blowfish;
import tw.com.joymall.Utils;
import tw.com.joymall.entity.AllPayHistory;
import tw.com.joymall.entity.Banner;
import tw.com.joymall.entity.Bulletin;
import tw.com.joymall.entity.Cart;
import tw.com.joymall.entity.Forgot;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.Packet;
import tw.com.joymall.entity.Regular;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.AllPayHistoryRepository;
import tw.com.joymall.repository.BannerRepository;
import tw.com.joymall.repository.BulletinRepository;
import tw.com.joymall.repository.CartRepository;
import tw.com.joymall.repository.ForgotRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.MerchandiseRepository;
import tw.com.joymall.repository.PacketRepository;
import tw.com.joymall.repository.PacketStatusRepository;
import tw.com.joymall.repository.RegularRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.Services;

/**
 * 首頁
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/")
public class WelcomeController {

	@Autowired
	private AllPayHistoryRepository allPayHistoryRepository;

	@Autowired
	private BannerRepository bannerRepository;

	@Autowired
	private BulletinRepository bulletinRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ForgotRepository forgotRepository;

	@Autowired
	private MerchandiseRepository merchandiseRepository;

	@Autowired
	private MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	private PacketRepository packetRepository;

	@Autowired
	private PacketStatusRepository packetStatusRepository;

	@Autowired
	private RegularRepository regularRepository;

	@Autowired
	private StaffRepository staffRepository;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);

	@Autowired
	private javax.servlet.ServletContext context;

	@Autowired
	private Services services;

	/**
	 * 首頁
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	@SuppressWarnings({"UnusedAssignment", "null"})
	private ModelAndView welcome(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());
		if (request.getRemoteUser() != null) {
			documentElement.setAttribute("remoteUser", null);
		}
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			documentElement.setAttribute("me", me.toString());
		}

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		Connection connection = entityManager.unwrap(java.sql.Connection.class);
		Statement statement = connection.createStatement();
		ResultSet resultSet = null;

		/*
		 連播橫幅
		 */
		Element elementBanners = Utils.createElement("banners", documentElement);
		for (Banner banner : bannerRepository.findAll(new Sort(Sort.Direction.ASC, "ordinal"))) {
			String href = banner.getHref();
			boolean external = banner.isExternal();

			Element elementBanner = Utils.createElementWithAttribute("banner", elementBanners, "id", banner.getId().toString());
			if (href != null) {
				elementBanner.setAttribute("external", Boolean.toString(external));
				elementBanner.setTextContent(href);
			}
		}

		/*
		 店家熱銷
		 */
		Element elementTopSales = Utils.createElement("topSales", documentElement);
		try {
			resultSet = statement.executeQuery("SELECT\"merchandise\"AS\"id\"FROM\"artMall\".\"public\".\"Cart\"GROUP BY\"merchandise\"ORDER BY\"sum\"(\"quantity\")DESC LIMIT'12'");
			while (resultSet.next()) {
				Merchandise merchandise = merchandiseRepository.findOne(resultSet.getLong("id"));
				MerchandiseImage merchandiseImage = merchandiseImageRepository.findTopByMerchandiseOrderByOrdinal(merchandise);

				Element elementTopSale = Utils.createElementWithAttribute("topSale", elementTopSales, "id", merchandise.getId().toString());
				Utils.createElementWithTextContent("booth", elementTopSale, merchandise.getShelf().getBooth().getName());
				Utils.createElementWithTextContent("name", elementTopSale, merchandise.getName());
				Utils.createElementWithTextContent("price", elementTopSale, Integer.toString(merchandise.getPrice()));
				if (merchandiseImage != null) {
					Utils.createElementWithTextContent("merchandiseImageId", elementTopSale, merchandiseImage.getId().toString());
				}
			}
		} catch (SQLException sqlException) {
			System.err.println(getClass().getCanonicalName() + ":\n" + sqlException.getLocalizedMessage());
		}

		/*
		 推薦商品
		 */
		Element elementRecommendations = Utils.createElement("recommendations", documentElement);
		try {
			resultSet = statement.executeQuery("SELECT\"id\"FROM\"artMall\".\"public\".\"Merchandise\"WHERE\"recommended\"='t'ORDER BY\"random\"()LIMIT'15'");
			while (resultSet.next()) {
				Merchandise merchandise = merchandiseRepository.findOne(resultSet.getLong("id"));
				MerchandiseImage merchandiseImage = merchandiseImageRepository.findTopByMerchandiseOrderByOrdinal(merchandise);

				Element elementRecommendation = Utils.createElementWithAttribute("recommendation", elementRecommendations, "id", merchandise.getId().toString());
				Utils.createElementWithTextContent("booth", elementRecommendation, merchandise.getShelf().getBooth().getName());
				Utils.createElementWithTextContent("name", elementRecommendation, merchandise.getName());
				Utils.createElementWithTextContent("price", elementRecommendation, Integer.toString(merchandise.getPrice()));
				if (merchandiseImage != null) {
					Utils.createElementWithTextContent("merchandiseImageId", elementRecommendation, merchandiseImage.getId().toString());
				}
			}
		} catch (SQLException sqlException) {
			System.err.println(getClass().getCanonicalName() + ":\n" + sqlException.getLocalizedMessage());
		}

		try {
			if (connection != null) {
				if (statement != null) {
					if (resultSet != null) {
						resultSet.close();
					}
					statement.close();
				}
			}
		} catch (Exception exception) {
			System.err.println(exception.getLocalizedMessage());
		} finally {
			resultSet = null;
			statement = null;
			connection = null;
		}
		entityManager.getTransaction().rollback();
		entityManager.close();

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 最新消息
	 *
	 * @param request
	 * @return 網頁
	 */
	@RequestMapping(value = "/announcements.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView announcements(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		String remoteUser = request.getRemoteUser();
		if (remoteUser != null) {
			documentElement.setAttribute("remoteUser", remoteUser);
		}

		Element elementAnnouncements = Utils.createElement("announcements", documentElement);
		for (Bulletin bulletin : bulletinRepository.findAll(new Sort(Sort.Direction.DESC, "when"))) {
			Utils.createElementWithTextContentAndAttribute("announcement", elementAnnouncements, bulletin.getHtml(), "subject", bulletin.getSubject());
		}

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("announcements");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 關於我們
	 *
	 * @param request
	 * @return 網頁
	 */
	@RequestMapping(value = "/about.htm", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView about(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		String remoteUser = request.getRemoteUser();
		if (remoteUser != null) {
			documentElement.setAttribute("remoteUser", remoteUser);
		}

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "about.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("markup", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("about");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 個人隱私權
	 *
	 * @param request
	 * @return 網頁
	 */
	@RequestMapping(value = "/privacy.htm", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView privacy(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		String remoteUser = request.getRemoteUser();
		if (remoteUser != null) {
			documentElement.setAttribute("remoteUser", remoteUser);
		}

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "privacy.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("markup", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("privacy");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 退換貨條款
	 *
	 * @param request
	 * @return 網頁
	 */
	@RequestMapping(value = "/policy.htm", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView policy(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		String remoteUser = request.getRemoteUser();
		if (remoteUser != null) {
			documentElement.setAttribute("remoteUser", remoteUser);
		}

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "policy.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("markup", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("policy");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 購物權利聲明
	 *
	 * @param request
	 * @return 網頁
	 */
	@RequestMapping(value = "/statement.htm", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView statement(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		String remoteUser = request.getRemoteUser();
		if (remoteUser != null) {
			documentElement.setAttribute("remoteUser", remoteUser);
		}

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "statement.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("markup", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("statement");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 加入店家
	 *
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/register.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView register(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "register.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("jDialog", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("register");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 加入店家
	 *
	 * @param login 店家帳號
	 * @param shadow 密碼
	 * @param name 店家抬頭
	 * @param address 實體地址
	 * @param cellular 手機號碼
	 * @param phone 市內電話
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/register.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView register(@RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String shadow, @RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String address, @RequestParam(defaultValue = "") String cellular, @RequestParam(defaultValue = "") String phone, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}
		String errorMessage = null;

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		try {
			login = login.trim().toLowerCase();
			if (login.isEmpty()) {
				throw new NullPointerException();
			}

			if (!EmailValidator.getInstance(false, false).isValid(login)) {
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
		Utils.createElementWithTextContent("login", elementForm, login);

		try {
			if (shadow.isEmpty()) {
				throw new NullPointerException();
			}

			if (shadow.length() < 8) {
				errorMessage = "密碼必須為八碼以上！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "密碼為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("shadow", elementForm, shadow);

		try {
			name = name.trim();
			if (name.isEmpty()) {
				errorMessage = "店家抬頭為必填！";
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("name", elementForm, name);

		address = address.trim();
		Utils.createElementWithTextContent("address", elementForm, address);

		phone = phone.trim().replaceAll("\\D", "");
		Utils.createElementWithTextContent("phone", elementForm, phone);

		cellular = cellular.trim().replaceAll("\\D", "");
		Utils.createElementWithTextContent("cellular", elementForm, cellular);

		if (errorMessage == null) {
			Staff booth = new Staff(false, login, Digest(shadow, "MD5", "UTF-8"), name);
			if (!address.isEmpty()) {
				booth.setAddress(address);
			}
			if (!phone.isEmpty()) {
				booth.setPhone(phone);
			}
			if (!cellular.isEmpty()) {
				booth.setPhone(cellular);
			}
			staffRepository.saveAndFlush(booth);

			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/successfulRegister.xsl"))).transform(new DOMSource(elementForm), new StreamResult(stringWriter));
			stringWriter.flush();
			stringWriter.close();
			try {
				services.buildHtmlEmail(login, "店家註冊成功通知信", stringWriter.toString()).send();
			} catch (EmailException emailException) {
				System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
				emailException.printStackTrace(System.err);
			}

			return new ModelAndView("redirect:/cPanel/");
		}
		elementForm.setAttribute("error", errorMessage);

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "register.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("jDialog", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("register");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 加入會員
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/signUp.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView signUp(HttpServletRequest request, HttpSession session) throws ParserConfigurationException, IOException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN);
		gregorianCalendar.add(Calendar.YEAR, -18);

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		Utils.createElementWithTextContent("birth", elementForm, simpleDateFormat.format(gregorianCalendar.getTime()));

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "signUp.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("jDialog", documentElement, stringBuilder.toString());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("signUp");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 加入會員
	 *
	 * @param email 帳號(電子郵件)
	 * @param shadow 密碼
	 * @param lastname 姓氏
	 * @param firstname 名字
	 * @param birth 生日
	 * @param sex 性別
	 * @param phone 聯絡電話
	 * @param address 預設地址
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 * @throws ParserConfigurationException
	 * @throws SQLException
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	@RequestMapping(value = "/signUp.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView signUp(@RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "") String shadow, @RequestParam(defaultValue = "") String lastname, @RequestParam(defaultValue = "") String firstname, @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam Date birth, @RequestParam(defaultValue = "", name = "gender") String sex, @RequestParam String phone, @RequestParam String address, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}
		String errorMessage = null;

		GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN);
		gregorianCalendar.add(Calendar.YEAR, -18);

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		Utils.createElementWithTextContent("birth", elementForm, simpleDateFormat.format(gregorianCalendar.getTime()));

		Path path = Paths.get(context.getRealPath(context.getContextPath()), "WEB-INF", "htm", "signUp.htm");
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
			stringBuilder.append(line);
		}
		Utils.createElementWithTextContent("jDialog", documentElement, stringBuilder.toString());

		try {
			email = email.trim().toLowerCase();
			if (email.isEmpty()) {
				throw new NullPointerException();
			}

			if (!EmailValidator.getInstance(false, false).isValid(email)) {
				errorMessage = "錯誤的帳號(電子郵件)格式！";
			}

			if (regularRepository.countByEmail(email) > 0) {
				errorMessage = "已存在的帳號(電子郵件)！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "帳號(電子郵件)為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("email", elementForm, email);

		try {
			if (shadow.isEmpty()) {
				throw new NullPointerException();
			}

			if (shadow.length() < 8) {
				errorMessage = "密碼必須為八碼以上！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "密碼為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("shadow", elementForm, shadow);

		try {
			lastname = lastname.trim();
			if (lastname.isEmpty()) {
				errorMessage = "姓氏為必填！";
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("lastname", elementForm, lastname);

		try {
			firstname = firstname.trim();
			if (firstname.isEmpty()) {
				errorMessage = "名字為必填！";
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("firstname", elementForm, firstname);

		if (birth == null) {
			errorMessage = "生日為必選！";
		} else {
			Utils.createElementWithTextContent("birth", elementForm, simpleDateFormat.format(birth));
		}

		Boolean gender = null;
		if (sex.equalsIgnoreCase("false") || sex.equalsIgnoreCase("true")) {
			gender = Boolean.parseBoolean(sex);
		}
		if (gender == null) {
			errorMessage = "性別為必選！";
		} else {
			Utils.createElementWithTextContent("gender", elementForm, Boolean.toString(gender));
		}

		phone = phone.trim().replaceAll("\\D", "");
		Utils.createElementWithTextContent("phone", elementForm, phone);

		address = address.trim();
		Utils.createElementWithTextContent("address", elementForm, address);

		if (errorMessage == null) {
			Regular regular = new Regular(lastname, firstname, email, Digest(shadow, "SHA-512", "UTF-8"), birth, gender);
			if (!phone.isEmpty()) {
				regular.setPhone(phone);
			}
			if (!address.isEmpty()) {
				regular.setAddress(address);
			}
			regularRepository.saveAndFlush(regular);

			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/successfulSignUp.xsl"))).transform(new DOMSource(elementForm), new StreamResult(stringWriter));
			stringWriter.flush();
			stringWriter.close();
			try {
				services.buildHtmlEmail(email, "會員註冊成功通知信", stringWriter.toString()).send();
			} catch (EmailException emailException) {
				System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
				emailException.printStackTrace(System.err);
			}

			return new ModelAndView("redirect:/logIn.asp");
		}
		elementForm.setAttribute("error", errorMessage);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("signUp");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 登入控制臺
	 *
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/formLoginPage.htm", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView formLoginPage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		if (request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/cPanel/");
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		if (request.getRemoteUser() != null) {
			documentElement.setAttribute("remoteUser", null);
		}

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("formLoginPage");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 忘記並重設店家密碼
	 *
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/forgot.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView forgot(HttpServletRequest request, HttpSession session) throws ParserConfigurationException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("boothForgot");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 忘記並重設店家密碼
	 *
	 * @param login 店家帳號
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/forgot.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView forgot(@RequestParam String login, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}
		Staff booth = null;
		String errorMessage = null;

		try {
			login = login.trim().toLowerCase();
			if (login.isEmpty()) {
				throw new NullPointerException();
			}

			booth = staffRepository.findOneByLoginAndInternalFalse(login);
			if (booth == null) {
				errorMessage = "找嘸店家！";
			} else {
				if (booth.getShadow().matches("^[0-9A-Z]{32}$")) {
					errorMessage = "已被停權的店家！";
				}
			}

			if (forgotRepository.countByBooth(booth) > 0) {
				errorMessage = "您已申請重設店家密碼！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "店家帳號為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		Utils.createElementWithTextContent("login", elementForm, login);

		if (errorMessage == null) {
			String code = null;
			boolean existed = true;
			GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN);
			while (existed) {
				code = Blowfish.encrypt(Long.toBinaryString(gregorianCalendar.getTimeInMillis()), true);
				if (forgotRepository.countByCode(code) == 0) {
					existed = false;
				}
			}
			Forgot forgot = new Forgot(booth, code, gregorianCalendar.getTime());
			forgotRepository.saveAndFlush(forgot);

			Utils.createElementWithTextContent("name", documentElement, booth.getName());
			Utils.createElementWithTextContent("code", documentElement, code);

			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/notifyBoothThatForgotPassword.xsl"))).transform(new DOMSource(elementForm), new StreamResult(stringWriter));
			stringWriter.flush();
			stringWriter.close();
			try {
				services.buildHtmlEmail(login, "忘記並重設店家密碼", stringWriter.toString()).send();
			} catch (EmailException emailException) {
				System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
				emailException.printStackTrace(System.err);
			}

			errorMessage = "已將重設密碼的電子郵件寄出，請確認查收，謝謝！";
		}

		elementForm.setAttribute("error", errorMessage);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("boothForgot");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 忘記並重設店家密碼
	 *
	 * @param code 辨識碼
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/reset.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView reset(@RequestParam(defaultValue = "") String code, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}
		String requestURI = request.getRequestURI();

		Forgot forgot = forgotRepository.findOneByCode(code);
		if (forgot == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", requestURI);

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		Utils.createElementWithTextContent("code", elementForm, code);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("boothReset");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 忘記並重設店家密碼
	 *
	 * @param code 辨識碼
	 * @param login 店家帳號
	 * @param shadow 新密碼
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/reset.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView reset(@RequestParam(defaultValue = "") String code, @RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String shadow, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}
		String requestURI = request.getRequestURI();

		Forgot forgot = forgotRepository.findOneByCode(code);
		if (forgot == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return null;
		}
		Staff booth = forgot.getBooth();

		String errorMessage = null;

		try {
			login = login.trim().toLowerCase();
			if (login.isEmpty()) {
				throw new NullPointerException();
			}

			if (!booth.getLogin().equals(login)) {
				errorMessage = "錯誤的店家帳號！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "店家帳號為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		if (shadow.isEmpty()) {
			errorMessage = "新密碼為必填！";
		}
		if (shadow.length() < 8) {
			errorMessage = "新密碼必須為八碼以上！";
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", requestURI);

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		Utils.createElementWithTextContent("login", elementForm, login);
		Utils.createElementWithTextContent("code", elementForm, code);

		if (errorMessage == null) {
			booth.setShadow(Digest(shadow, "MD5", "UTF-8"));
			staffRepository.saveAndFlush(booth);

			forgotRepository.delete(forgot);
			forgotRepository.flush();

			Utils.createElementWithTextContent("name", elementForm, booth.getName());
			Utils.createElementWithTextContent("shadow", elementForm, shadow);

			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/successfulResetBooth.xsl"))).transform(new DOMSource(elementForm), new StreamResult(stringWriter));
			stringWriter.flush();
			stringWriter.close();
			try {
				services.buildHtmlEmail(login, "忘記並重設店家密碼完成通知", stringWriter.toString()).send();
			} catch (EmailException emailException) {
				System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
				emailException.printStackTrace(System.err);
			}

			return new ModelAndView("redirect:/cPanel/");
		}

		elementForm.setAttribute("error", errorMessage);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("boothReset");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 會員登入
	 *
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/logIn.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView logIn(HttpServletRequest request, HttpSession session) throws ParserConfigurationException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("logIn");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 會員登入
	 *
	 * @param email 帳號
	 * @param shadow 密碼
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/logIn.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView logIn(@RequestParam String email, @RequestParam String shadow, HttpServletRequest request, HttpSession session) throws ParserConfigurationException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		String errorMessage = null;
		Regular regular = regularRepository.findOneByEmail(email.trim().toLowerCase());
		if (regular == null) {
			errorMessage = "找嘸會員！";
		} else {
			if (!org.apache.catalina.realm.RealmBase.Digest(shadow, "SHA-512", "UTF-8").equals(regular.getShadow())) {
				errorMessage = "密碼不符！";
				System.err.println(org.apache.catalina.realm.RealmBase.Digest(shadow, "SHA-512", "UTF-8"));
				System.err.println(regular.getShadow());
			}
		}

		if (errorMessage == null) {
			session.setAttribute("me", regular.getId());

			Boolean isCheckingOut = (Boolean) session.getAttribute("checkingOut");
			if (isCheckingOut != null && isCheckingOut) {
				return new ModelAndView("redirect:/cart/");
			} else {
				return new ModelAndView("redirect:/");
			}
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("error", errorMessage);
		Utils.createElementWithTextContent("email", elementForm, email);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("logIn");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 登出
	 *
	 * @param session
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/logOut.asp", method = RequestMethod.GET)
	private ModelAndView logOut(HttpSession session) throws ParserConfigurationException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			session.removeAttribute("me");
		}
		return new ModelAndView("redirect:/");
	}

	/**
	 * 忘記並重設會員密碼
	 *
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/forgotAndResetPassword.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView forgotAndResetPassword(HttpServletRequest request, HttpSession session) throws ParserConfigurationException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN);
		gregorianCalendar.add(Calendar.YEAR, -18);

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		Utils.createElementWithTextContent("birth", elementForm, simpleDateFormat.format(gregorianCalendar.getTime()));

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("forgotAndResetPassword");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 忘記並重設會員密碼
	 *
	 * @param email 帳號(電子郵件)
	 * @param birth 生日
	 * @param sex 性別
	 * @param shadow 新密碼
	 * @param request
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/forgotAndResetPassword.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	@SuppressWarnings("null")
	private ModelAndView forgotAndResetPassword(@RequestParam String email, @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam Date birth, @RequestParam("gender") String sex, @RequestParam String shadow, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException {
		if ((Integer) session.getAttribute("me") != null || request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}
		String errorMessage = null;

		Boolean gender = null;
		Regular regular = regularRepository.findOneByEmail(email);
		if (regular == null) {
			errorMessage = "找嘸會員！";
		} else {
			if (birth == null) {
				errorMessage = "生日為必選！";
			} else {
				if (!birth.equals(regular.getBirth())) {
					errorMessage = "生日不符！";
				}
			}

			if (sex.equalsIgnoreCase("false") || sex.equalsIgnoreCase("true")) {
				gender = Boolean.parseBoolean(sex);
			}
			if (gender == null) {
				errorMessage = "性別為必選！";
			} else {
				if (!gender == regular.getGender()) {
					errorMessage = "性別不符！";
				}
			}

			String oldShadow = regular.getShadow();
			if (oldShadow.matches("^[0-9A-Z]{128}$")) {
				errorMessage = "此帳號停用中！";
			} else {
				try {
					if (shadow.isEmpty()) {
						throw new NullPointerException();
					}

					if (shadow.length() < 8) {
						errorMessage = "密碼必須為八碼以上！";
					}
				} catch (NullPointerException nullPointerException) {
					errorMessage = "密碼為必填！";
				} catch (Exception exception) {
					errorMessage = exception.getLocalizedMessage();
				}
			}
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("error", errorMessage);
		Utils.createElementWithTextContent("email", elementForm, email);
		Utils.createElementWithTextContent("birth", elementForm, simpleDateFormat.format(birth));
		if (gender != null) {
			Utils.createElementWithTextContent("gender", elementForm, Boolean.toString(gender));
		}

		if (errorMessage == null) {
			regular.setShadow(Digest(shadow, "SHA-512", "UTF-8"));
			regularRepository.saveAndFlush(regular);

			Utils.createElementWithTextContent("lastname", elementForm, regular.getLastname());
			Utils.createElementWithTextContent("firstname", elementForm, regular.getFirstname());
			Utils.createElementWithTextContent("shadow", elementForm, shadow);

			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/successfulResetRegular.xsl"))).transform(new DOMSource(elementForm), new StreamResult(stringWriter));
			stringWriter.flush();
			stringWriter.close();
			try {
				services.buildHtmlEmail(email, "忘記並重設會員密碼完成通知", stringWriter.toString()).send();
			} catch (EmailException emailException) {
				System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
				emailException.printStackTrace(System.err);
			}

			return new ModelAndView("redirect:/");
		}

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("forgotAndResetPassword");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 會員個人資料管理
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/me.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView me(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		if (request.getRemoteUser() != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		Integer me = (Integer) session.getAttribute("me");
		if (me == null) {
			return new ModelAndView("redirect:/logIn.asp");
		}

		Regular regular = regularRepository.findOne(me);
		if (regular == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return null;
		}
		String phone = regular.getPhone();
		String address = regular.getAddress();

		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", requestURI);
		documentElement.setAttribute("me", me.toString());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		Utils.createElementWithTextContent("lastname", elementForm, regular.getLastname());
		Utils.createElementWithTextContent("firstname", elementForm, regular.getFirstname());
		Utils.createElementWithTextContent("email", elementForm, regular.getEmail());
		Utils.createElementWithTextContent("birth", elementForm, simpleDateFormat.format(regular.getBirth()));
		Utils.createElementWithTextContent("gender", elementForm, Boolean.toString(regular.getGender()));
		Utils.createElementWithTextContent("phone", elementForm, phone == null ? "" : phone);
		Utils.createElementWithTextContent("address", elementForm, address == null ? "" : address);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("me");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 會員個人資料管理
	 *
	 * @param lastname 姓氏
	 * @param firstname 名字
	 * @param email 帳號
	 * @param birth 生日
	 * @param sex 性別
	 * @param phone 聯絡電話
	 * @param address 預設地址
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/me.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView me(@RequestParam(defaultValue = "") String lastname, @RequestParam(defaultValue = "") String firstname, @RequestParam(defaultValue = "") String email, @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(defaultValue = "") Date birth, @RequestParam(defaultValue = "", name = "gender") String sex, @RequestParam(defaultValue = "") String phone, @RequestParam(defaultValue = "") String address, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		if (request.getRemoteUser() != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		Integer me = (Integer) session.getAttribute("me");
		if (me == null) {
			return new ModelAndView("redirect:/logIn.asp");
		}

		Regular regular = regularRepository.findOne(me);
		if (regular == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return null;
		}

		String errorMessage = null;

		try {
			lastname = lastname.trim();
			if (lastname.length() == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "姓氏為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			firstname = firstname.trim();
			if (firstname.length() == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "名字為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			firstname = firstname.trim();
			if (firstname.length() == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "名字為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		try {
			email = email.trim().toLowerCase();
			if (email.isEmpty()) {
				throw new NullPointerException();
			}

			if (!EmailValidator.getInstance(false, false).isValid(email)) {
				errorMessage = "錯誤的帳號(電子郵件)格式！";
			}

			if (regularRepository.countByEmailAndIdNot(email, regular.getId()) > 0) {
				errorMessage = "已存在的帳號(電子郵件)！";
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "帳號(電子郵件)為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}

		if (birth == null) {
			errorMessage = "生日為必選！";
		}

		Boolean gender = null;
		if (sex.equalsIgnoreCase("false") || sex.equalsIgnoreCase("true")) {
			gender = Boolean.parseBoolean(sex);
		}
		if (gender == null) {
			errorMessage = "性別為必選！";
		}

		phone = phone.trim().replaceAll("\\D", "");
		if (phone.length() == 0) {
			phone = null;
		}

		address = address.trim();
		if (address.length() == 0) {
			address = null;
		}

		if (errorMessage == null) {
			regular.setLastname(lastname);
			regular.setFirstname(firstname);
			regular.setEmail(email);
			regular.setBirth(birth);
			regular.setGender(gender);
			regular.setPhone(phone);
			regular.setAddress(address);
			regularRepository.saveAndFlush(regular);

			return new ModelAndView("redirect:/me.asp");
		}

		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", requestURI);
		documentElement.setAttribute("me", me.toString());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("error", errorMessage);
		Utils.createElementWithTextContent("lastname", elementForm, lastname == null ? "" : lastname);
		Utils.createElementWithTextContent("firstname", elementForm, firstname == null ? "" : firstname);
		Utils.createElementWithTextContent("email", elementForm, email == null ? "" : email);
		Utils.createElementWithTextContent("birth", elementForm, birth == null ? "" : simpleDateFormat.format(birth));
		Utils.createElementWithTextContent("gender", elementForm, gender == null ? "" : Boolean.toString(gender));
		Utils.createElementWithTextContent("phone", elementForm, phone == null ? "" : phone);
		Utils.createElementWithTextContent("address", elementForm, address == null ? "" : address);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("me");
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
		if (request.getRemoteUser() != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		Integer me = (Integer) session.getAttribute("me");
		if (me == null) {
			return new ModelAndView("redirect:/logIn.asp");
		}

		String requestURI = request.getRequestURI();
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", requestURI);
		documentElement.setAttribute("me", me.toString());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Utils.createElementWithAttribute("form", documentElement, "action", requestURI);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("shadow");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 變更密碼
	 *
	 * @param shadow 新密碼
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/shadow.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView shadow(@RequestParam(defaultValue = "") String shadow, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException, TransformerConfigurationException, TransformerException, IOException {
		if (request.getRemoteUser() != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		Integer me = (Integer) session.getAttribute("me");
		if (me == null) {
			return new ModelAndView("redirect:/logIn.asp");
		}

		Regular regular = regularRepository.findOne(me);
		if (regular == null) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return null;
		}

		String errorMessage = null;
		if (shadow.isEmpty()) {
			errorMessage = "新密碼為必填！";
		}
		if (shadow.length() < 8) {
			errorMessage = "新密碼必須為八碼以上！";
		}

		if (errorMessage == null) {
			regular.setShadow(Digest(shadow, "SHA-512", "UTF-8"));
			regularRepository.saveAndFlush(regular);

			Document document = Utils.newDocument();

			Element elementForm = Utils.createElement("form", document);
			Utils.createElementWithTextContent("lastname", elementForm, regular.getLastname());
			Utils.createElementWithTextContent("firstname", elementForm, regular.getFirstname());
			Utils.createElementWithTextContent("email", elementForm, regular.getEmail());
			Utils.createElementWithTextContent("shadow", elementForm, shadow);

			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/successfulResetRegular.xsl"))).transform(new DOMSource(elementForm), new StreamResult(stringWriter));
			stringWriter.flush();
			stringWriter.close();
			try {
				services.buildHtmlEmail(regular.getEmail(), "變更會員密碼完成通知", stringWriter.toString()).send();
			} catch (EmailException emailException) {
				System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
				emailException.printStackTrace(System.err);
			}

			return new ModelAndView("redirect:/");
		}

		String requestURI = request.getRequestURI();
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", requestURI);
		documentElement.setAttribute("me", me.toString());

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("error", errorMessage);

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("shadow");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 歐付寶：付款結果通知
	 *
	 * @param merchantId 特店(廠商)編號
	 * @param merchantTradeNo 交易編號
	 * @param rtnCode 交易狀態
	 * @param rtnMsg 交易訊息
	 * @param tradeNo 交易編號
	 * @param tradeAmt 交易金額
	 * @param paymentDate 付款時間
	 * @param paymentType 會員選擇的付款方式
	 * @param paymentTypeChargeFee 通路費
	 * @param tradeDate 訂單成立時間
	 * @param simulatePaid 是否為模擬付款
	 * @param checkMacValue 檢查碼
	 * @return 純文字
	 */
	@RequestMapping(value = "/receivable.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	protected String receivable(@RequestParam(name = "MerchantID", defaultValue = "") String merchantId, @RequestParam(name = "MerchantTradeNo", defaultValue = "") String merchantTradeNo, @RequestParam(name = "RtnCode", required = false) Short rtnCode, @RequestParam(name = "RtnMsg", required = false) String rtnMsg, @RequestParam(name = "TradeNo", required = false) String tradeNo, @RequestParam(name = "TradeAmt", required = false) Integer tradeAmt, @RequestParam(name = "PaymentDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss") Date paymentDate, @RequestParam(name = "PaymentType", required = false) String paymentType, @RequestParam(name = "PaymentTypeChargeFee", required = false) Integer paymentTypeChargeFee, @RequestParam(name = "TradeDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss") Date tradeDate, @RequestParam(name = "SimulatePaid", required = false) Short simulatePaid, @RequestParam(name = "CheckMacValue", defaultValue = "") String checkMacValue) throws UnsupportedEncodingException, ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException {
		Packet packet = packetRepository.findOneByMerchantTradeNo(merchantTradeNo);
		if (packet == null) {
			return "0|找不到相對應的交易編號！";
		}

		Staff booth = packet.getBooth();
		if (!Objects.equals(merchantId, booth.getMerchantID())) {
			return "0|錯誤的特店(廠商)編號！";
		}

		@SuppressWarnings("LocalVariableHidesMemberVariable")
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Map<String, String> parameterMap = new TreeMap<>();
		parameterMap.put("MerchantID", merchantId);
		parameterMap.put("MerchantTradeNo", merchantTradeNo);
		parameterMap.put("RtnCode", rtnCode.toString());
		parameterMap.put("RtnMsg", rtnMsg);
		parameterMap.put("TradeNo", tradeNo);
		parameterMap.put("TradeAmt", tradeAmt.toString());
		parameterMap.put("PaymentDate", simpleDateFormat.format(paymentDate));
		parameterMap.put("PaymentType", paymentType);
		parameterMap.put("PaymentTypeChargeFee", paymentTypeChargeFee.toString());
		parameterMap.put("TradeDate", simpleDateFormat.format(tradeDate));
		parameterMap.put("SimulatePaid", simulatePaid.toString());
		StringBuilder stringBuilder = new StringBuilder("HashKey=" + booth.getHashKey());
		for (Map.Entry<String, String> entrySet : parameterMap.entrySet()) {
			stringBuilder.append("&").append(entrySet.getKey()).append("=").append(entrySet.getValue());
		}
		stringBuilder.append("&HashIV=").append(booth.getHashIV());
		String returnCheckMacValue = Utils.md5(URLEncoder.encode(stringBuilder.toString(), "UTF-8").toLowerCase()).toUpperCase();
		if (!checkMacValue.equals(returnCheckMacValue)) {
			return "0|錯誤的檢查碼！";
		}

		packet.setPacketStatus(packetStatusRepository.findOne((short) 1));
		packetRepository.saveAndFlush(packet);

		AllPayHistory allPayHistory = new AllPayHistory();
		allPayHistory.setPacket(packet);
		allPayHistory.setTradeDesc(tradeNo);
		allPayHistory.setRtnCode(rtnCode);
		allPayHistory.setRtnMsg(rtnMsg);
		allPayHistory.setTradeNo(tradeNo);
		allPayHistory.setTradeAmt(tradeAmt);
		allPayHistory.setPaymentDate(paymentDate);
		allPayHistory.setPaymentType(paymentType);
		allPayHistory.setPaymentTypeChargeFee(paymentTypeChargeFee);
		allPayHistory.setTradeDate(tradeDate);
		allPayHistory.setSimulatePaid(simulatePaid == 1);
		allPayHistory.setCheckMacValue(returnCheckMacValue);
		allPayHistoryRepository.saveAndFlush(allPayHistory);

		Regular regular = packet.getRegular();
		Document document = Utils.newDocument();
		Element documentElement = Utils.createElement("document", document);
		Utils.createElementWithTextContent("regular", documentElement, regular.getLastname() + regular.getFirstname());
		Utils.createElementWithTextContent("booth", documentElement, booth.getName());
		Utils.createElementWithTextContent("recipient", documentElement, packet.getRecipient());
		Utils.createElementWithTextContent("phone", documentElement, packet.getPhone());
		Utils.createElementWithTextContent("address", documentElement, packet.getAddress());
		Utils.createElementWithTextContent("total", documentElement, Integer.toString(packet.getTotalAmount()));
		Element elementPacket = Utils.createElement("packet", documentElement);
		for (Cart cart : cartRepository.findByPacket(packet)) {
			Merchandise merchandise = cart.getMerchandise();
			int price = merchandise.getPrice();
			String specification = cart.getSpecification();
			short quantity = cart.getQuantity();

			Element elementCart = Utils.createElementWithTextContent("cart", elementPacket, merchandise.getName());
			if (specification != null) {
				elementCart.setAttribute("specification", specification);
			}
			elementCart.setAttribute("price", Integer.toString(price));
			elementCart.setAttribute("quantity", Short.toString(quantity));
			elementCart.setAttribute("subTotal", Integer.toString(price * quantity));
		}
		StringWriter stringWriter = new StringWriter();
		TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream("/receivable.xsl"))).transform(new DOMSource(document), new StreamResult(stringWriter));
		stringWriter.flush();
		stringWriter.close();
		try {
			services.buildHtmlEmail(regular.getEmail(), "e95 易購物商城訂單", stringWriter.toString()).send();
		} catch (EmailException emailException) {
			System.err.println(getClass().getCanonicalName() + ":\n" + emailException.getLocalizedMessage());
			emailException.printStackTrace(System.err);
			return "0|" + emailException.getLocalizedMessage();
		}

		return "1|OK";
	}

	/*
	 @RequestMapping(value = "/encrypt.txt", produces = "text/plain;charset=UTF-8", method = RequestMethod.GET)
	 @ResponseBody
	 private String encrypt() throws ParserConfigurationException, SQLException, Exception {
	 final String credentials = "+++";
	 String digested = org.apache.catalina.realm.RealmBase.Digest(credentials, "MD5", "UTF-8");
	 String digestedSha = org.apache.catalina.realm.RealmBase.Digest(credentials, "SHA-512", "UTF-8");
	 String md5php = Utils.md5(credentials);
	 String blowfish = Blowfish.encrypt(credentials, true);
	 return digested + "\n" + md5php + "\n" + digestedSha + "\n" + blowfish;
	 }
	 */
}
