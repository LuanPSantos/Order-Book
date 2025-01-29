package com.codeplays.orderbook.entity.order.service

import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.State.CREATING
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.wallet.model.Wallet
import com.codeplays.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.codeplays.orderbook.entity.wallet.gateway.WalletQueryGateway
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class CreateSaleOrderServiceTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @InjectMockKs
    lateinit var createOrderService: CreateSaleOrderService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should create an sale-order`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("100"), amountOfVibranium = 20)

        val walletSlot = slot<Wallet>()
        val saleOrderSlot = slot<Order>()

        every { walletQueryGateway.findById(eq(1)) } returns wallet
        every { walletCommandGateway.update(capture(walletSlot)) } just Runs
        every {
            orderCommandGateway.create(capture(saleOrderSlot))
        } returns Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("14"), size = 10, creationDate = dateTime)

        val saleOrder = createOrderService.createOrder(
            Order(
                walletId = 1,
                type = SALE,
                price = BigDecimal("14"),
                size = 10,
                creationDate = dateTime
            )
        )

        assertEquals(1, walletSlot.captured.id)
        assertEquals(BigDecimal("100"), walletSlot.captured.amountOfMoney)
        assertEquals(10, walletSlot.captured.amountOfVibranium)

        assertEquals(null, saleOrderSlot.captured.id)
        assertEquals(10, saleOrderSlot.captured.size)
        assertEquals(dateTime, saleOrderSlot.captured.creationDate)
        assertEquals(BigDecimal("14"), saleOrderSlot.captured.price)
        assertEquals(CREATING, saleOrderSlot.captured.state)
        assertEquals(SALE, saleOrderSlot.captured.type)
        assertEquals(1, saleOrderSlot.captured.walletId)

        assertEquals(1, saleOrder.id)
        assertEquals(10, saleOrder.size)
        assertEquals(dateTime, saleOrder.creationDate)
        assertEquals(BigDecimal("14"), saleOrder.price)
        assertEquals(CREATING, saleOrder.state)
        assertEquals(SALE, saleOrder.type)
        assertEquals(1, saleOrder.walletId)

        verify(exactly = 1) { walletQueryGateway.findById(eq(1)) }
        verify(exactly = 1) { walletCommandGateway.update(capture(walletSlot)) }
        verify(exactly = 1) { orderCommandGateway.create(capture(saleOrderSlot)) }
    }
}
