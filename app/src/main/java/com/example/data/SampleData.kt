package com.example.data

object SampleData {

    val initialGalleryItems = listOf(
        GalleryItemEntity(
            id = 1,
            title = "Royal Peacock Telugu Kalyana Pathrika",
            category = "Wedding Invitations",
            description = "Intricate traditional Telugu wedding invitation card with golden peacock motifs, Ganesha blessing crest, and premium foil finish.",
            startingPrice = 18.0,
            dimensions = "5 x 7 inches (Double Fold)",
            tag = "Bestseller",
            rating = 4.9f,
            accentHex = "#D97706",
            sampleStyle = "Gold Foil & Saffron Border",
            isFeatured = true
        ),
        GalleryItemEntity(
            id = 2,
            title = "Little Prince Cradle Ceremony Card",
            category = "Cradle Ceremony",
            description = "Enchanting baby boy / girl cradle ceremony (Barasala / Uyyala) card featuring cute wooden cradle art and floral garland vectors.",
            startingPrice = 14.0,
            dimensions = "4 x 6 inches (Single Card)",
            tag = "Trending",
            rating = 4.8f,
            accentHex = "#2563EB",
            sampleStyle = "Pastel Sky & Golden Cradle",
            isFeatured = true
        ),
        GalleryItemEntity(
            id = 3,
            title = "Bibipet Jathara & Festival Flex Hoarding",
            category = "Posters & Flex",
            description = "High-definition weatherproof vinyl flex banner designed for village temple jathara, cultural gatherings, and VIP stage backgrounds.",
            startingPrice = 280.0,
            dimensions = "10 x 6 feet (Star Flex)",
            tag = "Outdoor Print",
            rating = 5.0f,
            accentHex = "#DC2626",
            sampleStyle = "Vibrant Red & Devotional Sunburst",
            isFeatured = true
        ),
        GalleryItemEntity(
            id = 4,
            title = "Viral Devotional & Telugu Vlog Thumbnail",
            category = "Thumbnails",
            description = "High-CTR 1920x1080 YouTube thumbnail with bold typography, 3D text glow, and professional color grading for high click-through rate.",
            startingPrice = 149.0,
            dimensions = "1920 x 1080 px (16:9)",
            tag = "Digital Only",
            rating = 4.9f,
            accentHex = "#7C3AED",
            sampleStyle = "High CTR Bold 3D Type",
            isFeatured = true
        ),
        GalleryItemEntity(
            id = 5,
            title = "Velvet Matte Spot UV Visiting Cards",
            category = "Visiting Cards",
            description = "Ultra-premium business cards with soft-touch velvet lamination and raised spot gloss on logo for entrepreneurs and shops.",
            startingPrice = 350.0,
            dimensions = "3.5 x 2 inches (Box of 500)",
            tag = "Business",
            rating = 4.9f,
            accentHex = "#059669",
            sampleStyle = "Matte Black & Emerald Foil",
            isFeatured = false
        ),
        GalleryItemEntity(
            id = 6,
            title = "Birthday Celebration Photo Flex Banner",
            category = "Posters & Flex",
            description = "Customized birthday flex backdrop banner with high-resolution photo cutout, balloons, and customized Telugu/English wishes.",
            startingPrice = 220.0,
            dimensions = "6 x 4 feet",
            tag = "Celebration",
            rating = 4.7f,
            accentHex = "#DB2777",
            sampleStyle = "Festive Confetti & Neon",
            isFeatured = false
        ),
        GalleryItemEntity(
            id = 7,
            title = "Sacred Golden Ganesha Wedding Invite",
            category = "Wedding Invitations",
            description = "Classic red-saffron and gold themed traditional wedding invite card with Sanskrit shlokas and auspicious kalash art.",
            startingPrice = 22.0,
            dimensions = "7 x 5 inches (Enclosed Box)",
            tag = "Heritage",
            rating = 5.0f,
            accentHex = "#B45309",
            sampleStyle = "Deep Crimson & Gold Foil",
            isFeatured = false
        )
    )

