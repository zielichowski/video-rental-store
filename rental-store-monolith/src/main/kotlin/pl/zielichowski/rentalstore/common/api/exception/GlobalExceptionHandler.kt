package pl.zielichowski.rentalstore.common.api.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.naming.ServiceUnavailableException
import javax.validation.ValidationException

@ControllerAdvice
internal class GlobalExceptionHandler {
    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(ValidationException::class)
    fun handle(ex: ValidationException): ResponseEntity<ApiError> {
        log.warn("Handled ValidationException {message={}}", ex.message)
        return ResponseEntity(
                ApiError(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", ex.localizedMessage),
                HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handle(ex: RuntimeException): ResponseEntity<ApiError> {
        log.warn("Handled RuntimeException {message={}}", ex.message)
        return ResponseEntity(
                ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", "INTERNAL_SERVER_ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(ServiceUnavailableException::class)
    fun handle(ex: ServiceUnavailableException): ResponseEntity<ApiError> {
        log.warn("Handled ServiceUnavailableException {message={}}", ex.message)
        return ResponseEntity(
                ApiError(HttpStatus.SERVICE_UNAVAILABLE.value(), "SERVICE_UNAVAILABLE", "SERVICE_UNAVAILABLE"),
                HttpStatus.SERVICE_UNAVAILABLE
        )
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(ex: MissingRequestHeaderException): ResponseEntity<ApiError> {
        log.warn("Handled MissingRequestHeaderException {message={}}", ex.message)
        return ResponseEntity(
                ApiError(HttpStatus.BAD_REQUEST.value(),
                        "BAD_REQUEST",
                        String.format("Missing request header '%s'", ex.headerName)),
                HttpStatus.BAD_REQUEST
        )
    }

}
