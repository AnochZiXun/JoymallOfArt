package tw.com.joymall.controlpanel;

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import tw.com.joymall.entity.MerchandiseSpecification;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.MerchandiseSpecificationRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;
import tw.com.joymall.service.Services;

/**
 * 商品規格
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/merchandiseSpecification")
public class MerchandiseSpecificationController {

	@Autowired
	MerchandiseSpecificationRepository merchandiseSpecificationRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	ControlPanelService controlPanelService;

	@Autowired
	Services services;

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
		MerchandiseSpecification merchandiseSpecification = merchandiseSpecificationRepository.findOne(id);
		if (merchandiseSpecification == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品規格
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改商品規格");
		controlPanelService.loadMerchandiseSpecification(merchandiseSpecification, elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseSpecification/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	private ModelAndView update(@RequestParam(defaultValue = "") String name, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
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
		MerchandiseSpecification merchandiseSpecification = merchandiseSpecificationRepository.findOne(id);
		if (merchandiseSpecification == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的商品
			return null;
		}
		Merchandise merchandise = merchandiseSpecification.getMerchandise();
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改商品規格");
		String errorMessage = controlPanelService.saveMerchandiseSpecification(merchandiseSpecification, merchandise, name, elementForm);
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/merchandiseSpecification/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView("redirect:/cPanel/merchandise/" + merchandise.getId() + "/merchandiseSpecification/");
	}

	/**
	 * 刪除商品規格
	 *
	 * @param id 商品規格的主鍵
	 * @param request
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
		MerchandiseSpecification merchandiseSpecification = merchandiseSpecificationRepository.findOne(id);
		if (merchandiseSpecification == null) {
			return jsonObject.put("reason", "找不到商品規格！").toString();
		}

		merchandiseSpecificationRepository.delete(merchandiseSpecification);
		merchandiseSpecificationRepository.flush();
		return jsonObject.put("response", true).toString();
	}
}
