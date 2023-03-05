package tw.com.joymall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.joymall.entity.PacketStatus;

/**
 * 訂單狀態
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
public interface PacketStatusRepository extends JpaRepository<PacketStatus, Short> {
}
