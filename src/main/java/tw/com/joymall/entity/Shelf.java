package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 商品分類
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Shelf\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"booth\"", "\"name\""})
})
@NamedQueries({
	@NamedQuery(name = "Shelf.findAll", query = "SELECT s FROM Shelf s")
})
public class Shelf implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"booth\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne
	private Staff booth;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 8)
	@Column(nullable = false, name = "\"name\"", length = 8)
	private String name;

	/**
	 * 建構子
	 */
	public Shelf() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Shelf(Long id) {
		this.id = id;
	}

	/**
	 * @param booth 攤商
	 * @param name 商品分類名稱
	 */
	public Shelf(Staff booth, String name) {
		this.booth = booth;
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
		if (!(object instanceof Shelf)) {
			return false;
		}
		Shelf other = (Shelf) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Shelf[ id=" + id + " ]";
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
	 * @return 商品分類名稱
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 商品分類名稱
	 */
	public void setName(String name) {
		this.name = name;
	}
}
