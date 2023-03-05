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
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tw.com.joymall.Utils;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.MerchandiseImage;
import tw.com.joymall.entity.Staff;
import tw.com.joymall.repository.MerchandiseImageRepository;
import tw.com.joymall.repository.StaffRepository;
import tw.com.joymall.service.ControlPanelService;

/**
 * 商品圖片
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Controller
@RequestMapping("/cPanel/merchandiseImage")
public class MerchandiseImageController {

	@Autowired
	MerchandiseImageRepository merchandiseImageRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	ControlPanelService controlPanelService;

	/**
	 * 縮圖
	 *
	 * @param id 商品圖片的主鍵
	 * @param response
	 */
	@RequestMapping(value = "/{id:[\\d]+}.png", produces = "image/png", method = RequestMethod.GET)
	private void thumbnail(@PathVariable Long id, HttpServletResponse response) throws IOException {
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

		InputStream inputStream = new ByteArrayInputStream(content);
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		Integer height = bufferedImage.getHeight();
		ImageIO.write(Utils.rescaleImage(content, bufferedImage.getWidth() / Math.round(height.floatValue() / 128), 128), "PNG", response.getOutputStream());
		IOUtils.closeQuietly(inputStream);
	}

	/**
	 * 刪除商品圖片
	 *
	 * @param id 商品圖片的主鍵
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
		MerchandiseImage merchandiseImage = merchandiseImageRepository.findOne(id);
		if (merchandiseImage == null) {
			return jsonObject.put("reason", "找不到商品圖片！").toString();
		}
		Merchandise merchandise = merchandiseImage.getMerchandise();

		merchandiseImageRepository.delete(merchandiseImage);
		merchandiseImageRepository.flush();

		controlPanelService.sortMerchandiseImageByMerchandise(merchandise);
		return jsonObject.put("response", true).toString();
	}
}
