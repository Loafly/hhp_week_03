package kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WalletHistoryJpaRepository: JpaRepository<WalletHistoryEntity, Long> {
}