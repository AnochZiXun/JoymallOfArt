package tw.com.joymall.entity;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 會員
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Entity
@Table(catalog = "\"artMall\"", schema = "\"public\"", name = "\"Regular\"", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"\"email\""})
})
@NamedQueries({
	@NamedQuery(name = "Regular.findAll", query = "SELECT r FROM Regular r")
})
public class Regular implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false, name = "\"id\"")
	private Integer id;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 16)
	@Column(nullable = false, name = "\"lastname\"", length = 16)
	private String lastname;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 16)
	@Column(nullable = false, name = "\"firstname\"", length = 16)
	private String firstname;

	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email")
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(nullable = false, name = "\"email\"", length = 256)
	private String email;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(nullable = false, name = "\"shadow\"", length = 128)
	private String shadow;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"birth\"")
	@Temporal(TemporalType.DATE)
	private Date birth;

	@Basic(optional = false)
	@NotNull
	@Column(nullable = false, name = "\"gender\"")
	private boolean gender;

	@Size(max = 16)
	@Column(name = "\"phone\"", length = 16)
	private String phone;

	@Size(max = 32)
	@Column(name = "\"address\"", length = 32)
	private String address;

	/**
	 * 建構子
	 */
	public Regular() {
	}

	/**
	 * @param id 主鍵
	 */
	protected Regular(Integer id) {
		this.id = id;
	}

	/**
	 * @param lastname 姓氏
	 * @param firstname 名字
	 * @param email 電子郵件
	 * @param shadow 密碼
	 * @param birth 生日
	 * @param gender 性別
	 */
	public Regular(String lastname, String firstname, String email, String shadow, Date birth, boolean gender) {
		this.lastname = lastname;
		this.firstname = firstname;
		this.email = email;
		this.shadow = shadow;
		this.birth = birth;
		this.gender = gender;
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
		if (!(object instanceof Regular)) {
			return false;
		}
		Regular other = (Regular) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "tw.com.e95.entity.Regular[ id=" + id + " ]";
	}

	/**
	 * @return 主鍵
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id 主鍵
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return 姓氏
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname 姓氏
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return 名字
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname 名字
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return 電子郵件
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email 電子郵件
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return 密碼
	 */
	public String getShadow() {
		return shadow;
	}

	/**
	 * @param shadow 密碼
	 */
	public void setShadow(String shadow) {
		this.shadow = shadow;
	}

	/**
	 * @return 生日
	 */
	public Date getBirth() {
		return birth;
	}

	/**
	 * @param birth 生日
	 */
	public void setBirth(Date birth) {
		this.birth = birth;
	}

	/**
	 * @return 性別
	 */
	public boolean getGender() {
		return gender;
	}

	/**
	 * @param gender 性別
	 */
	public void setGender(boolean gender) {
		this.gender = gender;
	}

	/**
	 * @return 聯絡電話
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone 聯絡電話
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return 預設地址
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address 預設地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
