package tw.com.joymall;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * 輔助程式
 *
 * @author	P-C Lin (a.k.a 高科技黑手)
 */
public class Utils {

	public static GregorianCalendar DEFAULT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN);

	public final static String JQUERY_VERSION = "1.11.3";

	public final static String JQUERY_UI_VERSION = "1.11.4";

	/**
	 * createElement() 之後 appendChild()。
	 *
	 * @param tagName The name of the element type to instantiate. For XML,
	 * this is case-sensitive, otherwise it depends on the case-sensitivity
	 * of the markup language in use. In that case, the name is mapped to
	 * the canonical form of that markup by the DOM implementation.
	 * @param node The base node.
	 * @return The element added.
	 */
	public static Element createElement(String tagName, Node node) {
		Document document = node.getOwnerDocument();
		if (document == null) {
			document = (Document) node;
		}
		return (Element) node.appendChild(document.createElement(tagName));
	}

	/**
	 * createElement() 之後 appendChild() 最後 setAttribute()。
	 *
	 * @param tagName The name of the element type to instantiate. For XML,
	 * this is case-sensitive, otherwise it depends on the case-sensitivity
	 * of the markup language in use. In that case, the name is mapped to
	 * the canonical form of that markup by the DOM implementation.
	 * @param node The base node.
	 * @param name The name of the attribute to create or alter.
	 * @param value Value to set in string form.
	 * @return The element added.
	 */
	public static Element createElementWithAttribute(String tagName, Node node, String name, String value) {
		Element newChild = createElement(tagName, node);
		newChild.setAttribute(name, value);
		return newChild;
	}

	/**
	 * createElement() 之後 appendChild() 最後 createCDATASection()。
	 *
	 * @param tagName The name of the element type to instantiate. For XML,
	 * this is case-sensitive, otherwise it depends on the case-sensitivity
	 * of the markup language in use. In that case, the name is mapped to
	 * the canonical form of that markup by the DOM implementation.
	 * @param node The base node.
	 * @param data The data for the CDATASection contents.
	 * @return The element added.
	 */
	public static Element createElementWithCDATASection(String tagName, Node node, String data) {
		Element newChild = createElement(tagName, node);
		newChild.appendChild(newChild.getOwnerDocument().createCDATASection(data));
		return newChild;
	}

	/**
	 * createElement() 之後 appendChild() 然後 createCDATASection() 最後
	 * setAttribute()。
	 *
	 * @param tagName The name of the element type to instantiate. For XML,
	 * this is case-sensitive, otherwise it depends on the case-sensitivity
	 * of the markup language in use. In that case, the name is mapped to
	 * the canonical form of that markup by the DOM implementation.
	 * @param node The base node.
	 * @param data The data for the CDATASection contents.
	 * @param name The name of the attribute to create or alter.
	 * @param value Value to set in string form.
	 * @return The element added.
	 */
	public static Element createElementWithCDATASectionAndAttribute(String tagName, Node node, String data, String name, String value) {
		Element newChild = createElementWithCDATASection(tagName, node, data);
		newChild.setAttribute(name, value);
		return newChild;
	}

	/**
	 * createElement() 之後 appendChild() 最後 setTextContent()。
	 *
	 * @param tagName The name of the element type to instantiate. For XML,
	 * this is case-sensitive, otherwise it depends on the case-sensitivity
	 * of the markup language in use. In that case, the name is mapped to
	 * the canonical form of that markup by the DOM implementation.
	 * @param node The base node.
	 * @param textContent The data for the text contents.
	 * @return The element added.
	 */
	public static Element createElementWithTextContent(String tagName, Node node, String textContent) {
		Element newChild = createElement(tagName, node);
		newChild.setTextContent(textContent);
		return newChild;
	}

	/**
	 * createElement() 之後 appendChild() 然後 setTextContent() 最後
	 * setAttribute()。
	 *
	 * @param tagName The name of the element type to instantiate. For XML,
	 * this is case-sensitive, otherwise it depends on the case-sensitivity
	 * of the markup language in use. In that case, the name is mapped to
	 * the canonical form of that markup by the DOM implementation.
	 * @param node The base node.
	 * @param textContent The data for the text contents.
	 * @param name The name of the attribute to create or alter.
	 * @param value Value to set in string form.
	 * @return The element added.
	 */
	public static Element createElementWithTextContentAndAttribute(String tagName, Node node, String textContent, String name, String value) {
		Element newChild = createElementWithTextContent(tagName, node, textContent);
		newChild.setAttribute(name, value);
		return newChild;
	}

	/**
	 * 建立 Document。
	 *
	 * @return A new instance of a DOM Document object.
	 */
	public static Document newDocument() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}

	/**
	 * 建立 Document。
	 *
	 * @param awareness true if the parser produced will provide support for
	 * XML namespaces; false otherwise. By default the value of this is set
	 * to <b>false</b>.
	 * @return A new instance of a DOM Document object.
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be
	 * created which satisfies the configuration requested.
	 */
	public static Document newDocument(boolean awareness) throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(awareness);
		return documentBuilderFactory.newDocumentBuilder().newDocument();
	}

	/**
	 * 建立 Document。
	 *
	 * @param inputStream InputStream containing the content to be parsed.
	 * @return Document result of parsing the InputStream
	 */
	public static Document parseDocument(InputStream inputStream) throws IOException, ParserConfigurationException, SAXException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
	}

	/**
	 * 解析 InputStream 為 Document。
	 *
	 * @param inputStream InputStream containing the content to be parsed.
	 * @param awareness true if the parser produced will provide support for
	 * XML namespaces; false otherwise. By default the value of this is set
	 * to <b>false</b>.
	 * @return Document result of parsing the InputStream
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be
	 * created which satisfies the configuration requested.
	 * @throws IOException If any IO errors occur.
	 * @throws SAXException If any parse errors occur.
	 */
	public static Document parseDocument(InputStream inputStream, boolean awareness) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(awareness);
		return documentBuilderFactory.newDocumentBuilder().parse(inputStream);
	}

	/**
	 * 轉化為 XML。
	 *
	 * @param document The DOM node that will contain the Source tree.
	 * @param response The Result of transforming the xmlSource.
	 */
	public static void transform(Document document, HttpServletResponse response) throws Exception {
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(response.getOutputStream()));
	}

	/**
	 * 轉化為 XML。
	 *
	 * @param document The DOM node that will contain the Source tree.
	 * @param streamResult The Result of transforming the xmlSource.
	 */
	public static void transform(Document document, StreamResult streamResult) throws Exception {
		TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), streamResult);
	}

	/**
	 * @param string 字串
	 * @return 以 PHP 的md5()演算法加密過後的字串
	 */
	public static String md5(String string) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(string.getBytes());
			BigInteger bigInteger = new BigInteger(1, messageDigest.digest());
			string = bigInteger.toString(16);
			while (string.length() < 32) {
				string = "0" + string;
			}
			return string;
		} catch (Exception exception) {
			return "";
		}
	}

	/**
	 * 重新調整圖檔的寬高。
	 *
	 * @param bytes 圖檔內容
	 * @param width 寬
	 * @param height 高
	 * @return 新的圖檔內容
	 */
	public static BufferedImage rescaleImage(byte[] bytes, int width, int height) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		return resampleOp.filter(bufferedImage, null);
	}
}
