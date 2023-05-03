package com.github.kerellka.balanceservice.impl

import com.github.kerellka.balanceservice.api.BalanceService
import com.github.kerellka.balanceservice.persistence.BankAccountRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
open class BalanceServiceImpl(
    private val bankAccountRepository: BankAccountRepository
): BalanceService {


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    override fun getBalance(id: Long): Long? {
        return bankAccountRepository.findByIdOrNull(id)?.amount
            ?: throw NotFoundException("bank account with id = $id not found")
    }

    @Transactional
    override fun changeBalance(id: Long, amount: Long) {
        val bankAccount = bankAccountRepository.findByIdForUpdate(id)
            ?: throw NotFoundException("bank account with id = $id not found")

        bankAccount.amount += amount
    }

}

class NotFoundException(msg: String): RuntimeException(msg)