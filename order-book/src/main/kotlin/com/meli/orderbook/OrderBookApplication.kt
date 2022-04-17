package com.meli.orderbook

import com.meli.orderbook.infrastructure.config.db.repository.WalletRepository
import com.meli.orderbook.infrastructure.config.db.schema.WalletSchema
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.math.BigDecimal

@SpringBootApplication
class OrderBookApplication {

    @Bean
    fun start(walletRepository: WalletRepository): ApplicationRunner {
        return ApplicationRunner {

            walletRepository.save(WalletSchema(1, BigDecimal("100"), 100))
            walletRepository.save(WalletSchema(2, BigDecimal("100"), 100))
            walletRepository.save(WalletSchema(3, BigDecimal("100"), 100))
            walletRepository.save(WalletSchema(4, BigDecimal("100"), 100))
        }
    }
}

fun main(args: Array<String>) {
    runApplication<OrderBookApplication>(*args)
}
