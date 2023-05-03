package com.github.kerellka

import com.github.kerellka.proto.BalanceServiceGrpc
import com.github.kerellka.proto.BalanceServiceGrpc.BalanceServiceBlockingStub
import com.github.kerellka.proto.BankAccount.ChangeBalanceRequest
import com.github.kerellka.proto.BankAccount.GetBalanceRequest
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

    val stub = getClientStub(
        host = args[0].split(":")[0],
        port = args[0].split(":")[1].toInt()
    )

    val threadCount = args[1].toInt()
    val readQuota = args[2].toInt()
    val writeQuota = args[3].toInt()
    val lowerBoundId = args[4].split("..")[0].toInt()
    val upperBoundId = args[4].split("..")[1].toInt()
    val ids = lowerBoundId..upperBoundId

    val task = Runnable {
        while(true) {
            val readProbability = readQuota.toDouble() / (readQuota + writeQuota).toDouble()
            val randomId = ThreadLocalRandom.current().nextLong(ids.first.toLong(), ids.last.toLong())

            if (ThreadLocalRandom.current().nextDouble() < readProbability) {
                stub.getBalance(randomId)
            } else {
                stub.changeBalance(randomId, 1L)
            }
        }
    }

    val executor = Executors.newFixedThreadPool(threadCount)
    for (i in 1..threadCount) {
        executor.execute(task)
    }

    executor.shutdown()
    try {
        if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
            executor.shutdownNow()
        }
    } catch (e: InterruptedException) {
        executor.shutdownNow()
    }


}

fun getClientStub(host: String, port: Int): BalanceServiceGrpc.BalanceServiceBlockingStub {
    val channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build()

    return BalanceServiceGrpc.newBlockingStub(channel)
}

fun BalanceServiceBlockingStub.getBalance(id: Long) {
    val request = GetBalanceRequest.newBuilder().setId(id).build()
    this.getBalance(request)
}

fun BalanceServiceBlockingStub.changeBalance(id: Long, amount: Long) {
    val request = ChangeBalanceRequest.newBuilder().setId(id).setAmount(amount).build()
    this.changeBalance(request)
}