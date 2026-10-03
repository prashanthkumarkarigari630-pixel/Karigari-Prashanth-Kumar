package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.InquiryEntity
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*
import com.example.util.UpiPaymentHelper
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    orders: List<OrderEntity>,
    inquiries: List<InquiryEntity>,
    onUpdateOrderStatus: (orderId: String, newStatus: String, notes: String) -> Unit,
    onBroadcastAnnouncement: (title: String, message: String) -> Unit,
    onAddGalleryItem: (title: String, category: String, desc: String, price: Double, dimensions: String, tag: String, style: String, hex: String) -> Unit,
    onResolveInquiry: (Long) -> Unit,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Orders, 1 = Inquiries, 2 = Broadcast
    var orderStatusFilter by remember { mutableStateOf("All") }
    var selectedOrderForEdit by remember { mutableStateOf<OrderEntity?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showAddGalleryDialog by remember { mutableStateOf(false) }

    val statusFilters = listOf("All", OrderStatus.PENDING, OrderStatus.DESIGNING, OrderStatus.PRINTING, OrderStatus.READY_FOR_PICKUP, OrderStatus.DELIVERED)

    val filteredOrders = remember(orders, orderStatusFilter) {
        if (orderStatusFilter == "All") orders else orders.filter { it.status == orderStatusFilter }
    }

    val totalRevenue = remember(orders) {
        orders.sumOf { it.totalPrice }
    }
    val pendingCount = remember(orders) {
        orders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Studio Console",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = RoseTertiary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "OWNER",
                                    color = RoseTertiary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Prashanth Kumar Arts • Bibipet, Kamareddy",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onExitAdmin) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = AmberSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Customer Mode", color = AmberSecondary, fontSize = 12.sp)
                    }
                }
            )
        },
        modifier = modifier.testTag("admin_dashboard_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = IndigoPrimary.copy(alpha = 0.1f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Active Orders", fontSize = 11.sp, color = IndigoPrimary, fontWeight = FontWeight.Bold)
                            Text("$pendingCount", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = IndigoPrimary)
                            Text("${orders.size} Total orders", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberSecondary.copy(alpha = 0.1f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Est. Revenue", fontSize = 11.sp, color = AmberSecondary, fontWeight = FontWeight.Bold)
                            Text("₹${totalRevenue.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AmberSecondary)
                            Text("Studio orders", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = RoseTertiary.copy(alpha = 0.1f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        val openInquiries = inquiries.count { !it.isResolved }
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Inquiries", fontSize = 11.sp, color = RoseTertiary, fontWeight = FontWeight.Bold)
                            Text("$openInquiries", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = RoseTertiary)
                            Text("Customer leads", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Quick Studio Management Buttons (Broadcast Push & Add Work)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showBroadcastDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Broadcast Offer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showAddGalleryDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Add to Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sub tabs: Orders vs Inquiries
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Manage Orders (${orders.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Customer Inquiries (${inquiries.size})") }
                    )
                }
            }

            if (selectedTab == 0) {
                // Filter chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(statusFilters) { filter ->
                            FilterChip(
                                selected = orderStatusFilter == filter,
                                onClick = { orderStatusFilter = filter },
                                label = { Text(filter, fontSize = 11.sp) },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                if (filteredOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No orders matching '$orderStatusFilter'", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(filteredOrders, key = { it.id }) { order ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOrderForEdit = order }
                                .testTag("admin_order_card_${order.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(order.id, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = IndigoPrimary)
                                        Text("${order.customerName} (${order.customerPhone})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    OrderStatusBadge(status = order.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(order.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "${order.serviceCategory} • ₹${order.totalPrice.toInt()} • Qty: ${order.quantity} • ${order.paperType}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (order.specifications.isNotBlank()) {
                                    Text(
                                        "Specs: ${order.specifications}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Payment: ${order.paymentStatus} ${if (order.upiUtr.isNotBlank()) "(${order.upiUtr})" else ""}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (order.paymentStatus.contains("UPI")) StatusReady else AmberSecondary
                                    )

                                    Button(
                                        onClick = { selectedOrderForEdit = order },
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Update Status", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Inquiries list
                if (inquiries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No inquiries received yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(inquiries, key = { it.id }) { inq ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(inq.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(
                                        color = if (inq.isResolved) StatusReady.copy(alpha = 0.15f) else AmberSecondary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (inq.isResolved) "Resolved" else "New Inquiry",
                                            color = if (inq.isResolved) StatusReady else AmberSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text("Service: ${inq.serviceNeeded} • Phone: ${inq.phone}", fontSize = 11.sp, color = IndigoPrimary, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(inq.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { UpiPaymentHelper.openDialer(context, inq.phone) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Call", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            UpiPaymentHelper.openWhatsApp(
                                                context,
                                                "Hello ${inq.name}! This is Prashanth Kumar Arts (Bibipet) regarding your inquiry for ${inq.serviceNeeded}.",
                                                inq.phone
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("WhatsApp", fontSize = 11.sp)
                                    }

                                    if (!inq.isResolved) {
                                        Button(
                                            onClick = { onResolveInquiry(inq.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusReady),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(vertical = 4.dp)
                                        ) {
                                            Text("Done", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Update Order Status Modal Dialog
    if (selectedOrderForEdit != null) {
        val order = selectedOrderForEdit!!
        var newStatus by remember { mutableStateOf(order.status) }
        var updateNotes by remember { mutableStateOf(order.adminNotes) }

        Dialog(onDismissRequest = { selectedOrderForEdit = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Update Order: ${order.id}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = IndigoPrimary)
                    )
                    Text(
                        text = "Customer: ${order.customerName} (${order.customerPhone})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Change Order Stage:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    OrderStatus.allStatuses.forEach { statusOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { newStatus = statusOption }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = newStatus == statusOption,
                                onClick = { newStatus = statusOption }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(statusOption, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = updateNotes,
                        onValueChange = { updateNotes = it },
                        label = { Text("Studio Note to Customer") },
                        placeholder = { Text("e.g. Design proof ready for approval. Or: Printed & packed for pickup.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedOrderForEdit = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                onUpdateOrderStatus(order.id, newStatus, updateNotes)
                                selectedOrderForEdit = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save & Notify")
                        }
                    }
                }
            }
        }
    }

    // Broadcast Announcement Modal Dialog
    if (showBroadcastDialog) {
        var promoTitle by remember { mutableStateOf("Festival Flex & Print Offer!") }
        var promoMsg by remember { mutableStateOf("Get 15% discount on all Wedding Invitations & Village Jathara Flex printing at Bibipet studio.") }

        Dialog(onDismissRequest = { showBroadcastDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Broadcast Push Announcement",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AmberSecondary)
                    )
                    Text(
                        text = "Sends a real-time push notification to all customers and stores it in their notifications center.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = promoTitle,
                        onValueChange = { promoTitle = it },
                        label = { Text("Announcement Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = promoMsg,
                        onValueChange = { promoMsg = it },
                        label = { Text("Notification Message") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showBroadcastDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (promoTitle.isNotBlank() && promoMsg.isNotBlank()) {
                                    onBroadcastAnnouncement(promoTitle.trim(), promoMsg.trim())
                                    showBroadcastDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Send Push", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add New Gallery Item Dialog
    if (showAddGalleryDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCat by remember { mutableStateOf("Wedding Invitations") }
        var newDesc by remember { mutableStateOf("") }
        var newPrice by remember { mutableStateOf("20") }
        var newDims by remember { mutableStateOf("5 x 7 in") }
        var newTag by remember { mutableStateOf("Trending") }
        var newStyle by remember { mutableStateOf("Gold Foil Floral") }

        Dialog(onDismissRequest = { showAddGalleryDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Add Artwork to Portfolio",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = IndigoPrimary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Design Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newCat,
                        onValueChange = { newCat = it },
                        label = { Text("Category (Wedding, Cradle, Flex, Thumbnails)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Description & Printing Style") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("Starting ₹") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = newDims,
                            onValueChange = { newDims = it },
                            label = { Text("Dimensions") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddGalleryDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    val priceVal = newPrice.toDoubleOrNull() ?: 20.0
                                    onAddGalleryItem(newTitle, newCat, newDesc, priceVal, newDims, newTag, newStyle, "#1E3A8A")
                                    showAddGalleryDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Design")
                        }
                    }
                }
            }
        }
    }
}
