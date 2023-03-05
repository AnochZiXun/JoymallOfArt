package tw.com.joymall.controller;

import tw.com.joymall.entity.Cart;
import tw.com.joymall.entity.Regular;
import tw.com.joymall.entity.MerchandiseSpecification;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Packet;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.RegularRepository;
import tw.com.joymall.repository.PacketRepository;
import tw.com.joymall.repository.MerchandiseSpecificationRepository;
import tw.com.joymall.repository.CartRepository;
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
 * 結帳付款
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Controller
@RequestMapping("/checkOut")
public class CheckoutController {

	@Autowired
	CartRepository cartRepository;

	@Autowired
	MerchandiseRepository merchandiseRepository;

	@Autowired
	MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	MerchandiseSpecificationRepository merchandiseSpecificationRepository;

	@Autowired
	PacketRepository packetRepository;

	@Autowired
	RegularRepository regularRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	private Services services;

	/**
	 * 列表(結帳付款)
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @return 網頁
	 */
	@RequestMapping(value = "/{boothId:\\d+}/", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	private ModelAndView welcome(@PathVariable Integer boothId, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ParserConfigurationException {
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
			ModelAndView modelAndView = new ModelAndView("checkOut");
			modelAndView.getModelMap().addAttribute(new DOMSource(document));
			return modelAndView;
		}
		if (arrayList != null && !arrayList.isEmpty()) {
			int size = arrayList.size();
			documentElement.setAttribute("cart", size > 9 ? "9+" : Integer.toString(size));
		}

		Integer totalAmount = 0;
		StringBuilder stringBuilder = new StringBuilder();
		Element elementItems = Utils.createElement("items", documentElement);
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
					elementItem.setAttribute("specificationName", merchandiseSpecification.getName());
				}
				elementItem.setAttribute("price", Integer.toString(price));
				elementItem.setAttribute("quantity", Integer.toString(quantity));
				elementItem.setAttribute("subTotal", Integer.toString(subTotal));

				if (stringBuilder.length() > 0) {
					stringBuilder.append("#").append(merchandiseId);
				} else {
					stringBuilder.append(merchandiseId);
				}
				totalAmount += subTotal;
			}
		}

		Regular regular = regularRepository.findOne(me);
		Element elementRegular = Utils.createElement("regular", documentElement);
		elementRegular.setTextContent(regular.getLastname() + regular.getFirstname());
		elementRegular.setAttribute("phone", regular.getPhone());
		elementRegular.setAttribute("address", regular.getAddress());

		Staff booth = staffRepository.findOne(boothId);
		String boothName = booth.getName(), merchantID = booth.getMerchantID();
		Element elementStore = Utils.createElement("store", documentElement);
		elementStore.setAttribute("id", boothId.toString());
		elementStore.setAttribute("MerchantID", merchantID == null ? "" : merchantID);
		elementStore.setAttribute("totalAmount", totalAmount.toString());
		elementStore.setAttribute("tradeDesc", "易購物商城：".concat(boothName));
		elementStore.setAttribute("itemName", stringBuilder.toString());
		elementStore.setTextContent(boothName);

		ModelAndView modelAndView = new ModelAndView("checkOut");
		modelAndView.getModelMap().addAttribute(new DOMSource(document));
		return modelAndView;
	}

	/**
	 * 回傳歐付寶所需的參數們
	 *
	 * @param session
	 * @return JSONObject
	 */
	@RequestMapping(value = "/{boothId:\\d+}/", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SuppressWarnings("UseSpecificCatch")
	private String allPay(@RequestParam("PaymentType") String paymentType, @RequestParam("TotalAmount") Integer totalAmount, @RequestParam("TradeDesc") String tradeDesc, @RequestParam("ItemName") String itemName, @RequestParam("ReturnURL") String returnURL, @RequestParam("ChoosePayment") String choosePayment, @RequestParam("ClientBackURL") String clientBackURL, @RequestParam String recipient, @RequestParam String phone, @RequestParam String address, @PathVariable Integer boothId, HttpSession session) {
		JSONObject jsonObject = new JSONObject();

		Integer me = (Integer) session.getAttribute("me");
		if (me == null) {
			return jsonObject.put("reason", "請先登入！").put("redirect", "/logIn.asp").put("response", false).toString();
		}

		recipient = recipient.trim();
		if (recipient.isEmpty()) {
			return jsonObject.put("reason", "姓名為必填欄位！").put("response", false).toString();
		}
		phone = phone.trim().replaceAll("\\D", "");
		if (phone.isEmpty()) {
			return jsonObject.put("reason", "聯絡電話為必填欄位！").put("response", false).toString();
		}
		address = address.trim();
		if (address.isEmpty()) {
			return jsonObject.put("reason", "寄送地址為必填欄位！").put("response", false).toString();
		}

		Staff booth = staffRepository.findOne(boothId);

		ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) session.getAttribute("cart");
		if (arrayList == null || arrayList.isEmpty()) {
			return jsonObject.put("reason", "購物車是空的或您的登入週期已經逾時！").put("redirect", "/cart/").put("response", false).toString();
		}

		Map<String, String> map = new HashMap<>();
		try {
			String merchantID = booth.getMerchantID();
			map.put("merchantID", merchantID);

			GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN);
			String merchantTradeNo = Long.toString(gregorianCalendar.getTimeInMillis());
			Integer suffix = 0;
			while (packetRepository.countByMerchantTradeNo(merchantTradeNo) > 0) {
				suffix++;
			}
			merchantTradeNo += suffix.toString();
			map.put("merchantTradeNo", merchantTradeNo);

			String merchantTradeDate = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN).format(gregorianCalendar.getTime());
			map.put("merchantTradeDate", merchantTradeDate);

			Map<String, String> parameterMap = new TreeMap<>();
			parameterMap.put("MerchantID", merchantID);
			parameterMap.put("MerchantTradeNo", merchantTradeNo);
			parameterMap.put("MerchantTradeDate", merchantTradeDate);
			parameterMap.put("PaymentType", paymentType);
			parameterMap.put("TotalAmount", totalAmount.toString());
			parameterMap.put("TradeDesc", tradeDesc);
			parameterMap.put("ItemName", itemName);
			parameterMap.put("ReturnURL", returnURL);
			parameterMap.put("ChoosePayment", choosePayment);
			parameterMap.put("ClientBackURL", clientBackURL);
			StringBuilder stringBuilder = new StringBuilder("HashKey=" + booth.getHashKey());
			for (Map.Entry<String, String> entrySet : parameterMap.entrySet()) {
				stringBuilder.append("&").append(entrySet.getKey()).append("=").append(entrySet.getValue());
			}
			stringBuilder.append("&HashIV=").append(booth.getHashIV());
			String checkMacValue = Utils.md5(java.net.URLEncoder.encode(stringBuilder.toString(), "UTF-8").toLowerCase()).toUpperCase();
			map.put("checkMacValue", checkMacValue);

			Packet packet = new Packet(regularRepository.findOne(me), booth, merchantTradeNo, gregorianCalendar.getTime(), totalAmount);
			packet.setRecipient(recipient);
			packet.setPhone(phone);
			packet.setAddress(address);
			packetRepository.saveAndFlush(packet);

			Iterator<JSONObject> iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				JSONObject itemInCart = iterator.next();

				Cart cart;
				if (Objects.equals(boothId, itemInCart.getInt("booth"))) {
					Merchandise merchandise = merchandiseRepository.findOne(itemInCart.getLong("merchandise"));

					if (itemInCart.has("specification")) {
						MerchandiseSpecification merchandiseSpecification = merchandiseSpecificationRepository.findOne(itemInCart.getLong("specification"));

						cart = new Cart(merchandise, merchandiseSpecification.getName(), Integer.valueOf(itemInCart.getInt("quantity")).shortValue(), packet);
					} else {
						cart = new Cart(merchandise, null, Integer.valueOf(itemInCart.getInt("quantity")).shortValue(), packet);
					}

					cartRepository.saveAndFlush(cart);
					iterator.remove();
				}
			}
			session.setAttribute("cart", arrayList);
		} catch (Exception exception) {
			return jsonObject.put("reason", exception.getLocalizedMessage()).put("response", false).toString();
		}

		return jsonObject.put("response", true).put("result", map).toString();
	}
}
