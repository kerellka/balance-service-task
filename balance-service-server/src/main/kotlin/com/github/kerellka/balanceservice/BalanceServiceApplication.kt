package com.github.kerellka.balanceservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [HazelcastAutoConfiguration::class])
class BalanceServiceApplication

fun main(args: Array<String>) {
	runApplication<BalanceServiceApplication>(*args)
}
