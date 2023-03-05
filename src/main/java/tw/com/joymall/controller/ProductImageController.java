package tw.com.joymall.controller;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tw.com.joymall.Utils;
import tw.com.joymall.entity.Banner;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.BannerRepository;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.StaffRepository;

/**
 * 各種圖片
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/")
public class ProductImageController {

	@Autowired
	private BannerRepository bannerRepository;

	@Autowired
	private MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	StaffRepository staffRepository;

	/**
	 * 連播橫幅圖片
	 *
	 * @param id 連播橫幅圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/banner/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void bannerImage(@PathVariable Short id, HttpServletResponse response) throws IOException {
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
		ImageIO.write(Utils.rescaleImage(content, 980, 490), "PNG", response.getOutputStream());
	}

	/**
	 * 店家的 logo 圖
	 *
	 * @param id 攤商的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/logo/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	protected void logo(@PathVariable Integer id, HttpServletResponse response) throws IOException {
		Staff booth = staffRepository.findOne(id);
		if (booth == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] logo = booth.getLogo();
		if (logo == null || logo.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(logo, 119, 119), "PNG", response.getOutputStream());
	}

	/**
	 * 商品圖片(77&#178;)
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/seventySeven/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void seventySeven(@PathVariable Long id, HttpServletResponse response) throws IOException {
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = merchandiseImage.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(content, 77, 77), "PNG", response.getOutputStream());
	}

	/**
	 * 商品圖片(186&#178;)
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/oneEightySix/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void oneEightySix(@PathVariable Long id, HttpServletResponse response) throws IOException {
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = merchandiseImage.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(content, 186, 186), "PNG", response.getOutputStream());
	}

	/**
	 * 商品圖片(190&#178;)
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/oneNinety/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void oneNinety(@PathVariable Long id, HttpServletResponse response) throws IOException {
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = merchandiseImage.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(content, 190, 190), "PNG", response.getOutputStream());
	}

	/**
	 * 商品圖片(195&#178;)
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/oneNinetyFive/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void oneNinetyFive(@PathVariable Long id, HttpServletResponse response) throws IOException {
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = merchandiseImage.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(content, 195, 195), "PNG", response.getOutputStream());
	}

	/**
	 * 商品圖片(235&#178;)
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/twoThirtyFive/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void twoThirtyFive(@PathVariable Long id, HttpServletResponse response) throws IOException {
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = merchandiseImage.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(content, 235, 235), "PNG", response.getOutputStream());
	}

	/**
	 * 商品圖片(389&#178;)
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/threeEightyNine/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void threeEightyNine(@PathVariable Long id, HttpServletResponse response) throws IOException {
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		byte[] content = merchandiseImage.getContent();
		if (content == null || content.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		ImageIO.write(Utils.rescaleImage(content, 389, 389), "PNG", response.getOutputStream());
	}
}
