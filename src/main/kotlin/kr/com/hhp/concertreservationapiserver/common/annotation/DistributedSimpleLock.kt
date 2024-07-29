package kr.com.hhp.concertreservationapiserver.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedSimpleLock(val key: String, val expiration: Long = 30000)
