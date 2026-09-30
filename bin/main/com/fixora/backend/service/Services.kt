package com.fixora.backend.service

import com.fixora.backend.config.JwtTokenProvider
import com.fixora.backend.domain.entity.*
import com.fixora.backend.domain.repository.*
import com.fixora.backend.dto.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.UUID

@Service
class StaffAuthService(
    private val staffUserRepository: StaffUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun login(request: LoginRequest): AuthTokenResponse {
        val user = staffUserRepository.findByEmail(request.username)
            .orElseThrow { IllegalArgumentException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val token = jwtTokenProvider.generateToken(user.id!!, user.role.name, user.email)
        return AuthTokenResponse(token, user.id.toString(), user.name, user.role.name)
    }
}

@Service
class CustomerAuthService(
    private val customerRepository: CustomerRepository,
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun login(request: LoginRequest): AuthTokenResponse {
        val credential = customerCredentialRepository.findByPhoneOrEmail(request.username)
            .orElseThrow { IllegalArgumentException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, credential.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val customer = credential.customer
        val token = jwtTokenProvider.generateToken(customer.id!!, "ROLE_CUSTOMER", customer.email)
        return AuthTokenResponse(token, customer.id.toString(), customer.name, "ROLE_CUSTOMER")
    }

    @Transactional
    fun register(request: RegisterCustomerRequest): AuthTokenResponse {
        val customer = CustomerEntity(
            name = request.fullName,
            phone = request.phone,
            email = request.email,
            address = "Registered via Customer Portal"
        )
        val savedCustomer = customerRepository.save(customer)

        val credential = CustomerCredentialEntity(
            customer = savedCustomer,
            phoneOrEmail = request.email,
            passwordHash = passwordEncoder.encode(request.password)
        )
        customerCredentialRepository.save(credential)

        val token = jwtTokenProvider.generateToken(savedCustomer.id!!, "ROLE_CUSTOMER", savedCustomer.email)
        return AuthTokenResponse(token, savedCustomer.id.toString(), savedCustomer.name, "ROLE_CUSTOMER")
    }
}

@Service
class RepairManagementService(
    private val repairJobRepository: RepairJobRepository,
    private val customerRepository: CustomerRepository,
    private val deviceRepository: DeviceRepository,
    private val estimateRepository: EstimateRepository,
    private val hashVerificationService: HashVerificationService
) {
    fun getDashboardKpis(): StaffDashboardKpiDto {
        val allJobs = repairJobRepository.findAll()
        val todaysJobs = allJobs.size
        val pending = allJobs.count { it.status == RepairStatus.RECEIVED || it.status == RepairStatus.DIAGNOSIS }
        val inRepair = allJobs.count { it.status == RepairStatus.REPAIRING }
        val ready = allJobs.count { it.status == RepairStatus.READY_FOR_PICKUP }
        val completed = allJobs.count { it.status == RepairStatus.DELIVERED }

        return StaffDashboardKpiDto(
            todaysJobsCount = todaysJobs,
            pendingJobsCount = pending,
            inRepairCount = inRepair,
            readyForPickupCount = ready,
            completedCount = completed,
            todaysRevenueCents = 3450000L,
            outstandingAmountCents = 820000L,
            lowStockCount = 2
        )
    }

    @Transactional
    fun createRepairJob(request: CreateRepairJobRequest): RepairJobEntity {
        val customer = customerRepository.findById(request.customerId)
            .orElseThrow { IllegalArgumentException("Customer not found") }
        val device = deviceRepository.findById(request.deviceId)
            .orElseThrow { IllegalArgumentException("Device not found") }

        val jobNumber = "RS-2026-" + System.currentTimeMillis().toString().takeLast(5)
        val repairJob = RepairJobEntity(
            jobNumber = jobNumber,
            customer = customer,
            device = device,
            reportedProblem = request.reportedProblem,
            priority = RepairPriority.valueOf(request.priority),
            status = RepairStatus.RECEIVED,
            estimatedCostCents = request.estimatedCostCents
        )
        return repairJobRepository.save(repairJob)
    }

    @Transactional
    fun updateRepairStatus(jobId: UUID, newStatusStr: String): RepairJobEntity {
        val job = repairJobRepository.findById(jobId)
            .orElseThrow { IllegalArgumentException("Repair job not found") }
        val newStatus = RepairStatus.valueOf(newStatusStr)
        job.status = newStatus
        job.updatedAt = OffsetDateTime.now()

        if (newStatus == RepairStatus.DELIVERED) {
            job.completedAt = OffsetDateTime.now()
            job.recordHash = hashVerificationService.computeCanonicalHash(job)
        }
        return repairJobRepository.save(job)
    }
}

@Service
class HashVerificationService {
    fun computeCanonicalHash(repairJob: RepairJobEntity): String {
        val canonicalJson = "{\"deviceId\":\"${repairJob.device.publicDeviceId}\",\"job\":\"${repairJob.jobNumber}\",\"status\":\"${repairJob.status}\"}"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(canonicalJson.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun maskIdentifier(value: String?, visible: Int = 4): String {
        if (value.isNullOrBlank()) return "Not recorded"
        if (value.length <= visible) return "*".repeat(value.length)
        return "*".repeat(value.length - visible) + value.takeLast(visible)
    }
}
