package com.github.kerellka.balanceservice.grpc

import com.github.kerellka.balanceservice.api.BalanceService
import com.github.kerellka.balanceservice.impl.NotFoundException
import com.github.kerellka.balanceservice.impl.StatService
import com.github.kerellka.proto.BalanceServiceGrpcKt
import com.github.kerellka.proto.BankAccount.ChangeBalanceRequest
import com.github.kerellka.proto.BankAccount.GetBalanceRequest
import com.github.kerellka.proto.BankAccount.GetBalanceResponse
import com.google.protobuf.Empty
import com.google.protobuf.Int64Value
import io.grpc.Status
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class BalanceGrpcService(
    private val balanceService: BalanceService,
    private val statService: StatService
): BalanceServiceGrpcKt.BalanceServiceCoroutineImplBase() {

    override suspend fun getBalance(request: GetBalanceRequest): GetBalanceResponse {
        val amount = balanceService.getBalance(request.id)
        statService.incrementGetBalanceCounter()
        return amount?.let {
            GetBalanceResponse.newBuilder().setAmount(Int64Value.of(it)).build()
        } ?: GetBalanceResponse.newBuilder().setAmount(Int64Value.newBuilder().clearValue().build()).build()
    }

    override suspend fun changeBalance(request: ChangeBalanceRequest): Empty {
        balanceService.changeBalance(request.id, request.amount)
        statService.incrementChangeBalanceCounter()
        return Empty.getDefaultInstance()
    }

}

@GrpcAdvice
class GrpcExceptionAdvice {

    @GrpcExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): Status =
        Status.NOT_FOUND.withDescription(e.message).withCause(e)

}