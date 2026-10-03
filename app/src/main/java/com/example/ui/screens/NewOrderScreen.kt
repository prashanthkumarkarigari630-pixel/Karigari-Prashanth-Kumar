package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    prefilledCategory: String?,
    prefilledTitle: String?,
    onOrderPlaced: (
        serviceCategory: String,
        title: String,
        specifications: String,
        dimensions: String,
        quantity: Int,
        paperType: String,
        totalPrice: Double,
        advancePaid: Double,
        payViaUpiNow: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val serviceCategories = listOf(
        "Wedding Invitations",
        "Cradle Ceremony",
        "Posters & Flex",
        "Thumbnails",
        "Visiting Cards",
        "Photo Frames"
    )

    var selectedCategory by remember { mutableStateOf(prefilledCategory ?: serviceCategories[0]) }
    var orderTitle by remember { mutableStateOf(prefilledTitle ?: "") }
    var specifications by remember { mutableStateOf("") }
    var dimensions by remember { mutableStateOf("Standard 5 x 7 in") }
    var quantityText by remember { mutableStateOf("100") }
    var selectedPaperType by remember { mutableStateOf("300 GSM Art Gloss") }
    var payViaUpiNow by remember { mutableStateOf(true) }

    // Update state if prefill changes
    LaunchedEffect(prefilledCategory, prefilledTitle) {
        if (prefilledCategory != null) selectedCategory = prefilledCategory
        if (prefilledTitle != null && orderTitle.isBlank()) orderTitle = prefilledTitle
    }

    val paperOptions = when (selectedCategory) {
        "Wedding Invitations" -> listOf("350 GSM Metallic Gold Board", "300 GSM Textured Matte", "Digital E-Invite Card (JPEG/PDF)", "Glossy Board with Envelope")
        "Cradle Ceremony" -> listOf("Glossy 300 GSM Art Card", "Soft Velvet Pastel Card", "Digital HD E-Card", "Pearl Finish Board")
        "Posters & Flex" -> listOf("Frontlit Star Flex Heavy (Outdoor)", "Normal Vinyl Flex", "Backlit Glow Sign Film", "Gloss Laminated Paper Poster")
        "Thumbnails" -> listOf("1920x1080 Full HD (Digital)", "4K Ultra HD Export", "Editable PSD / Project Files Included")
        "Visiting Cards" -> listOf("Velvet Matte Lamination", "Spot UV Gloss on Logo", "Metallic Gold Foil Accents", "Regular 300 GSM Two-Side")
        else -> listOf("Premium Photo Paper", "Matte Laminated Mount", "Acrylic Glass Frame")
    }

    // Dynamic price calculation
    val quantity = quantityText.toIntOrNull() ?: 1
    val calculatedPrice = remember(selectedCategory, quantity, selectedPaperType) {
        when (selectedCategory) {
            "Wedding Invitations" -> {
                val perCard = if (selectedPaperType.contains("Gold")) 20.0 else if (selectedPaperType.contains("Digital")) 0.0 else 15.0
                if (selectedPaperType.contains("Digital")) 499.0 else perCard * quantity.coerceAtLeast(50)
            }
            "Cradle Ceremony" -> {
                val perCard = if (selectedPaperType.contains("Velvet")) 16.0 else if (selectedPaperType.contains("Digital")) 0.0 else 13.0
                if (selectedPaperType.contains("Digital")) 399.0 else perCard * quantity.coerceAtLeast(30)
            }
            "Posters & Flex" -> {
                // e.g. 6x4 feet = 24 sq ft * 22 rs * quantity
                180.0 * quantity.coerceAtLeast(1)
            }
            "Thumbnails" -> {
                149.0 * quantity.coerceAtLeast(1)
            }
            "Visiting Cards" -> {
                // box of 500
                if (selectedPaperType.contains("Spot UV")) 550.0 else 380.0
            }
            else -> 350.0 * quantity.coerceAtLeast(1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "New Design & Print Order",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Prashanth Kumar Arts • Bibipet, Kamareddy",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            )
        },
        modifier = modifier.testTag("new_order_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Selector
            item {
                Text(
                    text = "1. Choose Service Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expandedCategory by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Service Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("dropdown_service_category"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        serviceCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expandedCategory = false
                                    selectedPaperType = paperOptions.first()
                                }
                            )
                        }
                    }
                }
            }

            // Order Title
            item {
                Text(
                    text = "2. Event / Design Title",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = orderTitle,
                    onValueChange = { orderTitle = it },
                    placeholder = {
                        Text(
                            when (selectedCategory) {
                                "Wedding Invitations" -> "e.g. Ramesh weds Swapna Kalyana Pathrika"
                                "Cradle Ceremony" -> "e.g. Baby Aaradhya Uyyala Ceremony Invitation"
                                "Posters & Flex" -> "e.g. Bibipet Sri Rama Navami 10x6 Flex Banner"
                                "Thumbnails" -> "e.g. Kamareddy Jathara Vlog HD Thumbnail"
                                else -> "e.g. Prashanth Kumar Arts Visiting Cards"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_order_title"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Event Details / Specifications
            item {
                Text(
                    text = "3. Event Details & Text Content",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Enter dates, venue, groom/bride or baby names, family names, and custom Telugu/English wording:",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = specifications,
                    onValueChange = { specifications = it },
                    placeholder = {
                        Text("• Date & Muhurtham time\n• Venue address (e.g. Bibipet / Kamareddy)\n• Contact numbers or photos to include\n• Any specific color preference")
                    },
                    minLines = 4,
                    maxLines = 7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_specifications"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Dimensions & Quantity
            item {
                Text(
                    text = "4. Dimensions & Quantity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = dimensions,
                        onValueChange = { dimensions = it },
                        label = { Text("Size / Dimensions") },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("input_dimensions"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { char -> char.isDigit() } },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("input_quantity"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Material / Paper Selection
            item {
                Text(
                    text = "5. Material & Printing Finish",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expandedPaper by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPaper,
                    onExpandedChange = { expandedPaper = !expandedPaper },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPaperType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Paper / Media Grade") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPaper) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPaper,
                        onDismissRequest = { expandedPaper = false }
                    ) {
                        paperOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedPaperType = option
                                    expandedPaper = false
                                }
                            )
                        }
                    }
                }
            }

            // Price Estimation & Payment Option Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Estimated Total Price",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Text(
                                    text = "₹${String.format(java.util.Locale.US, "%.2f", calculatedPrice)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = IndigoPrimary
                                    )
                                )
                            }

                            Surface(
                                color = AmberSecondary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Affordable Studio Rate",
                                    color = AmberSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Payment Option:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { payViaUpiNow = true }
                        ) {
                            RadioButton(
                                selected = payViaUpiNow,
                                onClick = { payViaUpiNow = true }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Pay via UPI Instant (GPay / PhonePe / QR)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Fastest processing & immediate slot reservation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { payViaUpiNow = false }
                        ) {
                            RadioButton(
                                selected = !payViaUpiNow,
                                onClick = { payViaUpiNow = false }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Pay at Bibipet Studio on Pickup", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Pay in cash or UPI when collecting printed cards/flex", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                val canSubmit = orderTitle.isNotBlank() && quantity > 0
                Button(
                    onClick = {
                        val finalTitle = orderTitle.trim()
                        val finalSpecs = if (specifications.isNotBlank()) specifications.trim() else "Custom design specifications for $finalTitle"
                        onOrderPlaced(
                            selectedCategory,
                            finalTitle,
                            finalSpecs,
                            dimensions.trim(),
                            quantity,
                            selectedPaperType,
                            calculatedPrice,
                            if (payViaUpiNow) calculatedPrice else 0.0,
                            payViaUpiNow
                        )
                    },
                    enabled = canSubmit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_order_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (payViaUpiNow) "Place Order & Pay with UPI" else "Place Order (Pay on Delivery)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
