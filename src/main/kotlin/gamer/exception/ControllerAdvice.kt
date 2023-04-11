package gamer.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvice {

    private val logger = KotlinLogging.logger { }

    @ExceptionHandler(Throwable::class)
    private fun generic(e: Exception): ExceptionResponse {
        return handle(HttpStatus.INTERNAL_SERVER_ERROR, e)
    }

    private fun handle(httpStatus: HttpStatus, e: Exception): ExceptionResponse{
        logger.error("Exception caught in errorHandler. Message: ${e.message}")
        return ExceptionResponse(httpStatus, ExceptionBody(httpStatus, httpStatus.value(), e.message))
    }

    class ExceptionResponse(status: HttpStatus, body: ExceptionBody?) : ResponseEntity<ExceptionBody?>(body,status)
    class ExceptionBody(val status: HttpStatus, val code: Int, val message: String?)

}