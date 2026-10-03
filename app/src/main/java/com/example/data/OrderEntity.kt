package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String, // e.g. PKA-8392
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val serviceCategory: String, // "Wedding Invitation", "Cradle Ceremony", "Poster Print", "Thumbnail", "Flex Banner", "Visiting Cards", etc.
    val title: String,
    val specifications: String, // event date, names, venue, instructions
    val dimensions: String, // e.g., "A4 (8.27 x 11.69 in)", "6x4 Feet Flex", "1920x1080 FHD"
    val quantity: Int,
    val paperType: String, // e.g., "Glossy 300 GSM", "Matte Flex", "Digital Only", "Laminated Card"
    val totalPrice: Double,
    val advancePaid: Double,
    val status: String, // PENDING, DESIGNING, PROOF_READY, PRINTING, READY_FOR_PICKUP, DELIVERED, CANCELLED
    val paymentStatus: String, // UNPAID, PAID_UPI, COD
    val upiUtr: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryDays: Int = 2,
    val adminNotes: String = "",
    val proofPreviewUrl: String = ""
)

object OrderStatus {
    const val PENDING = "Pending Review"
    const val DESIGNING = "Designing in Progress"
    const val PROOF_READY = "Proof Ready for Approval"
    const val PRINTING = "Printing & Finishing"
    const val READY_FOR_PICKUP = "Ready for Pickup / Dispatch"
    const val DELIVERED = "Delivered / Completed"
    const val CANCELLED = "Cancelled"

    val allStatuses = listOf(
        PENDING,
        DESIGNING,
        PROOF_READY,
        PRINTING,
        READY_FOR_PICKUP,
        DELIVERED
    )
}

object PaymentStatus {
    const val UNPAID = "Unpaid"
    const val PAID_UPI = "Paid via UPI"
    const val COD = "Cash on Delivery / Pickup"
}
