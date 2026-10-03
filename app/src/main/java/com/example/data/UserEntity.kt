package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val password: String = "1234",
    val address: String = "Bibipet, Kamareddy",
    val role: String = "CUSTOMER", // "CUSTOMER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_designs", primaryKeys = ["userPhone", "designId"])
data class FavoriteDesignEntity(
    val userPhone: String,
    val designId: Long,
    val savedAt: Long = System.currentTimeMillis()
)
