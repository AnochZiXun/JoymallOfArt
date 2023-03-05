package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.joymall.entity.Accordion;

/**
 * 手風琴
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Repository
public interface AccordionRepository extends JpaRepository<Accordion, Short> {

	/**
	 * @return 手風琴們
	 */
	public Collection<Accordion> findByParentNullOrderByOrdinal();

	/**
	 * @param parent 上層手風琴
	 * @return 手風琴們
	 */
	public Collection<Accordion> findByParentOrderByOrdinal(@Param("parent") Accordion parent);
}
