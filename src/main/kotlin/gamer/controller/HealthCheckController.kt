package gamer.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HealthCheckController {
    @GetMapping
    fun healthCheck(): Mono<String> {
        return Mono.fromCallable { "Doing just great!" }
    }
}