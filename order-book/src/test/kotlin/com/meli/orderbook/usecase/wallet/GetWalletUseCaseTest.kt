package com.meli.orderbook.usecase.wallet

import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import com.meli.orderbook.usecase.wallet.GetWalletUseCase.Input
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class GetWalletUseCaseTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var orderQueryGateway: OrderQueryGateway

    @InjectMockKs
    lateinit var getWalletUseCase: GetWalletUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should get the wallet that has a sell-order in trade`() {
        every { walletQueryGateway.findById(eq(1)) } returns Wallet(1, BigDecimal("10"), 10)
        every {
            orderQueryGateway.findAllOrdersInTradeByWallet(eq(1))
        } returns listOf(Order(1, Order.Type.SELL, BigDecimal("10"), 1, dateTime, IN_TRADE))

        val output = getWalletUseCase.execute(Input(1))

        assertEquals(1, output.wallet.id)
        assertEquals(BigDecimal("10"), output.wallet.amountOfMoney)
        assertEquals(10, output.wallet.amountOfVibranium)
        assertEquals(BigDecimal("0"), output.inTrade.amountOfMoney)
        assertEquals(1, output.inTrade.amountOfVibranium)
    }

    @Test
    fun `Should get the wallet that has a buy-order in trade`() {
        every { walletQueryGateway.findById(eq(1)) } returns Wallet(1, BigDecimal("10"), 10)
        every {
            orderQueryGateway.findAllOrdersInTradeByWallet(eq(1))
        } returns listOf(Order(1, Order.Type.BUY, BigDecimal("10"), 1, dateTime, IN_TRADE))

        val output = getWalletUseCase.execute(Input(1))

        assertEquals(1, output.wallet.id)
        assertEquals(BigDecimal("10"), output.wallet.amountOfMoney)
        assertEquals(10, output.wallet.amountOfVibranium)
        assertEquals(BigDecimal("10"), output.inTrade.amountOfMoney)
        assertEquals(0, output.inTrade.amountOfVibranium)
    }

    @Test
    fun `Should get the wallet that has nothing in trade`() {
        every { walletQueryGateway.findById(eq(1)) } returns Wallet(1, BigDecimal("10"), 10)
        every {
            orderQueryGateway.findAllOrdersInTradeByWallet(eq(1))
        } returns listOf()

        val output = getWalletUseCase.execute(Input(1))

        assertEquals(1, output.wallet.id)
        assertEquals(BigDecimal("10"), output.wallet.amountOfMoney)
        assertEquals(10, output.wallet.amountOfVibranium)
        assertEquals(BigDecimal("0"), output.inTrade.amountOfMoney)
        assertEquals(0, output.inTrade.amountOfVibranium)
    }
}