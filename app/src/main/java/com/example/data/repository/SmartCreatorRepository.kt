package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.mikrotik.MikrotikApiService
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class SmartCreatorRepository(
    private val database: AppDatabase,
    val apiService: MikrotikApiService = MikrotikApiService()
) {
    val connections: Flow<List<RouterConnection>> = database.routerConnectionDao().getAllConnections()
    val profiles: Flow<List<NetworkProfile>> = database.profileDao().getAllProfiles()
    val allCards: Flow<List<VoucherCard>> = database.voucherCardDao().getAllCards()
    val templates: Flow<List<PrintTemplate>> = database.printTemplateDao().getAllTemplates()
    val reports: Flow<List<SalesReportRecord>> = database.salesReportDao().getAllReports()
    val posPoints: Flow<List<PosPoint>> = database.posPointDao().getAllPosPoints()

    fun getProfilesByType(type: NetworkType): Flow<List<NetworkProfile>> =
        database.profileDao().getProfilesByType(type)

    fun getCardsByType(type: NetworkType): Flow<List<VoucherCard>> =
        database.voucherCardDao().getCardsByType(type)

    fun searchCards(query: String): Flow<List<VoucherCard>> =
        database.voucherCardDao().searchCards(query)

    suspend fun saveConnection(connection: RouterConnection): Long {
        return database.routerConnectionDao().insertConnection(connection)
    }

    suspend fun deleteConnection(id: Long) {
        database.routerConnectionDao().deleteConnectionById(id)
    }

    suspend fun connectToRouter(
        host: String,
        port: Int,
        user: String,
        pass: String,
        useSsl: Boolean = false,
        isRest: Boolean = false
    ): Result<RouterSystemInfo> {
        return apiService.testConnection(host, port, user, pass, useSsl, isRest)
    }

    suspend fun fetchActiveSessions(): List<ActiveSession> {
        return apiService.fetchActiveUsers()
    }

    suspend fun fetchConnectedHosts(): List<ConnectedHost> {
        return apiService.fetchConnectedHosts()
    }

    suspend fun removeActiveUser(id: String): Boolean {
        return apiService.removeActiveUser(id)
    }

    suspend fun addProfile(profile: NetworkProfile): Long {
        return database.profileDao().insertProfile(profile)
    }

    suspend fun updateProfile(profile: NetworkProfile) {
        database.profileDao().updateProfile(profile)
    }

    suspend fun deleteProfile(id: Long) {
        database.profileDao().deleteProfileById(id)
    }

    suspend fun addSingleVoucher(
        username: String,
        password: String,
        profileName: String,
        type: NetworkType,
        price: Double,
        validityDays: Int,
        allowedTimeHours: Int,
        downloadLimitMb: Long,
        server: String,
        comment: String,
        boundMac: Boolean
    ): Long {
        val serialNumber = "SN-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + Random.nextInt(1000, 9999)
        val voucher = VoucherCard(
            username = username,
            password = password,
            serialNumber = serialNumber,
            profileName = profileName,
            type = type,
            price = price,
            validityDays = validityDays,
            allowedTimeHours = allowedTimeHours,
            downloadLimitMb = downloadLimitMb,
            server = server,
            comment = comment,
            boundMac = boundMac
        )
        val id = database.voucherCardDao().insertCard(voucher)

        // Sync with MikroTik RouterOS
        val uptimeLimit = if (allowedTimeHours > 0) "${allowedTimeHours}h" else ""
        apiService.createHotspotUser(
            user = username,
            pass = password,
            profile = profileName,
            server = server.ifBlank { "all" },
            comment = comment,
            limitUptime = uptimeLimit
        )

        // Record sale
        database.salesReportDao().insertReport(
            SalesReportRecord(
                dateString = "اليوم",
                profileName = profileName,
                type = type,
                cardsCount = 1,
                totalRevenue = price
            )
        )
        return id
    }

    suspend fun generateBatchVouchers(
        count: Int,
        profileName: String,
        type: NetworkType,
        userPatternOnlyDigits: Boolean,
        passPatternOnlyDigits: Boolean,
        userLength: Int,
        passLength: Int,
        prefix: String,
        suffix: String,
        price: Double,
        validityDays: Int,
        allowedTimeHours: Int,
        downloadLimitMb: Long,
        server: String,
        addValidityComment: Boolean,
        bindFirstDevice: Boolean
    ): List<VoucherCard> {
        val batchId = "BATCH-${System.currentTimeMillis()}"
        val datePrefix = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
        val generatedList = mutableListOf<VoucherCard>()

        val digitChars = "0123456789"
        val alphaChars = "23456789abcdefghjkmnpqrstuvwxyz"

        val comment = if (addValidityComment) {
            "صلاحية $validityDays يوم | وقت $allowedTimeHours ساعة"
        } else ""

        for (i in 1..count) {
            val userCore = (1..userLength)
                .map { if (userPatternOnlyDigits) digitChars.random() else alphaChars.random() }
                .joinToString("")
            val passCore = (1..passLength)
                .map { if (passPatternOnlyDigits) digitChars.random() else alphaChars.random() }
                .joinToString("")

            val fullUsername = "$prefix$userCore$suffix"
            val serial = "SN-$datePrefix-${Random.nextInt(10000, 99999)}"

            val card = VoucherCard(
                username = fullUsername,
                password = passCore,
                serialNumber = serial,
                profileName = profileName,
                type = type,
                price = price,
                validityDays = validityDays,
                allowedTimeHours = allowedTimeHours,
                downloadLimitMb = downloadLimitMb,
                server = server,
                comment = comment,
                boundMac = bindFirstDevice,
                batchId = batchId
            )
            generatedList.add(card)
        }

        database.voucherCardDao().insertCards(generatedList)

        // Push to MikroTik RouterOS
        val uptimeLimit = if (allowedTimeHours > 0) "${allowedTimeHours}h" else ""
        for (card in generatedList) {
            apiService.createHotspotUser(
                user = card.username,
                pass = card.password,
                profile = card.profileName,
                server = server.ifBlank { "all" },
                comment = card.comment,
                limitUptime = uptimeLimit
            )
        }

        // Record bulk sale
        database.salesReportDao().insertReport(
            SalesReportRecord(
                dateString = "اليوم",
                profileName = profileName,
                type = type,
                cardsCount = count,
                totalRevenue = price * count
            )
        )

        return generatedList
    }

    suspend fun deleteCard(id: Long) {
        database.voucherCardDao().deleteCardById(id)
    }

    suspend fun clearUsedCards(): Int {
        return database.voucherCardDao().deleteUsedCards()
    }

    suspend fun updatePrintTemplate(template: PrintTemplate) {
        database.printTemplateDao().updateTemplate(template)
    }

    suspend fun addPrintTemplate(template: PrintTemplate): Long {
        return database.printTemplateDao().insertTemplate(template)
    }

    suspend fun deletePrintTemplate(template: PrintTemplate) {
        database.printTemplateDao().deleteTemplate(template)
    }

    suspend fun rebootRouter(): Boolean {
        return apiService.rebootRouter()
    }

    suspend fun backupRouter(): String {
        return apiService.backupRouter()
    }

    suspend fun addPosPoint(posPoint: PosPoint): Long {
        return database.posPointDao().insert(posPoint)
    }
}
