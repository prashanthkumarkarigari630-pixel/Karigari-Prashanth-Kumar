package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.OrderStatus
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PKArtsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle target order ID if launched from push notification
        val targetOrderId = intent?.getStringExtra("TARGET_ORDER_ID")
        if (!targetOrderId.isNullOrBlank()) {
            viewModel.setSelectedTrackingOrderId(targetOrderId)
        }

        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: PKArtsViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request POST_NOTIFICATIONS on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Notification permission handled
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // State collections
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val userSession by viewModel.userSession.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val myOrders by viewModel.myOrders.collectAsStateWithLifecycle()
    val allGalleryItems by viewModel.allGalleryItems.collectAsStateWithLifecycle()
    val favoriteDesigns by viewModel.favoriteGalleryItems.collectAsStateWithLifecycle()
    val allFeedback by viewModel.allFeedback.collectAsStateWithLifecycle()
    val allInquiries by viewModel.allInquiries.collectAsStateWithLifecycle()
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val liveFirestoreOrder by viewModel.liveTrackingOrder.collectAsStateWithLifecycle()
    val prefillCategory by viewModel.prefillOrderCategory.collectAsStateWithLifecycle()
    val prefillTitle by viewModel.prefillOrderTitle.collectAsStateWithLifecycle()
    val selectedTrackingOrderId by viewModel.selectedTrackingOrderId.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()

    var showCustomerLoginDialog by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Show snackbar on message
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUiMessage()
        }
    }

    // Handle back button on sub-screens to return to Home
    if (currentTab != ScreenTab.HOME) {
        BackHandler {
            viewModel.selectTab(ScreenTab.HOME)
        }
    }

    val activeOrdersCount = remember(allOrders) {
        allOrders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }
    val unreadNotificationsCount = remember(allNotifications) {
        allNotifications.count { !it.isRead }
    }

    Scaffold(
        topBar = {
            Column {
                PKArtsTopAppBar(
                    userSession = userSession,
                    activeOrdersCount = activeOrdersCount,
                    onAdminClick = { viewModel.selectTab(ScreenTab.ADMIN_PANEL) },
                    onCustomerProfileClick = { viewModel.selectTab(ScreenTab.PROFILE) },
                    onOpenTracking = { viewModel.selectTab(ScreenTab.TRACKING) }
                )
            }
        },
        bottomBar = {
            PKArtsBottomNavBar(
                currentTab = currentTab,
                isAdmin = userSession.isAdmin,
                onTabSelected = { tab -> viewModel.selectTab(tab) },
                activeOrdersCount = activeOrdersCount
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        activeOrders = allOrders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED },
                        recentFeedback = allFeedback,
                        onNavigateTab = { tab -> viewModel.selectTab(tab) },
                        onStartOrder = { category ->
                            viewModel.prefillOrderFromGallery(category, "")
                        },
                        onSelectOrderForTracking = { orderId ->
                            viewModel.setSelectedTrackingOrderId(orderId)
                        }
                    )
                }

                ScreenTab.GALLERY -> {
                    GalleryScreen(
                        items = allGalleryItems,
                        isAdmin = userSession.isAdmin,
                        onOrderSimilar = { category, title ->
                            viewModel.prefillOrderFromGallery(category, title)
                        },
                        onAddNewItem = {
                            viewModel.selectTab(ScreenTab.ADMIN_PANEL)
                        },
                        onDeleteItem = { item ->
                            viewModel.deleteGalleryItem(item)
                        }
                    )
                }

                ScreenTab.NEW_ORDER -> {
                    NewOrderScreen(
                        prefilledCategory = prefillCategory,
                        prefilledTitle = prefillTitle,
                        onOrderPlaced = { category, title, specs, dims, qty, paper, price, advance, payNow ->
                            viewModel.placeOrder(
                                serviceCategory = category,
                                title = title,
                                specifications = specs,
                                dimensions = dims,
                                quantity = qty,
                                paperType = paper,
                                totalPrice = price,
                                advancePaid = advance,
                                payViaUpiNow = payNow
                            )
                            viewModel.clearPrefillOrder()
                        }
                    )
                }

                ScreenTab.TRACKING -> {
                    OrderTrackingScreen(
                        orders = allOrders,
                        liveFirestoreOrder = liveFirestoreOrder,
                        selectedOrderId = selectedTrackingOrderId,
                        onSelectOrder = { id -> viewModel.setSelectedTrackingOrderId(id) },
                        onConfirmUpiPayment = { orderId, utr ->
                            viewModel.confirmUpiPayment(orderId, utr)
                        },
                        onAdvanceOrderStatus = { orderId, nextStatus ->
                            viewModel.updateOrderStatus(orderId, nextStatus, "Live Firestore progression advance")
                        },
                        onNavigateNewOrder = { viewModel.selectTab(ScreenTab.NEW_ORDER) }
                    )
                }

                ScreenTab.CONTACT_REVIEWS -> {
                    ContactReviewsScreen(
                        userSession = userSession,
                        feedbackList = allFeedback,
                        onSubmitFeedback = { rating, category, comment, loc ->
                            viewModel.submitFeedback(rating, category, comment, loc)
                        },
                        onSubmitInquiry = { name, phone, email, service, msg ->
                            viewModel.submitInquiry(name, phone, email, service, msg)
                        }
                    )
                }

                ScreenTab.PROFILE -> {
                    ProfileScreen(
                        userSession = userSession,
                        myOrders = myOrders,
                        favoriteDesigns = favoriteDesigns,
                        onUpdateProfile = { name, phone, email, address ->
                            viewModel.updateProfile(name, phone, email, address)
                        },
                        onRegisterUser = { name, phone, email, address, pass ->
                            viewModel.registerUser(name, phone, email, address, pass)
                        },
                        onLoginUser = { identifier, pass ->
                            viewModel.loginCustomer(identifier, pass)
                        },
                        onToggleFavorite = { id ->
                            viewModel.toggleFavorite(id)
                        },
                        onSelectOrderForTracking = { orderId ->
                            viewModel.setSelectedTrackingOrderId(orderId)
                        },
                        onOrderSimilar = { category, title ->
                            viewModel.prefillOrderFromGallery(category, title)
                        },
                        onOpenAdminLogin = {
                            showAdminLoginDialog = true
                        },
                        onLogout = {
                            viewModel.logout()
                        }
                    )
                }

                ScreenTab.ADMIN_PANEL -> {
                    AdminDashboardScreen(
                        orders = allOrders,
                        inquiries = allInquiries,
                        onUpdateOrderStatus = { orderId, newStatus, notes ->
                            viewModel.updateOrderStatus(orderId, newStatus, notes)
                        },
                        onBroadcastAnnouncement = { title, message ->
                            viewModel.broadcastAnnouncement(title, message)
                        },
                        onAddGalleryItem = { title, category, desc, price, dims, tag, style, hex ->
                            viewModel.addGalleryItem(title, category, desc, price, dims, tag, style, hex)
                        },
                        onResolveInquiry = { inqId ->
                            viewModel.resolveInquiry(inqId)
                        },
                        onExitAdmin = {
                            viewModel.logoutAdmin()
                        }
                    )
                }
            }
        }
    }

    // Customer Login Dialog
    if (showCustomerLoginDialog) {
        CustomerLoginDialog(
            currentSession = userSession,
            onDismiss = { showCustomerLoginDialog = false },
            onSaveProfile = { name, phone, email ->
                viewModel.updateProfile(name, phone, email, userSession.address)
            },
            onSwitchToAdminLogin = {
                showCustomerLoginDialog = false
                showAdminLoginDialog = true
            }
        )
    }

    // Admin Passcode Login Dialog
    if (showAdminLoginDialog) {
        AdminLoginDialog(
            onDismiss = { showAdminLoginDialog = false },
            onAdminLogin = { passcode ->
                viewModel.loginAsAdmin(passcode)
            }
        )
    }

    // In-App Push Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = allNotifications,
            onDismiss = {
                showNotificationsDialog = false
                viewModel.markNotificationsRead()
            },
            onSelectOrder = { orderId ->
                viewModel.setSelectedTrackingOrderId(orderId)
            }
        )
    }
}
