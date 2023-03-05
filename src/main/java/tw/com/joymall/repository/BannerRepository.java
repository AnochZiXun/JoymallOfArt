package tw.com.joymall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.joymall.entity.Banner;

/**
 * 連播橫幅
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Short>, JpaSpecificationExecutor<Banner> {

	/**
	 * @param ordinal 排序
	 * @return 連播橫幅
	 */
	public Banner findOneByOrdinal(@Param("ordinal") short ordinal);
}
