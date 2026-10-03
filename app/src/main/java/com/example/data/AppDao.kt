package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerPhone = :phone OR customerEmail = :email ORDER BY createdAt DESC")
    fun getOrdersByCustomer(phone: String, email: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderByIdDirect(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus, adminNotes = :notes WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String, notes: String)

    @Query("UPDATE orders SET paymentStatus = :newPaymentStatus, upiUtr = :utr WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, newPaymentStatus: String, utr: String)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    // Gallery Items
    @Query("SELECT * FROM gallery_items ORDER BY id ASC")
    fun getAllGalleryItems(): Flow<List<GalleryItemEntity>>

    @Query("SELECT * FROM gallery_items WHERE category = :category ORDER BY id ASC")
    fun getGalleryByCategory(category: String): Flow<List<GalleryItemEntity>>

    @Query("SELECT * FROM gallery_items WHERE id = :id LIMIT 1")
    suspend fun getGalleryItemById(id: Long): GalleryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItem(item: GalleryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItems(items: List<GalleryItemEntity>)

    @Delete
    suspend fun deleteGalleryItem(item: GalleryItemEntity)

    // Users
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE (phone = :identifier OR email = :identifier) AND password = :password LIMIT 1")
    suspend fun authenticateUser(identifier: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // Favorites
    @Query("SELECT designId FROM favorite_designs WHERE userPhone = :userPhone")
    fun getFavoriteDesignIds(userPhone: String): Flow<List<Long>>

    @Query("SELECT * FROM gallery_items WHERE id IN (SELECT designId FROM favorite_designs WHERE userPhone = :userPhone)")
    fun getFavoriteGalleryItems(userPhone: String): Flow<List<GalleryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteDesignEntity)

    @Query("DELETE FROM favorite_designs WHERE userPhone = :userPhone AND designId = :designId")
    suspend fun deleteFavorite(userPhone: String, designId: Long)

    @Query("SELECT COUNT(*) FROM favorite_designs WHERE userPhone = :userPhone AND designId = :designId")
    suspend fun isDesignFavorite(userPhone: String, designId: Long): Int

    // Feedback
    @Query("SELECT * FROM feedback ORDER BY createdAt DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFeedback(feedbackList: List<FeedbackEntity>)

    // Inquiries
    @Query("SELECT * FROM inquiries ORDER BY createdAt DESC")
    fun getAllInquiries(): Flow<List<InquiryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: InquiryEntity)

    @Query("UPDATE inquiries SET isResolved = :isResolved WHERE id = :inquiryId")
    suspend fun updateInquiryStatus(inquiryId: Long, isResolved: Boolean)

    // In-App Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()
}
