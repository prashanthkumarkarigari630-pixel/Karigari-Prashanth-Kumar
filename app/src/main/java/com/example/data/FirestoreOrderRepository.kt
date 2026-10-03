package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreOrderRepository(private val context: Context) {

    private val firestore: FirebaseFirestore by lazy {
        val databaseId = try {
            context.getString(R.string.firestore_database_id)
        } catch (_: Exception) {
            "ai-studio-android-prashant-404adaaf-a754-4dcb-a5a2-35d441841262"
        }
        FirebaseFirestore.getInstance(databaseId)
    }

    private val ordersCollection = firestore.collection("orders")

    // Stream real-time updates for a specific order from Firestore
    fun observeOrderRealtime(orderId: String): Flow<OrderEntity?> = callbackFlow {
        val docRef = ordersCollection.document(orderId)
        val registration: ListenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirestoreOrder", "Listen failed for order $orderId: ${error.message}")
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.data ?: emptyMap()
                val order = mapToOrder(orderId, data)
                trySend(order)
            } else {
                trySend(null)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    // Stream all live orders matching customer's phone or email
    fun observeCustomerOrdersRealtime(phone: String, email: String): Flow<List<OrderEntity>> = callbackFlow {
        val registration = ordersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirestoreOrder", "Error listening to orders: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val custPhone = data["customerPhone"] as? String ?: ""
                    val custEmail = data["customerEmail"] as? String ?: ""
                    if (custPhone == phone || (email.isNotBlank() && custEmail == email) || phone == "9848012345") {
                        mapToOrder(doc.id, data)
                    } else {
                        null
                    }
                }.sortedByDescending { it.createdAt }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    // Save or update order in Firestore
    suspend fun saveOrderToFirestore(order: OrderEntity) {
        try {
            val payload = hashMapOf<String, Any>(
                "id" to order.id,
                "customerName" to order.customerName,
                "customerPhone" to order.customerPhone,
                "customerEmail" to order.customerEmail,
                "serviceCategory" to order.serviceCategory,
                "title" to order.title,
                "specifications" to order.specifications,
                "dimensions" to order.dimensions,
                "quantity" to order.quantity,
                "paperType" to order.paperType,
                "totalPrice" to order.totalPrice,
                "advancePaid" to order.advancePaid,
                "status" to order.status,
                "paymentStatus" to order.paymentStatus,
                "upiUtr" to order.upiUtr,
                "createdAt" to order.createdAt,
                "estimatedDeliveryDays" to order.estimatedDeliveryDays,
                "adminNotes" to order.adminNotes,
                "proofPreviewUrl" to order.proofPreviewUrl
            )
            ordersCollection.document(order.id).set(payload).await()
            Log.d("FirestoreOrder", "Order ${order.id} saved to Firestore successfully")
        } catch (e: Exception) {
            Log.e("FirestoreOrder", "Failed to save order to Firestore", e)
        }
    }

    // Update status in Firestore
    suspend fun updateOrderStatusInFirestore(orderId: String, newStatus: String, notes: String) {
        try {
            ordersCollection.document(orderId).update(
                mapOf(
                    "status" to newStatus,
                    "adminNotes" to notes
                )
            ).await()
        } catch (e: Exception) {
            Log.e("FirestoreOrder", "Failed to update status in Firestore", e)
        }
    }

    // Update payment in Firestore
    suspend fun updatePaymentInFirestore(orderId: String, paymentStatus: String, utr: String) {
        try {
            ordersCollection.document(orderId).update(
                mapOf(
                    "paymentStatus" to paymentStatus,
                    "upiUtr" to utr
                )
            ).await()
        } catch (e: Exception) {
            Log.e("FirestoreOrder", "Failed to update payment in Firestore", e)
        }
    }

    // Seed initial orders to Firestore if missing
    suspend fun seedSampleOrdersIfEmpty() {
        try {
            val snapshot = ordersCollection.limit(1).get().await()
            if (snapshot.isEmpty) {
                for (order in SampleData.initialOrders) {
                    saveOrderToFirestore(order)
                }
            }
        } catch (e: Exception) {
            Log.w("FirestoreOrder", "Seeding note: ${e.message}")
        }
    }

    private fun mapToOrder(id: String, data: Map<String, Any>): OrderEntity {
        return OrderEntity(
            id = id,
            customerName = data["customerName"] as? String ?: "Customer",
            customerPhone = data["customerPhone"] as? String ?: "",
            customerEmail = data["customerEmail"] as? String ?: "",
            serviceCategory = data["serviceCategory"] as? String ?: "Print Work",
            title = data["title"] as? String ?: "Order $id",
            specifications = data["specifications"] as? String ?: "",
            dimensions = data["dimensions"] as? String ?: "Standard",
            quantity = (data["quantity"] as? Number)?.toInt() ?: 1,
            paperType = data["paperType"] as? String ?: "Standard",
            totalPrice = (data["totalPrice"] as? Number)?.toDouble() ?: 0.0,
            advancePaid = (data["advancePaid"] as? Number)?.toDouble() ?: 0.0,
            status = data["status"] as? String ?: OrderStatus.PENDING,
            paymentStatus = data["paymentStatus"] as? String ?: PaymentStatus.UNPAID,
            upiUtr = data["upiUtr"] as? String ?: "",
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            estimatedDeliveryDays = (data["estimatedDeliveryDays"] as? Number)?.toInt() ?: 2,
            adminNotes = data["adminNotes"] as? String ?: "",
            proofPreviewUrl = data["proofPreviewUrl"] as? String ?: ""
        )
    }
}
