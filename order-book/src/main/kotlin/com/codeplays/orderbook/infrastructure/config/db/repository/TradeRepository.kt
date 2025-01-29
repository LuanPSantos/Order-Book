package com.codeplays.orderbook.infrastructure.config.db.repository

import com.codeplays.orderbook.infrastructure.config.db.schema.TradeSchema
import org.springframework.data.jpa.repository.JpaRepository

interface TradeRepository : JpaRepository<TradeSchema, Long>