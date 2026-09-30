package com.fixora.backend.controller

import com.fixora.backend.domain.entity.*
import com.fixora.backend.domain.repository.*
import com.fixora.backend.dto.*
import com.fixora.backend.service.RepairManagementService
import com.fixora.backend.service.StaffAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffAuthController(private val staffAuthService: StaffAuthService) {
    @PostMapping("/auth/login")
    @Operation(summary = "Staff User Login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthTokenResponse> {
        return ResponseEntity.ok(staffAuthService.login(request))
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffDashboardController(private val repairManagementService: RepairManagementService) {
    @GetMapping("/dashboard/kpis")
    @Operation(summary = "Get Shop KPI Overview")
    fun getKpis(): ResponseEntity<StaffDashboardKpiDto> {
        return ResponseEntity.ok(repairManagementService.getDashboardKpis())
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffCustomerController(private val customerRepository: CustomerRepository) {
    @GetMapping("/customers")
    @Operation(summary = "List all shop customers")
    fun getCustomers(): ResponseEntity<List<CustomerEntity>> =
        ResponseEntity.ok(customerRepository.findAll())

    @PostMapping("/customers")
    @Operation(summary = "Create new customer")
    fun createCustomer(@RequestBody req: CreateCustomerRequest): ResponseEntity<CustomerEntity> {
        val customer = CustomerEntity(
            name = req.name,
            phone = req.phone,
            email = req.email,
            address = req.address,
            notes = req.notes
        )
        return ResponseEntity.ok(customerRepository.save(customer))
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffDeviceController(
    private val deviceRepository: DeviceRepository,
    private val customerRepository: CustomerRepository
) {
    @GetMapping("/devices")
    @Operation(summary = "List all shop devices")
    fun getDevices(): ResponseEntity<List<DeviceEntity>> =
        ResponseEntity.ok(deviceRepository.findAll())

    @PostMapping("/devices")
    @Operation(summary = "Register device under customer")
    fun registerDevice(@RequestBody req: CreateDeviceRequest): ResponseEntity<DeviceEntity> {
        val customer = customerRepository.findById(req.customerId)
            .orElseThrow { IllegalArgumentException("Customer not found") }
        val publicId = "DP-" + System.currentTimeMillis().toString().takeLast(6).uppercase()
        val device = DeviceEntity(
            publicDeviceId = publicId,
            customer = customer,
            deviceType = DeviceType.valueOf(req.deviceType),
            brand = req.brand,
            model = req.model,
            serialNumber = req.serialNumber,
            imei = req.imei,
            color = req.color,
            conditionNotes = req.conditionNotes
        )
        return ResponseEntity.ok(deviceRepository.save(device))
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffRepairController(
    private val repairJobRepository: RepairJobRepository,
    private val repairManagementService: RepairManagementService
) {
    @GetMapping("/repairs")
    @Operation(summary = "List repair jobs")
    fun getRepairs(): ResponseEntity<List<RepairJobEntity>> =
        ResponseEntity.ok(repairJobRepository.findAll())

    @PostMapping("/repairs")
    @Operation(summary = "Create new repair job")
    fun createRepair(@RequestBody req: CreateRepairJobRequest): ResponseEntity<RepairJobEntity> {
        return ResponseEntity.ok(repairManagementService.createRepairJob(req))
    }

    @PatchMapping("/repairs/{id}/status")
    @Operation(summary = "Update repair lifecycle status")
    fun updateStatus(
        @PathVariable id: UUID,
        @RequestBody req: UpdateRepairStatusRequest
    ): ResponseEntity<RepairJobEntity> {
        return ResponseEntity.ok(repairManagementService.updateRepairStatus(id, req.newStatus))
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffInventoryController(private val inventoryPartRepository: InventoryPartRepository) {
    @GetMapping("/inventory")
    @Operation(summary = "List spare parts inventory")
    fun getParts(): ResponseEntity<List<InventoryPartEntity>> =
        ResponseEntity.ok(inventoryPartRepository.findAll())

    @PostMapping("/inventory")
    @Operation(summary = "Add spare part to inventory")
    fun addPart(@RequestBody req: CreateInventoryPartRequest): ResponseEntity<InventoryPartEntity> {
        val part = InventoryPartEntity(
            sku = req.sku,
            name = req.name,
            brand = req.brand,
            category = req.category,
            costPriceCents = req.costPriceCents,
            sellingPriceCents = req.sellingPriceCents,
            stockQuantity = req.stockQuantity,
            minimumStock = req.minimumStock,
            supplierName = req.supplierName
        )
        return ResponseEntity.ok(inventoryPartRepository.save(part))
    }
}