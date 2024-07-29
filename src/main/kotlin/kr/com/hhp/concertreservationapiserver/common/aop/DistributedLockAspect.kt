package kr.com.hhp.concertreservationapiserver.common.aop

import kr.com.hhp.concertreservationapiserver.common.annotation.DistributedSimpleLock
import kr.com.hhp.concertreservationapiserver.common.domain.repository.DistributedLockRepository
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.util.*

@Aspect
@Component
class DistributedLockAspect {

    @Autowired
    private lateinit var distributedLockRepository: DistributedLockRepository

    @Around("@annotation(distributedSimpleLock)")
    @Throws(Throwable::class)
    fun around(joinPoint: ProceedingJoinPoint, distributedSimpleLock: DistributedSimpleLock): Any? {
        val lockKey = parseKey(distributedSimpleLock.key, joinPoint)
        val lockValue = UUID.randomUUID().toString()
        val expiration = distributedSimpleLock.expiration

        val acquired = distributedLockRepository.lock(lockKey, lockValue, expiration)
        if (!acquired) {
            throw RuntimeException("Lock을 획득하지 못하였습니다.")
        }

        return try {
            joinPoint.proceed()
        } finally {
            distributedLockRepository.unlock(lockKey, lockValue)
        }
    }

    private fun parseKey(key: String, joinPoint: ProceedingJoinPoint): String {
        val parser: ExpressionParser = SpelExpressionParser()
        val context = StandardEvaluationContext()

        val methodSignature = joinPoint.signature as MethodSignature
        val parameterNames = methodSignature.parameterNames
        val parameterValues = joinPoint.args

        for (i in parameterNames.indices) {
            context.setVariable(parameterNames[i], parameterValues[i])
        }

        return parser.parseExpression(key).getValue(context, String::class.java) ?: key
    }
}