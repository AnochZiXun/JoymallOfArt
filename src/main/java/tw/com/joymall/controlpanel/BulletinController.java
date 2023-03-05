package tw.com.joymall.controlpanel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
import org.springframework.format.annotation.DateTimeFormat;
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
import tw.com.joymall.entity.Bulletin;
import tw.com.joymall.entity.InternalFrame;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.BulletinRepository;
import tw.com.joymall.repository.InternalFrameRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;
import tw.com.joymall.service.Services;

/**
 * 最新消息
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/bulletin")
public class BulletinController {

	@Autowired
	private BulletinRepository bulletinRepository;

	@Autowired
	private InternalFrameRepository internalFrameRepository;

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private ControlPanelService controlPanelService;

	@Autowired
	private Services services;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);

	/**
	 * 列表(攤商)
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
		Element elementPagination = Utils.createElementWithAttribute("pagination", documentElement, "action", request.getRequestURI());
		if (size < 1) {
			size = 10;//每頁幾筆
		}
		if (number < 0) {
			number = 0;//第幾頁
		}
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.DESC, "when");
		Page<Bulletin> pageOfEntities;
		pageOfEntities = bulletinRepository.findAll(pageRequest);
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
		for (Bulletin entity : pageOfEntities.getContent()) {
			Element elementRow = Utils.createElement("row", elementList);
			Utils.createElementWithTextContent("id", elementRow, entity.getId().toString());
			Utils.createElementWithTextContent("subject", elementRow, entity.getSubject());
			Utils.createElementWithTextContent("when", elementRow, simpleDateFormat.format(entity.getWhen()));
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/bulletin/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改最新消息
	 *
	 * @param id 最新消息的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView read(@PathVariable Short id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改最新消息");
		controlPanelService.loadBulletin(bulletin, elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/bulletin/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改最新消息
	 *
	 * @param subject 主旨
	 * @param html 內容
	 * @param when 發佈日期
	 * @param id 最新消息的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView update(@RequestParam String subject, @RequestParam String html, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date when, @PathVariable Short id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改最新消息");
		String errorMessage = controlPanelService.saveBulletin(bulletin, subject, html, when, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/bulletin/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/bulletin/");
	}

	/**
	 * 列表(&#60;IFRAME&#47;&#62;)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param id 目前最新消息的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView bulletinInternalFrames(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, @PathVariable Short id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
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
		Pageable pageRequest = new PageRequest(number, size);
		Page<InternalFrame> pageOfEntities;
		pageOfEntities = internalFrameRepository.findByBulletinOrderByOrdinal(bulletin, pageRequest);
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
		for (InternalFrame entity : pageOfEntities.getContent()) {
			Short width = entity.getWidth(), height = entity.getHeight();

			Element elementRow = Utils.createElement("row", elementList);
			Utils.createElementWithTextContent("id", elementRow, entity.getId().toString());
			Utils.createElementWithTextContent("src", elementRow, entity.getSrc());
			Utils.createElementWithTextContent("width", elementRow, width == null ? "" : width.toString());
			Utils.createElementWithTextContent("height", elementRow, height == null ? "" : height.toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 刪除最新消息
	 *
	 * @param id 最新消息的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/remove.json", produces = "application/json;charset=UTF-8", method = RequestMethod.DELETE)
	@ResponseBody
	private String remove(@PathVariable Short id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			return jsonObject.put("reason", "找不到工作人員或您為店家！").toString();
		}
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			return jsonObject.put("reason", "找不到最新消息！").toString();
		}

		if (internalFrameRepository.countByBulletin(bulletin) > 0) {
			return jsonObject.put("reason", "此最新消息下尚有關聯的 <IFRAME/> 故無法刪除！").toString();
		}

		bulletinRepository.delete(bulletin);
		bulletinRepository.flush();
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 新增 &#60;IFRAME&#47;&#62;
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView createInternalFrame(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增 &#60;IFRAME&#47;&#62;");
		controlPanelService.loadInternalFrame(new InternalFrame(), elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增 &#60;IFRAME&#47;&#62;
	 *
	 * @param src address of the resource
	 * @param width horizontal dimension
	 * @param height vertical dimension
	 * @param id 目前最新消息的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView createInternalFrame(@RequestParam String src, @RequestParam Short width, @RequestParam Short height, @PathVariable Short id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增 &#60;IFRAME&#47;&#62;");
		String errorMessage = controlPanelService.saveInternalFrame(new InternalFrame(), bulletin, src, width, height, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/bulletin/" + id + "/internalFrame/");
	}

	/**
	 * 新增最新消息
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
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止攤商存取
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增最新消息");
		controlPanelService.loadBulletin(new Bulletin(), elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/bulletin/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增最新消息
	 *
	 * @param subject 主旨
	 * @param html 內容
	 * @param when 發佈日期
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView create(@RequestParam String subject, @RequestParam String html, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date when, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增最新消息");
		String errorMessage = controlPanelService.saveBulletin(new Bulletin(), subject, html, when, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/bulletin/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/bulletin/");
	}

	/**
	 * 上移 &lt;IFRAME/&gt;
	 *
	 * @param id 目前最新消息的主鍵
	 * @param internalFrameId &lt;IFRAME/&gt; 的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/{internalFrameId:\\d+}/up.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String internalFrameUp(@PathVariable Short id, @PathVariable Long internalFrameId, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			return jsonObject.put("reason", "找不到工作人員或您為店家！").toString();
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(internalFrameId);
		if (internalFrame == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}

		controlPanelService.sortInternalFrameByBulletin(bulletin);
		if (internalFrame.getOrdinal() == 1) {
			return jsonObject.put("reason", "已達頂端！").toString();
		}

		short ordinal = internalFrame.getOrdinal();
		InternalFrame previousinternalFrame = internalFrameRepository.findOneByBulletinAndOrdinal(bulletin, Integer.valueOf(ordinal - 1).shortValue());
		short previousOrdinal = previousinternalFrame.getOrdinal();

		internalFrame.setOrdinal((short) -1);
		internalFrameRepository.saveAndFlush(internalFrame);

		previousinternalFrame.setOrdinal(ordinal);
		internalFrameRepository.saveAndFlush(previousinternalFrame);

		internalFrame.setOrdinal(previousOrdinal);
		internalFrameRepository.saveAndFlush(internalFrame);

		controlPanelService.sortInternalFrameByBulletin(bulletin);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 下移 &lt;IFRAME/&gt;
	 *
	 * @param id 目前最新消息的主鍵
	 * @param internalFrameId &lt;IFRAME/&gt; 的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/{internalFrameId:\\d+}/down.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String internalFrameDown(@PathVariable Short id, @PathVariable Long internalFrameId, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			return jsonObject.put("reason", "找不到工作人員或您為店家！").toString();
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(internalFrameId);
		if (internalFrame == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}
		Bulletin bulletin = bulletinRepository.findOne(id);
		if (bulletin == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}

		controlPanelService.sortInternalFrameByBulletin(bulletin);
		if (internalFrame.getOrdinal() >= internalFrameRepository.countByBulletin(bulletin)) {
			return jsonObject.put("reason", "已達底端！").toString();
		}

		short ordinal = internalFrame.getOrdinal();
		InternalFrame nextinternalFrame = internalFrameRepository.findOneByBulletinAndOrdinal(bulletin, Integer.valueOf(ordinal + 1).shortValue());
		short nextOrdinal = nextinternalFrame.getOrdinal();

		internalFrame.setOrdinal((short) -1);
		internalFrameRepository.saveAndFlush(internalFrame);

		nextinternalFrame.setOrdinal(ordinal);
		internalFrameRepository.saveAndFlush(nextinternalFrame);

		internalFrame.setOrdinal(nextOrdinal);
		internalFrameRepository.saveAndFlush(internalFrame);

		controlPanelService.sortInternalFrameByBulletin(bulletin);
		return jsonObject.put("response", true).toString();
	}
}
