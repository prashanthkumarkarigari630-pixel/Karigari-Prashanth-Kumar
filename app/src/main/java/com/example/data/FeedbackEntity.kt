package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerLocation: String = "Bibipet / Kamareddy",
    val rating: Int, // 1 to 5
    val serviceCategory: String,
    val comment: String,
    val dateText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isVerifiedCustomer: Boolean = true
)

@Entity(tableName = "inquiries")
data class InquiryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val serviceNeeded: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)
