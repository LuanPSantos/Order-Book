package com.meli.orderbook.infrastructure.config.db.repository

import com.meli.orderbook.infrastructure.config.db.schema.WalletSchema
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WalletRepository : JpaRepository<WalletSchema, Long> {

    @Query(
        "SELECT wallet FROM WalletSchema wallet " +
                "WHERE wallet.id = :id"
    )
    fun findWalletById(id: Long): WalletSchema
}