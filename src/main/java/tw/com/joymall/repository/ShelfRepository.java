package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import tw.com.joymall.entity.Shelf;
import tw.com.joymall.entity.Staff;

/**
 * 商品分類
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
public interface ShelfRepository extends JpaRepository<Shelf, Long>, JpaSpecificationExecutor<Shelf> {

	/**
	 * @param booth 攤商
	 * @param name 商品分類名稱
	 * @return 計數
	 */
	public long countByBoothAndName(@Param("booth") Staff booth, @Param("name") String name);

	/**
	 * @param booth 攤商
	 * @param name 商品分類名稱
	 * @param id 主鍵
	 * @return 計數
	 */
	public long countByBoothAndNameAndIdNot(@Param("booth") Staff booth, @Param("name") String name, @Param("id") Long id);

	/**
	 * @param booth 攤商
	 * @param pageable 可分頁
	 * @return 商品分類們
	 */
	public Page<Shelf> findByBooth(@Param("booth") Staff booth, Pageable pageable);

	/**
	 * @param booth 攤商
	 * @return 商品分類們
	 */
	public Collection<Shelf> findByBoothOrderByName(@Param("booth") Staff booth);
}
