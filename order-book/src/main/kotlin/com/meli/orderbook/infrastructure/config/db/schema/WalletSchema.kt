package com.meli.orderbook.infrastructure.config.db.schema

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class WalletSchema(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val amountOfMoney: BigDecimal,
    val amountOfVibranium: Int
)