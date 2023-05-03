package com.github.kerellka.balanceservice.api

interface BalanceService {

    /**
     *  Получение баланса
     *
     *  @param id идентификатор банковского счёта
     *  @return сумма денег на банковском счёте
     */
    fun getBalance(id: Long): Long?

    /**
     * Изменение баланса на определённое значение
     *
     * @param id идентификатор банковского счёта
     * @param amount сумма денег, которую нужно добавить к банковскому счёту
     */
    fun changeBalance(id: Long, amount: Long)

}