package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object UpiPaymentHelper {

    // Default UPI ID for Prashanth Kumar Arts
    const val DEFAULT_UPI_ID = "karigariprashanthkumar@okaxis"
    const val BUSINESS_NAME = "Prashanth Kumar Arts"
    const val STUDIO_PHONE = "+919440156789"
    const val STUDIO_EMAIL = "karigariprashanthkumar@gmail.com"
    const val STUDIO_LOCATION = "Bibipet, Kamareddy District, Telangana 503125"

    fun buildUpiUri(
        upiId: String = DEFAULT_UPI_ID,
        payeeName: String = BUSINESS_NAME,
        amount: Double,
        orderId: String,
        note: String = "Order payment for $orderId"
    ): Uri {
        val formattedAmount = String.format(java.util.Locale.US, "%.2f", amount)
        return Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("mc", "")
            .appendQueryParameter("tid", "TXN_${System.currentTimeMillis()}")
            .appendQueryParameter("tr", orderId)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", formattedAmount)
            .appendQueryParameter("cu", "INR")
            .build()
    }

    fun launchUpiIntent(
        context: Context,
        amount: Double,
        orderId: String,
        onAppNotFound: () -> Unit
    ) {
        val uri = buildUpiUri(amount = amount, orderId = orderId)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val chooser = Intent.createChooser(intent, "Pay ₹${String.format(java.util.Locale.US, "%.2f", amount)} with UPI")

        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "No UPI payment app found. Please copy UPI ID to pay.", Toast.LENGTH_LONG).show()
            onAppNotFound()
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "UPI ID") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun openDialer(context: Context, phoneNumber: String = STUDIO_PHONE) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openWhatsApp(context: Context, message: String, phoneNumber: String = STUDIO_PHONE) {
        val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp is not installed on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMapLocation(context: Context, address: String = "Bibipet, Kamareddy, Telangana") {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    fun sendEmail(context: Context, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$STUDIO_EMAIL")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }
}
