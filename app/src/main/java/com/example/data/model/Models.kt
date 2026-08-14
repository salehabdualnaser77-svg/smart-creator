package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "router_connections")
data class RouterConnection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "MikroTik Router",
    val host: String = "5.5.5.5",
    val port: Int = 8728,
    val username: String = "admin",
    val password: String = "",
    val isAutoLogin: Boolean = false,
    val lastConnected: Long = System.currentTimeMillis()
)

enum class NetworkType {
    HOTSPOT,
    USER_MANAGER
}

@Entity(tableName = "profiles")
data class NetworkProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: NetworkType = NetworkType.HOTSPOT,
    val allowedTimeHours: Int = 24,
    val validityDays: Int = 30,
    val price: Double = 500.0,
    val downloadLimitMb: Long = 1024, // in MiB
    val sharedUsers: Int = 1,
    val rateLimit: String = "2M/2M",
    val server: String = "all",
    val activeUsersCount: Int = 0
)

@Entity(tableName = "voucher_cards")
data class VoucherCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val serialNumber: String,
    val profileName: String,
    val type: NetworkType = NetworkType.HOTSPOT,
    val price: Double = 500.0,
    val validityDays: Int = 30,
    val allowedTimeHours: Int = 24,
    val downloadLimitMb: Long = 1024,
    val server: String = "all",
    val comment: String = "",
    val boundMac: Boolean = false,
    val boundMacAddress: String = "",
    val isUsed: Boolean = false,
    val isPrinted: Boolean = false,
    val batchId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "print_templates")
data class PrintTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "القالب الافتراضي",
    val showProfile: Boolean = true,
    val showPassword: Boolean = true,
    val showSerial: Boolean = true,
    val showUsername: Boolean = true,
    val showPrice: Boolean = true,
    val showQrCode: Boolean = true,
    val cardsPerPage: Int = 10,
    val columnsPerPage: Int = 2,
    val usernameLength: Int = 6,
    val passwordLength: Int = 6,
    val cardFontSizeSp: Int = 14,
    val passwordFontSizeSp: Int = 14,
    val serialFontSizeSp: Int = 11,
    val primaryColorHex: String = "#1D4ED8",
    val accentColorHex: String = "#F59E0B",
    val bgPattern: String = "default"
)

data class ActiveSession(
    val id: String,
    val username: String,
    val ipAddress: String,
    val macAddress: String,
    val uptime: String,
    val bytesInFormatted: String,
    val bytesOutFormatted: String,
    val timeLeft: String = "غير محدد",
    val loginBy: String = "http-pap",
    val server: String = "hotspot1"
)

data class ConnectedHost(
    val id: String,
    val macAddress: String,
    val ipAddress: String,
    val toIp: String = "",
    val server: String = "hotspot1",
    val authorized: Boolean = false,
    val bypassed: Boolean = false,
    val comment: String = ""
)

@Entity(tableName = "sales_reports")
data class SalesReportRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val profileName: String,
    val type: NetworkType,
    val cardsCount: Int,
    val totalRevenue: Double
)

@Entity(tableName = "pos_points")
data class PosPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val location: String,
    val balance: Double = 0.0,
    val cardsAssigned: Int = 0
)

data class RouterSystemInfo(
    val routerModel: String = "MikroTik RB750Gr3 (hEX)",
    val routerOsVersion: String = "RouterOS v7.14.3",
    val uptime: String = "14d 06:32:18",
    val cpuLoad: Int = 18,
    val freeMemoryMb: Int = 186,
    val totalMemoryMb: Int = 256,
    val freeHddMb: Int = 12,
    val totalHddMb: Int = 16,
    val totalActiveUsers: Int = 52,
    val totalHosts: Int = 89,
    val totalCards: Int = 340
)
