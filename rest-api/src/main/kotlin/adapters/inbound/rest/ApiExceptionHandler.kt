package adapters.inbound.rest

import adapters.inbound.rest.dto.ErrorResponse
import adapters.outbound.social.CouldNotCreateTweetException
import application.persistence.configuration.MissingConfiguration
import application.socialnetwork.MissingConfigurationSetup
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(MissingConfiguration::class)
    fun missingConfiguration(exception: MissingConfiguration): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(exception.message ?: "There is no configuration stored"))

    @ExceptionHandler(MissingConfigurationSetup::class)
    fun missingConfigurationSetup(exception: MissingConfigurationSetup): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .badRequest()
            .body(ErrorResponse(exception.message ?: "Missing configuration"))

    @ExceptionHandler(CouldNotCreateTweetException::class)
    fun couldNotCreateTweet(exception: CouldNotCreateTweetException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ErrorResponse(exception.message ?: "Could not create tweet"))
}
