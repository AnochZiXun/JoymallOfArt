package tw.com.joymall.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.joymall.entity.Cart;
import tw.com.joymall.entity.Packet;

/**
 * 明細
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	/**
	 * @param packet 訂單
	 * @return 訂單的明細
	 */
	public Collection<Cart> findByPacket(@Param("packet") Packet packet);
}
