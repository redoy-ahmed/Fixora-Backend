package com.fixora.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class FixoraBackendApplication

@RestController
class RootController {
    @GetMapping("/")
    fun root(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "service" to "Fixora REST API Engine",
                "version" to "1.0.0",
                "swagger" to "/swagger-ui.html",
                "health" to "/actuator/health"
            )
        )
    }
}

fun main(args: Array<String>) {
    runApplication<FixoraBackendApplication>(*args)
}
