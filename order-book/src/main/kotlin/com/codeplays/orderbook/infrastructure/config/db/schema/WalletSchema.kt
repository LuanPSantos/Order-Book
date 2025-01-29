package com.codeplays.orderbook.infrastructure.config.db.schema

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class WalletSchema(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val amountOfMoney: BigDecimal,
    val amountOfVibranium: Int
)