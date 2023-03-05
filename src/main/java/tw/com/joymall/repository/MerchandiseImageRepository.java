package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.MerchandiseImage;

/**
 * 商品圖片
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
public interface MerchandiseImageRepository extends JpaRepository<MerchandiseImage, Long>, JpaSpecificationExecutor<MerchandiseImage> {

	/**
	 * @param merchandise 商品
	 * @return 計數
	 */
	public long countByMerchandise(@Param("merchandise") Merchandise merchandise);

	/**
	 * @param merchandise 商品
	 * @return 商品圖片們
	 */
	public Collection<MerchandiseImage> findByMerchandiseOrderByOrdinal(@Param("merchandise") Merchandise merchandise);

	/**
	 * @param merchandise 商品
	 * @param pageable 可分頁
	 * @return 可分頁的商品圖片們
	 */
	public Page<MerchandiseImage> findByMerchandiseOrderByOrdinal(@Param("merchandise") Merchandise merchandise, Pageable pageable);

	/**
	 * @param merchandise 商品
	 * @param ordinal 排序
	 * @return 商品圖片
	 */
	public MerchandiseImage findOneByMerchandiseAndOrdinal(@Param("merchandise") Merchandise merchandise, @Param("ordinal") short ordinal);

	/**
	 * @param merchandise 商品
	 * @return 商品圖片
	 */
	public MerchandiseImage findTopByMerchandiseOrderByOrdinal(@Param("merchandise") Merchandise merchandise);
}
