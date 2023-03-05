package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.joymall.entity.Merchandise;
import tw.com.joymall.entity.Shelf;

/**
 * 商品
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Repository
public interface MerchandiseRepository extends JpaRepository<Merchandise, Long>, JpaSpecificationExecutor<Merchandise> {

	/**
	 * @param shelf 商品分類
	 * @return 計數
	 */
	public long countByShelf(@Param("shelf") Shelf shelf);

	/**
	 * @param shelf 商品分類
	 * @return 商品們
	 */
	public Collection<Merchandise> findByShelf(@Param("shelf") Shelf shelf);

	/**
	 * @param shelf 商品分類
	 * @param id 商品分類的主鍵
	 * @return 商品們
	 */
	public Collection<Merchandise> findByShelfAndIdNot(@Param("shelf") Shelf shelf, @Param("id") Long id);

	/**
	 * @param shelf 商品分類
	 * @param pageable 可分頁
	 * @return 可分頁的商品們
	 */
	public Page<Merchandise> findByShelf(@Param("shelf") Shelf shelf, Pageable pageable);
}
