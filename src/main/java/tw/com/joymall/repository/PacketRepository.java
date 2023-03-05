package tw.com.joymall.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.joymall.entity.Packet;
import tw.com.joymall.entity.PacketStatus;
import tw.com.joymall.entity.Staff;

/**
 * 訂單
 *
 * @author P-C Lin (a.k.a 高科技黑手)
 */
@Repository
public interface PacketRepository extends JpaRepository<Packet, Long>, JpaSpecificationExecutor<Packet> {

	/**
	 * @param merchantTradeNo 交易編號
	 * @return 計數
	 */
	public long countByMerchantTradeNo(@Param("MerchantTradeNo") String merchantTradeNo);

	/**
	 * @param merchantTradeNo 交易編號
	 * @return 訂單
	 */
	public Packet findOneByMerchantTradeNo(@Param("MerchantTradeNo") String merchantTradeNo);

	/**
	 * @param booth 攤商
	 * @param pageable 可分頁
	 * @return 訂單們
	 */
	public Page<Packet> findByBoothOrderByMerchantTradeDateDesc(@Param("booth") Staff booth, Pageable pageable);

	/**
	 * @param booth 攤商
	 * @param packetStatus 訂單狀態
	 * @param pageable 可分頁
	 * @return 訂單們
	 */
	public Page<Packet> findByBoothAndPacketStatusOrderByMerchantTradeDateDesc(@Param("booth") Staff booth, @Param("packetStatus") PacketStatus packetStatus, Pageable pageable);
}
