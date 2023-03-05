package tw.com.joymall.entity;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "AccessLog.findAll", query = "SELECT a FROM AccessLog a")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"AccessLog\"")
public class AccessLog implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@Size(max = 16)
	@Column(name = "\"remoteHost\"", length = 16)
	private String remoteHost;

	@Size(max = 256)
	@Column(name = "\"userName\"", length = 256)
	private String userName;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"timestamp\"")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Size(max = 128)
	@Column(name = "\"virtualHost\"", length = 128)
	private String virtualHost;

	@Size(max = 16)
	@Column(name = "\"method\"", length = 16)
	private String method;

	@Size(max = 2048)
	@Column(name = "\"query\"", length = 2048)
	private String query;

	@Column(name = "\"status\"")
	private Short status;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"bytes\"")
	private long bytes;

	@Size(max = 128)
	@Column(name = "\"referer\"", length = 128)
	private String referer;

	@Size(max = 256)
	@Column(name = "\"userAgent\"", length = 256)
	private String userAgent;

	/**
	 * 建構子
	 */
	public AccessLog() {
	}

	/**
	 * @param id 主鍵
	 */
	protected AccessLog(Long id) {
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
		if (!(object instanceof AccessLog)) {
			return false;
		}
		AccessLog other = (AccessLog) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.AccessLog[ id=" + id + " ]";
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
	 * @return 訪客 IP 位址
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * @param remoteHost 訪客 IP 位址
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/**
	 * @return 使用者帳號
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName 使用者帳號
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return 造訪時戳
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp 造訪時戳
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return 虛擬主機
	 */
	public String getVirtualHost() {
		return virtualHost;
	}

	/**
	 * @param virtualHost 虛擬主機
	 */
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	/**
	 * @return 請求方式
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method 請求方式
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return 請求網址
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query 請求網址
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return 回應狀態碼
	 */
	public Short getStatus() {
		return status;
	}

	/**
	 * @param status 回應狀態碼
	 */
	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * @return 流量
	 */
	public long getBytes() {
		return bytes;
	}

	/**
	 * @param bytes 流量
	 */
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return 來源網址
	 */
	public String getReferer() {
		return referer;
	}

	/**
	 * @param referer 來源網址
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 * @return 瀏覽器
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent 瀏覽器
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
