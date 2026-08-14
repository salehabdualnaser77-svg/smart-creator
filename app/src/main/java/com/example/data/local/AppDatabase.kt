package com.example.data.local

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromNetworkType(type: NetworkType): String = type.name

    @TypeConverter
    fun toNetworkType(value: String): NetworkType = try {
        NetworkType.valueOf(value)
    } catch (e: Exception) {
        NetworkType.HOTSPOT
    }
}

@Database(
    entities = [
        RouterConnection::class,
        NetworkProfile::class,
        VoucherCard::class,
        PrintTemplate::class,
        SalesReportRecord::class,
        PosPoint::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routerConnectionDao(): RouterConnectionDao
    abstract fun profileDao(): ProfileDao
    abstract fun voucherCardDao(): VoucherCardDao
    abstract fun printTemplateDao(): PrintTemplateDao
    abstract fun salesReportDao(): SalesReportDao
    abstract fun posPointDao(): PosPointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_creator_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Default Router connection
            val connDao = database.routerConnectionDao()
            connDao.insertConnection(
                RouterConnection(
                    name = "ميكروتك الشبكة الرئيسية",
                    host = "5.5.5.5",
                    port = 8728,
                    username = "admin",
                    password = "",
                    isAutoLogin = false
                )
            )

            // Default Profiles
            val profileDao = database.profileDao()
            val initialProfiles = listOf(
                NetworkProfile(
                    name = "1_Hour_500MB",
                    type = NetworkType.HOTSPOT,
                    allowedTimeHours = 1,
                    validityDays = 1,
                    price = 100.0,
                    downloadLimitMb = 500,
                    sharedUsers = 1,
                    rateLimit = "2M/2M",
                    activeUsersCount = 14
                ),
                NetworkProfile(
                    name = "3_Hours_1GB",
                    type = NetworkType.HOTSPOT,
                    allowedTimeHours = 3,
                    validityDays = 2,
                    price = 200.0,
                    downloadLimitMb = 1024,
                    sharedUsers = 1,
                    rateLimit = "3M/3M",
                    activeUsersCount = 22
                ),
                NetworkProfile(
                    name = "Daily_2GB",
                    type = NetworkType.HOTSPOT,
                    allowedTimeHours = 24,
                    validityDays = 1,
                    price = 300.0,
                    downloadLimitMb = 2048,
                    sharedUsers = 1,
                    rateLimit = "4M/4M",
                    activeUsersCount = 16
                ),
                NetworkProfile(
                    name = "Weekly_7GB",
                    type = NetworkType.HOTSPOT,
                    allowedTimeHours = 168,
                    validityDays = 7,
                    price = 1000.0,
                    downloadLimitMb = 7168,
                    sharedUsers = 1,
                    rateLimit = "5M/5M",
                    activeUsersCount = 8
                ),
                NetworkProfile(
                    name = "Monthly_VIP_30GB",
                    type = NetworkType.USER_MANAGER,
                    allowedTimeHours = 720,
                    validityDays = 30,
                    price = 3500.0,
                    downloadLimitMb = 30720,
                    sharedUsers = 2,
                    rateLimit = "10M/10M",
                    activeUsersCount = 19
                ),
                NetworkProfile(
                    name = "Gaming_NoLimit_3H",
                    type = NetworkType.USER_MANAGER,
                    allowedTimeHours = 3,
                    validityDays = 1,
                    price = 500.0,
                    downloadLimitMb = 0,
                    sharedUsers = 1,
                    rateLimit = "15M/15M",
                    activeUsersCount = 7
                )
            )
            profileDao.insertAll(initialProfiles)

            // Default Print Template
            val templateDao = database.printTemplateDao()
            templateDao.insertTemplate(
                PrintTemplate(
                    name = "القالب الأساسي الملون",
                    showProfile = true,
                    showPassword = true,
                    showSerial = true,
                    showUsername = true,
                    showPrice = true,
                    showQrCode = true,
                    cardsPerPage = 10,
                    columnsPerPage = 2,
                    usernameLength = 6,
                    passwordLength = 6,
                    cardFontSizeSp = 13,
                    passwordFontSizeSp = 14,
                    serialFontSizeSp = 10,
                    primaryColorHex = "#1D4ED8",
                    accentColorHex = "#F59E0B"
                )
            )

            // Seed initial sample vouchers
            val voucherDao = database.voucherCardDao()
            val vouchers = listOf(
                VoucherCard(
                    username = "748921",
                    password = "748",
                    serialNumber = "SN-2026-001",
                    profileName = "3_Hours_1GB",
                    type = NetworkType.HOTSPOT,
                    price = 200.0,
                    validityDays = 2,
                    allowedTimeHours = 3,
                    downloadLimitMb = 1024,
                    comment = "صالح ليومين"
                ),
                VoucherCard(
                    username = "893412",
                    password = "893",
                    serialNumber = "SN-2026-002",
                    profileName = "Daily_2GB",
                    type = NetworkType.HOTSPOT,
                    price = 300.0,
                    validityDays = 1,
                    allowedTimeHours = 24,
                    downloadLimitMb = 2048,
                    comment = "صالح 24 ساعة"
                ),
                VoucherCard(
                    username = "vip_saleh",
                    password = "pass9921",
                    serialNumber = "SN-VIP-099",
                    profileName = "Monthly_VIP_30GB",
                    type = NetworkType.USER_MANAGER,
                    price = 3500.0,
                    validityDays = 30,
                    allowedTimeHours = 720,
                    downloadLimitMb = 30720,
                    comment = "عميل مميز - صالح 30 يوم"
                )
            )
            voucherDao.insertCards(vouchers)

            // Seed initial sales reports
            val salesDao = database.salesReportDao()
            salesDao.insertAll(
                listOf(
                    SalesReportRecord(
                        dateString = "اليوم",
                        profileName = "Daily_2GB",
                        type = NetworkType.HOTSPOT,
                        cardsCount = 45,
                        totalRevenue = 13500.0
                    ),
                    SalesReportRecord(
                        dateString = "اليوم",
                        profileName = "3_Hours_1GB",
                        type = NetworkType.HOTSPOT,
                        cardsCount = 80,
                        totalRevenue = 16000.0
                    ),
                    SalesReportRecord(
                        dateString = "أمس",
                        profileName = "Weekly_7GB",
                        type = NetworkType.HOTSPOT,
                        cardsCount = 12,
                        totalRevenue = 12000.0
                    ),
                    SalesReportRecord(
                        dateString = "هذا الأسبوع",
                        profileName = "Monthly_VIP_30GB",
                        type = NetworkType.USER_MANAGER,
                        cardsCount = 15,
                        totalRevenue = 52500.0
                    )
                )
            )

            // Seed POS points
            val posDao = database.posPointDao()
            posDao.insert(
                PosPoint(
                    name = "بقالة الأمل",
                    phone = "777123456",
                    location = "الشارع العام",
                    balance = 4500.0,
                    cardsAssigned = 50
                )
            )
            posDao.insert(
                PosPoint(
                    name = "كافيه النور",
                    phone = "771987654",
                    location = "بجانب الجامعة",
                    balance = 8200.0,
                    cardsAssigned = 100
                )
            )
        }
    }
}
