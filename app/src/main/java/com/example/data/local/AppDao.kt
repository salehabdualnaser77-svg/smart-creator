package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouterConnectionDao {
    @Query("SELECT * FROM router_connections ORDER BY lastConnected DESC")
    fun getAllConnections(): Flow<List<RouterConnection>>

    @Query("SELECT * FROM router_connections WHERE isAutoLogin = 1 LIMIT 1")
    suspend fun getAutoLoginConnection(): RouterConnection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: RouterConnection): Long

    @Delete
    suspend fun deleteConnection(connection: RouterConnection)

    @Query("DELETE FROM router_connections WHERE id = :id")
    suspend fun deleteConnectionById(id: Long)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<NetworkProfile>>

    @Query("SELECT * FROM profiles WHERE type = :type ORDER BY name ASC")
    fun getProfilesByType(type: NetworkType): Flow<List<NetworkProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: NetworkProfile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<NetworkProfile>)

    @Update
    suspend fun updateProfile(profile: NetworkProfile)

    @Delete
    suspend fun deleteProfile(profile: NetworkProfile)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)
}

@Dao
interface VoucherCardDao {
    @Query("SELECT * FROM voucher_cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<VoucherCard>>

    @Query("SELECT * FROM voucher_cards WHERE type = :type ORDER BY createdAt DESC")
    fun getCardsByType(type: NetworkType): Flow<List<VoucherCard>>

    @Query("SELECT * FROM voucher_cards WHERE username LIKE '%' || :query || '%' OR serialNumber LIKE '%' || :query || '%' OR comment LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchCards(query: String): Flow<List<VoucherCard>>

    @Query("SELECT * FROM voucher_cards WHERE batchId = :batchId")
    suspend fun getCardsByBatch(batchId: String): List<VoucherCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: VoucherCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<VoucherCard>)

    @Update
    suspend fun updateCard(card: VoucherCard)

    @Delete
    suspend fun deleteCard(card: VoucherCard)

    @Query("DELETE FROM voucher_cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("DELETE FROM voucher_cards WHERE isUsed = 1")
    suspend fun deleteUsedCards(): Int

    @Query("DELETE FROM voucher_cards")
    suspend fun clearAllCards()
}

@Dao
interface PrintTemplateDao {
    @Query("SELECT * FROM print_templates ORDER BY id ASC")
    fun getAllTemplates(): Flow<List<PrintTemplate>>

    @Query("SELECT * FROM print_templates LIMIT 1")
    suspend fun getDefaultTemplate(): PrintTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: PrintTemplate): Long

    @Update
    suspend fun updateTemplate(template: PrintTemplate)

    @Delete
    suspend fun deleteTemplate(template: PrintTemplate)
}

@Dao
interface SalesReportDao {
    @Query("SELECT * FROM sales_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<SalesReportRecord>>

    @Query("SELECT * FROM sales_reports WHERE type = :type ORDER BY timestamp DESC")
    fun getReportsByType(type: NetworkType): Flow<List<SalesReportRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SalesReportRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<SalesReportRecord>)
}

@Dao
interface PosPointDao {
    @Query("SELECT * FROM pos_points ORDER BY name ASC")
    fun getAllPosPoints(): Flow<List<PosPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posPoint: PosPoint): Long

    @Update
    suspend fun update(posPoint: PosPoint)

    @Delete
    suspend fun delete(posPoint: PosPoint)
}
