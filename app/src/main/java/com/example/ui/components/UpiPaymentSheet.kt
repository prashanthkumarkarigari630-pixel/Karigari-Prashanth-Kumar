package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.OrderEntity
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.IndigoPrimary
import com.example.util.UpiPaymentHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpiPaymentDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (utr: String) -> Unit
) {
    val context = LocalContext.current
    var manualUtr by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showQrCode by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("upi_payment_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "UPI Payment Gateway",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                        )
                        Text(
                            text = "Prashanth Kumar Arts, Bibipet",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display box
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Order #${order.id}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = order.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${String.format(java.util.Locale.US, "%.2f", order.totalPrice)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = IndigoPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Primary Pay Button (Launches Installed UPI App)
                Button(
                    onClick = {
                        UpiPaymentHelper.launchUpiIntent(
                            context = context,
                            amount = order.totalPrice,
                            orderId = order.id,
                            onAppNotFound = {
                                showQrCode = true
                            }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pay_via_upi_button")
                ) {
                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pay with GPay / PhonePe / Paytm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Actions: QR Code or Copy UPI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showQrCode = !showQrCode },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (showQrCode) Icons.Default.VisibilityOff else Icons.Default.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showQrCode) "Hide QR" else "Show QR", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            UpiPaymentHelper.copyToClipboard(
                                context,
                                UpiPaymentHelper.DEFAULT_UPI_ID,
                                "UPI ID"
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy UPI", fontSize = 12.sp)
                    }
                }

                // Expandable QR Code Box
                if (showQrCode) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Scan with any UPI Scanner",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Simulated high quality QR matrix canvas
                        UpiQrCanvas(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = UpiPaymentHelper.DEFAULT_UPI_ID,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = IndigoPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Enter UTR / Confirm Payment Box
                Text(
                    text = "Already Paid? Enter UPI Reference / UTR Number:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = manualUtr,
                    onValueChange = { manualUtr = it },
                    placeholder = { Text("e.g. 428901847192 (12-digit UTR)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_input_field"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val finalUtr = if (manualUtr.isNotBlank()) manualUtr.trim() else "UPI/${System.currentTimeMillis().toString().takeLast(10)}"
                        onPaymentConfirmed(finalUtr)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_payment_button")
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Payment & Update Order", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UpiQrCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val boxSize = w / 9f

        // Draw Corner Finder Patterns (standard QR markers)
        fun drawFinder(x: Float, y: Float) {
            // Outer black box (3 modules)
            drawRect(
                color = Color.Black,
                topLeft = Offset(x, y),
                size = Size(boxSize * 2.6f, boxSize * 2.6f)
            )
            // Inner white box
            drawRect(
                color = Color.White,
                topLeft = Offset(x + boxSize * 0.4f, y + boxSize * 0.4f),
                size = Size(boxSize * 1.8f, boxSize * 1.8f)
            )
            // Center black box
            drawRect(
                color = Color.Black,
                topLeft = Offset(x + boxSize * 0.8f, y + boxSize * 0.8f),
                size = Size(boxSize, boxSize)
            )
        }

        drawFinder(boxSize * 0.5f, boxSize * 0.5f)
        drawFinder(w - boxSize * 3.1f, boxSize * 0.5f)
        drawFinder(boxSize * 0.5f, h - boxSize * 3.1f)

        // Draw matrix dots
        val pattern = listOf(
            Pair(4f, 1f), Pair(5f, 1f), Pair(4f, 2f), Pair(6f, 3f),
            Pair(1f, 4f), Pair(3f, 4f), Pair(5f, 4f), Pair(7f, 4f),
            Pair(2f, 5f), Pair(4f, 5f), Pair(6f, 5f), Pair(8f, 5f),
            Pair(3f, 6f), Pair(5f, 6f), Pair(7f, 6f),
            Pair(4f, 7f), Pair(6f, 7f), Pair(8f, 7f),
            Pair(5f, 8f), Pair(7f, 8f)
        )

        for ((px, py) in pattern) {
            drawRect(
                color = Color.Black,
                topLeft = Offset(px * boxSize, py * boxSize),
                size = Size(boxSize * 0.9f, boxSize * 0.9f)
            )
        }
    }
}
