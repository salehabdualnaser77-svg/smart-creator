package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MikrotikConnectionConfig
import com.example.data.local.MikrotikDataStore
import com.example.data.model.*
import com.example.data.repository.SmartCreatorRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    MAIN_DASHBOARD,
    HOTSPOT_MENU,
    USER_MANAGER_MENU,
    ADD_SINGLE_CARD,
    ADD_BATCH_CARDS,
    ACTIVE_HOSTS,
    PROFILES_MANAGER,
    TEMPLATE_EDITOR,
    CARDS_LIST,
    SETTINGS,
    POS_POINTS
}

data class UiNotification(
    val message: String,
    val isError: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SmartCreatorRepository
    private val dataStore: MikrotikDataStore = MikrotikDataStore(application)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = SmartCreatorRepository(database)
    }

    // DataStore Connection Preferences Flow
    val connectionConfig: StateFlow<MikrotikConnectionConfig> = dataStore.connectionConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MikrotikConnectionConfig())

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentNetworkType = MutableStateFlow(NetworkType.HOTSPOT)
    val currentNetworkType: StateFlow<NetworkType> = _currentNetworkType.asStateFlow()

    // Connection State
    private val _systemInfo = MutableStateFlow<RouterSystemInfo?>(null)
    val systemInfo: StateFlow<RouterSystemInfo?> = _systemInfo.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _isConnectedToRouter = MutableStateFlow(true)
    val isConnectedToRouter: StateFlow<Boolean> = _isConnectedToRouter.asStateFlow()

    private val _activeSessions = MutableStateFlow<List<ActiveSession>>(emptyList())
    val activeSessions: StateFlow<List<ActiveSession>> = _activeSessions.asStateFlow()

    private val _connectedHosts = MutableStateFlow<List<ConnectedHost>>(emptyList())
    val connectedHosts: StateFlow<List<ConnectedHost>> = _connectedHosts.asStateFlow()

    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    // Reports Picker Dialog State
    private val _showReportsDialog = MutableStateFlow(false)
    val showReportsDialog: StateFlow<Boolean> = _showReportsDialog.asStateFlow()

    private val _selectedReportType = MutableStateFlow(NetworkType.HOTSPOT)
    val selectedReportType: StateFlow<NetworkType> = _selectedReportType.asStateFlow()

    // Print Preview State
    private val _previewCards = MutableStateFlow<List<VoucherCard>>(emptyList())
    val previewCards: StateFlow<List<VoucherCard>> = _previewCards.asStateFlow()

    private val _showPrintPreviewDialog = MutableStateFlow(false)
    val showPrintPreviewDialog: StateFlow<Boolean> = _showPrintPreviewDialog.asStateFlow()

    // Search query for cards list
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Reactive DB flows
    val savedConnections: StateFlow<List<RouterConnection>> = repository.connections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profiles: StateFlow<List<NetworkProfile>> = repository.profiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCards: StateFlow<List<VoucherCard>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allCards
            else repository.searchCards(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: StateFlow<List<PrintTemplate>> = repository.templates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salesReports: StateFlow<List<SalesReportRecord>> = repository.reports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posPoints: StateFlow<List<PosPoint>> = repository.posPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: AppScreen, networkType: NetworkType? = null) {
        if (networkType != null) {
            _currentNetworkType.value = networkType
        }
        _currentScreen.value = screen
    }

    fun setReportsDialogVisible(visible: Boolean, type: NetworkType = NetworkType.HOTSPOT) {
        _selectedReportType.value = type
        _showReportsDialog.value = visible
    }

    fun setPrintPreviewVisible(visible: Boolean, cards: List<VoucherCard> = emptyList()) {
        _previewCards.value = cards
        _showPrintPreviewDialog.value = visible
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearNotification() {
        _notification.value = null
    }

    fun showNotification(msg: String, isError: Boolean = false) {
        _notification.value = UiNotification(msg, isError)
    }

    fun connectRouter(
        host: String,
        port: Int,
        user: String,
        pass: String,
        saveConnection: Boolean,
        isAutoLogin: Boolean,
        useSsl: Boolean = false,
        isRest: Boolean = false
    ) {
        viewModelScope.launch {
            _isConnecting.value = true
            val result = repository.connectToRouter(host, port, user, pass, useSsl, isRest)
            _isConnecting.value = false

            result.onSuccess { info ->
                _systemInfo.value = info
                _isConnectedToRouter.value = true
                if (saveConnection) {
                    repository.saveConnection(
                        RouterConnection(
                            name = "ميكروتك $host",
                            host = host,
                            port = port,
                            username = user,
                            password = pass,
                            isAutoLogin = isAutoLogin
                        )
                    )
                    dataStore.saveConnectionConfig(
                        MikrotikConnectionConfig(
                            host = host,
                            port = port,
                            username = user,
                            password = pass,
                            autoLogin = isAutoLogin,
                            useSsl = useSsl,
                            isRest = isRest,
                            lastConnected = System.currentTimeMillis()
                        )
                    )
                } else {
                    dataStore.recordConnectionSuccess()
                }
                refreshActiveData()
                _currentScreen.value = AppScreen.MAIN_DASHBOARD
                showNotification("تم الاتصال بالمايكروتك بنجاح")
            }.onFailure { err ->
                _isConnectedToRouter.value = false
                showNotification("فشل الاتصال: ${err.localizedMessage}", isError = true)
            }
        }
    }

    fun saveConnectionToDataStore(
        host: String,
        port: Int,
        user: String,
        pass: String,
        autoLogin: Boolean = false,
        useSsl: Boolean = false,
        isRest: Boolean = false
    ) {
        viewModelScope.launch {
            dataStore.updateCredentials(
                host = host.trim(),
                port = port,
                username = user.trim(),
                password = pass,
                autoLogin = autoLogin,
                useSsl = useSsl,
                isRest = isRest
            )
            repository.saveConnection(
                RouterConnection(
                    name = "ميكروتك ${host.trim()}",
                    host = host.trim(),
                    port = port,
                    username = user.trim(),
                    password = pass,
                    isAutoLogin = autoLogin
                )
            )
            showNotification("تم حفظ إعدادات المايكروتك محلياً في DataStore بنجاح")
        }
    }

    fun clearDataStoreCredentials() {
        viewModelScope.launch {
            dataStore.clearCredentials()
            showNotification("تم مسح بيانات الاتصال المخزنة في DataStore")
        }
    }

    fun testConnection(
        host: String,
        port: Int,
        user: String,
        pass: String,
        useSsl: Boolean = false,
        isRest: Boolean = false,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isConnecting.value = true
            val result = repository.connectToRouter(host, port, user, pass, useSsl, isRest)
            _isConnecting.value = false

            result.onSuccess { info ->
                _systemInfo.value = info
                onComplete(true, "تم الاتصال بنجاح! الموديل: ${info.routerModel} | المعالج: ${info.cpuLoad}%")
            }.onFailure { err ->
                onComplete(false, "تعذر الاتصال بالمايكروتك: ${err.localizedMessage ?: "تأكد من IP والمنفذ"}")
            }
        }
    }

    fun refreshActiveData() {
        viewModelScope.launch {
            try {
                val sessions = repository.fetchActiveSessions()
                val hosts = repository.fetchConnectedHosts()
                _activeSessions.value = sessions
                _connectedHosts.value = hosts
                _systemInfo.value = _systemInfo.value?.copy(
                    totalActiveUsers = sessions.size,
                    totalHosts = hosts.size
                )
            } catch (e: Exception) {
                showNotification("خطأ أثناء جلب البيانات: ${e.message}", isError = true)
            }
        }
    }

    fun disconnectUser(session: ActiveSession) {
        viewModelScope.launch {
            _activeSessions.value = _activeSessions.value.filter { it.id != session.id }
            showNotification("تم فصل المستخدم ${session.username} بنجاح")
        }
    }

    fun addSingleVoucher(
        username: String,
        pass: String,
        profileName: String,
        server: String,
        comment: String,
        boundMac: Boolean
    ) {
        viewModelScope.launch {
            val profile = profiles.value.find { it.name == profileName }
            val price = profile?.price ?: 200.0
            val validity = profile?.validityDays ?: 1
            val hours = profile?.allowedTimeHours ?: 24
            val dl = profile?.downloadLimitMb ?: 1024

            repository.addSingleVoucher(
                username = username,
                password = pass,
                profileName = profileName,
                type = _currentNetworkType.value,
                price = price,
                validityDays = validity,
                allowedTimeHours = hours,
                downloadLimitMb = dl,
                server = server,
                comment = comment,
                boundMac = boundMac
            )
            showNotification("تم إضافة الكرت $username بنجاح")
            _currentScreen.value = AppScreen.CARDS_LIST
        }
    }

    fun generateBatchVouchers(
        count: Int,
        profileName: String,
        userPatternDigits: Boolean,
        passPatternDigits: Boolean,
        userLen: Int,
        passLen: Int,
        prefix: String,
        suffix: String,
        price: Double,
        validityDays: Int,
        allowedHours: Int,
        downloadLimitMb: Long,
        server: String,
        addValidityComment: Boolean,
        bindFirstDevice: Boolean,
        andPrint: Boolean
    ) {
        viewModelScope.launch {
            val generated = repository.generateBatchVouchers(
                count = count,
                profileName = profileName,
                type = _currentNetworkType.value,
                userPatternOnlyDigits = userPatternDigits,
                passPatternOnlyDigits = passPatternDigits,
                userLength = userLen,
                passLength = passLen,
                prefix = prefix,
                suffix = suffix,
                price = price,
                validityDays = validityDays,
                allowedTimeHours = allowedHours,
                downloadLimitMb = downloadLimitMb,
                server = server,
                addValidityComment = addValidityComment,
                bindFirstDevice = bindFirstDevice
            )
            showNotification("تم إنشاء $count كرت بنجاح")
            if (andPrint) {
                setPrintPreviewVisible(true, generated)
            } else {
                _currentScreen.value = AppScreen.CARDS_LIST
            }
        }
    }

    fun deleteCard(card: VoucherCard) {
        viewModelScope.launch {
            repository.deleteCard(card.id)
            showNotification("تم حذف الكرت ${card.username}")
        }
    }

    fun addOrUpdateProfile(
        id: Long,
        name: String,
        timeHours: Int,
        validityDays: Int,
        price: Double,
        downloadLimitMb: Long,
        sharedUsers: Int = 1
    ) {
        viewModelScope.launch {
            val newProfile = NetworkProfile(
                id = id,
                name = name,
                type = _currentNetworkType.value,
                allowedTimeHours = timeHours,
                validityDays = validityDays,
                price = price,
                downloadLimitMb = downloadLimitMb,
                sharedUsers = sharedUsers
            )
            if (id == 0L) {
                repository.addProfile(newProfile)
                showNotification("تمت إضافة الباقة $name")
            } else {
                repository.updateProfile(newProfile)
                showNotification("تم تعديل الباقة $name")
            }
        }
    }

    fun deleteProfile(profile: NetworkProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile.id)
            showNotification("تم حذف الباقة ${profile.name}")
        }
    }

    fun saveTemplate(template: PrintTemplate) {
        viewModelScope.launch {
            if (template.id == 0L) {
                repository.addPrintTemplate(template)
            } else {
                repository.updatePrintTemplate(template)
            }
            showNotification("تم حفظ القالب بنجاح")
        }
    }

    fun rebootRouter() {
        viewModelScope.launch {
            repository.rebootRouter()
            showNotification("تم إرسال أمر إعادة تشغيل المايكروتك")
        }
    }

    fun backupRouter() {
        viewModelScope.launch {
            val fileName = repository.backupRouter()
            showNotification("تم إنشاء النسخة الاحتياطية: $fileName")
        }
    }

    fun cleanupExpiredCards() {
        viewModelScope.launch {
            val deleted = repository.clearUsedCards()
            showNotification("تم تنظيف $deleted كروت منتهية")
        }
    }

    fun deleteSavedConnection(connection: RouterConnection) {
        viewModelScope.launch {
            repository.deleteConnection(connection.id)
            showNotification("تم حذف الاتصال المحفوظ")
        }
    }

    fun addPosPoint(name: String, location: String, phone: String, balance: Double, cardsAssigned: Int) {
        viewModelScope.launch {
            repository.addPosPoint(
                PosPoint(
                    name = name,
                    location = location,
                    phone = phone,
                    balance = balance,
                    cardsAssigned = cardsAssigned
                )
            )
            showNotification("تمت إضافة نقطة البيع: $name")
        }
    }
}
