package com.fixora.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FixoraBackendApplication

fun main(args: Array<String>) {
    runApplication<FixoraBackendApplication>(*args)
}
