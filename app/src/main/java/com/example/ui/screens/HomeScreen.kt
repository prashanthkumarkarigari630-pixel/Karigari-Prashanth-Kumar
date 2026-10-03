package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedbackEntity
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.ui.ScreenTab
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseTertiary
import com.example.util.UpiPaymentHelper

data class ServiceOffering(
    val title: String,
    val subtitle: String,
    val startingPrice: String,
    val category: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val badge: String? = null
)

@Composable
fun HomeScreen(
    activeOrders: List<OrderEntity>,
    recentFeedback: List<FeedbackEntity>,
    onNavigateTab: (ScreenTab) -> Unit,
    onStartOrder: (category: String) -> Unit,
    onSelectOrderForTracking: (orderId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val services = listOf(
        ServiceOffering(
            title = "Wedding Invitations",
            subtitle = "Kalyana Pathrikalu, Gold Foil, Peacock Art",
            startingPrice = "From ₹18 / card",
            category = "Wedding Invitations",
            icon = Icons.Default.Favorite,
            gradientColors = listOf(Color(0xFFB45309), Color(0xFFD97706)),
            badge = "Popular"
        ),
        ServiceOffering(
            title = "Cradle Ceremony Cards",
            subtitle = "Uyyala Bonalu, Barasala, Baby Name Invites",
            startingPrice = "From ₹14 / card",
            category = "Cradle Ceremony",
            icon = Icons.Default.ChildCare,
            gradientColors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)),
            badge = "Special"
        ),
        ServiceOffering(
            title = "Posters & Flex Banners",
            subtitle = "Jathara, Temple Banners, Shop Hoardings",
            startingPrice = "From ₹18 / sq.ft",
            category = "Posters & Flex",
            icon = Icons.Default.ViewCarousel,
            gradientColors = listOf(Color(0xFFB91C1C), Color(0xFFEF4444)),
            badge = "Fast Delivery"
        ),
        ServiceOffering(
            title = "YouTube & Vlog Thumbnails",
            subtitle = "High CTR, 3D Typography, Devotional & Tech",
            startingPrice = "From ₹149 / thumb",
            category = "Thumbnails",
            icon = Icons.Default.SmartDisplay,
            gradientColors = listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6))
        ),
        ServiceOffering(
            title = "Visiting & Business Cards",
            subtitle = "Velvet Matte, Spot UV, Luxury Metallic",
            startingPrice = "From ₹350 / 500 pcs",
            category = "Visiting Cards",
            icon = Icons.Default.Badge,
            gradientColors = listOf(Color(0xFF047857), Color(0xFF10B981))
        ),
        ServiceOffering(
            title = "Photo Frames & Digital Art",
            subtitle = "Laminated wooden frames & portrait cutouts",
            startingPrice = "From ₹250 / frame",
            category = "Photo Frames",
            icon = Icons.Default.CropOriginal,
            gradientColors = listOf(Color(0xFFC026D3), Color(0xFFE879F9))
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Studio Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF1E3A8A), Color(0xFF312E81))
                        )
                    )
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AmberSecondary,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "ESTD. BIBIPET, KAMAREDDY",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = AmberSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Trusted Print Studio",
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Prashanth Kumar Arts",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Creative Designs & High-Definition Printing at Affordable Prices.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFE2E8F0),
                            lineHeight = 20.sp
                        )
                    )

                    Text(
                        text = "Posters • YouTube Thumbnails • Wedding Cards • Cradle Invites • Flex Printing",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigateTab(ScreenTab.NEW_ORDER) },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_order_button")
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Order", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { onNavigateTab(ScreenTab.GALLERY) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_gallery_button")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("See Portfolio", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Quick Direct Contact Strip (Bibipet local buttons: Call, WhatsApp, Location)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call studio
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { UpiPaymentHelper.openDialer(context) }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Studio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // WhatsApp studio
                Surface(
                    color = Color(0xFF25D366).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            UpiPaymentHelper.openWhatsApp(
                                context,
                                "Hello Prashanth Kumar Arts! I want to inquire about custom design and printing services at Bibipet."
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                }

                // Location map
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { UpiPaymentHelper.openMapLocation(context) }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bibipet Map", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Active Order Tracker Card (if active orders exist)
        if (activeOrders.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Order Updates",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { onNavigateTab(ScreenTab.TRACKING) }) {
                            Text("View All (${activeOrders.size})", fontSize = 12.sp)
                        }
                    }

                    val topOrder = activeOrders.first()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOrderForTracking(topOrder.id) }
                            .testTag("home_active_order_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = topOrder.id,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                OrderStatusBadge(status = topOrder.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = topOrder.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${topOrder.serviceCategory} • Qty: ${topOrder.quantity} • ₹${topOrder.totalPrice.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            if (topOrder.adminNotes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Note: ${topOrder.adminNotes}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Services Catalog Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Our Services & Print Works",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Affordable pricing for Bibipet & Kamareddy district",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(services) { service ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onStartOrder(service.category) }
                    .testTag("service_card_${service.category.replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(service.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = service.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (service.badge != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AmberSecondary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = service.badge,
                                        color = AmberSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = service.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = service.startingPrice,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = IndigoPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Order",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Studio Features & Guarantees
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Why Choose Prashanth Kumar Arts?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val features = listOf(
                        Triple(Icons.Default.Bolt, "Same Day & Fast Delivery", "Urgent village jathara flex banners & invitation cards ready in 24 hours."),
                        Triple(Icons.Default.Translate, "Authentic Telugu Typography", "Accurate Telugu auspicious mantras, shlokas & stylish custom fonts."),
                        Triple(Icons.Default.CurrencyRupee, "Affordable & Fair Rates", "Direct studio rates with no agent commissions or hidden fees."),
                        Triple(Icons.Default.HighQuality, "High Definition Color Press", "Ultra-sharp 1440 DPI Konica digital printing with UV protection.")
                    )

                    features.forEach { (icon, title, desc) ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            }
        }

        // Customer Reviews Snippet
        if (recentFeedback.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Customer Love",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "4.9 ★ Rating from Bibipet & Kamareddy customers",
                                style = MaterialTheme.typography.bodySmall.copy(color = AmberSecondary, fontWeight = FontWeight.SemiBold)
                            )
                        }
                        TextButton(onClick = { onNavigateTab(ScreenTab.CONTACT_REVIEWS) }) {
                            Text("See All", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(recentFeedback.take(4)) { review ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.width(260.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = review.customerName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        StarRatingBar(rating = review.rating, starSize = 14)
                                    }
                                    Text(
                                        text = review.serviceCategory,
                                        fontSize = 11.sp,
                                        color = IndigoPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = review.comment,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
