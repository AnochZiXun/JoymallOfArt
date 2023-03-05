package tw.com.joymall.controller;

import tw.com.joymall.entity.InternalFrame;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.ShelfRepository;
import tw.com.joymall.repository.InternalFrameRepository;
import tw.com.joymall.repository.StaffRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.*;
import tw.com.joymall.Utils;
import tw.com.joymall.service.Services;

/**
 * 攤商
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Controller
@RequestMapping("/store")
public class StoreController {

	@Autowired
	private InternalFrameRepository internalFrameRepository;

	@Autowired
	private ShelfRepository shelfRepository;

	@Autowired
	private StaffRepository staffRepository;

	@Autowired
	private Services services;

	/**
	 * 攤商簡介
	 *
	 * @param id 攤商的主鍵
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{id:[\\d]+}/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Staff booth = staffRepository.findOne(id);
		if (booth == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸攤商
			return null;
		}
		String html = booth.getHtml();
		Collection<InternalFrame> internalFrames = internalFrameRepository.findByBoothOrderByOrdinal(booth);
		Collection<Shelf> shelves = shelfRepository.findByBoothOrderByName(booth);

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

		Element elementBooth = Utils.createElementWithAttribute("booth", documentElement, "id", booth.getId().toString());
		Utils.createElementWithTextContent("name", elementBooth, booth.getName());
		Utils.createElementWithTextContent("html", elementBooth, html == null || html.isEmpty() ? "" : html);
		if (!internalFrames.isEmpty()) {
			Element elementInternalFrames = Utils.createElement("internalFrames", elementBooth);
			for (InternalFrame internalFrame : internalFrames) {
				Short width = internalFrame.getWidth(), height = internalFrame.getHeight();

				Element elementInternalFrame = Utils.createElementWithTextContent("internalFrame", elementInternalFrames, internalFrame.getSrc());
				if (width != null) {
					elementInternalFrame.setAttribute("width", width.toString());
				}
				if (height != null) {
					elementInternalFrame.setAttribute("height", height.toString());
				}
			}
		}

		if (!shelves.isEmpty()) {
			Element elementShelves = Utils.createElement("shelves", elementBooth);
			for (Shelf shelf : shelves) {
				Utils.createElementWithTextContentAndAttribute("shelf", elementShelves, shelf.getName(), "id", shelf.getId().toString());
			}
		}

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("store");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}
}
