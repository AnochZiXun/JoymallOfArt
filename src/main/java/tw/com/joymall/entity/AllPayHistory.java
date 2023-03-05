package tw.com.joymall.entity;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * 歐付寶支付歷程
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "AllPayHistory.findAll", query = "SELECT a FROM AllPayHistory a")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"AllPayHistory\"")
public class AllPayHistory implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"packet\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne(optional = false)
	private Packet packet;

	@Size(max = 256)
	@Column(name = "\"tradeDesc\"", length = 256)
	private String tradeDesc;

	@Column(name = "\"rtnCode\"")
	private Short rtnCode;

	@Size(max = 256)
	@Column(name = "\"rtnMsg\"", length = 256)
	private String rtnMsg;

	@Size(max = 32)
	@Column(name = "\"tradeNo\"", length = 32)
	private String tradeNo;

	@Column(name = "\"tradeAmt\"")
	private Integer tradeAmt;

	@Column(name = "\"paymentDate\"")
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;

	@Size(max = 32)
	@Column(name = "\"paymentType\"", length = 32)
	private String paymentType;

	@Column(name = "\"paymentTypeChargeFee\"")
	private Integer paymentTypeChargeFee;

	@Column(name = "\"tradeDate\"")
	@Temporal(TemporalType.TIMESTAMP)
	private Date tradeDate;

	@Column(name = "\"simulatePaid\"")
	private Boolean simulatePaid;

	@Size(max = 64)
	@Column(name = "\"checkMacValue\"", length = 64)
	private String checkMacValue;

	/**
	 * 建構子
	 */
	public AllPayHistory() {
	}

	/**
	 * @param id 主鍵
	 */
	protected AllPayHistory(Long id) {
		this.id = id;
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
		if (!(object instanceof AllPayHistory)) {
			return false;
		}
		AllPayHistory other = (AllPayHistory) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.AllPayHistory[ id=" + id + " ]";
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
	 * @return 訂單
	 */
	public Packet getPacket() {
		return packet;
	}

	/**
	 * @param packet 訂單
	 */
	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	/**
	 * @return 交易描述(攤商抬頭)
	 */
	public String getTradeDesc() {
		return tradeDesc;
	}

	/**
	 * @param tradeDesc 交易描述(攤商抬頭)
	 */
	public void setTradeDesc(String tradeDesc) {
		this.tradeDesc = tradeDesc;
	}

	/**
	 * @return 交易狀態(付款結果通知)
	 */
	public Short getRtnCode() {
		return rtnCode;
	}

	/**
	 * @param rtnCode 交易狀態(付款結果通知)
	 */
	public void setRtnCode(Short rtnCode) {
		this.rtnCode = rtnCode;
	}

	/**
	 * @return 交易訊息(付款結果通知)
	 */
	public String getRtnMsg() {
		return rtnMsg;
	}

	/**
	 * @param rtnMsg 交易訊息(付款結果通知)
	 */
	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}

	/**
	 * @return 交易編號(付款結果通知)
	 */
	public String getTradeNo() {
		return tradeNo;
	}

	/**
	 * @param tradeNo 交易編號(付款結果通知)
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	/**
	 * @return 交易金額(付款結果通知)
	 */
	public Integer getTradeAmt() {
		return tradeAmt;
	}

	/**
	 * @param tradeAmt 交易金額(付款結果通知)
	 */
	public void setTradeAmt(Integer tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	/**
	 * @return 付款時間(付款結果通知)
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate 付款時間(付款結果通知)
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return 會員選擇的付款方式(付款結果通知)
	 */
	public String getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType 會員選擇的付款方式(付款結果通知)
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return 通路費(付款結果通知)
	 */
	public Integer getPaymentTypeChargeFee() {
		return paymentTypeChargeFee;
	}

	/**
	 * @param paymentTypeChargeFee 通路費(付款結果通知)
	 */
	public void setPaymentTypeChargeFee(Integer paymentTypeChargeFee) {
		this.paymentTypeChargeFee = paymentTypeChargeFee;
	}

	/**
	 * @return 訂單成立時間(付款結果通知)
	 */
	public Date getTradeDate() {
		return tradeDate;
	}

	/**
	 * @param tradeDate 訂單成立時間(付款結果通知)
	 */
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	/**
	 * @return 是否為模擬付款(付款結果通知)
	 */
	public Boolean getSimulatePaid() {
		return simulatePaid;
	}

	/**
	 * @param simulatePaid 是否為模擬付款(付款結果通知)
	 */
	public void setSimulatePaid(Boolean simulatePaid) {
		this.simulatePaid = simulatePaid;
	}

	/**
	 * @return 檢查碼(付款結果通知)
	 */
	public String getCheckMacValue() {
		return checkMacValue;
	}

	/**
	 * @param checkMacValue 檢查碼(付款結果通知)
	 */
	public void setCheckMacValue(String checkMacValue) {
		this.checkMacValue = checkMacValue;
	}
}
