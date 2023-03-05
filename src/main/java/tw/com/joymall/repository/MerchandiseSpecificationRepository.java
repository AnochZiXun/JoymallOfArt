package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.MerchandiseSpecification;

/**
 * 商品規格
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
public interface MerchandiseSpecificationRepository extends JpaRepository<MerchandiseSpecification, Long>, JpaSpecificationExecutor<MerchandiseSpecification> {

	/**
	 * @param merchandise 商品
	 * @param name 商品規格名稱
	 * @return 計數
	 */
	public long countByMerchandiseAndName(@Param("merchandise") Merchandise merchandise, @Param("name") String name);

	/**
	 * @param merchandise 商品
	 * @param name 商品規格名稱
	 * @param id 商品規格的主鍵
	 * @return 計數
	 */
	public long countByMerchandiseAndNameAndIdNot(@Param("merchandise") Merchandise merchandise, @Param("name") String name, @Param("id") Long id);

	/**
	 * @param merchandise 商品
	 * @return 商品規格們
	 */
	public Collection<MerchandiseSpecification> findByMerchandiseOrderByName(@Param("merchandise") Merchandise merchandise);

	/**
	 * @param merchandise 商品
	 * @param pageable 可分頁
	 * @return 可分頁的商品規格們
	 */
	public Page<MerchandiseSpecification> findByMerchandiseOrderByName(@Param("merchandise") Merchandise merchandise, Pageable pageable);
}
