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
import tw.com.joymall.entity.Bulletin;
import tw.com.joymall.entity.InternalFrame;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.InternalFrameRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;
import tw.com.joymall.service.Services;

/**
 * nested browsing context
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/internalFrame")
public class InternalFrameController {

	@Autowired
	InternalFrameRepository internalFrameRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	ControlPanelService controlPanelService;

	@Autowired
	Services services;

	/**
	 * 修改 &lt;IFRAME/&gt;
	 *
	 * @param id &lt;IFRAME/&gt; 的主鍵
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
		if (myself == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(id);
		if (internalFrame == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的 &lt;IFRAME/&gt;
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改 &#60;IFRAME&#47;&#62;");
		controlPanelService.loadInternalFrame(internalFrame, elementForm);

		ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/form");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 修改 &lt;IFRAME/&gt;
	 *
	 * @param src address of the resource
	 * @param width horizontal dimension
	 * @param height vertical dimension
	 * @param id 目前 &lt;IFRAME/&gt; 的主鍵
	 * @param internalFrameId &lt;IFRAME/&gt; 的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:\\d+}.asp", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
	@SuppressWarnings("UnusedAssignment")
	private ModelAndView update(@RequestParam String src, @RequestParam Short width, @RequestParam Short height, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);//禁止會員存取
			return null;
		}
		Staff myself = staffRepository.findOneByLogin(request.getRemoteUser());
		if (myself == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸
			return null;
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(id);
		if (internalFrame == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//不存在的 &lt;IFRAME/&gt;
			return null;
		}
		String requestURI = request.getRequestURI();

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "internal", Boolean.toString(myself.isInternal()));
		documentElement.setAttribute("requestURI", requestURI);
		services.buildAsideElement(document, request);

		Element elementForm = Utils.createElementWithAttribute("form", documentElement, "action", requestURI);
		elementForm.setAttribute("legend", "修改 &#60;IFRAME&#47;&#62;");
		String errorMessage = null, redirect = null;
		Staff booth = internalFrame.getBooth();
		if (booth != null) {
			errorMessage = controlPanelService.saveInternalFrame(internalFrame, booth, src, width, height, elementForm);
			redirect = "redirect:/cPanel/booth/internalFrame/";
		}
		Bulletin bulletin = internalFrame.getBulletin();
		if (bulletin != null) {
			errorMessage = controlPanelService.saveInternalFrame(internalFrame, bulletin, src, width, height, elementForm);
			redirect = "redirect:/cPanel/bulletin/" + bulletin.getId() + "/internalFrame/";
		}
		Merchandise merchandise = internalFrame.getMerchandise();
		if (merchandise != null) {
			errorMessage = controlPanelService.saveInternalFrame(internalFrame, merchandise, src, width, height, elementForm);
			redirect = "redirect:/cPanel/merchandise/" + merchandise.getId() + "/internalFrame/";
		}
		if (errorMessage != null) {
			elementForm.setAttribute("error", errorMessage);

			ModelAndView modelAndView = new ModelAndView("cPanel/internalFrame/form");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}

		return new ModelAndView(redirect);
	}

	/**
	 * 刪除 &lt;IFRAME/&gt;
	 *
	 * @param id &lt;IFRAME/&gt; 的主鍵
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{id:\\d+}/remove.json", produces = "application/json;charset=UTF-8", method = RequestMethod.DELETE)
	@ResponseBody
	private String remove(@PathVariable Long id, HttpSession session) throws ParserConfigurationException, SQLException {
		JSONObject jsonObject = new JSONObject();
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			return jsonObject.put("reason", "一般會員無法使用！").toString();
		}
		InternalFrame internalFrame = internalFrameRepository.findOne(id);
		if (internalFrame == null) {
			return jsonObject.put("reason", "找不到 &lt;IFRAME/&gt;！").toString();
		}

		Staff booth = internalFrame.getBooth();
		Bulletin bulletin = internalFrame.getBulletin();
		Merchandise merchandise = internalFrame.getMerchandise();

		internalFrameRepository.delete(internalFrame);
		internalFrameRepository.flush();

		if (booth != null) {
			controlPanelService.sortInternalFrameByBooth(booth);
		}
		if (bulletin != null) {
			controlPanelService.sortInternalFrameByBulletin(bulletin);
		}
		if (merchandise != null) {
			controlPanelService.sortInternalFrameByMerchandise(null);
		}
		return jsonObject.put("response", true).toString();
	}
}
