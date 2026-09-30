package com.fixora.backend.domain.entity

enum class StaffRole {
    ROLE_OWNER,
    ROLE_MANAGER,
    ROLE_RECEPTIONIST,
    ROLE_TECHNICIAN,
    ROLE_ACCOUNTANT
}

enum class DeviceType {
    MOBILE,
    TABLET,
    LAPTOP,
    SMARTWATCH,
    GAMING_CONSOLE,
    OTHER
}

enum class RepairPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

enum class RepairStatus {
    RECEIVED,
    DIAGNOSIS,
    WAITING_FOR_APPROVAL,
    APPROVED,
    WAITING_FOR_PARTS,
    REPAIRING,
    QUALITY_CHECK,
    READY_FOR_PICKUP,
    DELIVERED,
    CANCELLED
}

enum class EstimateStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class PaymentMethod {
    CASH,
    CARD,
    MOBILE_PAYMENT,
    BANK_TRANSFER
}

enum class WarrantyStatus {
    ACTIVE,
    EXPIRED,
    VOID
}

enum class ClaimStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED
}

enum class RecipientType {
    STAFF,
    CUSTOMER
}

enum class NotificationTargetType {
    REPAIR,
    INVOICE,
    DEVICE,
    WARRANTY,
    INVENTORY
}