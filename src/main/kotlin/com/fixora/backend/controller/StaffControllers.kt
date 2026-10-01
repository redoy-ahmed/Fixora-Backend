package com.fixora.backend.controller

import com.fixora.backend.domain.entity.*
import com.fixora.backend.domain.repository.*
import com.fixora.backend.dto.*
import com.fixora.backend.service.RepairManagementService
import com.fixora.backend.service.StaffAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
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
class StaffInvoiceController(
    private val invoiceRepository: InvoiceRepository,
    private val repairJobRepository: RepairJobRepository
) {
    @GetMapping("/invoices")
    @Operation(summary = "List all invoices")
    fun getInvoices(): ResponseEntity<List<InvoiceEntity>> =
        ResponseEntity.ok(invoiceRepository.findAll())

    @PostMapping("/invoices")
    @Operation(summary = "Generate itemized invoice for repair job")
    fun createInvoice(@RequestBody req: CreateInvoiceRequest): ResponseEntity<InvoiceEntity> {
        val repairJob = repairJobRepository.findById(req.repairJobId)
            .orElseThrow { IllegalArgumentException("Repair job not found") }

        val grandTotal = req.partsTotalCents + req.laborTotalCents + req.taxCents - req.discountCents
        val dueCents = (grandTotal - req.amountPaidCents).coerceAtLeast(0L)
        val isPaid = dueCents == 0L

        val invoiceNumber = "INV-2026-" + System.currentTimeMillis().toString().takeLast(5)
        val invoice = InvoiceEntity(
            invoiceNumber = invoiceNumber,
            repairJob = repairJob,
            customer = repairJob.customer,
            partsTotalCents = req.partsTotalCents,
            laborTotalCents = req.laborTotalCents,
            discountCents = req.discountCents,
            taxCents = req.taxCents,
            grandTotalCents = grandTotal,
            amountPaidCents = req.amountPaidCents,
            amountDueCents = dueCents,
            isPaidInFull = isPaid
        )
        return ResponseEntity.ok(invoiceRepository.save(invoice))
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffReportController(
    private val repairJobRepository: RepairJobRepository,
    private val invoiceRepository: InvoiceRepository,
    private val branchRepository: BranchRepository,
    private val staffUserRepository: StaffUserRepository
) {

    @GetMapping("/reports/summary")
    @Operation(summary = "Get global & branch-wise analytics reports")
    fun getReportSummary(
        @RequestParam(required = false) branchName: String?
    ): ResponseEntity<StaffReportSummaryDto> {
        val allJobs = repairJobRepository.findAll()
        val allInvoices = invoiceRepository.findAll()
        val allBranches = branchRepository.findAll()
        val allStaff = staffUserRepository.findAll()

        val totalJobs = allJobs.size.toLong()
        val completed = allJobs.count { it.status == RepairStatus.DELIVERED }.toLong()
        val inRepair = allJobs.count { it.status == RepairStatus.REPAIRING }.toLong()

        val grossRevenue = allInvoices.sumOf { it.amountPaidCents }
        val partsCost = allInvoices.sumOf { it.partsTotalCents }
        val netProfit = if (grossRevenue > partsCost) grossRevenue - partsCost else grossRevenue / 2
        val taxCollected = allInvoices.sumOf { it.taxCents }
        val unpaidInvoices = allInvoices.count { !it.isPaidInFull }.toLong()

        val branchReportList = if (allBranches.isNotEmpty()) {
            allBranches.map { branch ->
                val branchStaffCount = allStaff.count { it.branchName.equals(branch.name, ignoreCase = true) }.toLong()
                BranchReportDto(
                    branchName = branch.name,
                    jobsCount = (totalJobs / allBranches.size).coerceAtLeast(1),
                    revenueCents = (grossRevenue / allBranches.size),
                    activeStaffCount = branchStaffCount
                )
            }
        } else {
            listOf(
                BranchReportDto(
                    branchName = "Main Branch",
                    jobsCount = totalJobs,
                    revenueCents = grossRevenue,
                    activeStaffCount = allStaff.size.toLong()
                )
            )
        }

        return ResponseEntity.ok(
            StaffReportSummaryDto(
                totalJobsCount = totalJobs,
                completedJobsCount = completed,
                inRepairJobsCount = inRepair,
                totalRevenueCents = grossRevenue,
                totalPartsCostCents = partsCost,
                netProfitCents = netProfit,
                totalTaxCents = taxCollected,
                unpaidInvoicesCount = unpaidInvoices,
                branchReports = branchReportList
            )
        )
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffSettingsController(private val shopConfigRepository: ShopConfigRepository) {

    @GetMapping("/settings")
    @Operation(summary = "Get global shop configuration settings")
    fun getSettings(): ResponseEntity<ShopConfigDto> {
        val config = shopConfigRepository.findById(1).orElseGet {
            shopConfigRepository.save(ShopConfigEntity(id = 1))
        }
        return ResponseEntity.ok(
            ShopConfigDto(
                shopName = config.shopName,
                tagline = config.tagline,
                primaryPhone = config.primaryPhone,
                primaryEmail = config.primaryEmail,
                currencySymbol = config.currencySymbol,
                taxRatePercent = config.taxRatePercent,
                defaultWarrantyDays = config.defaultWarrantyDays,
                receiptFooterText = config.receiptFooterText
            )
        )
    }

    @PostMapping("/settings")
    @Operation(summary = "Save/update global shop configuration settings")
    fun saveSettings(@RequestBody req: ShopConfigDto): ResponseEntity<ShopConfigDto> {
        val config = shopConfigRepository.findById(1).orElseGet { ShopConfigEntity(id = 1) }
        config.shopName = req.shopName
        config.tagline = req.tagline
        config.primaryPhone = req.primaryPhone
        config.primaryEmail = req.primaryEmail
        config.currencySymbol = req.currencySymbol
        config.taxRatePercent = req.taxRatePercent
        config.defaultWarrantyDays = req.defaultWarrantyDays
        config.receiptFooterText = req.receiptFooterText
        config.updatedAt = OffsetDateTime.now()

        val saved = shopConfigRepository.save(config)
        return ResponseEntity.ok(
            ShopConfigDto(
                shopName = saved.shopName,
                tagline = saved.tagline,
                primaryPhone = saved.primaryPhone,
                primaryEmail = saved.primaryEmail,
                currencySymbol = saved.currencySymbol,
                taxRatePercent = saved.taxRatePercent,
                defaultWarrantyDays = saved.defaultWarrantyDays,
                receiptFooterText = saved.receiptFooterText
            )
        )
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffUserController(
    private val staffUserRepository: StaffUserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @GetMapping("/users")
    @Operation(summary = "List all staff users")
    fun getStaffUsers(): ResponseEntity<List<StaffUserEntity>> =
        ResponseEntity.ok(staffUserRepository.findAll())

    @PostMapping("/users")
    @Operation(summary = "Create staff user account")
    fun createStaffUser(@RequestBody req: CreateStaffUserRequest): ResponseEntity<StaffUserEntity> {
        val user = StaffUserEntity(
            name = req.name,
            email = req.email,
            passwordHash = passwordEncoder.encode(req.password ?: "password123"),
            role = StaffRole.valueOf(req.role),
            branchName = req.branchName ?: "Main Branch"
        )
        return ResponseEntity.ok(staffUserRepository.save(user))
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update full staff user account")
    fun updateStaffUser(
        @PathVariable id: UUID,
        @RequestBody req: UpdateStaffUserRequest
    ): ResponseEntity<StaffUserEntity> {
        val user = staffUserRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staff user not found") }
        user.name = req.name
        user.email = req.email
        user.role = StaffRole.valueOf(req.role)
        if (!req.branchName.isNullOrBlank()) {
            user.branchName = req.branchName
        }
        if (!req.password.isNullOrBlank()) {
            user.passwordHash = passwordEncoder.encode(req.password)
        }
        user.updatedAt = OffsetDateTime.now()
        return ResponseEntity.ok(staffUserRepository.save(user))
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "Update staff user role")
    fun updateStaffRole(
        @PathVariable id: UUID,
        @RequestBody req: UpdateStaffRoleRequest
    ): ResponseEntity<StaffUserEntity> {
        val user = staffUserRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staff user not found") }
        user.role = StaffRole.valueOf(req.role)
        user.updatedAt = OffsetDateTime.now()
        return ResponseEntity.ok(staffUserRepository.save(user))
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete staff user account")
    fun deleteStaffUser(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        staffUserRepository.deleteById(id)
        return ResponseEntity.ok(mapOf("status" to "DELETED", "message" to "Staff account deleted successfully"))
    }
}

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Operations")
class StaffBranchController(private val branchRepository: BranchRepository) {

    @GetMapping("/branches")
    @Operation(summary = "List all shop branches")
    fun getBranches(): ResponseEntity<List<BranchDto>> {
        val dtos = branchRepository.findAll().map {
            BranchDto(
                id = it.id!!,
                name = it.name,
                address = it.address,
                phone = it.phone,
                email = it.email,
                isMainBranch = it.isMainBranch
            )
        }
        return ResponseEntity.ok(dtos)
    }

    @PostMapping("/branches")
    @Operation(summary = "Create new shop branch")
    fun createBranch(@RequestBody req: CreateBranchRequest): ResponseEntity<BranchDto> {
        val isFirst = branchRepository.count() == 0L
        val branch = BranchEntity(
            name = req.name,
            address = req.address,
            phone = req.phone,
            email = req.email,
            isMainBranch = if (isFirst) true else req.isMainBranch
        )
        val saved = branchRepository.save(branch)
        return ResponseEntity.ok(
            BranchDto(
                id = saved.id!!,
                name = saved.name,
                address = saved.address,
                phone = saved.phone,
                email = saved.email,
                isMainBranch = saved.isMainBranch
            )
        )
    }

    @DeleteMapping("/branches/{id}")
    @Operation(summary = "Delete branch by ID")
    fun deleteBranch(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        branchRepository.deleteById(id)
        return ResponseEntity.ok(mapOf("status" to "DELETED", "message" to "Branch removed successfully"))
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

    @PostMapping("/repairs/intake")
    @Operation(summary = "Create repair intake ticket wizard")
    fun createIntakeTicket(@RequestBody req: CreateIntakeTicketRequest): ResponseEntity<RepairJobEntity> {
        return ResponseEntity.ok(repairManagementService.createIntakeTicket(req))
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

    @PutMapping("/inventory/{id}")
    @Operation(summary = "Update spare part in inventory")
    fun updatePart(
        @PathVariable id: UUID,
        @RequestBody req: UpdateInventoryPartRequest
    ): ResponseEntity<InventoryPartEntity> {
        val part = inventoryPartRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Inventory item not found") }
        part.sku = req.sku
        part.name = req.name
        part.brand = req.brand
        part.category = req.category
        part.costPriceCents = req.costPriceCents
        part.sellingPriceCents = req.sellingPriceCents
        part.stockQuantity = req.stockQuantity
        part.minimumStock = req.minimumStock
        part.supplierName = req.supplierName
        part.updatedAt = OffsetDateTime.now()
        return ResponseEntity.ok(inventoryPartRepository.save(part))
    }

    @DeleteMapping("/inventory/{id}")
    @Operation(summary = "Delete spare part from inventory")
    fun deletePart(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        inventoryPartRepository.deleteById(id)
        return ResponseEntity.ok(mapOf("status" to "DELETED", "message" to "Inventory item deleted successfully"))
    }
}
