package gamer.controller.filter

import mu.KotlinLogging
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class LoggingFilter: WebFilter {

    // TODO config for each proyect
    private val EXCLUDED_URIS = listOf("/health-check")
    private val LOGGER = KotlinLogging.logger {}
    private val ONE_MILLIS_IN_NANO = BigDecimal(1000000)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = System.nanoTime()
        val logEnabled = !EXCLUDED_URIS.contains(exchange.request.uri.path)

        return if (logEnabled) {
            this.logRequest(exchange.request)
            try {
                chain.filter(exchange)
            } finally {
                this.logResponse(exchange.response, startTime)
                //httpResponse.copyBodyToResponse()
            }
        } else {
            chain.filter(exchange)
        }
    }

    private fun logRequest(request: ServerHttpRequest) {
        val queryString = if (request.uri.query.isNullOrBlank()) "" else "?" + request.uri.query
        val headers = this.buildHeaders(request.headers.values.flatten()) { headerName -> request.headers[headerName]?.first() }
        val body = request.body
        LOGGER.info("[REQUEST] -> {}: {}{} - body = {} - headers = {}", request.method, request.uri, queryString, body, headers)
    }

    private fun logResponse(response: ServerHttpResponse, startTime: Long) {
        val spentTime = BigDecimal(System.nanoTime() - startTime).divide(ONE_MILLIS_IN_NANO, 2, RoundingMode.CEILING)
        LOGGER.info("[RESPONSE] -> status = {} - body: {} - spent {} millis", response.statusCode, "BODY TO COMPLETE", spentTime) // TODO como saco el body?
    }

    private fun buildHeaders(headerNames: List<String>, headerExtractor: (String)->String?): String {
        return headerNames.map { h -> StringBuilder("'").append(h).append(":").append(headerExtractor(h)).append("'") }.toString() // TODO test this
    }

/*    private fun logRequest(request: HttpServletRequest) {
        val queryString = if (StringUtils.isBlank(request.queryString)) "" else "?" + request.queryString
        val headers = this.buildHeaders(Collections.list(request.headerNames)) { headerName -> request.getHeader(headerName) }
        val xuow = request.getHeader("x-uow") ?: request.getHeader("X-UOW")
        val body = this.toString(request.inputStream)
        LOGGER.info("[REQUEST] [{}] -> {}: {}{} - body = {} - headers = {}", xuow, request.method, request.requestURI, queryString, body, headers)
    }

    private fun logResponse(response: ContentCachingResponseWrapper, startTime: Long) {
        val spentTime = BigDecimal(System.nanoTime() - startTime).divide(ONE_MILLIS_IN_NANO, 2, RoundingMode.CEILING)
        LOGGER.info("[RESPONSE] -> status = {} - body: {} - spent {} millis", response.status, String(response.contentAsByteArray), spentTime)
    }*/
}