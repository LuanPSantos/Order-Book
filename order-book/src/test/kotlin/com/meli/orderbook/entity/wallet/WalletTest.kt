package com.meli.orderbook.entity.wallet

import com.meli.orderbook.entity.wallet.model.Wallet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.math.BigDecimal

class WalletTest {

    @Test
    fun `Should not subtract a negative size`() {
        val wallet = Wallet(1, BigDecimal.TEN, 5)

        val exception = assertThrows<IllegalArgumentException> {
            wallet.subtractAssets(-1)
        }

        assertEquals("Invalid size to subtract", exception.message)

        assertEquals(5, wallet.getTheAmountOfAssets())
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.getTheAmountOfMoney())
    }

    @Test
    fun `Should not subtract when size is greater than the amount in the wallet`() {
        val wallet = Wallet(1, BigDecimal.TEN, 5)

        val exception = assertThrows<IllegalArgumentException> {
            wallet.subtractAssets(15)
        }

        assertEquals("Invalid size to subtract", exception.message)

        assertEquals(5, wallet.getTheAmountOfAssets())
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.getTheAmountOfMoney())
    }

    @Test
    fun `Should subtract a valid size`() {
        val wallet = Wallet(1, BigDecimal.TEN, 5)

        wallet.subtractAssets(3)

        assertEquals(2, wallet.getTheAmountOfAssets())
        assertEquals(1, wallet.id)
        assertEquals(BigDecimal.TEN, wallet.getTheAmountOfMoney())
    }

    @Test
    fun `Should deposit more money to wallet`() {
        val wallet = Wallet(1, BigDecimal.TEN, 5)

        wallet.depositMoney(BigDecimal.TEN)

        assertEquals(BigDecimal("20"), wallet.getTheAmountOfMoney())
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.getTheAmountOfAssets())
    }

    @Test
    fun `Should not deposit a nagative amount of money`() {
        val wallet = Wallet(1, BigDecimal.TEN, 5)

        val exception = assertThrows<IllegalArgumentException> {
            wallet.depositMoney(BigDecimal.TEN.negate())
        }

        assertEquals("Invalid deposit value", exception.message)

        assertEquals(BigDecimal("10"), wallet.getTheAmountOfMoney())
        assertEquals(1, wallet.id)
        assertEquals(5, wallet.getTheAmountOfAssets())
    }
}