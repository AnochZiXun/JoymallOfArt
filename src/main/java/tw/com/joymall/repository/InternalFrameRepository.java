package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.joymall.entity.Bulletin;
import tw.com.joymall.entity.InternalFrame;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Staff;

/**
 * nested browsing context
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Repository
public interface InternalFrameRepository extends JpaRepository<InternalFrame, Long>, JpaSpecificationExecutor<InternalFrame> {

	/**
	 * @param booth 最新消息
	 * @return 計數
	 */
	public long countByBooth(@Param("booth") Staff booth);

	/**
	 * @param bulletin 攤商
	 * @return 計數
	 */
	public long countByBulletin(@Param("bulletin") Bulletin bulletin);

	/**
	 * @param merchandise 商品
	 * @return 計數
	 */
	public long countByMerchandise(@Param("merchandise") Merchandise merchandise);

	/**
	 * @param booth 攤商
	 * @return &lt;IFRAME/&gt; 們
	 */
	Collection<InternalFrame> findByBoothOrderByOrdinal(@Param("booth") Staff booth);

	/**
	 * @param bulletin 最新消息
	 * @return &lt;IFRAME/&gt; 們
	 */
	Collection<InternalFrame> findByBulletinOrderByOrdinal(@Param("bulletin") Bulletin bulletin);

	/**
	 * @param merchandise 商品
	 * @return &lt;IFRAME/&gt; 們
	 */
	Collection<InternalFrame> findByMerchandiseOrderByOrdinal(@Param("merchandise") Merchandise merchandise);

	/**
	 * @param booth 攤商
	 * @param pageable 可分頁
	 * @return 可分頁的 &lt;IFRAME/&gt; 們
	 */
	Page<InternalFrame> findByBoothOrderByOrdinal(@Param("booth") Staff booth, Pageable pageable);

	/**
	 * @param bulletin 最新消息
	 * @param pageable 可分頁
	 * @return 可分頁的 &lt;IFRAME/&gt; 們
	 */
	Page<InternalFrame> findByBulletinOrderByOrdinal(@Param("bulletin") Bulletin bulletin, Pageable pageable);

	/**
	 * @param merchandise 商品
	 * @param pageable 可分頁
	 * @return 可分頁的 &lt;IFRAME/&gt; 們
	 */
	Page<InternalFrame> findByMerchandiseOrderByOrdinal(@Param("merchandise") Merchandise merchandise, Pageable pageable);

	/**
	 * @param booth 攤商
	 * @param ordinal 排序
	 * @return &lt;IFRAME/&gt;
	 */
	InternalFrame findOneByBoothAndOrdinal(@Param("booth") Staff booth, @Param("ordinal") short ordinal);

	/**
	 * @param bulletin 最新消息
	 * @param ordinal 排序
	 * @return &lt;IFRAME/&gt;
	 */
	InternalFrame findOneByBulletinAndOrdinal(@Param("bulletin") Bulletin bulletin, @Param("ordinal") short ordinal);

	/**
	 * @param merchandise 商品
	 * @param ordinal 排序
	 * @return &lt;IFRAME/&gt;
	 */
	InternalFrame findOneByMerchandiseAndOrdinal(@Param("merchandise") Merchandise merchandise, @Param("ordinal") short ordinal);
}
