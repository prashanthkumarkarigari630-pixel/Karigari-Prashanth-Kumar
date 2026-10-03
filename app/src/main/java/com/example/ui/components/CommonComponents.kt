package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderStatus
import com.example.ui.ScreenTab
import com.example.ui.UserSession
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PKArtsTopAppBar(
    userSession: UserSession,
    activeOrdersCount: Int,
    onAdminClick: () -> Unit,
    onCustomerProfileClick: () -> Unit,
    onOpenTracking: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(IndigoPrimary, AmberSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Studio Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Prashanth Kumar Arts",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = AmberSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Bibipet, Kamareddy",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (userSession.isAdmin) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = RoseTertiary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    color = RoseTertiary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        actions = {
            // Tracking / Orders icon with badge
            IconButton(
                onClick = onOpenTracking,
                modifier = Modifier.testTag("action_tracking_button")
            ) {
                BadgedBox(
                    badge = {
                        if (activeOrdersCount > 0) {
                            Badge(
                                containerColor = AmberSecondary,
                                contentColor = Color.White
                            ) {
                                Text("$activeOrdersCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalShipping,
                        contentDescription = "Track Orders"
                    )
                }
            }

            // User / Admin Profile Button
            IconButton(
                onClick = {
                    if (userSession.isAdmin) {
                        onAdminClick()
                    } else {
                        onCustomerProfileClick()
                    }
                },
                modifier = Modifier.testTag("action_profile_button")
            ) {
                if (userSession.isAdmin) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Mode",
                        tint = RoseTertiary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Customer Account"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Composable
fun PKArtsBottomNavBar(
    currentTab: ScreenTab,
    isAdmin: Boolean,
    onTabSelected: (ScreenTab) -> Unit,
    activeOrdersCount: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == ScreenTab.HOME,
            onClick = { onTabSelected(ScreenTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.GALLERY,
            onClick = { onTabSelected(ScreenTab.GALLERY) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.GALLERY) Icons.Filled.PhotoLibrary else Icons.Outlined.PhotoLibrary,
                    contentDescription = "Gallery"
                )
            },
            label = { Text("Gallery", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_gallery")
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.NEW_ORDER,
            onClick = { onTabSelected(ScreenTab.NEW_ORDER) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.NEW_ORDER) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                    contentDescription = "New Order"
                )
            },
            label = { Text("New Order", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_new_order")
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.TRACKING,
            onClick = { onTabSelected(ScreenTab.TRACKING) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeOrdersCount > 0) {
                            Badge(
                                containerColor = AmberSecondary,
                                contentColor = Color.White
                            ) {
                                Text("$activeOrdersCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == ScreenTab.TRACKING) Icons.Filled.TrackChanges else Icons.Outlined.TrackChanges,
                        contentDescription = "Tracking"
                    )
                }
            },
            label = { Text("Track", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_track")
        )

        if (isAdmin) {
            NavigationBarItem(
                selected = currentTab == ScreenTab.ADMIN_PANEL,
                onClick = { onTabSelected(ScreenTab.ADMIN_PANEL) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == ScreenTab.ADMIN_PANEL) Icons.Filled.DashboardCustomize else Icons.Outlined.DashboardCustomize,
                        contentDescription = "Admin"
                    )
                },
                label = { Text("Admin", fontSize = 11.sp) },
                modifier = Modifier.testTag("nav_admin")
            )
        } else {
            NavigationBarItem(
                selected = currentTab == ScreenTab.CONTACT_REVIEWS,
                onClick = { onTabSelected(ScreenTab.CONTACT_REVIEWS) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == ScreenTab.CONTACT_REVIEWS) Icons.Filled.Reviews else Icons.Outlined.RateReview,
                        contentDescription = "Reviews"
                    )
                },
                label = { Text("Contact", fontSize = 11.sp) },
                modifier = Modifier.testTag("nav_contact")
            )
        }
    }
}

@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        OrderStatus.PENDING -> Triple(StatusPending.copy(alpha = 0.18f), StatusPending, Icons.Default.HourglassTop)
        OrderStatus.DESIGNING -> Triple(StatusDesigning.copy(alpha = 0.18f), StatusDesigning, Icons.Default.Draw)
        OrderStatus.PROOF_READY -> Triple(StatusProof.copy(alpha = 0.18f), StatusProof, Icons.Default.Preview)
        OrderStatus.PRINTING -> Triple(StatusPrinting.copy(alpha = 0.18f), StatusPrinting, Icons.Default.Print)
        OrderStatus.READY_FOR_PICKUP -> Triple(StatusReady.copy(alpha = 0.18f), StatusReady, Icons.Default.CheckCircle)
        OrderStatus.DELIVERED -> Triple(StatusDelivered.copy(alpha = 0.18f), StatusDelivered, Icons.Default.DoneAll)
        OrderStatus.CANCELLED -> Triple(StatusCancelled.copy(alpha = 0.18f), StatusCancelled, Icons.Default.Cancel)
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, Icons.Default.Info)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun OrderTrackingTimeline(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        Pair(OrderStatus.PENDING, "Order Received"),
        Pair(OrderStatus.DESIGNING, "Designing Art"),
        Pair(OrderStatus.PROOF_READY, "Proof Ready"),
        Pair(OrderStatus.PRINTING, "Print & Finish"),
        Pair(OrderStatus.READY_FOR_PICKUP, "Ready for Pickup"),
        Pair(OrderStatus.DELIVERED, "Completed")
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
        steps.forEachIndexed { index, (statusKey, stepLabel) ->
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
                // Indicator column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(stepColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone && !isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
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
                                .height(32.dp)
                                .background(
                                    if (currentIndex > index) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Label & Description
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)
                ) {
                    Text(
                        text = stepLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) AmberSecondary else if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = getStepDescription(statusKey),
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

private fun getStepDescription(status: String): String {
    return when (status) {
        OrderStatus.PENDING -> "Inquiry recorded, design team verifying specs"
        OrderStatus.DESIGNING -> "Artistic typography, imagery & layout in progress"
        OrderStatus.PROOF_READY -> "Preview sample ready for client approval"
        OrderStatus.PRINTING -> "Sent to Konica Minolta high-res digital/flex press"
        OrderStatus.READY_FOR_PICKUP -> "Packed and ready at Bibipet studio counter"
        OrderStatus.DELIVERED -> "Handed over to customer with satisfaction"
        else -> ""
    }
}

@Composable
fun StarRatingBar(
    rating: Int,
    maxRating: Int = 5,
    onRatingChanged: ((Int) -> Unit)? = null,
    starSize: Int = 20,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        for (i in 1..maxRating) {
            val isFilled = i <= rating
            val icon = if (isFilled) Icons.Filled.Star else Icons.Outlined.StarOutline
            val tint = if (isFilled) AmberSecondary else MaterialTheme.colorScheme.surfaceVariant

            Icon(
                imageVector = icon,
                contentDescription = "$i Stars",
                tint = tint,
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable(enabled = onRatingChanged != null) {
                        onRatingChanged?.invoke(i)
                    }
            )
        }
    }
}
