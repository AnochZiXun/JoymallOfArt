package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 手風琴
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Accordion.findAll", query = "SELECT a FROM Accordion a")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Accordion\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"fragment\""}),
	@UniqueConstraint(columnNames = {"\"name\""}),
	@UniqueConstraint(columnNames = {"\"parent\"", "\"ordinal\""})
})
public class Accordion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Short id;

	@JoinColumn(name = "\"parent\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private Accordion parent;

	@Size(max = 16)
	@Column(name = "\"fragment\"", length = 16)
	private String fragment;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"internal\"")
	private boolean internal;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 8)
	@Column(nullable = false, name = "\"name\"", length = 8)
	private String name;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"ordinal\"")
	private short ordinal;

	/**
	 * 建構子
	 */
	public Accordion() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Accordion(Short id) {
		this.id = id;
	}

	/**
	 * @param internal 內部使用
	 * @param name header or container name
	 * @param ordinal 排序
	 */
	public Accordion(boolean internal, String name, short ordinal) {
		this.internal = internal;
		this.name = name;
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
		if (!(object instanceof Accordion)) {
			return false;
		}
		Accordion other = (Accordion) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Accordion[ id=" + id + " ]";
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
	 * @return 上層手風琴
	 */
	public Accordion getParent() {
		return parent;
	}

	/**
	 * @param parent 上層手風琴
	 */
	public void setParent(Accordion parent) {
		this.parent = parent;
	}

	/**
	 * @return 網址片段
	 */
	public String getFragment() {
		return fragment;
	}

	/**
	 * @param fragment 網址片段
	 */
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	/**
	 * @return 內部使用
	 */
	public boolean isInternal() {
		return internal;
	}

	/**
	 * @param internal 內部使用
	 */
	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	/**
	 * @return header or container name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name header or container name
	 */
	public void setName(String name) {
		this.name = name;
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
