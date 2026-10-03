package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.data.PaymentStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.UpiPaymentDialog
import com.example.ui.theme.*
import com.example.util.NotificationHelper
import com.example.util.UpiPaymentHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orders: List<OrderEntity>,
    liveFirestoreOrder: OrderEntity?,
    selectedOrderId: String?,
    onSelectOrder: (String?) -> Unit,
    onConfirmUpiPayment: (orderId: String, utr: String) -> Unit,
    onAdvanceOrderStatus: ((orderId: String, nextStatus: String) -> Unit)? = null,
    onNavigateNewOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf(selectedOrderId ?: "") }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Live Tracker, 1 = All Orders
    var activePaymentOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var activeReceiptOrder by remember { mutableStateOf<OrderEntity?>(null) }

    // Synchronize search query with selectedOrderId if passed
    LaunchedEffect(selectedOrderId) {
        if (!selectedOrderId.isNullOrBlank() && searchQuery.isBlank()) {
            searchQuery = selectedOrderId
        }
    }

    // Determine the active order being tracked live
    val activeTrackedOrder: OrderEntity? = remember(liveFirestoreOrder, orders, selectedOrderId, searchQuery) {
        val targetId = when {
            selectedOrderId != null -> selectedOrderId
            searchQuery.isNotBlank() -> searchQuery.trim()
            orders.isNotEmpty() -> orders.first().id
            else -> null
        }
        if (targetId != null) {
            // First check real-time firestore object
            if (liveFirestoreOrder != null && liveFirestoreOrder.id.equals(targetId, ignoreCase = true)) {
                liveFirestoreOrder
            } else {
                orders.firstOrNull { it.id.equals(targetId, ignoreCase = true) }
            }
        } else {
            orders.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Live Firestore Streaming Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Real-Time Order Tracking",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Live status & progression powered by Cloud Firestore",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    // Pulsating Live Badge
                    Surface(
                        color = Color(0xFF047857).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE STREAM",
                                color = Color(0xFF047857),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search / Select input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSelectOrder(it.ifBlank { null })
                    },
                    placeholder = { Text("Search by Order ID (e.g. PKA-8492)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                onSelectOrder(null)
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_search_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Select Chips for customer orders
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(orders.take(5)) { ord ->
                        val isSelected = activeTrackedOrder?.id == ord.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                searchQuery = ord.id
                                onSelectOrder(ord.id)
                            },
                            label = {
                                Text(
                                    text = "${ord.id} (${ord.serviceCategory.take(12)})",
                                    fontSize = 11.sp
                                )
                            },
                            leadingIcon = {
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndigoPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Current Progression Tracker") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("All Orders (${orders.size})") }
                    )
                }
            }
        },
        modifier = modifier.testTag("order_tracking_screen")
    ) { padding ->
        if (selectedTab == 0) {
            // Live Progression Screen for activeTrackedOrder
            if (activeTrackedOrder == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.TrackChanges,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No order selected for live tracking",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateNewOrder,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create a New Print Order")
                        }
                    }
                }
            } else {
                val order = activeTrackedOrder
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Live Progression Hero Card
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Order #${order.id}",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = IndigoPrimary
                                            )
                                        )
                                        Text(
                                            text = "Prashanth Kumar Arts Studio, Bibipet",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    OrderStatusBadge(status = order.status)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = order.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${order.serviceCategory} • ${order.dimensions} • Qty: ${order.quantity} • ${order.paperType}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Real-time Progression Bar & Percentage
                                val progressPercent = when (order.status) {
                                    OrderStatus.PENDING -> 0.15f
                                    OrderStatus.DESIGNING -> 0.40f
                                    OrderStatus.PROOF_READY -> 0.60f
                                    OrderStatus.PRINTING -> 0.80f
                                    OrderStatus.READY_FOR_PICKUP -> 0.95f
                                    OrderStatus.DELIVERED -> 1.0f
                                    else -> 0.10f
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current Progression: ${(progressPercent * 100).toInt()}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = IndigoPrimary
                                    )
                                    Text(
                                        text = order.status,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = AmberSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { progressPercent },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (progressPercent >= 0.95f) StatusReady else IndigoPrimary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Visual Progression Stepper
                                DetailedProgressionStepper(currentStatus = order.status)
                            }
                        }
                    }

                    // Live Studio Notes & Proof Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Draw,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Live Designer & Print Updates",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = IndigoPrimary
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (order.adminNotes.isNotBlank()) {
                                        order.adminNotes
                                    } else {
                                        "Designs are being verified by Prashanth Kumar. Proof will be sent for WhatsApp review."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                if (order.proofPreviewUrl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Image, contentDescription = null, tint = AmberSecondary)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("Watermarked Proof Sample Ready", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("Tap WhatsApp below to confirm approval", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Event Specifications & Delivery Info Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Order Specifications & Delivery",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Customer Name:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(order.customerName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Mobile Number:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(order.customerPhone, fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Est. Turnaround:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${order.estimatedDeliveryDays} Days (Bibipet Studio Counter)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }

                                if (order.specifications.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("Customer Requirement Details:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(order.specifications, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(12.dp))

                                // Payment status row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total Amount Due", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            "₹${String.format(Locale.US, "%.2f", order.totalPrice)}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = IndigoPrimary
                                        )
                                    }

                                    Surface(
                                        color = if (order.paymentStatus == PaymentStatus.PAID_UPI) StatusReady.copy(alpha = 0.15f) else AmberSecondary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = order.paymentStatus,
                                            color = if (order.paymentStatus == PaymentStatus.PAID_UPI) StatusReady else AmberSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (order.upiUtr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("UTR: ${order.upiUtr}", fontSize = 10.sp, color = StatusReady)
                                }
                            }
                        }
                    }

                    // Direct Action Buttons (UPI Pay, Digital Receipt, WhatsApp, Call)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (order.paymentStatus != PaymentStatus.PAID_UPI) {
                                Button(
                                    onClick = { activePaymentOrder = order },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pay with UPI (GPay / PhonePe / Paytm)", fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { activeReceiptOrder = order },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Digital Bill", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        UpiPaymentHelper.openWhatsApp(
                                            context,
                                            "Hello Prashanth Kumar! I am tracking order ${order.id} (${order.title}) at Bibipet studio. Current stage: ${order.status}."
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp Studio", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { UpiPaymentHelper.openDialer(context) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(0.8f)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Real-Time Progression Test Sandbox (Advances stage in Firestore and demonstrates real-time listening)
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ Real-Time Progression Test Sandbox",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = "Tap to advance this order's status in Firestore and observe the live streaming progression bar update instantly.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                val nextStage = when (order.status) {
                                    OrderStatus.PENDING -> OrderStatus.DESIGNING
                                    OrderStatus.DESIGNING -> OrderStatus.PROOF_READY
                                    OrderStatus.PROOF_READY -> OrderStatus.PRINTING
                                    OrderStatus.PRINTING -> OrderStatus.READY_FOR_PICKUP
                                    OrderStatus.READY_FOR_PICKUP -> OrderStatus.DELIVERED
                                    else -> OrderStatus.PENDING
                                }

                                Button(
                                    onClick = {
                                        onAdvanceOrderStatus?.invoke(order.id, nextStage)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simulate Live Firestore Update → $nextStage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // All Orders Tab List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders, key = { it.id }) { ord ->
                    OrderTrackingCard(
                        order = ord,
                        onPayUpi = { activePaymentOrder = ord },
                        onViewReceipt = { activeReceiptOrder = ord },
                        onSendAlert = {
                            NotificationHelper.sendOrderStatusNotification(
                                context,
                                ord.id,
                                ord.title,
                                ord.status,
                                "Order ${ord.id} is in '${ord.status}' stage at Bibipet studio."
                            )
                        }
                    )
                }
            }
        }
    }

    // UPI Payment Sheet Dialog
    if (activePaymentOrder != null) {
        UpiPaymentDialog(
            order = activePaymentOrder!!,
            onDismiss = { activePaymentOrder = null },
            onPaymentConfirmed = { utr ->
                onConfirmUpiPayment(activePaymentOrder!!.id, utr)
                activePaymentOrder = null
            }
        )
    }

    // Receipt Dialog
    if (activeReceiptOrder != null) {
        OrderReceiptDialog(
            order = activeReceiptOrder!!,
            onDismiss = { activeReceiptOrder = null }
        )
    }
}

