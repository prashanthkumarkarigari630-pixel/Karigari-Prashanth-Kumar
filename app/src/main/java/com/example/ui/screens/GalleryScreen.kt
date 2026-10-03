package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.window.Dialog
import com.example.data.GalleryItemEntity
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    items: List<GalleryItemEntity>,
    isAdmin: Boolean,
    onOrderSimilar: (category: String, title: String) -> Unit,
    onAddNewItem: () -> Unit,
    onDeleteItem: (GalleryItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Wedding Invitations", "Cradle Ceremony", "Posters & Flex", "Thumbnails", "Visiting Cards")
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedItemForDetail by remember { mutableStateOf<GalleryItemEntity?>(null) }

    val filteredItems = remember(items, selectedCategory) {
        if (selectedCategory == "All") items else items.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Previous Work Showcase",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Crafted by Prashanth Kumar Arts, Bibipet",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    if (isAdmin) {
                        FilledTonalButton(
                            onClick = onAddNewItem,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = IndigoPrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Work", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 12.sp) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndigoPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("gallery_screen")
    ) { padding ->
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No designs found in this category",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    GalleryCard(
                        item = item,
                        isAdmin = isAdmin,
                        onClick = { selectedItemForDetail = item },
                        onOrderSimilar = { onOrderSimilar(item.category, item.title) },
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
        }
    }

    // Detail Modal Dialog
    if (selectedItemForDetail != null) {
        val item = selectedItemForDetail!!
        Dialog(onDismissRequest = { selectedItemForDetail = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Artwork visual
                    GalleryArtworkVisual(
                        category = item.category,
                        accentHex = item.accentHex,
                        title = item.title,
                        styleText = item.sampleStyle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = IndigoPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.category,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        StarRatingBar(rating = item.rating.toInt(), starSize = 15)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Specs row
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Standard Size", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(item.dimensions, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            Column {
                                Text("Starting Price", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${item.startingPrice}", fontWeight = FontWeight.Bold, color = IndigoPrimary, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            selectedItemForDetail = null
                            onOrderSimilar(item.category, item.title)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Brush, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Order This Design Style", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryCard(
    item: GalleryItemEntity,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onOrderSimilar: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("gallery_card_${item.id}")
    ) {
        Column {
            // Artwork Visual Top
            GalleryArtworkVisual(
                category = item.category,
                accentHex = item.accentHex,
                title = item.title,
                styleText = item.sampleStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AmberSecondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.tag,
                                color = AmberSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.dimensions,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "From ₹${item.startingPrice.toInt()}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = IndigoPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOrderSimilar,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Order Similar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (isAdmin) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseTertiary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryArtworkVisual(
    category: String,
    accentHex: String,
    title: String,
    styleText: String,
    modifier: Modifier = Modifier
) {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(accentHex))
    } catch (_: Exception) {
        IndigoPrimary
    }

    val icon: ImageVector = when (category) {
        "Wedding Invitations" -> Icons.Default.Favorite
        "Cradle Ceremony" -> Icons.Default.ChildCare
        "Posters & Flex" -> Icons.Default.ViewCarousel
        "Thumbnails" -> Icons.Default.SmartDisplay
        "Visiting Cards" -> Icons.Default.Badge
        else -> Icons.Default.Brush
    }

    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    listOf(baseColor, baseColor.copy(alpha = 0.75f), Color(0xFF0F172A))
                )
            )
            .padding(14.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        // Decorative background patterns
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.4f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.2f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = styleText,
                        color = Color(0xFFFDE68A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
