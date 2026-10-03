package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery_items")
data class GalleryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // Wedding Invitation, Cradle Ceremony, Posters & Flex, Thumbnails, Visiting Cards, Festival & Birthday
    val description: String,
    val startingPrice: Double,
    val dimensions: String,
    val tag: String,
    val rating: Float = 4.9f,
    val accentHex: String = "#1E3A8A", // For beautiful custom canvas/badge preview
    val sampleStyle: String = "Traditional Floral Gold",
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
