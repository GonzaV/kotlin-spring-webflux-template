package gamer.connector

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class Connector(val baseUrl: String, val ssl: Boolean) {
    val client: WebClient = WebClient.create(baseUrl)

    inline fun <reified T> getMono(path: String): Mono<T> {
        return client.get().uri(buildURI(path)).exchangeToMono { response ->
            manageResponse<Mono<T>>(
                response,
                { responseToMono(response) },
                { response.createError() }
            )
        }
    }

    inline fun <reified T> getFlux(path: String): Flux<T> {
        return client.get().uri(path).exchangeToFlux{ response ->
            manageResponse<Flux<T>>(
                response,
                { responseToFlux(response)},
                { Flux.from(response.createError()) }
            )
        }
    }

    fun <T> manageResponse(response: ClientResponse, successfulBlock: (response: ClientResponse) -> T, unsuccessfulBlock: () -> T): T {
        return if (response.statusCode().equals(HttpStatus.OK)){
            successfulBlock(response)
        } else {
            unsuccessfulBlock()
        }
    }

    inline fun <reified T> responseToMono(response: ClientResponse): Mono<T> {
        return response.bodyToMono(T::class.java)
    }

    inline fun <reified T> responseToFlux(response: ClientResponse): Flux<T> {
        return if (response.statusCode().equals(HttpStatus.OK)){
            response.bodyToFlux(T::class.java)
        } else {
            Flux.from(response.createError())
        }
    }

    fun buildURI(path: String): String {
        return (baseUrl + path).let {
            if(ssl) "https://$it" else "http://$it"
        }
    }
}