package com.meli.orderbook.usecase.trade

import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.meli.orderbook.entity.trade.model.Trade
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
        val trade = Trade(1, 2, 1, 2, SELL, 10, BigDecimal("10"), BigDecimal("0"), dateTime, 1)

        every { tradeHistoryQueryGateway.getHistory(eq(0), eq(1)) } returns listOf(trade)

        val output = getTradeHistoryUseCase.execute(GetTradeHistoryUseCase.Input(0, 1))

        assertEquals(1, output.trades.size)

        assertEquals(SELL, output.trades[0].type)
        assertEquals(10, output.trades[0].size)
        assertEquals(1, output.trades[0].id)
        assertEquals(dateTime, output.trades[0].creationDate)
        assertEquals(BigDecimal("10"), output.trades[0].price)
        assertEquals(2, output.trades[0].buyerOrderId)
        assertEquals(BigDecimal("0"), output.trades[0].change)
        assertEquals(1, output.trades[0].sellOrderId)
    }
}