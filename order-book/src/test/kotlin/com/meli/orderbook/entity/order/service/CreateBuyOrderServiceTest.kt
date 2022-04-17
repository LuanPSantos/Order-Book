package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CREATING
import com.meli.orderbook.entity.order.model.Order.Type.BUY
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

class CreateBuyOrderServiceTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @InjectMockKs
    lateinit var createOrderService: CreateBuyOrderService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should create an buy-order`() {
        val wallet = Wallet(1, BigDecimal("100"), 20)

        val walletSlot = slot<Wallet>()
        val buyOrderSlot = slot<Order>()

        every { walletQueryGateway.findById(eq(1)) } returns wallet
        every { walletCommandGateway.update(capture(walletSlot)) } just Runs
        every {
            orderCommandGateway.create(capture(buyOrderSlot))
        } returns Order(1, BUY, BigDecimal("14"), 5, dateTime, id = 1)

        val buyOrder = createOrderService.createOrder(Order(1, BUY, BigDecimal("14"), 5, dateTime))

        assertEquals(1, walletSlot.captured.id)
        assertEquals(BigDecimal("30"), walletSlot.captured.amountOfMoney)
        assertEquals(20, walletSlot.captured.amountOfVibranium)

        assertEquals(null, buyOrderSlot.captured.id)
        assertEquals(5, buyOrderSlot.captured.size)
        assertEquals(dateTime, buyOrderSlot.captured.creationDate)
        assertEquals(BigDecimal("14"), buyOrderSlot.captured.price)
        assertEquals(CREATING, buyOrderSlot.captured.state)
        assertEquals(BUY, buyOrderSlot.captured.type)
        assertEquals(1, buyOrderSlot.captured.walletId)

        assertEquals(1, buyOrder.id)
        assertEquals(5, buyOrder.size)
        assertEquals(dateTime, buyOrder.creationDate)
        assertEquals(BigDecimal("14"), buyOrder.price)
        assertEquals(CREATING, buyOrder.state)
        assertEquals(BUY, buyOrder.type)
        assertEquals(1, buyOrder.walletId)

        verify(exactly = 1) { walletQueryGateway.findById(eq(1)) }
        verify(exactly = 1) { walletCommandGateway.update(capture(walletSlot)) }
        verify(exactly = 1) { orderCommandGateway.create(capture(buyOrderSlot)) }
    }
}
