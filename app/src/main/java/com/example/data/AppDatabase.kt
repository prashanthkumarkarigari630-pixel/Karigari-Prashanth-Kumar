package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        OrderEntity::class,
        GalleryItemEntity::class,
        FeedbackEntity::class,
        InquiryEntity::class,
        UserEntity::class,
        FavoriteDesignEntity::class,
        AppNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pk_arts_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    val dao = database.appDao()

                    // Seed initial showcase portfolio
                    dao.insertGalleryItems(SampleData.initialGalleryItems)

                    // Seed initial sample orders
                    for (order in SampleData.initialOrders) {
                        dao.insertOrder(order)
                    }

                    // Seed initial feedback
                    dao.insertAllFeedback(SampleData.initialFeedback)

                    // Seed default demo user and admin
                    dao.insertUser(
                        UserEntity(
                            id = 1,
                            name = "Ramesh Goud",
                            phone = "9848012345",
                            email = "ramesh.goud@example.com",
                            password = "1234",
                            address = "Beside Hanuman Temple, Bibipet, Kamareddy",
                            role = "CUSTOMER"
                        )
                    )
                    dao.insertUser(
                        UserEntity(
                            id = 2,
                            name = "Prashanth Kumar",
                            phone = "9440156789",
                            email = "karigariprashanthkumar@gmail.com",
                            password = "7788",
                            address = "Main Road Studio, Bibipet, Kamareddy",
                            role = "ADMIN"
                        )
                    )

                    // Seed initial favorite
                    dao.insertFavorite(
                        FavoriteDesignEntity(
                            userPhone = "9848012345",
                            designId = 1
                        )
                    )

                    // Seed initial notifications
                    dao.insertNotification(
                        AppNotificationEntity(
                            title = "Order Confirmed",
                            message = "Order PKA-8492 is in Designing stage. Prashanth Kumar Arts is crafting your design!",
                            type = "ORDER_UPDATE",
                            targetOrderId = "PKA-8492",
                            timestamp = System.currentTimeMillis() - 3600000L
                        )
                    )
                    dao.insertNotification(
                        AppNotificationEntity(
                            title = "Festive Season Print Discount!",
                            message = "Get 15% discount on all Wedding Invitation packages and Village Jathara flex banners at Bibipet studio.",
                            type = "PROMOTION",
                            timestamp = System.currentTimeMillis() - 86400000L
                        )
                    )
                }
            }
        }
    }
}
