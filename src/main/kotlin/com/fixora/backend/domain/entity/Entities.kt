package com.fixora.backend.domain.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "shop_config")
class ShopConfigEntity(
    @Id var id: Int = 1,
    @Column(name = "shop_name", nullable = false) var shopName: String = "TechCare Fixora",
    @Column(nullable = false) var tagline: String = "Professional Electronics Repair",
    @Column(name = "primary_phone", nullable = false) var primaryPhone: String = "+880 1711 000000",
    @Column(name = "primary_email", nullable = false) var primaryEmail: String = "support@techcare.com",
    @Column(name = "currency_symbol", nullable = false) var currencySymbol: String = "$",
    @Column(name = "tax_rate_percent", nullable = false) var taxRatePercent: Double = 5.0,
    @Column(name = "default_warranty_days", nullable = false) var defaultWarrantyDays: Int = 90,
    @Column(name = "receipt_footer_text", nullable = false) var receiptFooterText: String = "Thank you for choosing TechCare Fixora.",
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "branches")
class BranchEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(nullable = false, unique = true) var name: String,
    @Column(nullable = false) var address: String,
    @Column(nullable = false) var phone: String,
    @Column(nullable = false) var email: String,
    @Column(name = "is_main_branch", nullable = false) var isMainBranch: Boolean = false,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "staff_users")
class StaffUserEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(nullable = false) var name: String,
    @Column(nullable = false, unique = true) var email: String,
    @Column(name = "password_hash", nullable = false) var passwordHash: String,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var role: StaffRole,
    @Column(name = "branch_name", nullable = false) var branchName: String,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "customers")
class CustomerEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(nullable = false) var name: String,
    @Column(nullable = false, unique = true) var phone: String,
    @Column(nullable = false, unique = true) var email: String,
    @Column(nullable = false) var address: String,
    var notes: String? = null,
    @Column(name = "is_active", nullable = false) var isActive: Boolean = true,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "customer_credentials")
class CustomerCredentialEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false, unique = true)
    var customer: CustomerEntity,
    @Column(name = "phone_or_email", nullable = false, unique = true) var phoneOrEmail: String,
    @Column(name = "password_hash", nullable = false) var passwordHash: String,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "devices")
class DeviceEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(name = "public_device_id", nullable = false, unique = true) var publicDeviceId: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false)
    var customer: CustomerEntity,
    @Enumerated(EnumType.STRING) @Column(name = "device_type", nullable = false) var deviceType: DeviceType,
    @Column(nullable = false) var brand: String,
    @Column(nullable = false) var model: String,
    @Column(name = "serial_number") var serialNumber: String? = null,
    var imei: String? = null,
    var color: String? = null,
    @Column(name = "condition_notes") var conditionNotes: String? = null,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "repair_jobs")
class RepairJobEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(name = "job_number", nullable = false, unique = true) var jobNumber: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false)
    var customer: CustomerEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "device_id", nullable = false)
    var device: DeviceEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_technician_id")
    var assignedTechnician: StaffUserEntity? = null,
    @Column(name = "reported_problem", nullable = false) var reportedProblem: String,
    @Column(name = "internal_diagnosis") var internalDiagnosis: String? = null,
    @Column(name = "customer_diagnosis") var customerDiagnosis: String? = null,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var priority: RepairPriority = RepairPriority.NORMAL,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: RepairStatus = RepairStatus.RECEIVED,
    @Column(name = "estimated_cost_cents", nullable = false) var estimatedCostCents: Long = 0L,
    @Column(name = "final_cost_cents", nullable = false) var finalCostCents: Long = 0L,
    @Column(name = "record_hash") var recordHash: String? = null,
    @Column(name = "estimated_completion_at") var estimatedCompletionAt: OffsetDateTime? = null,
    @Column(name = "completed_at") var completedAt: OffsetDateTime? = null,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "estimates")
class EstimateEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "repair_job_id", nullable = false, unique = true)
    var repairJob: RepairJobEntity,
    @Column(name = "fault_summary", nullable = false) var faultSummary: String,
    @Column(name = "parts_cost_cents", nullable = false) var partsCostCents: Long = 0L,
    @Column(name = "labor_cost_cents", nullable = false) var laborCostCents: Long = 0L,
    @Column(name = "discount_cents", nullable = false) var discountCents: Long = 0L,
    @Column(name = "additional_charges_cents", nullable = false) var additionalChargesCents: Long = 0L,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: EstimateStatus = EstimateStatus.PENDING,
    @Column(name = "rejection_reason") var rejectionReason: String? = null,
    @Column(name = "approved_at") var approvedAt: OffsetDateTime? = null,
    @Column(name = "rejected_at") var rejectedAt: OffsetDateTime? = null,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "inventory_parts")
class InventoryPartEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(nullable = false, unique = true) var sku: String,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var brand: String,
    @Column(nullable = false) var category: String,
    @Column(name = "cost_price_cents", nullable = false) var costPriceCents: Long,
    @Column(name = "selling_price_cents", nullable = false) var sellingPriceCents: Long,
    @Column(name = "stock_quantity", nullable = false) var stockQuantity: Int = 0,
    @Column(name = "minimum_stock", nullable = false) var minimumStock: Int = 2,
    @Column(name = "supplier_name") var supplierName: String? = null,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "invoices")
class InvoiceEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(name = "invoice_number", nullable = false, unique = true) var invoiceNumber: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "repair_job_id", nullable = false)
    var repairJob: RepairJobEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false)
    var customer: CustomerEntity,
    @Column(name = "parts_total_cents", nullable = false) var partsTotalCents: Long = 0L,
    @Column(name = "labor_total_cents", nullable = false) var laborTotalCents: Long = 0L,
    @Column(name = "discount_cents", nullable = false) var discountCents: Long = 0L,
    @Column(name = "tax_cents", nullable = false) var taxCents: Long = 0L,
    @Column(name = "grand_total_cents", nullable = false) var grandTotalCents: Long = 0L,
    @Column(name = "amount_paid_cents", nullable = false) var amountPaidCents: Long = 0L,
    @Column(name = "amount_due_cents", nullable = false) var amountDueCents: Long = 0L,
    @Column(name = "is_paid_in_full", nullable = false) var isPaidInFull: Boolean = false,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "repair_job_id", nullable = false)
    var repairJob: RepairJobEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "invoice_id", nullable = false)
    var invoice: InvoiceEntity,
    @Column(name = "amount_cents", nullable = false) var amountCents: Long,
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false) var paymentMethod: PaymentMethod,
    @Column(name = "transaction_reference", nullable = false) var transactionReference: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "received_by_staff_id", nullable = false)
    var receivedByStaff: StaffUserEntity,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "warranties")
class WarrantyEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "repair_job_id", nullable = false)
    var repairJob: RepairJobEntity,
    @Column(name = "duration_days", nullable = false) var durationDays: Int = 90,
    @Column(nullable = false) var terms: String,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: WarrantyStatus = WarrantyStatus.ACTIVE,
    @Column(name = "starts_at", nullable = false) var startsAt: OffsetDateTime,
    @Column(name = "ends_at", nullable = false) var endsAt: OffsetDateTime,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "warranty_claims")
class WarrantyClaimEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "warranty_id", nullable = false)
    var warranty: WarrantyEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false)
    var customer: CustomerEntity,
    @Column(name = "issue_description", nullable = false) var issueDescription: String,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: ClaimStatus = ClaimStatus.SUBMITTED,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "notifications")
class NotificationEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    @Column(name = "recipient_id", nullable = false) var recipientId: UUID,
    @Enumerated(EnumType.STRING) @Column(name = "recipient_type", nullable = false) var recipientType: RecipientType,
    @Column(nullable = false) var title: String,
    @Column(nullable = false) var message: String,
    @Enumerated(EnumType.STRING) @Column(name = "target_type", nullable = false) var targetType: NotificationTargetType,
    @Column(name = "target_entity_id", nullable = false) var targetEntityId: String,
    @Column(name = "is_read", nullable = false) var isRead: Boolean = false,
    @Column(name = "created_at", nullable = false) var createdAt: OffsetDateTime = OffsetDateTime.now()
)
