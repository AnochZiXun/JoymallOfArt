package tw.com.joymall.repository;

import tw.com.joymall.entity.Staff;
import tw.com.joymall.entity.Forgot;
import org.springframework.data.repository.query.Param;

/**
 * 忘記密碼
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@org.springframework.stereotype.Repository
public interface ForgotRepository extends org.springframework.data.jpa.repository.JpaRepository<Forgot, Long> {

	/**
	 * @param booth 攤商
	 * @return 計數
	 */
	public long countByBooth(@Param("booth") Staff booth);

	/**
	 * @param code 辨識碼
	 * @return 計數
	 */
	public long countByCode(@Param("code") String code);

	/**
	 * @param code 辨識碼
	 * @return 忘記密碼
	 */
	public Forgot findOneByCode(@Param("code") String code);
}
