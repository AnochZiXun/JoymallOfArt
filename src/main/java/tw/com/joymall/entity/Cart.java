package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 明細
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Cart.findAll", query = "SELECT c FROM Cart c")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Cart\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"merchandise\"", "\"specification\"", "\"packet\""})
})
public class Cart implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"merchandise\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private Merchandise merchandise;

	@Size(max = 8)
	@Column(name = "\"specification\"", length = 8)
	private String specification;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"quantity\"")
	private short quantity;

	@JoinColumn(name = "\"packet\"", referencedColumnName = "\"id\"")
	@ManyToOne
	private Packet packet;

	/**
	 * 建構子
	 */
	public Cart() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Cart(Long id) {
		this.id = id;
	}

	/**
	 * @param merchandise 商品
	 * @param specification 規格
	 * @param quantity 數量
	 * @param packet 訂單
	 */
	public Cart(Merchandise merchandise, String specification, short quantity, Packet packet) {
		this.merchandise = merchandise;
		this.specification = specification;
		this.quantity = quantity;
		this.packet = packet;
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
		if (!(object instanceof Cart)) {
			return false;
		}
		Cart other = (Cart) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Cart[ id=" + id + " ]";
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
	 * @return 規格
	 */
	public String getSpecification() {
		return specification;
	}

	/**
	 * @param specification 規格
	 */
	public void setSpecification(String specification) {
		this.specification = specification;
	}

	/**
	 * @return 數量
	 */
	public short getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity 數量
	 */
	public void setQuantity(short quantity) {
		this.quantity = quantity;
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
}
