package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.PKArtsApplication
import com.example.data.*
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class UserSession(
    val id: Long = 1,
    val name: String = "Ramesh Goud",
    val phone: String = "9848012345",
    val email: String = "ramesh.goud@example.com",
    val address: String = "Beside Hanuman Temple, Bibipet, Kamareddy",
    val isCustomerLoggedIn: Boolean = true,
    val isAdmin: Boolean = false
)

enum class ScreenTab {
    HOME,
    GALLERY,
    NEW_ORDER,
    TRACKING,
    CONTACT_REVIEWS,
    ADMIN_PANEL,
    PROFILE
}

class PKArtsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = (application as PKArtsApplication).database.appDao()
    private val firestoreRepo = FirestoreOrderRepository(application)

    // Navigation Tab
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // User session
    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    // Pre-fill request when user taps "Order Similar" from Gallery
    private val _prefillOrderCategory = MutableStateFlow<String?>(null)
    val prefillOrderCategory: StateFlow<String?> = _prefillOrderCategory.asStateFlow()

    private val _prefillOrderTitle = MutableStateFlow<String?>(null)
    val prefillOrderTitle: StateFlow<String?> = _prefillOrderTitle.asStateFlow()

    // Target order ID for deep-link / direct tracking
    private val _selectedTrackingOrderId = MutableStateFlow<String?>("PKA-8492")
    val selectedTrackingOrderId: StateFlow<String?> = _selectedTrackingOrderId.asStateFlow()

    // Real-time tracking from Firestore
    val liveTrackingOrder: StateFlow<OrderEntity?> = _selectedTrackingOrderId
        .flatMapLatest { orderId ->
            if (orderId.isNullOrBlank()) {
                flowOf(null)
            } else {
                firestoreRepo.observeOrderRealtime(orderId)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Message Banner / Toast
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Room Flows
    val allOrders: StateFlow<List<OrderEntity>> = dao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGalleryItems: StateFlow<List<GalleryItemEntity>> = dao.getAllGalleryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeedback: StateFlow<List<FeedbackEntity>> = dao.getAllFeedback()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInquiries: StateFlow<List<InquiryEntity>> = dao.getAllInquiries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotificationEntity>> = dao.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User's Favorite Design IDs
    val favoriteDesignIds: StateFlow<List<Long>> = _userSession
        .flatMapLatest { session -> dao.getFavoriteDesignIds(session.phone) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User's Favorite Gallery Items
    val favoriteGalleryItems: StateFlow<List<GalleryItemEntity>> = _userSession
        .flatMapLatest { session -> dao.getFavoriteGalleryItems(session.phone) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User's Personal Orders
    val myOrders: StateFlow<List<OrderEntity>> = _userSession
        .flatMapLatest { session ->
            if (session.isAdmin) {
                dao.getAllOrders()
            } else {
                dao.getOrdersByCustomer(session.phone, session.email)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepo.seedSampleOrdersIfEmpty()
        }
    }

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun setSelectedTrackingOrderId(orderId: String?) {
        _selectedTrackingOrderId.value = orderId
        if (orderId != null) {
            _currentTab.value = ScreenTab.TRACKING
        }
    }

    fun prefillOrderFromGallery(category: String, title: String) {
        _prefillOrderCategory.value = category
        _prefillOrderTitle.value = title
        _currentTab.value = ScreenTab.NEW_ORDER
    }

    fun clearPrefillOrder() {
        _prefillOrderCategory.value = null
        _prefillOrderTitle.value = null
    }

    // Toggle Favorite Design
    fun toggleFavorite(designId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val phone = _userSession.value.phone
            val count = dao.isDesignFavorite(phone, designId)
            if (count > 0) {
                dao.deleteFavorite(phone, designId)
                _uiMessage.value = "Removed from saved designs"
            } else {
                dao.insertFavorite(FavoriteDesignEntity(userPhone = phone, designId = designId))
                _uiMessage.value = "Saved to your favorite designs!"
            }
        }
    }

    // User Registration
    fun registerUser(name: String, phone: String, email: String, address: String, password: String): Boolean {
        if (name.isBlank() || phone.isBlank()) {
            _uiMessage.value = "Please enter name and phone number"
            return false
        }
        viewModelScope.launch(Dispatchers.IO) {
            val newUser = UserEntity(
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                address = address.ifBlank { "Bibipet, Kamareddy" },
                password = password.ifBlank { "1234" },
                role = "CUSTOMER"
            )
            val id = dao.insertUser(newUser)
            _userSession.value = UserSession(
                id = id,
                name = newUser.name,
                phone = newUser.phone,
                email = newUser.email,
                address = newUser.address,
                isCustomerLoggedIn = true,
                isAdmin = false
            )
        }
        _uiMessage.value = "Account created! Welcome, $name"
        return true
    }

    // User Login
    fun loginCustomer(identifier: String, password: String): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            val user = dao.authenticateUser(identifier.trim(), password.trim())
            if (user != null) {
                _userSession.value = UserSession(
                    id = user.id,
                    name = user.name,
                    phone = user.phone,
                    email = user.email,
                    address = user.address,
                    isCustomerLoggedIn = true,
                    isAdmin = user.role == "ADMIN"
                )
                _uiMessage.value = "Welcome back, ${user.name}!"
            } else {
                // If not found in DB, fallback to quick local profile login
                _userSession.value = UserSession(
                    name = if (identifier.contains("@")) identifier.substringBefore("@") else "Customer",
                    phone = if (identifier.all { it.isDigit() }) identifier else "9848012345",
                    email = if (identifier.contains("@")) identifier else "customer@example.com",
                    isCustomerLoggedIn = true,
                    isAdmin = false
                )
                _uiMessage.value = "Logged in successfully!"
            }
        }
        return true
    }

    // Update Profile Information
    fun updateProfile(name: String, phone: String, email: String, address: String) {
        val updated = _userSession.value.copy(
            name = name.trim(),
            phone = phone.trim(),
            email = email.trim(),
            address = address.trim()
        )
        _userSession.value = updated
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateUser(
                UserEntity(
                    id = updated.id,
                    name = updated.name,
                    phone = updated.phone,
                    email = updated.email,
                    address = updated.address,
                    role = if (updated.isAdmin) "ADMIN" else "CUSTOMER"
                )
            )
        }
        _uiMessage.value = "Profile information updated successfully!"
    }

    fun loginAsAdmin(passcode: String): Boolean {
        if (passcode == "7788" || passcode == "admin" || passcode.equals("Bibipet", ignoreCase = true)) {
            _userSession.value = _userSession.value.copy(
                name = "Prashanth Kumar",
                phone = "9440156789",
                email = "karigariprashanthkumar@gmail.com",
                address = "Main Road Studio, Bibipet, Kamareddy",
                isAdmin = true
            )
            _uiMessage.value = "Admin Access Granted: Welcome Prashanth Kumar!"
            _currentTab.value = ScreenTab.ADMIN_PANEL
            return true
        }
        return false
    }

    fun logout() {
        _userSession.value = UserSession(
            name = "Guest User",
            phone = "9000000000",
            email = "guest@example.com",
            address = "Bibipet, Kamareddy",
            isCustomerLoggedIn = false,
            isAdmin = false
        )
        _currentTab.value = ScreenTab.HOME
        _uiMessage.value = "Logged out successfully"
    }

    fun logoutAdmin() {
        _userSession.value = _userSession.value.copy(
            isAdmin = false
        )
        _currentTab.value = ScreenTab.HOME
        _uiMessage.value = "Switched to Customer Mode"
    }

    fun placeOrder(
        serviceCategory: String,
        title: String,
        specifications: String,
        dimensions: String,
        quantity: Int,
        paperType: String,
        totalPrice: Double,
        advancePaid: Double,
        payViaUpiNow: Boolean
    ): String {
        val randomNum = Random.nextInt(1000, 9999)
        val orderId = "PKA-$randomNum"
        val paymentStatus = if (payViaUpiNow) PaymentStatus.PAID_UPI else PaymentStatus.UNPAID

        val newOrder = OrderEntity(
            id = orderId,
            customerName = _userSession.value.name,
            customerPhone = _userSession.value.phone,
            customerEmail = _userSession.value.email,
            serviceCategory = serviceCategory,
            title = title,
            specifications = specifications,
            dimensions = dimensions,
            quantity = quantity,
            paperType = paperType,
            totalPrice = totalPrice,
            advancePaid = advancePaid,
            status = OrderStatus.PENDING,
            paymentStatus = paymentStatus,
            upiUtr = if (payViaUpiNow) "UPI/${System.currentTimeMillis().toString().takeLast(12)}" else "",
            createdAt = System.currentTimeMillis(),
            estimatedDeliveryDays = if (serviceCategory.contains("Flex") || serviceCategory.contains("Poster")) 1 else 2,
            adminNotes = "Order received at Bibipet studio. Requirements being verified."
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insertOrder(newOrder)
            firestoreRepo.saveOrderToFirestore(newOrder)

            // Insert in-app notification
            dao.insertNotification(
                AppNotificationEntity(
                    title = "Order Placed ($orderId)",
                    message = "Your order for $title has been received by Prashanth Kumar Arts.",
                    type = "ORDER_UPDATE",
                    targetOrderId = orderId
                )
            )

            // Trigger system push notification
            NotificationHelper.sendOrderStatusNotification(
                getApplication(),
                orderId,
                title,
                OrderStatus.PENDING,
                "Your order $orderId has been placed with Prashanth Kumar Arts! We will begin designing shortly."
            )
        }

        _selectedTrackingOrderId.value = orderId
        _currentTab.value = ScreenTab.TRACKING
        _uiMessage.value = "Order $orderId placed successfully!"
        return orderId
    }

    fun updateOrderStatus(orderId: String, newStatus: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val order = dao.getOrderByIdDirect(orderId)
            dao.updateOrderStatus(orderId, newStatus, notes)
            firestoreRepo.updateOrderStatusInFirestore(orderId, newStatus, notes)

            val orderTitle = order?.title ?: "Your Order"
            val notificationMsg = "Order $orderId update: $newStatus. ${notes.ifBlank { "Thank you for choosing PK Arts Bibipet!" }}"

            // Record in app notification
            dao.insertNotification(
                AppNotificationEntity(
                    title = "Status: $newStatus",
                    message = notificationMsg,
                    type = "ORDER_UPDATE",
                    targetOrderId = orderId
                )
            )

            // Trigger real-time push notification
            NotificationHelper.sendOrderStatusNotification(
                getApplication(),
                orderId,
                orderTitle,
                newStatus,
                notificationMsg
            )
        }
        _uiMessage.value = "Order $orderId status updated to '$newStatus'"
    }

    fun confirmUpiPayment(orderId: String, utr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val order = dao.getOrderByIdDirect(orderId)
            dao.updatePaymentStatus(orderId, PaymentStatus.PAID_UPI, utr)
            firestoreRepo.updatePaymentInFirestore(orderId, PaymentStatus.PAID_UPI, utr)

            val msg = "UPI Payment of ₹${order?.totalPrice ?: 0.0} confirmed (UTR: $utr). Processing your order!"

            dao.insertNotification(
                AppNotificationEntity(
                    title = "Payment Confirmed ($orderId)",
                    message = msg,
                    type = "ORDER_UPDATE",
                    targetOrderId = orderId
                )
            )

            NotificationHelper.sendOrderStatusNotification(
                getApplication(),
                orderId,
                order?.title ?: "Order",
                "Payment Confirmed",
                msg
            )
        }
        _uiMessage.value = "UPI payment confirmed for order $orderId!"
    }

    // Broadcast Promotional Announcement from Admin
    fun broadcastAnnouncement(title: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertNotification(
                AppNotificationEntity(
                    title = title,
                    message = message,
                    type = "ANNOUNCEMENT"
                )
            )
            NotificationHelper.sendPromotionalNotification(
                getApplication(),
                title,
                message
            )
        }
        _uiMessage.value = "Promotional announcement broadcast to all users!"
    }

    fun markNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.markAllNotificationsAsRead()
        }
    }

    fun submitFeedback(
        rating: Int,
        serviceCategory: String,
        comment: String,
        location: String
    ) {
        val feedback = FeedbackEntity(
            customerName = _userSession.value.name,
            customerLocation = location.ifBlank { "Kamareddy / Bibipet" },
            rating = rating,
            serviceCategory = serviceCategory,
            comment = comment,
            dateText = "Just now",
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertFeedback(feedback)
        }
        _uiMessage.value = "Thank you for your feedback! It helps our studio grow."
    }

    fun submitInquiry(
        name: String,
        phone: String,
        email: String,
        serviceNeeded: String,
        message: String
    ) {
        val inquiry = InquiryEntity(
            name = name,
            phone = phone,
            email = email,
            serviceNeeded = serviceNeeded,
            message = message,
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertInquiry(inquiry)
        }
        _uiMessage.value = "Inquiry sent to Prashanth Kumar Arts! We will reach out shortly."
    }

    fun addGalleryItem(
        title: String,
        category: String,
        description: String,
        startingPrice: Double,
        dimensions: String,
        tag: String,
        style: String,
        accentHex: String
    ) {
        val item = GalleryItemEntity(
            title = title,
            category = category,
            description = description,
            startingPrice = startingPrice,
            dimensions = dimensions,
            tag = tag.ifBlank { "Custom Work" },
            sampleStyle = style,
            accentHex = accentHex.ifBlank { "#1E3A8A" },
            isFeatured = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertGalleryItem(item)
        }
        _uiMessage.value = "New design '$title' added to portfolio showcase!"
    }

    fun deleteGalleryItem(item: GalleryItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteGalleryItem(item)
        }
        _uiMessage.value = "Portfolio item deleted"
    }

    fun deleteOrder(order: OrderEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteOrder(order)
        }
        _uiMessage.value = "Order ${order.id} deleted"
    }

    fun resolveInquiry(inquiryId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateInquiryStatus(inquiryId, true)
        }
        _uiMessage.value = "Inquiry marked as resolved"
    }
}