@Composable
fun DetailedProgressionStepper(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        Triple(OrderStatus.PENDING, "1. Order Verified", "Specifications checked by Prashanth Kumar"),
        Triple(OrderStatus.DESIGNING, "2. Designing in Progress", "Telugu fonts, vector crests, photo cutouts & color grading"),
        Triple(OrderStatus.PROOF_READY, "3. Proof Approval", "Preview sample ready; waiting for client WhatsApp confirmation"),
        Triple(OrderStatus.PRINTING, "4. Printing & Press", "Sent to Konica Minolta 1440 DPI digital/flex press with UV coat"),
        Triple(OrderStatus.READY_FOR_PICKUP, "5. Ready for Pickup", "Packed and available at Bibipet studio counter or dispatch"),
        Triple(OrderStatus.DELIVERED, "6. Delivered", "Handed over to happy customer with 100% satisfaction")
    )

    val currentIndex = when (currentStatus) {
        OrderStatus.PENDING -> 0
        OrderStatus.DESIGNING -> 1
        OrderStatus.PROOF_READY -> 2
        OrderStatus.PRINTING -> 3
        OrderStatus.READY_FOR_PICKUP -> 4
        OrderStatus.DELIVERED -> 5
        OrderStatus.CANCELLED -> -1
        else -> 0
    }

    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, (statusKey, stepTitle, stepDetail) ->
            val isDone = currentIndex >= index && currentIndex != -1
            val isCurrent = currentIndex == index

            val stepColor by animateColorAsState(
                targetValue = when {
                    isCurrent -> AmberSecondary
                    isDone -> IndigoPrimary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                label = "step_color"
            )

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circle with number or check
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(stepColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone && !isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                color = if (isDone || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(38.dp)
                                .background(
                                    if (currentIndex > index) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stepTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrent) AmberSecondary else if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AmberSecondary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = AmberSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = stepDetail,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun OrderTrackingCard(
    order: OrderEntity,
    onPayUpi: () -> Unit,
    onViewReceipt: () -> Unit,
    onSendAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    val formattedDate = remember(order.createdAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(order.createdAt))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("tracking_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.id,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoPrimary
                        )
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    )
                }
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "${order.serviceCategory} • ${order.dimensions} • Qty: ${order.quantity} • ${order.paperType}",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            if (order.adminNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Studio note: ${order.adminNotes}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${String.format(Locale.US, "%.2f", order.totalPrice)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoPrimary
                        )
                    )
                    Text(
                        text = order.paymentStatus,
                        fontSize = 11.sp,
                        color = if (order.paymentStatus == PaymentStatus.PAID_UPI) StatusReady else AmberSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onSendAlert) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Alert", tint = AmberSecondary)
                    }

                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bill", fontSize = 11.sp)
                    }

                    if (order.paymentStatus != PaymentStatus.PAID_UPI) {
                        Button(
                            onClick = onPayUpi,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Pay UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderReceiptDialog(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val formattedDate = remember(order.createdAt) {
        val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(order.createdAt))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRASHANTH KUMAR ARTS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = IndigoPrimary)
                        )
                        Text(
                            text = "Bibipet, Kamareddy District, Telangana",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Order Receipt:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(order.id, fontWeight = FontWeight.Bold, color = IndigoPrimary, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Date:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formattedDate, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Customer:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(order.customerName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Text(order.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Service: ${order.serviceCategory}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Specs: ${order.dimensions} | ${order.paperType} | Qty: ${order.quantity}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount Due", fontWeight = FontWeight.Bold)
                        Text(
                            "₹${String.format(Locale.US, "%.2f", order.totalPrice)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoPrimary,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        UpiPaymentHelper.copyToClipboard(
                            context,
                            "PRASHANTH KUMAR ARTS (Bibipet) Receipt:\nOrder: ${order.id}\nItem: ${order.title}\nAmount: ₹${order.totalPrice}\nStatus: ${order.status}\nPayment: ${order.paymentStatus}",
                            "Receipt"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Digital Bill")
                }
            }
        }
    }
}
