package tw.com.joymall.entity;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 最新消息
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Bulletin\"")
@NamedQueries({
	@NamedQuery(name = "Bulletin.findAll", query = "SELECT b FROM Bulletin b")
})
public class Bulletin implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Short id;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(nullable = false, name = "\"subject\"", length = 128)
	private String subject;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 65536)
	@Column(nullable = false, name = "\"html\"", length = 65536)
	private String html;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"when\"")
	@Temporal(TemporalType.DATE)
	private Date when;

	/**
	 * 建構子
	 */
	public Bulletin() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Bulletin(Short id) {
		this.id = id;
	}

	/**
	 * @param subject 主旨
	 * @param html HTML內容
	 * @param when 發佈日期
	 */
	public Bulletin(String subject, String html, Date when) {
		this.subject = subject;
		this.html = html;
		this.when = when;
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
		if (!(object instanceof Bulletin)) {
			return false;
		}
		Bulletin other = (Bulletin) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Bulletin[ id=" + id + " ]";
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
	 * @return 主旨
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject 主旨
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return HTML內容
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param html HTML內容
	 */
	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * @return 發佈日期
	 */
	public Date getWhen() {
		return when;
	}

	/**
	 * @param when 發佈日期
	 */
	public void setWhen(Date when) {
		this.when = when;
	}
}
