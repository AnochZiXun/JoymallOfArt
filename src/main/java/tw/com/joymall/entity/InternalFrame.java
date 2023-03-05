package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * nested browsing context
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "InternalFrame.findAll", query = "SELECT i FROM InternalFrame i")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"InternalFrame\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"bulletin\"", "\"ordinal\""}),
	@UniqueConstraint(columnNames = {"\"booth\"", "\"ordinal\""}),
	@UniqueConstraint(columnNames = {"\"merchandise\"", "\"ordinal\""})
})
public class InternalFrame implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"bulletin\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private Bulletin bulletin;

	@JoinColumn(name = "\"booth\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private Staff booth;

	@JoinColumn(name = "\"merchandise\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private Merchandise merchandise;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 2147483647)
	@Column(nullable = false, name = "\"src\"", length = 2147483647)
	private String src;

	@Column(name = "\"width\"")
	private Short width;

	@Column(name = "\"height\"")
	private Short height;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"ordinal\"")
	private short ordinal;

	/**
	 * 建構子
	 */
	public InternalFrame() {
	}

	/**
	 * @param id 主鍵
	 */
	protected InternalFrame(Long id) {
		this.id = id;
	}

	/**
	 * @param bulletin 最新消息
	 * @param src address of the resource
	 * @param ordinal 排序
	 */
	public InternalFrame(Bulletin bulletin, String src, short ordinal) {
		this.bulletin = bulletin;
		this.src = src;
		this.ordinal = ordinal;
	}

	/**
	 * @param booth 攤商
	 * @param src address of the resource
	 * @param ordinal 排序
	 */
	public InternalFrame(Staff booth, String src, short ordinal) {
		this.booth = booth;
		this.src = src;
		this.ordinal = ordinal;
	}

	/**
	 * @param merchandise 商品
	 * @param src address of the resource
	 * @param ordinal 排序
	 */
	public InternalFrame(Merchandise merchandise, String src, short ordinal) {
		this.merchandise = merchandise;
		this.src = src;
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
		if (!(object instanceof InternalFrame)) {
			return false;
		}
		InternalFrame other = (InternalFrame) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.InternalFrame[ id=" + id + " ]";
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
	 * @return 最新消息
	 */
	public Bulletin getBulletin() {
		return bulletin;
	}

	/**
	 * @param bulletin 最新消息
	 */
	public void setBulletin(Bulletin bulletin) {
		this.bulletin = bulletin;
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
	 * @return address of the resource
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src address of the resource
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * @return horizontal dimension
	 */
	public Short getWidth() {
		return width;
	}

	/**
	 * @param width horizontal dimension
	 */
	public void setWidth(Short width) {
		this.width = width;
	}

	/**
	 * @return vertical dimension
	 */
	public Short getHeight() {
		return height;
	}

	/**
	 * @param height vertical dimension
	 */
	public void setHeight(Short height) {
		this.height = height;
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
