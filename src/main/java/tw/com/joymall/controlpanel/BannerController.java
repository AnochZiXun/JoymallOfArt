package tw.com.joymall.controlpanel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.io.IOUtils;
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
import tw.com.joymall.entity.Banner;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.BannerRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;
import tw.com.joymall.service.Services;

/**
 * 連播橫幅
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/banner")
public class BannerController {

	@Autowired
	private BannerRepository bannerRepository;

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private ControlPanelService controlPanelService;

	@Autowired
	private Services services;

	/**
	 * 列表(連播橫幅)
	 *
	 * @param size 每頁幾筆
	 * @param number 第幾頁
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(@RequestParam(value = "s", defaultValue = "10") Integer size, @RequestParam(value = "p", defaultValue = "0") Integer number, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException {
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
		Pageable pageRequest = new PageRequest(number, size, Sort.Direction.ASC, "ordinal");
		Page<Banner> pageOfEntities;
		pageOfEntities = bannerRepository.findAll(pageRequest);
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
		for (Banner entity : pageOfEntities.getContent()) {
			Element elementRow = Utils.createElement("row", elementList);
			Utils.createElementWithTextContent("id", elementRow, entity.getId().toString());
		}

		ModelAndView modelAndView = new ModelAndView("cPanel/banner/welcome");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 縮圖
	 *
	 * @param id 連播橫幅的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void thumbnail(@PathVariable Short id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止攤商存取
			return;
		}
		Banner banner = bannerRepository.findOne(id);
		if (banner == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = banner.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		InputStream inputStream = new ByteArrayInputStream(content);
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		Integer height = bufferedImage.getHeight();
		ImageIO.write(Utils.rescaleImage(content, bufferedImage.getWidth() / Math.round(height.floatValue() / 128), 128), "PNG", response.getOutputStream());
		IOUtils.closeQuietly(inputStream);
	}

	/**
	 * 上移連播橫幅
	 *
	 * @param id 連播橫幅的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/up.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String up(@PathVariable Short id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			return jsonObject.put("reason", "找不到工作人員或您為店家！").toString();
		}
		Banner banner = bannerRepository.findOne(id);
		if (banner == null) {
			return jsonObject.put("reason", "找不到連播橫幅！").toString();
		}

		controlPanelService.sortBanners();
		if (banner.getOrdinal() == 1) {
			return jsonObject.put("reason", "已達頂端！").toString();
		}

		short ordinal = banner.getOrdinal();
		Banner previousBanner = bannerRepository.findOneByOrdinal(Integer.valueOf(ordinal - 1).shortValue());
		short previousOrdinal = previousBanner.getOrdinal();

		banner.setOrdinal((short) -1);
		bannerRepository.saveAndFlush(banner);

		previousBanner.setOrdinal(ordinal);
		bannerRepository.saveAndFlush(previousBanner);

		banner.setOrdinal(previousOrdinal);
		bannerRepository.saveAndFlush(banner);

		return jsonObject.put("response", true).toString();
	}

	/**
	 * 下移連播橫幅
	 *
	 * @param id 連播橫幅的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/down.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String down(@PathVariable Short id, HttpServletRequest request, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null || !myself.isInternal()) {
			return jsonObject.put("reason", "找不到工作人員或您為店家！").toString();
		}
		Banner banner = bannerRepository.findOne(id);
		if (banner == null) {
			return jsonObject.put("reason", "找不到連播橫幅！").toString();
		}

		controlPanelService.sortBanners();
		if (banner.getOrdinal() >= bannerRepository.count()) {
			return jsonObject.put("reason", "已達底端！").toString();
		}

		short ordinal = banner.getOrdinal();
		Banner nextBanner = bannerRepository.findOneByOrdinal(Integer.valueOf(ordinal + 1).shortValue());
		short nextOrdinal = nextBanner.getOrdinal();

		banner.setOrdinal((short) -1);
		bannerRepository.saveAndFlush(banner);

		nextBanner.setOrdinal(ordinal);
		bannerRepository.saveAndFlush(nextBanner);

		banner.setOrdinal(nextOrdinal);
		bannerRepository.saveAndFlush(banner);

		return jsonObject.put("response", true).toString();
	}

	/**
	 * 刪除連播橫幅
	 *
	 * @param id 連播橫幅的主鍵
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
		Banner banner = bannerRepository.findOne(id);
		if (banner == null) {
			return jsonObject.put("reason", "找不到連播橫幅！").toString();
		}

		bannerRepository.delete(banner);
		bannerRepository.flush();

		controlPanelService.sortBanners();
		return jsonObject.put("response", true).toString();
	}

	/**
	 * 修改連播橫幅
	 *
	 * @param id 連播橫幅的主鍵
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
		Banner banner = bannerRepository.findOne(id);
		if (banner == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改連播橫幅");
		controlPanelService.loadBanner(banner, elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/banner/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改連播橫幅
	 *
	 * @param multipartFile 圖檔
	 * @param href 連結
	 * @param external 外部連結
	 * @param id 連播橫幅的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView update(@RequestParam(required = false, value = "content") MultipartFile multipartFile, @RequestParam(defaultValue = "") String href, @RequestParam(defaultValue = "false") boolean external, @PathVariable Short id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		Banner banner = bannerRepository.findOne(id);
		if (banner == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改連播橫幅");
		String errorMessage = controlPanelService.saveBanner(banner, multipartFile, href, external, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/banner/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/banner/");
	}

	/**
	 * 新增連播橫幅
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
		elementForm.setAttribute("legend", "新增連播橫幅");
		controlPanelService.loadBanner(new Banner(), elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/banner/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 新增連播橫幅
	 *
	 * @param multipartFile 圖檔
	 * @param href 連結
	 * @param external 外部連結
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/add.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView create(@RequestParam(required = false, value = "content") MultipartFile multipartFile, @RequestParam(defaultValue = "") String href, @RequestParam(defaultValue = "false") boolean external, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException {
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
		elementForm.setAttribute("legend", "新增連播橫幅");
		String errorMessage = controlPanelService.saveBanner(new Banner(), multipartFile, href, external, elementForm);

		if (errorMessage == null) {
			return new ModelAndView("redirect:/cPanel/banner/");
		}

		elementForm.setAttribute("error", errorMessage);
		ModelAndView modelAndView = new ModelAndView("cPanel/banner/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}
}
