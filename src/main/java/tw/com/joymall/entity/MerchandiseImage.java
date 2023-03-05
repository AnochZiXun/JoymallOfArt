package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 商品圖片
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "MerchandiseImage.findAll", query = "SELECT m FROM MerchandiseImage m")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"MerchandiseImage\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"merchandise\"", "\"ordinal\""})
})
public class MerchandiseImage implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"merchandise\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne(optional = false)
	private Merchandise merchandise;

	@Basic(optional = false)
	@NotNull
	@Lob
	@Column(nullable = false, name = "\"content\"")
	private byte[] content;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"ordinal\"")
	private short ordinal;

	/**
	 * 建構子
	 */
	public MerchandiseImage() {
	}

	/**
	 * @param id 主鍵
	 */
	protected MerchandiseImage(Long id) {
		this.id = id;
	}

	/**
	 * @param merchandise 商品
	 * @param content 內容
	 * @param ordinal 排序
	 */
	public MerchandiseImage(Merchandise merchandise, byte[] content, short ordinal) {
		this.merchandise = merchandise;
		this.content = content;
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
		if (!(object instanceof MerchandiseImage)) {
			return false;
		}
		MerchandiseImage other = (MerchandiseImage) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.repository.MerchandiseImage[ id=" + id + " ]";
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
	 * @return 商品
	 */
	public Merchandise getMerchandise() {
		return merchandise;
	}

	/**
	 * @param merchandise 商品
	 */
	public void setMerchandise(Merchandise merchandise) {
		this.merchandise = merchandise;
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
