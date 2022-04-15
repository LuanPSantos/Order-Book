package com.meli.orderbook.usecase.wallet

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

class GetWalletUseCaseTest {

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @InjectMockKs
    lateinit var getWalletUseCase: GetWalletUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should get the wallet`() {
        every { walletQueryGateway.findById(eq(1)) } returns Wallet(1, BigDecimal("10"), 10)

        val output = getWalletUseCase.execute(Input(1))

        assertEquals(1, output.wallet.id)
        assertEquals(BigDecimal("10"), output.wallet.amountOfMoney)
        assertEquals(10, output.wallet.amountOfAssets)
    }
}