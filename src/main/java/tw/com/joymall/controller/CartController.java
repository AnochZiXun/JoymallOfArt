package tw.com.joymall.controller;

import tw.com.joymall.entity.MerchandiseSpecification;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.MerchandiseSpecificationRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.MerchandiseRepository;
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
import tw.com.joymall.service.Services;

/**
 * 我的購物車
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	MerchandiseRepository merchandiseRepository;

	@Autowired
	MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	MerchandiseSpecificationRepository merchandiseSpecificationRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	private Services services;

	/**
	 * 列表(我的購物車)
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(HttpServletRequest request, HttpSession session) throws ParserConfigurationException {
		if (request.getRemoteUser() != null) {
			return new ModelAndView("redirect:/");
		}

		Document document = Utils.newDocument();
		Element documentElement = Utils.createElementWithAttribute("document", document, "requestURI", request.getRequestURI());
		Integer me = (Integer) session.getAttribute("me");
		if (me != null) {
			documentElement.setAttribute("me", me.toString());
		}
		services.buildFooterElement(documentElement);//底部

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList == null || arrayList.isEmpty()) {
			ModelAndView modelAndView = new ModelAndView("cart");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		HashSet<Integer> hashSet = new HashSet<>();
		for (JSONObject itemInCart : arrayList) {
			hashSet.add(itemInCart.getInt("booth"));
		}

		Element elementStores = Utils.createElement("stores", documentElement);
		for (Integer boothId : hashSet) {
			Staff booth = staffRepository.findOne(boothId);

			Element elementStore = Utils.createElement("store", elementStores);
			elementStore.setAttribute("id", boothId.toString());
			elementStore.setAttribute("name", booth.getName());

			Integer total = 0;
			Element elementItems = Utils.createElement("items", elementStore);
			for (JSONObject itemInCart : arrayList) {
				if (itemInCart.getInt("booth") == boothId) {
					Long merchandiseId = itemInCart.getLong("merchandise");
					Merchandise merchandise = merchandiseRepository.findOne(merchandiseId);
					MerchandiseImage merchandiseImage = merchandiseImageRepository.findTopByMerchandiseOrderByOrdinal(merchandise);
					MerchandiseSpecification merchandiseSpecification = null;
					if (itemInCart.has("specification")) {
						merchandiseSpecification = merchandiseSpecificationRepository.findOne(itemInCart.getLong("specification"));
					}
					int price = merchandise.getPrice(), quantity = itemInCart.getInt("quantity"), subTotal = price * quantity;

					Element elementItem = Utils.createElement("item", elementItems);
					elementItem.setAttribute("id", merchandiseId.toString());
					elementItem.setTextContent(merchandise.getName());
					if (merchandiseImage != null) {
						elementItem.setAttribute("imageId", merchandiseImage.getId().toString());
					}
					if (merchandiseSpecification != null) {
						elementItem.setAttribute("specificationId", merchandiseSpecification.getId().toString());
						elementItem.setAttribute("specificationName", merchandiseSpecification.getName());
					}
					elementItem.setAttribute("price", Integer.toString(price));
					elementItem.setAttribute("quantity", Integer.toString(quantity));
					elementItem.setAttribute("subTotal", Integer.toString(subTotal));

					total += subTotal;
				}
			}
			elementStore.setAttribute("total", total.toString());
		}

		ModelAndView modelAndView = new ModelAndView("cart");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 變更購物車產品數量
	 *
	 * @param theMerchandiseId 商品的主鍵
	 * @param theSpecificationId 商品規格的主鍵
	 * @param theQuantity 新數量
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/quantity.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String quantity(@RequestParam("merchandise") Long theMerchandiseId, @RequestParam(name = "specification", required = false) Long theSpecificationId, @RequestParam(name = "quantity", required = false) Short theQuantity, HttpSession session) {
		JSONObject jsonObject = new JSONObject();

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList == null || arrayList.isEmpty()) {
			return jsonObject.put("reason", "購物車是空的或您的登入週期已經逾時！").put("redirect", "/cart/").put("response", false).toString();
		}

		if (theQuantity == null) {
			return jsonObject.put("reason", "數量為必填！").put("response", false).toString();
		}
		if (theQuantity < 0) {
			return jsonObject.put("reason", "數量必須大於或等於零！").put("response", false).toString();
		}

		Iterator<JSONObject> iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			JSONObject itemInCart = iterator.next();
			Long merchandiseId = itemInCart.getLong("merchandise");

			if (itemInCart.has("specification")) {
				if (Objects.equals(merchandiseId, theMerchandiseId) && Objects.equals(itemInCart.getLong("specification"), theSpecificationId)) {
					if (theQuantity == 0) {
						iterator.remove();
					} else {
						itemInCart.put("quantity", theQuantity);
					}
				}
			} else {
				if (Objects.equals(merchandiseId, theMerchandiseId)) {
					if (theQuantity == 0) {
						iterator.remove();
					} else {
						itemInCart.put("quantity", theQuantity);
					}
				}
			}
		}
		session.setAttribute("cart", arrayList);

		return jsonObject.put("reason", "已成功變更購物車明細！").put("response", true).toString();
	}

	/**
	 * 結帳付款
	 *
	 * @param boothId 攤商的主鍵
	 * @param request
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/checkOut.json", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	private String checkout(@RequestParam("store") Integer boothId, HttpServletRequest request, HttpSession session) {
		JSONObject jsonObject = new JSONObject();

		Integer me = (Integer) session.getAttribute("me");
		if (me == null) {
			session.setAttribute("checkingOut", true);
			return jsonObject.put("reason", "請先登入！").put("redirect", "/logIn.asp").put("response", false).toString();
		}

		if (request.getRemoteUser() != null) {
			return jsonObject.put("reason", "您為店家或工作人員，無法結帳付款！").put("redirect", "/").put("response", false).toString();
		}

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList == null || arrayList.isEmpty()) {
			return jsonObject.put("reason", "購物車是空的！").put("redirect", "/").put("response", false).toString();
		}

		Staff booth = staffRepository.findOne(boothId);
		boolean existed = false;
		for (JSONObject itemInCart : arrayList) {
			Merchandise merchandise = merchandiseRepository.findOne(itemInCart.getLong("merchandise"));
			if (Objects.equals(boothId, merchandise.getShelf().getBooth().getId())) {
				existed = true;
				break;
			}
		}
		if (!existed) {
			return jsonObject.put("reason", "購物車中並沒有" + booth.getName() + "的產品！").put("response", false).toString();
		}

		return jsonObject.put("redirect", "/checkOut/" + boothId + "/").toString();
	}
}
