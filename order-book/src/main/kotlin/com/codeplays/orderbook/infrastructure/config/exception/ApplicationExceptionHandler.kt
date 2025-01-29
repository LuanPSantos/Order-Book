package com.codeplays.orderbook.infrastructure.config.exception

import com.codeplays.orderbook.entity.order.exception.InvalidOrderType
import com.codeplays.orderbook.entity.wallet.exception.WalletOperationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ApplicationExceptionHandler {

    @ExceptionHandler(EmptyResultDataAccessException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun notFoundHandler(exception: EmptyResultDataAccessException): ApplicationError {
        return ApplicationError(exception.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun notFoundHandler(exception: Exception): ApplicationError {
        return ApplicationError(exception.message)
    }

    @ExceptionHandler(WalletOperationException::class, InvalidOrderType::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun badRequestHandler(exception: WalletOperationException): ApplicationError {
        return ApplicationError(exception.message)
    }

    data class ApplicationError(
        val message: String?
    )
}