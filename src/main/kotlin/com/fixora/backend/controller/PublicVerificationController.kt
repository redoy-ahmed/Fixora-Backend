package com.fixora.backend.controller

import com.fixora.backend.domain.repository.DeviceRepository
import com.fixora.backend.domain.repository.RepairJobRepository
import com.fixora.backend.dto.VerificationResultDto
import com.fixora.backend.service.HashVerificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "Public Record Verification")
class PublicVerificationController(
    private val repairJobRepository: RepairJobRepository,
    private val deviceRepository: DeviceRepository,
    private val hashVerificationService: HashVerificationService
) {
    @GetMapping("/verify/{recordHash}")
    @Operation(summary = "Public REST verification endpoint for QR code scans")
    fun verifyRecord(@PathVariable recordHash: String): ResponseEntity<VerificationResultDto> {
        val found = repairJobRepository.findByRecordHash(recordHash)
        return if (found.isPresent) {
            val job = found.get()
            ResponseEntity.ok(
                VerificationResultDto(
                    isVerified = true,
                    recordHash = recordHash,
                    serverHash = job.recordHash,
                    timestamp = job.completedAt?.toInstant()?.toEpochMilli(),
                    message = "Record Hash verified against Fixora REST API. Authentic and unmodified."
                )
            )
        } else {
            ResponseEntity.ok(
                VerificationResultDto(
                    isVerified = false,
                    recordHash = recordHash,
                    serverHash = null,
                    timestamp = null,
                    message = "Record Hash NOT found in Fixora database. Record may be invalid or corrupted."
                )
            )
        }
    }

    @GetMapping("/device/{publicDeviceId}")
    @Operation(summary = "Public device summary lookup for QR scans")
    fun getPublicDeviceSummary(@PathVariable publicDeviceId: String): ResponseEntity<Map<String, Any>> {
        val device = deviceRepository.findByPublicDeviceId(publicDeviceId)
            .orElseThrow { IllegalArgumentException("Device not found") }

        return ResponseEntity.ok(
            mapOf(
                "publicDeviceId" to device.publicDeviceId,
                "brand" to device.brand,
                "model" to device.model,
                "deviceType" to device.deviceType.name,
                "maskedSerialNumber" to hashVerificationService.maskIdentifier(device.serialNumber),
                "isVerified" to true
            )
        )
    }

    @GetMapping("/passport/{publicDeviceId}")
    @Operation(summary = "Public digital device passport lookup")
    fun getFullDevicePassport(@PathVariable publicDeviceId: String): ResponseEntity<Map<String, Any>> {
        val device = deviceRepository.findByPublicDeviceId(publicDeviceId)
            .orElseThrow { IllegalArgumentException("Device not found with ID $publicDeviceId") }

        val repairHistory = repairJobRepository.findAll().filter { it.device.id == device.id }.map { job ->
            mapOf(
                "jobNumber" to job.jobNumber,
                "status" to job.status.name,
                "reportedProblem" to job.reportedProblem,
                "completedAt" to (job.completedAt?.toString() ?: "In Progress"),
                "recordHash" to (job.recordHash ?: "Pending")
            )
        }

        return ResponseEntity.ok(
            mapOf(
                "publicDeviceId" to device.publicDeviceId,
                "brand" to device.brand,
                "model" to device.model,
                "deviceType" to device.deviceType.name,
                "color" to (device.color ?: "Standard"),
                "maskedSerialNumber" to hashVerificationService.maskIdentifier(device.serialNumber),
                "customerName" to device.customer.name,
                "serviceHistory" to repairHistory,
                "isVerified" to true
            )
        )
    }
}
