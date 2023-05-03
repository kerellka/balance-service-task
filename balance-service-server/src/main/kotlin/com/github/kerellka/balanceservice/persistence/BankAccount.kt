package com.github.kerellka.balanceservice.persistence

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bank_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class BankAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "amount")
    var amount: Long
)

@Repository
interface BankAccountRepository : JpaRepository<BankAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankAccount b WHERE b.id = :id")
    fun findByIdForUpdate(@Param("id") id: Long): BankAccount?

}
