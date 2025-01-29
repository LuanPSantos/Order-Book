package com.codeplays.orderbook.entity.wallet.model

import com.codeplays.orderbook.entity.wallet.exception.WalletOperationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class WalletTest {

    @Test
    fun `Should not subtract a negative size`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        val exception = assertThrows<WalletOperationException> {
            wallet.subtractVibranium(-1)
        }

        assertEquals("Cant subtract -1 from 5 vibranium in wallet", exception.message)

        assertEquals(5, wallet.amountOfVibranium)
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.amountOfMoney)
    }

    @Test
    fun `Should not subtract when size is greater than the amount in the wallet`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        val exception = assertThrows<WalletOperationException> {
            wallet.subtractVibranium(15)
        }

        assertEquals("Cant subtract 15 from 5 vibranium in wallet", exception.message)

        assertEquals(5, wallet.amountOfVibranium)
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.amountOfMoney)
    }

    @Test
    fun `Should not deposit when size is lass than zero`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        val exception = assertThrows<WalletOperationException> {
            wallet.depositVibranium(-1)
        }

        assertEquals("Cant deposit -1 vibranium in wallet", exception.message)

        assertEquals(5, wallet.amountOfVibranium)
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.amountOfMoney)
    }

    @Test
    fun `Should  deposit when size to wallet`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        wallet.depositVibranium(5)

        assertEquals(10, wallet.amountOfVibranium)
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.amountOfMoney)
    }

    @Test
    fun `Should subtract a valid size`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        wallet.subtractVibranium(3)

        assertEquals(2, wallet.amountOfVibranium)
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.amountOfMoney)
    }

    @Test
    fun `Should deposit more money to wallet`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        wallet.depositMoney(BigDecimal.TEN)

        assertEquals(BigDecimal("20"), wallet.amountOfMoney)
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.amountOfVibranium)
    }

    @Test
    fun `Should not deposit a nagative amount of money`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        val exception = assertThrows<WalletOperationException> {
            wallet.depositMoney(BigDecimal.TEN.negate())
        }

        assertEquals("Cant deposit -10 in wallet", exception.message)

        assertEquals(BigDecimal("10"), wallet.amountOfMoney)
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.amountOfVibranium)
    }

    @Test
    fun `Should not subtract a nagative amount of money`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        val exception = assertThrows<WalletOperationException> {
            wallet.subtractMoney(BigDecimal.TEN.negate())
        }

        assertEquals("Cant subtract -10 (reais) from 10 (reais) in wallet", exception.message)

        assertEquals(BigDecimal("10"), wallet.amountOfMoney)
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.amountOfVibranium)
    }

    @Test
    fun `Should not subtract when the amount subtracting is greater than the wallet`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal.ONE, amountOfVibranium = 5)

        val exception = assertThrows<WalletOperationException> {
            wallet.subtractMoney(BigDecimal.TEN)
        }

        assertEquals("Cant subtract 10 (reais) from 1 (reais) in wallet", exception.message)

        assertEquals(BigDecimal.ONE, wallet.amountOfMoney)
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.amountOfVibranium)
    }

    @Test
    fun `Should subtract money from wallet`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 5)

        wallet.subtractMoney(BigDecimal.ONE)

        assertEquals(BigDecimal("9"), wallet.amountOfMoney)
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.amountOfVibranium)
    }
}