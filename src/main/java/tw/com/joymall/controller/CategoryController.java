package tw.com.joymall.controller;

import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.repository.ShelfRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.MerchandiseRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.*;
import tw.com.joymall.Utils;

/**
 * 商品分類
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Controller
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private MerchandiseRepository merchandiseRepository;

	@Autowired
	private MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	ShelfRepository shelfRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	private tw.com.joymall.service.Services services;

	@RequestMapping(value = "/{id:[\\d]+}/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Shelf shelf = shelfRepository.findOne(id);
		if (shelf == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸商品分類
			return null;
		}
		Staff booth = shelf.getBooth();

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

		Utils.createElementWithTextContentAndAttribute("shelf", elementBooth, shelf.getName(), "id", shelf.getId().toString());

		Element elementShelves = Utils.createElement("shelves", elementBooth);
		for (Shelf s : shelfRepository.findByBoothOrderByName(booth)) {
			Utils.createElementWithTextContentAndAttribute("shelf", elementShelves, s.getName(), "id", s.getId().toString());
		}

		Element elementMerchandises = Utils.createElement("merchandises", elementBooth);
		for (Merchandise merchandise : merchandiseRepository.findByShelf(shelf)) {
			Element elementMerchandise = Utils.createElementWithTextContent("merchandise", elementMerchandises, merchandise.getName());
			elementMerchandise.setAttribute("id", merchandise.getId().toString());
			if (merchandiseImageRepository.countByMerchandise(merchandise) > 0) {
				elementMerchandise.setAttribute("merchandiseImageId", merchandiseImageRepository.findTopByMerchandiseOrderByOrdinal(merchandise).getId().toString());
			}
			elementMerchandise.setAttribute("price", Integer.toString(merchandise.getPrice()));
		}

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("category");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}
}
