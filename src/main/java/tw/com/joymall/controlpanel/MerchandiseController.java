package tw.com.joymall.controlpanel;

import java.io.IOException;
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
import tw.com.joymall.entity.InternalFrame;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.MerchandiseSpecification;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.InternalFrameRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.MerchandiseRepository;
import tw.com.joymall.repository.MerchandiseSpecificationRepository;
import tw.com.joymall.repository.ShelfRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;
import tw.com.joymall.service.Services;

/**
 * 商品
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/merchandise")
public class MerchandiseController {

	@Autowired
	private MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	private MerchandiseSpecificationRepository merchandiseSpecificationRepository;

	@Autowired
	private InternalFrameRepository internalFrameRepository;

	@Autowired
	private MerchandiseRepository merchandiseRepository;

	@Autowired
	private ShelfRepository shelfRepository;

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private ControlPanelService controlPanelService;

	@Autowired
	private Services services;

	/**
	 * 讀取商品
	 *
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView read(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		Shelf shelfMerchandise = merchandise.getShelf();
		if (!Objects.equals(myself, shelfMerchandise.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改商品");
		controlPanelService.loadMerchandise(merchandise, elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandise/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改商品
	 *
	 * @param selectedShelfId 選擇的商品分類主鍵
	 * @param name 商品名稱
	 * @param price 單價
	 * @param html 描述
	 * @param carrying 上架|下架
	 * @param inStock 有貨
	 * @param recommend 推薦
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView update(@RequestParam("shelf") Long selectedShelfId, @RequestParam String name, @RequestParam Integer price, @RequestParam String html, @RequestParam(defaultValue = "true") Boolean carrying, @RequestParam(defaultValue = "true") Boolean inStock, @RequestParam(defaultValue = "false") Boolean recommend, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		Shelf shelfMerchandise = merchandise.getShelf();
		if (!Objects.equals(myself, shelfMerchandise.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}
		Shelf shelfSelected = shelfRepository.findOne(selectedShelfId);
		if (shelfSelected == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品分類
			return null;
		}
		if (!Objects.equals(myself, shelfSelected.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//選擇的商品分類不屬於此攤商
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改商品");
		String errorMessage = controlPanelService.saveMerchandise(merchandise, shelfSelected, name, price, html, carrying, inStock, recommend, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/shelf/merchandise/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/shelf/" + selectedShelfId + "/");
	}

	/**
	 * 上架狀態
	 *
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/carrying.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String carrying(@PathVariable Long id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}
		if (!Objects.equals(myself, merchandise.getShelf().getBooth())) {
			return jsonObject.put("reason", "此商品不屬於您的！").toString();
		}

		merchandise.setCarrying(!merchandise.isCarrying());
		merchandiseRepository.saveAndFlush(merchandise);
		return jsonObject.put("redirect", "/cPanel/merchandise/" + id + ".asp").put("response", true).toString();
	}

	/**
	 * 庫儲狀態
	 *
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/inStock.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String inStock(@PathVariable Long id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}
		if (!Objects.equals(myself, merchandise.getShelf().getBooth())) {
			return jsonObject.put("reason", "此商品不屬於您的！").toString();
		}

		merchandise.setInStock(!merchandise.isInStock());
		merchandiseRepository.saveAndFlush(merchandise);
		return jsonObject.put("redirect", "/cPanel/merchandise/" + id + ".asp").put("response", true).toString();
	}

	/**
	 * 推薦與否
	 *
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/recommended.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String recommended(@PathVariable Long id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}
		if (!Objects.equals(myself, merchandise.getShelf().getBooth())) {
			return jsonObject.put("reason", "此商品不屬於您的！").toString();
		}

		merchandise.setRecommended(!merchandise.isRecommended());
		merchandiseRepository.saveAndFlush(merchandise);
		return jsonObject.put("redirect", "/cPanel/merchandise/" + id + ".asp").put("response", true).toString();
	}

	/**
	 * 列表(&lt;IFRAME/&gt;)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView internalFrames(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

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
		Pageable pageRequest = new PageRequest(number, size);
		Page<InternalFrame> pageOfEntities;
		pageOfEntities = internalFrameRepository.findByMerchandiseOrderByOrdinal(merchandise, pageRequest);
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

			Element rowElement = Utils.createElement("row", elementList);
			Utils.createElementWithTextContent("id", rowElement, entity.getId().toString());
			Utils.createElementWithTextContent("src", rowElement, entity.getSrc());
			Utils.createElementWithTextContent("width", rowElement, width == null ? "" : width.toString());
			Utils.createElementWithTextContent("height", rowElement, height == null ? "" : height.toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 上移 &lt;IFRAME/&gt;
	 *
	 * @param id 目前商品的主鍵
	 * @param internalFrameId &lt;IFRAME/&gt; 的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/{internalFrameId:\\d+}/up.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String internalFrameUp(@PathVariable Long id, @PathVariable Long internalFrameId, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(internalFrameId);
		if (internalFrame == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}

		controlPanelService.sortInternalFrameByMerchandise(merchandise);
		if (internalFrame.getOrdinal() == 1) {
			return jsonObject.put("reason", "已達頂端！").toString();
		}

		short ordinal = internalFrame.getOrdinal();
		InternalFrame previousInternalFrame = internalFrameRepository.findOneByMerchandiseAndOrdinal(merchandise, Integer.valueOf(ordinal - 1).shortValue());
		short previousOrdinal = previousInternalFrame.getOrdinal();

		internalFrame.setOrdinal((short) -1);
		internalFrameRepository.saveAndFlush(internalFrame);

		previousInternalFrame.setOrdinal(ordinal);
		internalFrameRepository.saveAndFlush(previousInternalFrame);

		internalFrame.setOrdinal(previousOrdinal);
		internalFrameRepository.saveAndFlush(internalFrame);

		controlPanelService.sortInternalFrameByMerchandise(merchandise);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 下移 &lt;IFRAME/&gt;
	 *
	 * @param id 目前商品的主鍵
	 * @param internalFrameId &lt;IFRAME/&gt; 的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/{internalFrameId:\\d+}/down.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String internalFrameDown(@PathVariable Long id, @PathVariable Long internalFrameId, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(internalFrameId);
		if (internalFrame == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}

		controlPanelService.sortInternalFrameByMerchandise(merchandise);
		if (internalFrame.getOrdinal() >= internalFrameRepository.countByMerchandise(merchandise)) {
			return jsonObject.put("reason", "已達底端！").toString();
		}

		short ordinal = internalFrame.getOrdinal();
		InternalFrame nextInternalFrame = internalFrameRepository.findOneByMerchandiseAndOrdinal(merchandise, Integer.valueOf(ordinal + 1).shortValue());
		short nextOrdinal = nextInternalFrame.getOrdinal();

		internalFrame.setOrdinal((short) -1);
		internalFrameRepository.saveAndFlush(internalFrame);

		nextInternalFrame.setOrdinal(ordinal);
		internalFrameRepository.saveAndFlush(nextInternalFrame);

		internalFrame.setOrdinal(nextOrdinal);
		internalFrameRepository.saveAndFlush(internalFrame);

		controlPanelService.sortInternalFrameByMerchandise(merchandise);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 新增 &lt;IFRAME/&gt;
	 *
	 * @param id 目前商品分類的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView createInternalFrame(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		Shelf shelfMerchandise = merchandise.getShelf();
		if (!Objects.equals(myself, shelfMerchandise.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
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
	 * 新增 &lt;IFRAME/&gt;
	 *
	 * @param src address of the resource
	 * @param width horizontal dimension
	 * @param height vertical dimension
	 * @param id 目前商品分類的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/internalFrame/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView createInternalFrame(@RequestParam String src, @RequestParam Short width, @RequestParam Short height, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		Shelf shelfMerchandise = merchandise.getShelf();
		if (!Objects.equals(myself, shelfMerchandise.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增 &#60;IFRAME&#47;&#62;");
		String errorMessage = controlPanelService.saveInternalFrame(new InternalFrame(), merchandise, src, width, height, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/merchandise/" + id + "/internalFrame/");
	}

	/**
	 * 列表(商品圖片)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseImage/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView merchandiseImages(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

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
		Pageable pageRequest = new PageRequest(number, size);
		Page<MerchandiseImage> pageOfEntities;
		pageOfEntities = merchandiseImageRepository.findByMerchandiseOrderByOrdinal(merchandise, pageRequest);
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
		for (MerchandiseImage entity : pageOfEntities.getContent()) {
			Element rowElement = Utils.createElement("row", elementList);
			Utils.createElementWithTextContent("id", rowElement, entity.getId().toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseImage/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品圖片
	 *
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseImage/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView createMerchandiseImage(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增商品圖片");

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseImage/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品圖片
	 *
	 * @param multipartFile 圖檔
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseImage/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView createMerchandiseImage(@RequestParam(required = false, value = "content") MultipartFile multipartFile, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String errorMessage = null, requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增商品圖片");

		byte[] content = null;
		try {
			content = multipartFile.getBytes();
			if (content.length == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "店家圖像為必選！";
		} catch (IOException ioException) {
			System.err.println(getClass().getCanonicalName() + ":\n" + ioException.getLocalizedMessage());
		}

		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseImage/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		MerchandiseImage merchandiseImage = new MerchandiseImage(merchandise, content, Long.valueOf(merchandiseImageRepository.countByMerchandise(merchandise) + 1).shortValue());
		merchandiseImageRepository.saveAndFlush(merchandiseImage);
		return new ModelAndView("redirect:/cPanel/merchandise/" + id + "/merchandiseImage/");
	}

	/**
	 * 上移商品圖片
	 *
	 * @param id 目前商品的主鍵
	 * @param merchandiseImageId 商品圖片的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseImage/{merchandiseImageId:\\d+}/up.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String merchandiseImageUp(@PathVariable Long id, @PathVariable Long merchandiseImageId, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(merchandiseImageId);
		if (merchandiseImage == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}

		controlPanelService.sortMerchandiseImageByMerchandise(merchandise);
		if (merchandiseImage.getOrdinal() == 1) {
			return jsonObject.put("reason", "已達頂端！").toString();
		}

		short ordinal = merchandiseImage.getOrdinal();
		MerchandiseImage previousMerchandiseImage = merchandiseImageRepository.findOneByMerchandiseAndOrdinal(merchandise, Integer.valueOf(ordinal - 1).shortValue());
		short previousOrdinal = previousMerchandiseImage.getOrdinal();

		merchandiseImage.setOrdinal((short) -1);
		merchandiseImageRepository.saveAndFlush(merchandiseImage);

		previousMerchandiseImage.setOrdinal(ordinal);
		merchandiseImageRepository.saveAndFlush(previousMerchandiseImage);

		merchandiseImage.setOrdinal(previousOrdinal);
		merchandiseImageRepository.saveAndFlush(merchandiseImage);

		controlPanelService.sortMerchandiseImageByMerchandise(merchandise);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 下移商品圖片
	 *
	 * @param id 目前商品的主鍵
	 * @param merchandiseImageId 商品圖片的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseImage/{merchandiseImageId:\\d+}/down.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String merchandiseImageDown(@PathVariable Long id, @PathVariable Long merchandiseImageId, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(merchandiseImageId);
		if (merchandiseImage == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找不到商品！").toString();
		}

		controlPanelService.sortMerchandiseImageByMerchandise(merchandise);
		if (merchandiseImage.getOrdinal() >= merchandiseImageRepository.countByMerchandise(merchandise)) {
			return jsonObject.put("reason", "已達底端！").toString();
		}

		short ordinal = merchandiseImage.getOrdinal();
		MerchandiseImage nextMerchandiseImage = merchandiseImageRepository.findOneByMerchandiseAndOrdinal(merchandise, Integer.valueOf(ordinal + 1).shortValue());
		short nextOrdinal = nextMerchandiseImage.getOrdinal();

		merchandiseImage.setOrdinal((short) -1);
		merchandiseImageRepository.saveAndFlush(merchandiseImage);

		nextMerchandiseImage.setOrdinal(ordinal);
		merchandiseImageRepository.saveAndFlush(nextMerchandiseImage);

		merchandiseImage.setOrdinal(nextOrdinal);
		merchandiseImageRepository.saveAndFlush(merchandiseImage);

		controlPanelService.sortMerchandiseImageByMerchandise(merchandise);
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 列表(商品規格)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseSpecification/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView merchandiseSpecifications(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

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
		Pageable pageRequest = new PageRequest(number, size);
		Page<MerchandiseSpecification> pageOfEntities;
		pageOfEntities = merchandiseSpecificationRepository.findByMerchandiseOrderByName(merchandise, pageRequest);
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
		for (MerchandiseSpecification entity : pageOfEntities.getContent()) {
			Element rowElement = Utils.createElement("row", elementList);
			Utils.createElementWithTextContent("id", rowElement, entity.getId().toString());
			Utils.createElementWithTextContent("name", rowElement, entity.getName());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseSpecification/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品規格
	 *
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseSpecification/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView createMerchandiseSpecifications(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增商品規格");
		controlPanelService.loadMerchandiseSpecification(new MerchandiseSpecification(), elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseSpecification/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品規格
	 *
	 * @param name 商品規格名稱
	 * @param id 目前商品的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandiseSpecification/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView createMerchandiseSpecifications(@RequestParam(defaultValue = "") String name, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "新增商品規格");
		String errorMessage = controlPanelService.saveMerchandiseSpecification(new MerchandiseSpecification(), merchandise, name, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseSpecification/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/merchandise/" + id + "/merchandiseSpecification/");
	}
}
