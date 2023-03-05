package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 工作人員|攤商
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Staff\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"login\""})
})
@NamedQueries({
	@NamedQuery(name = "Staff.findAll", query = "SELECT s FROM Staff s")
})
public class Staff implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Integer id;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"internal\"")
	private boolean internal;

	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email")
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(nullable = false, name = "\"login\"", length = 256)
	private String login;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(nullable = false, name = "\"shadow\"", length = 128)
	private String shadow;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(nullable = false, name = "\"name\"", length = 32)
	private String name;

	@Column(name = "\"logo\"")
	@Lob
	private byte[] logo;

	@Size(max = 65536)
	@Column(name = "\"html\"", length = 65536)
	private String html;

	@Size(max = 32)
	@Column(name = "\"address\"", length = 32)
	private String address;

	@Size(max = 16)
	@Column(name = "\"cellular\"", length = 16)
	private String cellular;

	@Size(max = 16)
	@Column(name = "\"phone\"", length = 16)
	private String phone;

	@Size(max = 32)
	@Column(name = "\"representative\"", length = 32)
	private String representative;

	@Size(max = 16)
	@Column(name = "\"merchantID\"", length = 16)
	private String merchantID;

	@Size(max = 32)
	@Column(name = "\"hashKey\"", length = 32)
	private String hashKey;

	@Size(max = 32)
	@Column(name = "\"hashIV\"", length = 32)
	private String hashIV;

	/**
	 * 建構子
	 */
	public Staff() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Staff(Integer id) {
		this.id = id;
	}

	/**
	 * @param internal 是否為內部工作人員
	 * @param login 帳號(電子郵件)
	 * @param shadow 密碼
	 * @param name 工作人員暱稱|攤商抬頭
	 */
	public Staff(boolean internal, String login, String shadow, String name) {
		this.internal = internal;
		this.login = login;
		this.shadow = shadow;
		this.name = name;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Staff)) {
			return false;
		}
		Staff other = (Staff) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Staff[ id=" + id + " ]";
	}

	/**
	 * @return 主鍵
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id 主鍵
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return 內部工作人員
	 */
	public boolean isInternal() {
		return internal;
	}

	/**
	 * @param internal 內部工作人員
	 */
	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	/**
	 * @return 帳號(電子郵件)
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login 帳號(電子郵件)
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return 密碼
	 */
	public String getShadow() {
		return shadow;
	}

	/**
	 * @param shadow 密碼
	 */
	public void setShadow(String shadow) {
		this.shadow = shadow;
	}

	/**
	 * @return 工作人員暱稱|攤商抬頭
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 工作人員暱稱|攤商抬頭
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 攤商圖像
	 */
	public byte[] getLogo() {
		return logo;
	}

	/**
	 * @param logo 攤商圖像
	 */
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	/**
	 * @return HTML內容(攤商簡介)
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param html HTML內容(攤商簡介)
	 */
	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * @return 實體地址
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address 實體地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return 手機號碼
	 */
	public String getCellular() {
		return cellular;
	}

	/**
	 * @param cellular 手機號碼
	 */
	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	/**
	 * @return 市內電話
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone 市內電話
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return 聯絡代表
	 */
	public String getRepresentative() {
		return representative;
	}

	/**
	 * @param representative 聯絡代表
	 */
	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	/**
	 * @return 歐付寶特店編號
	 */
	public String getMerchantID() {
		return merchantID;
	}

	/**
	 * @param merchantID 歐付寶特店編號
	 */
	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}

	/**
	 * @return 歐付寶AIO介接的HashKey
	 */
	public String getHashKey() {
		return hashKey;
	}

	/**
	 * @param hashKey 歐付寶AIO介接的HashKey
	 */
	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}

	/**
	 * @return 歐付寶AIO介接的HashIV
	 */
	public String getHashIV() {
		return hashIV;
	}

	/**
	 * @param hashIV 歐付寶AIO介接的HashIV
	 */
	public void setHashIV(String hashIV) {
		this.hashIV = hashIV;
	}
}
