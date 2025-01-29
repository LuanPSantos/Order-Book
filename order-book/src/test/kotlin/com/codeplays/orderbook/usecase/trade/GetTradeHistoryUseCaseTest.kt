package com.codeplays.orderbook.usecase.trade

import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.codeplays.orderbook.entity.trade.model.Trade
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class GetTradeHistoryUseCaseTest {

    @MockK
    lateinit var tradeHistoryQueryGateway: TradeHistoryQueryGateway

    @InjectMockKs
    lateinit var getTradeHistoryUseCase: GetTradeHistoryUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should get the trade history`() {
        val dateTime = LocalDateTime.now()
        val trade = Trade(
            id = 1,
            saleOrderId = 1,
            purchaseOrderId = 2,
            saleWalletId = 1,
            purchaseWalletId = 2,
            type = SALE,
            size = 10,
            price = BigDecimal("10"),
            change = BigDecimal("0"),
            creationDate = dateTime
        )

        every { tradeHistoryQueryGateway.getHistory(eq(0), eq(1)) } returns listOf(trade)

        val output = getTradeHistoryUseCase.execute(GetTradeHistoryUseCase.Input(0, 1))

        assertEquals(1, output.trades.size)

        assertEquals(SALE, output.trades[0].type)
        assertEquals(10, output.trades[0].size)
        assertEquals(1, output.trades[0].id)
        assertEquals(dateTime, output.trades[0].creationDate)
        assertEquals(BigDecimal("10"), output.trades[0].price)
        assertEquals(2, output.trades[0].purchaseOrderId)
        assertEquals(BigDecimal("0"), output.trades[0].change)
        assertEquals(1, output.trades[0].saleOrderId)
    }
}