package com.fixora.backend.dto

import java.time.OffsetDateTime
import java.util.UUID

// Common Auth DTOs
data class LoginRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String
) {
    val identifier: String
        get() = username ?: email ?: throw IllegalArgumentException("Username or email must be provided")
}

data class RegisterCustomerRequest(val fullName: String, val phone: String, val email: String, val password: String)
data class AuthTokenResponse(val token: String, val userId: String, val displayName: String, val role: String)

// Branch DTOs
data class CreateBranchRequest(
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val isMainBranch: Boolean = false
)

data class BranchDto(
    val id: UUID,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val isMainBranch: Boolean
)

// Staff DTOs
data class CreateStaffUserRequest(
    val name: String,
    val email: String,
    val role: String,
    val password: String? = "password123",
    val branchName: String? = "Main Branch"
)

data class StaffDashboardKpiDto(
    val todaysJobsCount: Int,
    val pendingJobsCount: Int,
    val inRepairCount: Int,
    val readyForPickupCount: Int,
    val completedCount: Int,
    val todaysRevenueCents: Long,
    val outstandingAmountCents: Long,
    val lowStockCount: Int
)

data class CreateCustomerRequest(
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val notes: String? = null
)

data class CreateDeviceRequest(
    val customerId: UUID,
    val deviceType: String,
    val brand: String,
    val model: String,
    val serialNumber: String?,
    val imei: String?,
    val color: String?,
    val conditionNotes: String?
)

data class CreateRepairJobRequest(
    val customerId: UUID,
    val deviceId: UUID,
    val reportedProblem: String,
    val priority: String,
    val estimatedCostCents: Long
)

data class UpdateRepairStatusRequest(val newStatus: String)
data class CreateDiagnosisEstimateRequest(
    val faultSummary: String,
    val partsCostCents: Long,
    val laborCostCents: Long,
    val discountCents: Long,
    val additionalChargesCents: Long
)

data class CreateInventoryPartRequest(
    val sku: String,
    val name: String,
    val brand: String,
    val category: String,
    val costPriceCents: Long,
    val sellingPriceCents: Long,
    val stockQuantity: Int,
    val minimumStock: Int,
    val supplierName: String?
)

data class CreateInvoiceRequest(
    val repairJobId: UUID,
    val partsTotalCents: Long,
    val laborTotalCents: Long,
    val discountCents: Long,
    val taxCents: Long,
    val amountPaidCents: Long
)

data class RecordPaymentRequest(
    val repairJobId: UUID,
    val invoiceId: UUID,
    val amountCents: Long,
    val paymentMethod: String,
    val transactionReference: String
)

data class IssueWarrantyRequest(val repairJobId: UUID, val durationDays: Int, val terms: String)

// Customer DTOs (Masked & Privacy Preserving)
data class CustomerRepairDto(
    val id: UUID,
    val jobNumber: String,
    val deviceName: String,
    val status: String,
    val reportedIssue: String,
    val customerDiagnosis: String?,
    val expectedCompletionDate: String?,
    val technicianName: String?,
    val estimate: CustomerEstimateDto?
)

data class CustomerEstimateDto(
    val partsCostCents: Long,
    val laborCostCents: Long,
    val discountCents: Long,
    val grandTotalCents: Long,
    val status: String
)

data class CustomerPassportDto(
    val deviceId: UUID,
    val publicDeviceId: String,
    val brand: String,
    val model: String,
    val maskedSerialNumber: String,
    val registeredOn: String,
    val serviceHistory: List<CustomerServiceRecordDto>,
    val warrantyHistory: List<CustomerWarrantyDto>,
    val verification: VerificationResultDto
)

data class CustomerServiceRecordDto(
    val serviceDate: String,
    val jobNumber: String,
    val summary: String,
    val statusLabel: String
)

data class CustomerWarrantyDto(
    val warrantyId: UUID,
    val startDate: String,
    val endDate: String,
    val terms: String,
    val isActive: Boolean
)

data class VerificationResultDto(
    val isVerified: Boolean,
    val recordHash: String,
    val serverHash: String?,
    val timestamp: Long?,
    val message: String
)

data class EstimateDecisionRequest(val reason: String? = null)
data class SubmitWarrantyClaimRequest(val warrantyId: UUID, val issueDescription: String)
