package tw.com.joymall.entity;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 訂單
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Packet.findAll", query = "SELECT p FROM Packet p")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Packet\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"merchantTradeNo\""})
})
public class Packet implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"booth\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne
	private Staff booth;

	@JoinColumn(name = "\"regular\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne(optional = false)
	private Regular regular;

	@JoinColumn(name = "\"packetStatus\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private PacketStatus packetStatus;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(nullable = false, name = "\"merchantTradeNo\"", length = 32)
	private String merchantTradeNo;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"merchantTradeDate\"")
	@Temporal(TemporalType.TIMESTAMP)
	private Date merchantTradeDate;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"totalAmount\"")
	private int totalAmount;

	@Size(max = 64)
	@Column(name = "\"checkMacValue\"", length = 64)
	private String checkMacValue;

	@Size(max = 32)
	@Column(name = "\"recipient\"", length = 32)
	private String recipient;

	@Size(max = 16)
	@Column(name = "\"phone\"", length = 16)
	private String phone;

	@Size(max = 32)
	@Column(name = "\"address\"", length = 32)
	private String address;

	/**
	 * 建構子
	 */
	public Packet() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Packet(Long id) {
		this.id = id;
	}

	/**
	 * @param booth 攤商
	 * @param regular 會員
	 * @param merchantTradeNo 交易編號
	 * @param merchantTradeDate 交易時間
	 * @param totalAmount 交易金額
	 */
	public Packet(Regular regular, Staff booth, String merchantTradeNo, Date merchantTradeDate, int totalAmount) {
		this.regular = regular;
		this.booth = booth;
		this.merchantTradeNo = merchantTradeNo;
		this.merchantTradeDate = merchantTradeDate;
		this.totalAmount = totalAmount;
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
		if (!(object instanceof Packet)) {
			return false;
		}
		Packet other = (Packet) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Packet[ id=" + id + " ]";
	}

	/**
	 * @return 主鍵
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id 主鍵
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return 攤商
	 */
	public Staff getBooth() {
		return booth;
	}

	/**
	 * @param booth 攤商
	 */
	public void setBooth(Staff booth) {
		this.booth = booth;
	}

	/**
	 * @return 會員
	 */
	public Regular getRegular() {
		return regular;
	}

	/**
	 * @param regular 會員
	 */
	public void setRegular(Regular regular) {
		this.regular = regular;
	}

	/**
	 * @return 訂單狀態
	 */
	public PacketStatus getPacketStatus() {
		return packetStatus;
	}

	/**
	 * @param packetStatus 訂單狀態
	 */
	public void setPacketStatus(PacketStatus packetStatus) {
		this.packetStatus = packetStatus;
	}

	/**
	 * @return 交易編號
	 */
	public String getMerchantTradeNo() {
		return merchantTradeNo;
	}

	/**
	 * @param merchantTradeNo 交易編號
	 */
	public void setMerchantTradeNo(String merchantTradeNo) {
		this.merchantTradeNo = merchantTradeNo;
	}

	/**
	 * @return 交易時間
	 */
	public Date getMerchantTradeDate() {
		return merchantTradeDate;
	}

	/**
	 * @param merchantTradeDate 交易時間
	 */
	public void setMerchantTradeDate(Date merchantTradeDate) {
		this.merchantTradeDate = merchantTradeDate;
	}

	/**
	 * @return 交易金額
	 */
	public int getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount 交易金額
	 */
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return 歐付寶AIO的檢查碼
	 */
	public String getCheckMacValue() {
		return checkMacValue;
	}

	/**
	 * @param checkMacValue 歐付寶AIO的檢查碼
	 */
	public void setCheckMacValue(String checkMacValue) {
		this.checkMacValue = checkMacValue;
	}

	/**
	 * @return 收件者
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient 收件者
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return 聯絡電話
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone 聯絡電話
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return 運送地址
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address 運送地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
