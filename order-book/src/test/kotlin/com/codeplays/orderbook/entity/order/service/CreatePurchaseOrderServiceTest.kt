package com.codeplays.orderbook.entity.order.service

import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.State.CREATING
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
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

class CreatePurchaseOrderServiceTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @InjectMockKs
    lateinit var createOrderService: CreatePurchaseOrderService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should create an purchase-order`() {
        val wallet = Wallet(id = 1, amountOfMoney = BigDecimal("100"), amountOfVibranium = 20)

        val walletSlot = slot<Wallet>()
        val purchaseOrderSlot = slot<Order>()

        every { walletQueryGateway.findById(eq(1)) } returns wallet
        every { walletCommandGateway.update(capture(walletSlot)) } just Runs
        every {
            orderCommandGateway.create(capture(purchaseOrderSlot))
        } returns Order(id = 1, walletId = 1, type = PURCHASE, price = BigDecimal("14"), size = 5, creationDate = dateTime)

        val purchaseOrder = createOrderService.createOrder(
            Order(
                walletId = 1,
                type = PURCHASE,
                price = BigDecimal("14"),
                size = 5,
                creationDate = dateTime
            )
        )

        assertEquals(1, walletSlot.captured.id)
        assertEquals(BigDecimal("30"), walletSlot.captured.amountOfMoney)
        assertEquals(20, walletSlot.captured.amountOfVibranium)

        assertEquals(null, purchaseOrderSlot.captured.id)
        assertEquals(5, purchaseOrderSlot.captured.size)
        assertEquals(dateTime, purchaseOrderSlot.captured.creationDate)
        assertEquals(BigDecimal("14"), purchaseOrderSlot.captured.price)
        assertEquals(CREATING, purchaseOrderSlot.captured.state)
        assertEquals(PURCHASE, purchaseOrderSlot.captured.type)
        assertEquals(1, purchaseOrderSlot.captured.walletId)

        assertEquals(1, purchaseOrder.id)
        assertEquals(5, purchaseOrder.size)
        assertEquals(dateTime, purchaseOrder.creationDate)
        assertEquals(BigDecimal("14"), purchaseOrder.price)
        assertEquals(CREATING, purchaseOrder.state)
        assertEquals(PURCHASE, purchaseOrder.type)
        assertEquals(1, purchaseOrder.walletId)

        verify(exactly = 1) { walletQueryGateway.findById(eq(1)) }
        verify(exactly = 1) { walletCommandGateway.update(capture(walletSlot)) }
        verify(exactly = 1) { orderCommandGateway.create(capture(purchaseOrderSlot)) }
    }
}
