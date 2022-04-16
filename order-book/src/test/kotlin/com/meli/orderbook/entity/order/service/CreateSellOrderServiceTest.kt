package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CREATING
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.wallet.model.Wallet
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class CreateSellOrderServiceTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @InjectMockKs
    lateinit var createOrderService: CreateSellOrderService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should create an sell-order`() {
        val wallet = Wallet(1, BigDecimal("100"), 20)

        val walletSlot = slot<Wallet>()
        val sellOrderSlot = slot<Order>()

        every { walletQueryGateway.findById(eq(1)) } returns wallet
        every { walletCommandGateway.update(capture(walletSlot)) } just Runs
        every {
            orderCommandGateway.create(capture(sellOrderSlot))
        } returns Order(1, SELL, BigDecimal("14"), 10, dateTime, id = 1)

        val sellOrder = createOrderService.createOrder(Order(1, SELL, BigDecimal("14"), 10, dateTime))

        assertEquals(1, walletSlot.captured.id)
        assertEquals(BigDecimal("100"), walletSlot.captured.amountOfMoney)
        assertEquals(10, walletSlot.captured.amountOfVibranium)

        assertEquals(null, sellOrderSlot.captured.id)
        assertEquals(10, sellOrderSlot.captured.size)
        assertEquals(dateTime, sellOrderSlot.captured.creationDate)
        assertEquals(BigDecimal("14"), sellOrderSlot.captured.price)
        assertEquals(CREATING, sellOrderSlot.captured.state)
        assertEquals(SELL, sellOrderSlot.captured.type)
        assertEquals(1, sellOrderSlot.captured.walletId)

        assertEquals(1, sellOrder.id)
        assertEquals(10, sellOrder.size)
        assertEquals(dateTime, sellOrder.creationDate)
        assertEquals(BigDecimal("14"), sellOrder.price)
        assertEquals(CREATING, sellOrder.state)
        assertEquals(SELL, sellOrder.type)
        assertEquals(1, sellOrder.walletId)

        verify(exactly = 1) { walletQueryGateway.findById(eq(1)) }
        verify(exactly = 1) { walletCommandGateway.update(capture(walletSlot)) }
        verify(exactly = 1) { orderCommandGateway.create(capture(sellOrderSlot)) }
    }
}
