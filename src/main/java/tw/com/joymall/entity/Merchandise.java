package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 商品
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Merchandise.findAll", query = "SELECT m FROM Merchandise m")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Merchandise\"")
public class Merchandise implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"shelf\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne(optional = false)
	private Shelf shelf;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 16)
	@Column(nullable = false, name = "\"name\"", length = 16)
	private String name;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"price\"")
	private int price;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 65536)
	@Column(nullable = false, name = "\"html\"", length = 65536)
	private String html;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"carrying\"")
	private boolean carrying;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"inStock\"")
	private boolean inStock;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"recommended\"")
	private boolean recommended;

	/**
	 * 建構子
	 */
	public Merchandise() {
		this.carrying = true;
		this.inStock = true;
		this.recommended = false;
	}

	/**
	 * @param id 主鍵
	 */
	protected Merchandise(Long id) {
		this.id = id;
		this.carrying = true;
		this.inStock = true;
		this.recommended = false;
	}

	/**
	 * @param shelf 商品分類
	 * @param name 商品名稱
	 * @param price 單價
	 * @param html HTML內容(描述)
	 * @param carrying 上架|下架
	 * @param inStock 有貨
	 * @param recommended 推薦
	 */
	public Merchandise(Shelf shelf, String name, int price, String html, boolean carrying, boolean inStock, boolean recommended) {
		this.shelf = shelf;
		this.name = name;
		this.price = price;
		this.html = html;
		this.carrying = carrying;
		this.inStock = inStock;
		this.recommended = recommended;
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
		if (!(object instanceof Merchandise)) {
			return false;
		}
		Merchandise other = (Merchandise) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Merchandise[ id=" + id + " ]";
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
	 * @return 商品分類
	 */
	public Shelf getShelf() {
		return shelf;
	}

	/**
	 * @param shelf 商品分類
	 */
	public void setShelf(Shelf shelf) {
		this.shelf = shelf;
	}

	/**
	 * @return 商品名稱
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 商品名稱
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 單價
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @param price 單價
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * @return HTML內容(描述)
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param html HTML內容(描述)
	 */
	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * @return 上架|下架
	 */
	public boolean isCarrying() {
		return carrying;
	}

	/**
	 * @param carrying 上架|下架
	 */
	public void setCarrying(boolean carrying) {
		this.carrying = carrying;
	}

	/**
	 * @return 有貨
	 */
	public boolean isInStock() {
		return inStock;
	}

	/**
	 * @param inStock 有貨
	 */
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	/**
	 * @return 推薦
	 */
	public boolean isRecommended() {
		return recommended;
	}

	/**
	 * @param recommended 推薦
	 */
	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}
}
