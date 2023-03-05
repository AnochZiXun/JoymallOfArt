package tw.com.joymall.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;
import tw.com.joymall.Utils;
import tw.com.joymall.entity.Banner;
import tw.com.joymall.entity.Bulletin;
import tw.com.joymall.entity.InternalFrame;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.MerchandiseSpecification;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.BannerRepository;
import tw.com.joymall.repository.BulletinRepository;
import tw.com.joymall.repository.InternalFrameRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.MerchandiseRepository;
import tw.com.joymall.repository.MerchandiseSpecificationRepository;
import tw.com.joymall.repository.ShelfRepository;

/**
 * 控制臺服務層
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Service
@Transactional
public class ControlPanelService {

	@Autowired
	private BannerRepository bannerRepository;

	@Autowired
	private BulletinRepository bulletinRepository;

	@Autowired
	private InternalFrameRepository internalFrameRepository;

	@Autowired
	private MerchandiseRepository merchandiseRepository;

	@Autowired
	private MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	private MerchandiseSpecificationRepository merchandiseSpecificationRepository;

	@Autowired
	private ShelfRepository shelfRepository;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);

	/**
	 * 載入連播橫幅
	 *
	 * @param entity 連播橫幅
	 * @param parentNode 父元素
	 */
	public void loadBanner(Banner entity, Element parentNode) {
		Short id = entity.getId();
		if (id != null) {
			Utils.createElementWithTextContent("id", parentNode, id.toString());
		}

		String href = entity.getHref();//連結
		Utils.createElementWithTextContent("href", parentNode, href == null ? "" : href);

		Boolean external = entity.isExternal();//外部連結
		Utils.createElementWithTextContent("external", parentNode, external == null ? "false" : Boolean.toString(external));
	}

	/**
	 * 儲存連播橫幅
	 *
	 * @param entity 連播橫幅
	 * @param multipartFile 圖檔
	 * @param href 連結
	 * @param external 外部連結
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveBanner(Banner entity, MultipartFile multipartFile, String href, Boolean external, Element parentNode) {
		Short id = entity.getId();
		String errorMessage = null;

		byte[] content = null;
		if (id == null) {
			try {
				content = multipartFile.getBytes();
				if (content.length == 0) {
					content = null;
					throw new NullPointerException();
				}
			} catch (NullPointerException nullPointerException) {
				errorMessage = "圖檔為必選！";
			} catch (IOException ioException) {
				errorMessage = ioException.getLocalizedMessage();
			}
		}

		try {
			href = href.trim();
			if (href.isEmpty()) {
				href = null;
			}
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("href", parentNode, href);

		if (href != null && external == null) {
			errorMessage = "當連結不為空時外部連結為必選！";
		}
		Utils.createElementWithTextContent("external", parentNode, external == null ? "false" : external.toString());

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			if (id == null) {
				entity.setContent(content);
			}
			entity.setHref(href);
			entity.setExternal(external);
			if (id == null) {
				entity.setOrdinal(Long.valueOf(bannerRepository.count() + 1).shortValue());
			}
			bannerRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 載入最新消息
	 *
	 * @param entity 最新消息
	 * @param parentNode 父元素
	 */
	public void loadBulletin(Bulletin entity, Element parentNode) {
		Short id = entity.getId();
		if (id != null) {
			Utils.createElementWithTextContent("id", parentNode, id.toString());
		}

		String subject = entity.getSubject();//主旨
		Utils.createElementWithTextContent("subject", parentNode, subject == null ? "" : subject);

		String html = entity.getHtml();//主旨
		Utils.createElementWithTextContent("html", parentNode, html == null ? "" : html);

		Date when = entity.getWhen();//發佈日期
		Utils.createElementWithTextContent("when", parentNode, when == null ? "" : simpleDateFormat.format(when));
	}

	/**
	 * 儲存最新消息
	 *
	 * @param entity 最新消息
	 * @param subject 主旨
	 * @param html 內容
	 * @param when 發佈日期
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveBulletin(Bulletin entity, String subject, String html, Date when, Element parentNode) {
		String errorMessage = null;

		try {
			subject = subject.trim();
			if (subject.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "主旨為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("subject", parentNode, subject);

		try {
			html = html.trim();
			if (html.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "內容為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("html", parentNode, html);

		if (when == null) {
			errorMessage = "發佈日期為必填！";
		}
		Utils.createElementWithTextContent("when", parentNode, when == null ? "" : simpleDateFormat.format(when));

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setSubject(subject);
			entity.setHtml(html);
			entity.setWhen(when);
			bulletinRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 載入 &lt;IFRAME/&gt;
	 *
	 * @param entity nested browsing context
	 * @param parentNode 父元素
	 */
	public void loadInternalFrame(InternalFrame entity, Element parentNode) {
		String src = entity.getSrc();//address of the resource
		Utils.createElementWithTextContent("src", parentNode, src == null ? "" : src);

		Short width = entity.getWidth();//horizontal dimension
		Utils.createElementWithTextContent("width", parentNode, width == null ? "" : width.toString());

		Short height = entity.getHeight();//vertical dimension
		Utils.createElementWithTextContent("height", parentNode, height == null ? "" : height.toString());
	}

	/**
	 * 儲存 &lt;IFRAME/&gt;
	 *
	 * @param entity nested browsing context
	 * @param booth 商品
	 * @param src address of the resource
	 * @param width horizontal dimension
	 * @param height vertical dimension
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveInternalFrame(InternalFrame entity, Staff booth, String src, Short width, Short height, Element parentNode) {
		String errorMessage = null;

		try {
			src = src.trim();
			if (src.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "網址為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("src", parentNode, src);

		if (width != null && width <= 0) {
			width = null;
		}
		Utils.createElementWithTextContent("width", parentNode, width == null ? "" : width.toString());

		if (height != null && height <= 0) {
			height = null;
		}
		Utils.createElementWithTextContent("height", parentNode, height == null ? "" : height.toString());

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setBooth(booth);
			entity.setSrc(src);
			entity.setWidth(width);
			entity.setHeight(height);
			if (entity.getId() == null) {
				entity.setOrdinal(Long.valueOf(internalFrameRepository.countByBooth(booth) + 1).shortValue());
			}
			internalFrameRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 儲存 &lt;IFRAME/&gt;
	 *
	 * @param entity nested browsing context
	 * @param bulletin 最新消息
	 * @param src address of the resource
	 * @param width horizontal dimension
	 * @param height vertical dimension
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveInternalFrame(InternalFrame entity, Bulletin bulletin, String src, Short width, Short height, Element parentNode) {
		String errorMessage = null;

		try {
			src = src.trim();
			if (src.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "網址為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("src", parentNode, src);

		if (width != null && width <= 0) {
			width = null;
		}
		Utils.createElementWithTextContent("width", parentNode, width == null ? "" : width.toString());

		if (height != null && height <= 0) {
			height = null;
		}
		Utils.createElementWithTextContent("height", parentNode, height == null ? "" : height.toString());

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setBulletin(bulletin);
			entity.setSrc(src);
			entity.setWidth(width);
			entity.setHeight(height);
			if (entity.getId() == null) {
				entity.setOrdinal(Long.valueOf(internalFrameRepository.countByBulletin(bulletin) + 1).shortValue());
			}
			internalFrameRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 儲存 &lt;IFRAME/&gt;
	 *
	 * @param entity nested browsing context
	 * @param merchandise 商品
	 * @param src address of the resource
	 * @param width horizontal dimension
	 * @param height vertical dimension
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveInternalFrame(InternalFrame entity, Merchandise merchandise, String src, Short width, Short height, Element parentNode) {
		String errorMessage = null;

		try {
			src = src.trim();
			if (src.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "網址為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("src", parentNode, src);

		if (width != null && width <= 0) {
			width = null;
		}
		Utils.createElementWithTextContent("width", parentNode, width == null ? "" : width.toString());

		if (height != null && height <= 0) {
			height = null;
		}
		Utils.createElementWithTextContent("height", parentNode, height == null ? "" : height.toString());

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setMerchandise(merchandise);
			entity.setSrc(src);
			entity.setWidth(width);
			entity.setHeight(height);
			if (entity.getId() == null) {
				entity.setOrdinal(Long.valueOf(internalFrameRepository.countByMerchandise(merchandise) + 1).shortValue());
			}
			internalFrameRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 載入商品
	 *
	 * @param entity 商品
	 * @param parentNode 父元素
	 */
	public void loadMerchandise(Merchandise entity, Element parentNode) {
		Long id = entity.getId();
		if (id != null) {
			Utils.createElementWithTextContent("id", parentNode, id.toString());//主鍵

			Shelf theShelf = entity.getShelf();//商品分類
			Element elementBooths = Utils.createElement("shelfs", parentNode);
			for (Shelf shelf : shelfRepository.findByBoothOrderByName(theShelf.getBooth())) {
				Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementBooths, shelf.getName(), "value", shelf.getId().toString());
				if (Objects.equals(shelf, theShelf)) {
					elementOption.setAttribute("selected", null);
				}
			}
		}

		String name = entity.getName();//商品分類名稱
		Utils.createElementWithTextContent("name", parentNode, name == null ? "" : name);

		Integer price = entity.getPrice();//單價
		Utils.createElementWithTextContent("price", parentNode, price == null ? "" : price.toString());

		String html = entity.getHtml();//HTML內容(描述)
		Utils.createElementWithTextContent("html", parentNode, html == null ? "" : html);

		Boolean carrying = entity.isCarrying();//上架|下架
		Utils.createElementWithTextContent("carrying", parentNode, carrying == null ? "true" : Boolean.toString(carrying));

		Boolean inStock = entity.isCarrying();//有貨
		Utils.createElementWithTextContent("inStock", parentNode, inStock == null ? "true" : Boolean.toString(inStock));

		Boolean recommended = entity.isCarrying();//推薦
		Utils.createElementWithTextContent("recommended", parentNode, recommended == null ? "false" : Boolean.toString(recommended));
	}

	/**
	 * 儲存商品
	 *
	 * @param entity 商品
	 * @param shelf 商品分類
	 * @param name 商品名稱
	 * @param price width
	 * @param html html
	 * @param carrying carrying
	 * @param inStock 有貨
	 * @param recommended 推薦
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveMerchandise(Merchandise entity, Shelf shelf, String name, int price, String html, Boolean carrying, Boolean inStock, Boolean recommended, Element parentNode) {
		String errorMessage = null;

		Long id = entity.getId();
		if (id != null) {
			Shelf theShelf = entity.getShelf();//商品分類
			Element elementBooths = Utils.createElement("shelfs", parentNode);
			for (Shelf s : shelfRepository.findByBoothOrderByName(theShelf.getBooth())) {
				Element elementOption = Utils.createElementWithTextContentAndAttribute("option", elementBooths, s.getName(), "value", s.getId().toString());
				if (Objects.equals(s, theShelf)) {
					elementOption.setAttribute("selected", null);
				}
			}
		}

		try {
			name = name.trim();
			if (name.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "商品名稱為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("name", parentNode, name);

		try {
			html = html.trim();
			if (html.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "描述為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("html", parentNode, html);

		Utils.createElementWithTextContent("carrying", parentNode, carrying == null ? "true" : Boolean.toString(carrying));

		Utils.createElementWithTextContent("inStock", parentNode, inStock == null ? "true" : Boolean.toString(inStock));

		Utils.createElementWithTextContent("recommended", parentNode, recommended == null ? "false" : Boolean.toString(recommended));

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setShelf(shelf);
			entity.setName(name);
			entity.setPrice(price);
			entity.setHtml(html);
			entity.setCarrying(carrying);
			entity.setInStock(inStock);
			entity.setRecommended(recommended);
			merchandiseRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 載入商品規格
	 *
	 * @param entity 商品規格
	 * @param parentNode 父元素
	 */
	public void loadMerchandiseSpecification(MerchandiseSpecification entity, Element parentNode) {
		String name = entity.getName();//商品規格名稱
		Utils.createElementWithTextContent("name", parentNode, name == null ? "" : name);
	}

	/**
	 * 儲存商品規格
	 *
	 * @param entity 商品規格
	 * @param merchandise 商品
	 * @param name 商品規格名稱
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveMerchandiseSpecification(MerchandiseSpecification entity, Merchandise merchandise, String name, Element parentNode) {
		String errorMessage = null;

		try {
			name = name.trim();
			if (name.isEmpty()) {
				throw new NullPointerException();
			}

			Long id = entity.getId();
			if (id == null) {
				if (merchandiseSpecificationRepository.countByMerchandiseAndName(merchandise, name) > 0) {
					errorMessage = "已存在的商品規格名稱！";
				}
			} else {
				if (merchandiseSpecificationRepository.countByMerchandiseAndNameAndIdNot(merchandise, name, id) > 0) {
					errorMessage = "已存在的商品規格名稱！";
				}
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "商品規格名稱為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("name", parentNode, name);

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setMerchandise(merchandise);
			entity.setName(name);
			merchandiseSpecificationRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 載入商品分類名稱
	 *
	 * @param entity 商品分類
	 * @param parentNode 父元素
	 */
	public void loadShelf(Shelf entity, Element parentNode) {
		String name = entity.getName();//商品分類名稱
		Utils.createElementWithTextContent("name", parentNode, name == null ? "" : name);
	}

	/**
	 * 儲存商品分類名稱
	 *
	 * @param entity 商品分類
	 * @param booth 攤商
	 * @param name 商品分類名稱
	 * @param parentNode 父元素
	 * @return 錯誤訊息
	 */
	public String saveShelf(Shelf entity, Staff booth, String name, Element parentNode) {
		String errorMessage = null;

		try {
			name = name.trim();
			if (name.isEmpty()) {
				throw new NullPointerException();
			}

			Long id = entity.getId();
			if (id == null) {
				if (shelfRepository.countByBoothAndName(booth, name) > 0) {
					errorMessage = "已存在的商品分類名稱！";
				}
			} else {
				if (shelfRepository.countByBoothAndNameAndIdNot(booth, name, id) > 0) {
					errorMessage = "已存在的商品分類名稱！";
				}
			}
		} catch (NullPointerException nullPointerException) {
			errorMessage = "商品分類名稱為必填！";
		} catch (Exception exception) {
			errorMessage = exception.getLocalizedMessage();
		}
		Utils.createElementWithTextContent("name", parentNode, name);

		/*
		 錯誤訊息如為空則寫入資料庫
		 */
		if (errorMessage == null) {
			entity.setBooth(booth);
			entity.setName(name);
			shelfRepository.saveAndFlush(entity);
		}

		return errorMessage;
	}

	/**
	 * 重新排序連播橫幅
	 */
	public void sortBanners() {
		short ordinal = 1;
		for (Banner banner : bannerRepository.findAll(new Sort(Sort.Direction.ASC, "ordinal"))) {
			if (banner.getOrdinal() != ordinal) {
				banner.setOrdinal(ordinal);
				bannerRepository.saveAndFlush(banner);
			}
			ordinal++;
		}
	}

	/**
	 * @param booth 攤商
	 */
	public void sortInternalFrameByBooth(Staff booth) {
		short ordinal = 1;
		for (InternalFrame internalFrame : internalFrameRepository.findByBoothOrderByOrdinal(booth)) {
			if (internalFrame.getOrdinal() != ordinal) {
				internalFrame.setOrdinal(ordinal);
				internalFrameRepository.saveAndFlush(internalFrame);
			}
			ordinal++;
		}
	}

	/**
	 * @param bulletin 最新消息
	 */
	public void sortInternalFrameByBulletin(Bulletin bulletin) {
		short ordinal = 1;
		for (InternalFrame internalFrame : internalFrameRepository.findByBulletinOrderByOrdinal(bulletin)) {
			if (internalFrame.getOrdinal() != ordinal) {
				internalFrame.setOrdinal(ordinal);
				internalFrameRepository.saveAndFlush(internalFrame);
			}
			ordinal++;
		}
	}

	/**
	 * @param merchandise 商品
	 */
	public void sortInternalFrameByMerchandise(Merchandise merchandise) {
		short ordinal = 1;
		for (InternalFrame internalFrame : internalFrameRepository.findByMerchandiseOrderByOrdinal(merchandise)) {
			if (internalFrame.getOrdinal() != ordinal) {
				internalFrame.setOrdinal(ordinal);
				internalFrameRepository.saveAndFlush(internalFrame);
			}
			ordinal++;
		}
	}

	/**
	 * @param merchandise 商品
	 */
	public void sortMerchandiseImageByMerchandise(Merchandise merchandise) {
		short ordinal = 1;
		for (MerchandiseImage merchandiseImage : merchandiseImageRepository.findByMerchandiseOrderByOrdinal(merchandise)) {
			if (merchandiseImage.getOrdinal() != ordinal) {
				merchandiseImage.setOrdinal(ordinal);
				merchandiseImageRepository.saveAndFlush(merchandiseImage);
			}
			ordinal++;
		}
	}
}
