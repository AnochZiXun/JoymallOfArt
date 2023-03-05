package tw.com.joymall.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import tw.com.joymall.entity.Regular;

/**
 * 會員
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Repository
public interface RegularRepository extends JpaRepository<Regular, Integer>, JpaSpecificationExecutor<Regular> {

	/**
	 * @param email 帳號(電子郵件)
	 * @return 計數
	 */
	public long countByEmail(@Param("email") String email);

	/**
	 * @param email 帳號(電子郵件)
	 * @param id 主鍵
	 * @return 計數
	 */
	public long countByEmailAndIdNot(@Param("email") String email, @Param("id") Integer id);

	/**
	 * @param email 帳號(電子郵件)
	 * @return 會員
	 */
	public Regular findOneByEmail(@Param("email") String email);
}
