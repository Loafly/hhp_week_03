package kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa

import jakarta.persistence.LockModeType
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WalletJpaRepository: JpaRepository<WalletEntity, Long> {

    fun findByWalletId(walletId: Long): WalletEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM wallet as w WHERE w.walletId = :walletId")
    fun findByWalletIdWithXLock(walletId: Long): WalletEntity?
    fun findByUserId(walletId: Long): WalletEntity?
}