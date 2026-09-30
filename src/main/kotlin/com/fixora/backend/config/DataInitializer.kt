package com.fixora.backend.config

import com.fixora.backend.domain.entity.*
import com.fixora.backend.domain.repository.*
import com.fixora.backend.service.HashVerificationService
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class DataInitializer(
    private val staffUserRepository: StaffUserRepository,
    private val customerRepository: CustomerRepository,
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val deviceRepository: DeviceRepository,
    private val repairJobRepository: RepairJobRepository,
    private val inventoryPartRepository: InventoryPartRepository,
    private val passwordEncoder: PasswordEncoder,
    private val hashVerificationService: HashVerificationService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (staffUserRepository.count() > 0) return

        // 1. Staff Users
        val staff1 = staffUserRepository.save(
            StaffUserEntity(
                name = "Karim Rahman",
                email = "karim@techcare.com",
                passwordHash = passwordEncoder.encode("password123"),
                role = StaffRole.ROLE_OWNER,
                branchName = "Dhaka Main Branch"
            )
        )
        val staff2 = staffUserRepository.save(
            StaffUserEntity(
                name = "Hasan Mahmud",
                email = "hasan@techcare.com",
                passwordHash = passwordEncoder.encode("password123"),
                role = StaffRole.ROLE_TECHNICIAN,
                branchName = "Dhaka Main Branch"
            )
        )

        // 2. Customer & Credentials
        val customer = customerRepository.save(
            CustomerEntity(
                name = "Rahim Ahmed",
                phone = "+8801700000000",
                email = "rahim@example.com",
                address = "Dhanmondi, Dhaka"
            )
        )
        customerCredentialRepository.save(
            CustomerCredentialEntity(
                customer = customer,
                phoneOrEmail = "rahim@example.com",
                passwordHash = passwordEncoder.encode("password123")
            )
        )

        // 3. Devices
        val dev1 = deviceRepository.save(
            DeviceEntity(
                publicDeviceId = "DP-83A92F",
                customer = customer,
                deviceType = DeviceType.MOBILE,
                brand = "Samsung",
                model = "Galaxy S24 Ultra",
                serialNumber = "R5CT30ABCDE",
                color = "Titanium Black"
            )
        )

        // 4. Repair Jobs
        val job1 = repairJobRepository.save(
            RepairJobEntity(
                jobNumber = "RS-2026-00101",
                customer = customer,
                device = dev1,
                assignedTechnician = staff2,
                reportedProblem = "Shattered OLED display glass and touch unresponsive",
                customerDiagnosis = "Display assembly replaced and touch digitizer calibrated.",
                priority = RepairPriority.HIGH,
                status = RepairStatus.REPAIRING,
                estimatedCostCents = 1850000L
            )
        )
        job1.recordHash = hashVerificationService.computeCanonicalHash(job1)
        repairJobRepository.save(job1)

        // 5. Inventory Parts
        inventoryPartRepository.save(
            InventoryPartEntity(
                sku = "DISP-S24U",
                name = "Samsung S24 Ultra OLED Screen",
                brand = "Samsung",
                category = "Display",
                costPriceCents = 1200000L,
                sellingPriceCents = 1600000L,
                stockQuantity = 8,
                minimumStock = 2
            )
        )
    }
}