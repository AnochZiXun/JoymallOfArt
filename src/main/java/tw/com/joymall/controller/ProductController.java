package tw.com.joymall.controller;

import tw.com.joymall.entity.MerchandiseSpecification;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.repository.ShelfRepository;
import tw.com.joymall.repository.MerchandiseSpecificationRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.MerchandiseRepository;
import java.sql.SQLException;
import java.util.*;
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
 * 商品
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private MerchandiseRepository merchandiseRepository;

	@Autowired
	private MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	private MerchandiseSpecificationRepository merchandiseSpecificationRepository;

	@Autowired
	ShelfRepository shelfRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	private tw.com.joymall.service.Services services;

	@RequestMapping(value = "/{id:[\\d]+}/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException, SQLException {
		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);//找嘸產品
			return null;
		}
		Staff booth = merchandise.getShelf().getBooth();
		Collection<MerchandiseImage> merchandiseImages = merchandiseImageRepository.findByMerchandiseOrderByOrdinal(merchandise);
		Collection<MerchandiseSpecification> merchandiseSpecifications = merchandiseSpecificationRepository.findByMerchandiseOrderByName(merchandise);

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
		ArrayList<Merchandise> otherMerchandises = new ArrayList<>();

		Element elementShelves = Utils.createElement("shelves", elementBooth);
		for (Shelf shelf : shelfRepository.findByBoothOrderByName(booth)) {
			Utils.createElementWithTextContentAndAttribute("shelf", elementShelves, shelf.getName(), "id", shelf.getId().toString());

			otherMerchandises.addAll(merchandiseRepository.findByShelfAndIdNot(shelf, merchandise.getId()));
		}

		Element elementOtherMerchandises = Utils.createElement("otherMerchandises", elementBooth);
		for (Merchandise otherMerchandise : otherMerchandises) {
			Element elementOtherMerchandise = Utils.createElementWithTextContent("otherMerchandise", elementOtherMerchandises, otherMerchandise.getName());
			elementOtherMerchandise.setAttribute("id", otherMerchandise.getId().toString());
			elementOtherMerchandise.setAttribute("price", Integer.toString(otherMerchandise.getPrice()));
			if (merchandiseImageRepository.countByMerchandise(otherMerchandise) > 0) {
				elementOtherMerchandise.setAttribute("merchandiseImageId", merchandiseImageRepository.findTopByMerchandiseOrderByOrdinal(otherMerchandise).getId().toString());
			}
		}

		Element elementMerchandise = Utils.createElement("merchandise", elementBooth);
		Utils.createElementWithTextContent("name", elementMerchandise, merchandise.getName());
		Utils.createElementWithTextContent("price", elementMerchandise, Integer.toString(merchandise.getPrice()));
		Utils.createElementWithTextContent("html", elementMerchandise, merchandise.getHtml());
		Utils.createElementWithTextContent("carrying", elementMerchandise, Boolean.toString(merchandise.isCarrying()));

		if (!merchandiseImages.isEmpty()) {
			Element elementImages = Utils.createElement("images", elementMerchandise);
			for (MerchandiseImage merchandiseImage : merchandiseImages) {
				Utils.createElementWithAttribute("image", elementImages, "id", merchandiseImage.getId().toString());
			}
		}

		if (!merchandiseSpecifications.isEmpty()) {
			Element elementSpecifications = Utils.createElement("specifications", elementMerchandise);
			for (MerchandiseSpecification merchandiseSpecification : merchandiseSpecifications) {
				Utils.createElementWithTextContentAndAttribute("option", elementSpecifications, merchandiseSpecification.getName(), "value", merchandiseSpecification.getId().toString());
			}
		}

		/*
		 底部
		 */
		services.buildFooterElement(documentElement);

		ModelAndView modelAndView = new ModelAndView("product");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	@RequestMapping(value = "/{id:[\\d]+}/add.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String add(@PathVariable Long id, @RequestParam(name = "specification", required = false) Long merchandiseSpecificationId, @RequestParam(required = false) Short quantity, HttpSession session) {
		JSONObject jsonObject = new JSONObject();

		Merchandise merchandise = merchandiseRepository.findOne(id);
		if (merchandise == null) {
			return jsonObject.put("reason", "找嘸此產品！").toString();
		}
		Integer boothId = merchandise.getShelf().getBooth().getId();

		if (quantity == null) {
			return jsonObject.put("reason", "數量為必填！！").toString();
		}
		if (quantity < 1) {
			return jsonObject.put("reason", "數量必須大於零！").toString();
		}

		JSONObject item = new JSONObject().put("merchandise", id).put("booth", boothId).put("specification", merchandiseSpecificationId).put("quantity", quantity);

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList == null) {
			arrayList = new ArrayList<>();
			arrayList.add(item);
		} else {
			boolean existed = false;
			for (JSONObject itemInCart : arrayList) {
				if (itemInCart.has("specification")) {
					if (itemInCart.getLong("merchandise") == id && itemInCart.getInt("booth") == boothId && itemInCart.getLong("specification") == merchandiseSpecificationId) {
						itemInCart.put("quantity", itemInCart.getInt("quantity") + quantity);
						existed = true;
					}
				} else {
					if (itemInCart.getLong("merchandise") == id && itemInCart.getInt("booth") == boothId) {
						itemInCart.put("quantity", itemInCart.getInt("quantity") + quantity);
						existed = true;
					}
				}
			}
			if (!existed) {
				arrayList.add(item);
			}
		}
		session.setAttribute("cart", arrayList);

		return jsonObject.put("reason", "已將此產品加入購物車！").put("response", true).toString();
	}
}
