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
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.MerchandiseRepository;
import tw.com.joymall.repository.ShelfRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;
import tw.com.joymall.service.Services;

/**
 * 商品分類
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/shelf")
public class ShelfController {

	@Autowired
	MerchandiseRepository merchandiseRepository;

	@Autowired
	ShelfRepository shelfRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	ControlPanelService controlPanelService;

	@Autowired
	Services services;

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
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
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
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.ASC, "name");
		Page<Shelf> pageOfEntities;
		pageOfEntities = shelfRepository.findByBooth(myself, pageRequest);
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
		for (Shelf entity : pageOfEntities.getContent()) {
			Element rowElement = Utils.createElement("row", listElement);
			Utils.createElementWithTextContent("id", rowElement, entity.getId().toString());
			Utils.createElementWithTextContent("name", rowElement, entity.getName());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/shelf/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品分類
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
		if (myself == null || myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止工作人員存取
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "新增商品分類");
		controlPanelService.loadShelf(new Shelf(), elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/shelf/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品分類
	 *
	 * @param name 商品分類名稱
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView create(@RequestParam String name, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "新增商品分類");
		String errorMessage = controlPanelService.saveShelf(new Shelf(), myself, name, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			Utils.createElementWithTextContent("name", elementForm, name);

			ModelAndView modelAndView = new ModelAndView("cPanel/shelf/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/shelf/");
	}

	/**
	 * 讀取商品分類
	 *
	 * @param id 主鍵
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
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品分類
			return null;
		}
		if (!Objects.equals(myself, shelf.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "修改商品分類");
		controlPanelService.loadShelf(shelf, elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/shelf/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改商品分類
	 *
	 * @param id 主鍵
	 * @param name 商品分類名稱
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView update(@PathVariable Long id, @RequestParam String name, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品分類
			return null;
		}
		if (!Objects.equals(myself, shelf.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "修改商品分類");
		String errorMessage = controlPanelService.saveShelf(shelf, myself, name, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			Utils.createElementWithTextContent("name", elementForm, name);

			ModelAndView modelAndView = new ModelAndView("cPanel/shelf/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/shelf/");
	}

	/**
	 * 刪除商品分類
	 *
	 * @param id 主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/remove.json", produces = "application/json;charset=UTF-8", method = RequestMethod.DELETE)
	@ResponseBody
	private String remove(@PathVariable Long id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || myself.isInternal()) {
			return jsonObject.put("reason", "找不到店家或您為工作人員！").toString();
		}
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			return jsonObject.put("reason", "找不到商品分類！").toString();
		}
		if (!Objects.equals(myself, shelf.getBooth())) {
			return jsonObject.put("reason", "此商品分類不屬於您的！").toString();
		}

		if (merchandiseRepository.countByShelf(shelf) > 0) {
			return jsonObject.put("reason", "此商品分類下尚有關聯的商品故無法刪除！").toString();
		}

		shelfRepository.delete(shelf);
		shelfRepository.flush();
		return jsonObject.put("redirect", "/cPanel/shelf/").put("response", true).toString();
	}

	/**
	 * 列表(商品)
	 *
	 * @param id 主鍵
	 * @return 重導向
	 */
	@RequestMapping(value = "/{id:\\d+}/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView merchandises(@PathVariable Long id) throws ParserConfigurationException, SQLException {
		return new ModelAndView("redirect:/cPanel/shelf/" + id + "/merchandise/");
	}

	/**
	 * 列表(商品)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param id 主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandise/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView merchandises(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品分類
			return null;
		}
		if (!Objects.equals(myself, shelf.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
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
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.ASC, "name");
		Page<Merchandise> pageOfEntities;
		pageOfEntities = merchandiseRepository.findByShelf(shelf, pageRequest);
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
		for (Merchandise entity : pageOfEntities.getContent()) {
			Element rowElement = Utils.createElement("row", listElement);
			Utils.createElementWithTextContent("id", rowElement, entity.getId().toString());
			Utils.createElementWithTextContent("name", rowElement, entity.getName());
			Utils.createElementWithTextContent("price", rowElement, Integer.toString(entity.getPrice()));
			Utils.createElementWithTextContent("carrying", rowElement, Boolean.toString(entity.isCarrying()));
			Utils.createElementWithTextContent("inStock", rowElement, Boolean.toString(entity.isInStock()));
			Utils.createElementWithTextContent("recommended", rowElement, Boolean.toString(entity.isRecommended()));
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandise/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品
	 *
	 * @param id 目前商品分類的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandise/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView createMerchandise(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品分類
			return null;
		}
		if (!Objects.equals(myself, shelf.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "新增商品");
		controlPanelService.loadMerchandise(new Merchandise(), elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandise/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增商品
	 *
	 * @param name 商品名稱
	 * @param price 單價
	 * @param html HTML內容(描述)
	 * @param carrying 上架|下架
	 * @param inStock 有貨
	 * @param recommend 推薦
	 * @param id 目前商品分類的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}/merchandise/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView createMerchandise(@RequestParam String name, @RequestParam Integer price, @RequestParam String html, @RequestParam(defaultValue = "true") Boolean carrying, @RequestParam(defaultValue = "true") Boolean inStock, @RequestParam(defaultValue = "false") Boolean recommend, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品分類
			return null;
		}
		if (!Objects.equals(myself, shelf.getBooth())) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//此商品分類不屬於此攤商
			return null;
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", request.getRequestURI());
		elementForm.setAttribute("legend", "新增商品");
		String errorMessage = controlPanelService.saveMerchandise(new Merchandise(), shelf, name, price, html, carrying, inStock, recommend, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/merchandise/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/shelf/" + id + "/");
	}
}
