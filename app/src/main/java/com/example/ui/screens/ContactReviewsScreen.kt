package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedbackEntity
import com.example.ui.UserSession
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.StatusReady
import com.example.util.UpiPaymentHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactReviewsScreen(
    userSession: UserSession,
    feedbackList: List<FeedbackEntity>,
    onSubmitFeedback: (rating: Int, serviceCategory: String, comment: String, location: String) -> Unit,
    onSubmitInquiry: (name: String, phone: String, email: String, serviceNeeded: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Contact & Inquiries, 1 = Customer Feedback

    // Inquiry form state
    var inquiryName by remember { mutableStateOf(userSession.name) }
    var inquiryPhone by remember { mutableStateOf(userSession.phone) }
    var inquiryEmail by remember { mutableStateOf(userSession.email) }
    var inquiryService by remember { mutableStateOf("Wedding Invitations") }
    var inquiryMessage by remember { mutableStateOf("") }
    var inquirySubmitted by remember { mutableStateOf(false) }

    // Feedback form state
    var showWriteReview by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableStateOf(5) }
    var reviewService by remember { mutableStateOf("Wedding Invitations") }
    var reviewLocation by remember { mutableStateOf(userSession.address) }
    var reviewComment by remember { mutableStateOf("") }

    val serviceOptions = listOf(
        "Wedding Invitations",
        "Cradle Ceremony",
        "Posters & Flex",
        "Thumbnails",
        "Visiting Cards",
        "Photo Frames",
        "General Printing Inquiry"
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Contact & Reviews",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Prashanth Kumar Arts • Bibipet, Kamareddy",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Contact Studio") },
                        icon = { Icon(Icons.Default.ContactPhone, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_contact")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Reviews & Suggestions") },
                        icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_reviews")
                    )
                }
            }
        },
        modifier = modifier.testTag("contact_reviews_screen")
    ) { padding ->
        if (selectedTab == 0) {
            // Contact & Inquiries Screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Studio Info Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(IndigoPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Prashanth Kumar Arts",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Owner & Designer: Karigari Prashanth Kumar",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(14.dp))

                            // Contact items
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Studio Location", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Main Road, Bibipet, Kamareddy District, Telangana 503125", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Phone / WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("+91 94401 56789 / +91 98480 12345", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Email", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("karigariprashanthkumar@gmail.com", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Direct Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { UpiPaymentHelper.openDialer(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call Now", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        UpiPaymentHelper.openWhatsApp(
                                            context,
                                            "Hello Prashanth Kumar Arts! I want to get designs/printing done at Bibipet."
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { UpiPaymentHelper.openMapLocation(context) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Map", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Inquiries Form
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_form_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Send Inquiry Directly to Business",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Have a question about wedding cards, urgent flex prints, or thumbnail rates? Send a note directly to Prashanth Kumar.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = inquiryName,
                                onValueChange = { inquiryName = it },
                                label = { Text("Your Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_inquiry_name"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = inquiryPhone,
                                    onValueChange = { inquiryPhone = it },
                                    label = { Text("Mobile Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_inquiry_phone"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = inquiryEmail,
                                    onValueChange = { inquiryEmail = it },
                                    label = { Text("Email (Optional)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_inquiry_email"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Service Needed
                            var expandedService by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expandedService,
                                onExpandedChange = { expandedService = !expandedService },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = inquiryService,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Service Needed") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedService) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedService,
                                    onDismissRequest = { expandedService = false }
                                ) {
                                    serviceOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt) },
                                            onClick = {
                                                inquiryService = opt
                                                expandedService = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = inquiryMessage,
                                onValueChange = { inquiryMessage = it },
                                label = { Text("Inquiry Message / Requirement") },
                                placeholder = { Text("Describe your custom size, quantity, event date, or price question...") },
                                minLines = 3,
                                maxLines = 5,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_inquiry_message"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (inquiryName.isNotBlank() && inquiryPhone.isNotBlank() && inquiryMessage.isNotBlank()) {
                                        onSubmitInquiry(
                                            inquiryName.trim(),
                                            inquiryPhone.trim(),
                                            inquiryEmail.trim(),
                                            inquiryService,
                                            inquiryMessage.trim()
                                        )
                                        inquiryMessage = ""
                                        inquirySubmitted = true
                                    }
                                },
                                enabled = inquiryName.isNotBlank() && inquiryPhone.isNotBlank() && inquiryMessage.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_inquiry_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send Inquiry Directly", fontWeight = FontWeight.Bold)
                            }

                            if (inquirySubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = StatusReady.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusReady)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Your inquiry has been delivered! Prashanth Kumar will respond shortly.",
                                            color = StatusReady,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Customer Feedback & Reviews Screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ratings Summary Banner
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "4.9",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberSecondary
                                    )
                                )
                                StarRatingBar(rating = 5, starSize = 18)
                                Text(
                                    text = "Based on 120+ reviews",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "100% Quality Print",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = "Bibipet & Kamareddy",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showWriteReview = !showWriteReview },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showWriteReview) Icons.Default.Close else Icons.Default.RateReview,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (showWriteReview) "Cancel" else "Write a Review", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Expandable Write Review Form
                if (showWriteReview) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("write_review_card")
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Share Your Experience & Suggestions",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Help Prashanth Kumar Arts grow and provide even better services to our community.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Rating Bar
                                Text("Your Overall Rating:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                StarRatingBar(
                                    rating = reviewRating,
                                    onRatingChanged = { reviewRating = it },
                                    starSize = 28
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Service dropdown
                                var expandedRevService by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expandedRevService,
                                    onExpandedChange = { expandedRevService = !expandedRevService },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = reviewService,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Service Utilized") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRevService) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedRevService,
                                        onDismissRequest = { expandedRevService = false }
                                    ) {
                                        serviceOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text(opt) },
                                                onClick = {
                                                    reviewService = opt
                                                    expandedRevService = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = reviewLocation,
                                    onValueChange = { reviewLocation = it },
                                    label = { Text("Your Location / Village") },
                                    placeholder = { Text("e.g. Bibipet, Kamareddy, Bhiknoor") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = reviewComment,
                                    onValueChange = { reviewComment = it },
                                    label = { Text("Review, Comments & Suggestions") },
                                    placeholder = { Text("How was the print quality, Telugu fonts, delivery speed, and pricing?") },
                                    minLines = 3,
                                    maxLines = 5,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_review_comment"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (reviewComment.isNotBlank()) {
                                            onSubmitFeedback(
                                                reviewRating,
                                                reviewService,
                                                reviewComment.trim(),
                                                reviewLocation.trim()
                                            )
                                            reviewComment = ""
                                            showWriteReview = false
                                        }
                                    },
                                    enabled = reviewComment.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("submit_review_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Post Review & Feedback", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Reviews List
                items(feedbackList, key = { it.id }) { review ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feedback_card_${review.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = review.customerName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (review.isVerifiedCustomer) {
                                        Surface(
                                            color = StatusReady.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Verified Customer",
                                                color = StatusReady,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                StarRatingBar(rating = review.rating, starSize = 14)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "${review.serviceCategory} • ${review.customerLocation}",
                                    fontSize = 11.sp,
                                    color = IndigoPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = review.dateText,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = review.comment,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
