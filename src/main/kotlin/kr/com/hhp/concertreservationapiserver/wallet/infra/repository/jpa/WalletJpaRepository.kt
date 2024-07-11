package kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletJpaRepository: JpaRepository<WalletEntity, Long> {

    fun findByWalletId(walletId: Long): WalletEntity?
}