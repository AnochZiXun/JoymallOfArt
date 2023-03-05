package tw.com.joymall.entity;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 忘記密碼
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Forgot.findAll", query = "SELECT f FROM Forgot f")
})
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Forgot\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"code\""})
})
public class Forgot implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Long id;

	@JoinColumn(name = "\"booth\"", referencedColumnName = "\"id\"", nullable = false)
	@ManyToOne(optional = false)
	private Staff booth;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 16)
	@Column(nullable = false, name = "\"code\"", length = 16)
	private String code;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"when\"")
	@Temporal(TemporalType.TIMESTAMP)
	private Date when;

	/**
	 * 建構子
	 */
	public Forgot() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Forgot(Long id) {
		this.id = id;
	}

	/**
	 * @param booth 攤商
	 * @param code 辨識碼
	 * @param when 何時
	 */
	public Forgot(Staff booth, String code, Date when) {
		this.booth = booth;
		this.code = code;
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
		if (!(object instanceof Forgot)) {
			return false;
		}
		Forgot other = (Forgot) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Forgot[ id=" + id + " ]";
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
	 * @return 辨識碼
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code 辨識碼
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return 何時
	 */
	public Date getWhen() {
		return when;
	}

	/**
	 * @param when 何時
	 */
	public void setWhen(Date when) {
		this.when = when;
	}
}
