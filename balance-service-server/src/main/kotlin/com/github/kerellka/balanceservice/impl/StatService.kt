package com.github.kerellka.balanceservice.impl

import org.apache.commons.logging.LogFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
@EnableScheduling
class StatService {

    companion object {
        private val LOGGER = LogFactory.getLog(StatService::class.java)
    }

    private val getBalanceCounter = AtomicLong(0)
    private val changeBalanceCounter = AtomicLong(0)

    @Scheduled(cron = "* * * * * *")
    fun logStats() {
        val rps = calculateRps()
        LOGGER.info("rps = $rps")
        reset()
    }

    fun incrementGetBalanceCounter() {
        getBalanceCounter.incrementAndGet()
    }

    fun incrementChangeBalanceCounter() {
        changeBalanceCounter.incrementAndGet()
    }

    private fun reset() {
        getBalanceCounter.set(0)
        changeBalanceCounter.set(0)
    }

    private fun calculateRps(): Long =
        getBalanceCounter.get() + changeBalanceCounter.get()

}