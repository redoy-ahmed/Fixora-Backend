package com.fixora.backend.domain.repository

import com.fixora.backend.domain.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface StaffUserRepository : JpaRepository<StaffUserEntity, UUID> {
    fun findByEmail(email: String): Optional<StaffUserEntity>
}

@Repository
interface CustomerRepository : JpaRepository<CustomerEntity, UUID> {
    fun findByPhoneOrEmail(phone: String, email: String): Optional<CustomerEntity>
}

@Repository
interface CustomerCredentialRepository : JpaRepository<CustomerCredentialEntity, UUID> {
    @Query("SELECT c FROM CustomerCredentialEntity c WHERE c.phoneOrEmail = :phoneOrEmail")
    fun findByPhoneOrEmail(@Param("phoneOrEmail") phoneOrEmail: String): Optional<CustomerCredentialEntity>
    fun findByCustomerId(customerId: UUID): Optional<CustomerCredentialEntity>
}

@Repository
interface DeviceRepository : JpaRepository<DeviceEntity, UUID> {
    fun findByPublicDeviceId(publicDeviceId: String): Optional<DeviceEntity>
    fun findAllByCustomerId(customerId: UUID): List<DeviceEntity>
}

@Repository
interface RepairJobRepository : JpaRepository<RepairJobEntity, UUID> {
    fun findByJobNumber(jobNumber: String): Optional<RepairJobEntity>
    fun findAllByCustomerId(customerId: UUID): List<RepairJobEntity>
    fun findByRecordHash(recordHash: String): Optional<RepairJobEntity>
}

@Repository
interface EstimateRepository : JpaRepository<EstimateEntity, UUID> {
    fun findByRepairJobId(repairJobId: UUID): Optional<EstimateEntity>
}

@Repository
interface InventoryPartRepository : JpaRepository<InventoryPartEntity, UUID> {
    fun findBySku(sku: String): Optional<InventoryPartEntity>
}

@Repository
interface InvoiceRepository : JpaRepository<InvoiceEntity, UUID> {
    fun findByInvoiceNumber(invoiceNumber: String): Optional<InvoiceEntity>
    fun findAllByCustomerId(customerId: UUID): List<InvoiceEntity>
    fun findByRepairJobId(repairJobId: UUID): Optional<InvoiceEntity>
}

@Repository
interface PaymentRepository : JpaRepository<PaymentEntity, UUID> {
    fun findAllByRepairJobId(repairJobId: UUID): List<PaymentEntity>
}

@Repository
interface WarrantyRepository : JpaRepository<WarrantyEntity, UUID> {
    fun findByRepairJobId(repairJobId: UUID): Optional<WarrantyEntity>
    fun findAllByRepairJob_Customer_Id(customerId: UUID): List<WarrantyEntity>
}

@Repository
interface WarrantyClaimRepository : JpaRepository<WarrantyClaimEntity, UUID> {
    fun findAllByCustomerId(customerId: UUID): List<WarrantyClaimEntity>
}

@Repository
interface NotificationRepository : JpaRepository<NotificationEntity, UUID> {
    fun findAllByRecipientIdOrderByCreatedAtDesc(recipientId: UUID): List<NotificationEntity>
}
