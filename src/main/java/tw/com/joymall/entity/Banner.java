package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 連播橫幅
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Banner.findAll", query = "SELECT b FROM Banner b")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Banner\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"ordinal\""})
})
public class Banner implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Short id;

	@Basic(optional = false)
	@NotNull
	@Lob
	@Column(nullable = false, name = "\"content\"")
	private byte[] content;

	@Size(max = 2048)
	@Column(name = "\"href\"", length = 2048)
	private String href;

	@Basic(optional = false)
	@Column(name = "\"external\"")
	private boolean external;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"ordinal\"")
	private short ordinal;

	/**
	 * 建構子
	 */
	public Banner() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Banner(Short id) {
		this.id = id;
	}

	/**
	 * @param content 內容
	 * @param ordinal 排序
	 */
	public Banner(byte[] content, short ordinal) {
		this.content = content;
		this.href = null;
		this.external = false;
		this.ordinal = ordinal;
	}

	/**
	 * @param content 內容
	 * @param href 連結
	 * @param external 外部連結
	 * @param ordinal 排序
	 */
	public Banner(byte[] content, String href, boolean external, short ordinal) {
		this.content = content;
		this.href = href;
		this.external = external;
		this.ordinal = ordinal;
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
		if (!(object instanceof Banner)) {
			return false;
		}
		Banner other = (Banner) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Banner[ id=" + id + " ]";
	}

	/**
	 * @return 主鍵
	 */
	public Short getId() {
		return id;
	}

	/**
	 * @param id 主鍵
	 */
	public void setId(Short id) {
		this.id = id;
	}

	/**
	 * @return 內容
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * @param content 內容
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * @return 連結
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href 連結
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return 外部連結
	 */
	public boolean isExternal() {
		return external;
	}

	/**
	 * @param external 外部連結
	 */
	public void setExternal(boolean external) {
		this.external = external;
	}

	/**
	 * @return 排序
	 */
	public short getOrdinal() {
		return ordinal;
	}

	/**
	 * @param ordinal 排序
	 */
	public void setOrdinal(short ordinal) {
		this.ordinal = ordinal;
	}
}