    val initialOrders = listOf(
        OrderEntity(
            id = "PKA-8492",
            customerName = "Ramesh Goud",
            customerPhone = "9848012345",
            customerEmail = "ramesh.goud@example.com",
            serviceCategory = "Wedding Invitations",
            title = "Ramesh & Swapna Kalyana Pathrika",
            specifications = "Event Date: 24th Nov 2026. Venue: Sri Venkateshwara Gardens, Kamareddy. Double side print in Telugu with auspicious mantras.",
            dimensions = "5 x 7 inches",
            quantity = 250,
            paperType = "350 GSM Metallic Gold Board",
            totalPrice = 4500.0,
            advancePaid = 2000.0,
            status = OrderStatus.DESIGNING,
            paymentStatus = PaymentStatus.PAID_UPI,
            upiUtr = "UPI/628193810294",
            createdAt = System.currentTimeMillis() - 86400000L * 1, // 1 day ago
            estimatedDeliveryDays = 2,
            adminNotes = "Draft 1 created. Checking Telugu spellings with client.",
            proofPreviewUrl = "https://example.com/proofs/pka-8492.jpg"
        ),
        OrderEntity(
            id = "PKA-7821",
            customerName = "Anil Kumar",
            customerPhone = "9440156789",
            customerEmail = "anil.kumar@example.com",
            serviceCategory = "Posters & Flex",
            title = "Hanuman Temple Festival Flex Banner",
            specifications = "Size 8x4 feet. Star Flex with eyelets on all 4 corners. Venue: Main Road, Bibipet.",
            dimensions = "8 x 4 feet",
            quantity = 2,
            paperType = "Frontlit Heavy Star Flex",
            totalPrice = 1200.0,
            advancePaid = 1200.0,
            status = OrderStatus.PRINTING,
            paymentStatus = PaymentStatus.PAID_UPI,
            upiUtr = "UPI/629014523910",
            createdAt = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
            estimatedDeliveryDays = 1,
            adminNotes = "Printed on high resolution Konica machine. Finishing eyelets.",
            proofPreviewUrl = "https://example.com/proofs/pka-7821.jpg"
        ),
        OrderEntity(
            id = "PKA-6104",
            customerName = "Sneha Reddy",
            customerPhone = "9989011223",
            customerEmail = "sneha.reddy@example.com",
            serviceCategory = "Cradle Ceremony",
            title = "Aadya Cradle Ceremony Invitation Cards",
            specifications = "Baby girl Uyyala ceremony. Pastel floral theme with photo of baby. Date: 15th Oct 2026.",
            dimensions = "4 x 6 inches",
            quantity = 100,
            paperType = "Glossy 300 GSM Art Card",
            totalPrice = 1500.0,
            advancePaid = 500.0,
            status = OrderStatus.READY_FOR_PICKUP,
            paymentStatus = PaymentStatus.PAID_UPI,
            upiUtr = "UPI/610492817263",
            createdAt = System.currentTimeMillis() - 86400000L * 3,
            estimatedDeliveryDays = 0,
            adminNotes = "Packed in protective box. Ready at Bibipet studio counter.",
            proofPreviewUrl = "https://example.com/proofs/pka-6104.jpg"
        )
    )

    val initialFeedback = listOf(
        FeedbackEntity(
            id = 1,
            customerName = "Srikanth Rao",
            customerLocation = "Kamareddy Town",
            rating = 5,
            serviceCategory = "Wedding Invitations",
            comment = "Prashanth Kumar designed our wedding card with great patience. The Telugu fonts and gold foil prints came out phenomenal! Highly recommend to everyone in Kamareddy district.",
            dateText = "Yesterday"
        ),
        FeedbackEntity(
            id = 2,
            customerName = "Mahesh Chary",
            customerLocation = "Bibipet",
            rating = 5,
            serviceCategory = "Posters & Flex",
            comment = "Super fast printing! We ordered 3 flex banners for our village festival at Bibipet and got delivery on the same day. Affordable pricing and sharp colors.",
            dateText = "3 days ago"
        ),
        FeedbackEntity(
            id = 3,
            customerName = "Pooja Varma",
            customerLocation = "Bhiknoor / Kamareddy",
            rating = 5,
            serviceCategory = "Cradle Ceremony",
            comment = "The baby cradle invitation card was so cute and creative! Our family and relatives loved the design. Prashanth brother gave very friendly support.",
            dateText = "Last week"
        ),
        FeedbackEntity(
            id = 4,
            customerName = "Kiran Media Vlogs",
            customerLocation = "Online Client",
            rating = 5,
            serviceCategory = "Thumbnails",
            comment = "Got YouTube thumbnails designed for my channel. CTR jumped from 4% to 11%! Best graphic designer in Telangana.",
            dateText = "2 weeks ago"
        )
    )
}
