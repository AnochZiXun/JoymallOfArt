package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 商品規格
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "MerchandiseSpecification.findAll", query = "SELECT ms FROM MerchandiseSpecification ms")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"MerchandiseSpecification\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"merchandise\"", "\"name\""})
})
public class MerchandiseSpecification implements java.io.Serializable {

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
	@Size(max = 8)
	@Column(nullable = false, name = "\"name\"", length = 8)
	private String name;

	/**
	 * 建構子
	 */
	public MerchandiseSpecification() {
	}

	/**
	 * @param id 主鍵
	 */
	protected MerchandiseSpecification(Long id) {
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
		if (!(object instanceof MerchandiseSpecification)) {
			return false;
		}
		MerchandiseSpecification other = (MerchandiseSpecification) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.MerchandiseSpecification[ id=" + id + " ]";
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
	 * @return 商品規格名稱
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 商品規格名稱
	 */
	public void setName(String name) {
		this.name = name;
	}
}
