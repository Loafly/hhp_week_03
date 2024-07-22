package kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WalletHistoryJpaRepository: JpaRepository<WalletHistoryEntity, Long> {
}