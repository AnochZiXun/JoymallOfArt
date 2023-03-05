package tw.com.joymall.entity;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 訂單狀態
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "PacketStatus.findAll", query = "SELECT p FROM PacketStatus p")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"PacketStatus\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"name\""})
})
public class PacketStatus implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Short id;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 8)
	@Column(nullable = false, name = "\"name\"", length = 8)
	private String name;

	/**
	 * 建構子
	 */
	public PacketStatus() {
	}

	/**
	 * @param id 主鍵
	 */
	protected PacketStatus(Short id) {
		this.id = id;
	}

	/**
	 * @param name 訂單狀態名稱
	 */
	public PacketStatus(String name) {
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
		if (!(object instanceof PacketStatus)) {
			return false;
		}
		PacketStatus other = (PacketStatus) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.PacketStatus[ id=" + id + " ]";
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
	 * @return 訂單狀態名稱
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 訂單狀態名稱
	 */
	public void setName(String name) {
		this.name = name;
	}
}
