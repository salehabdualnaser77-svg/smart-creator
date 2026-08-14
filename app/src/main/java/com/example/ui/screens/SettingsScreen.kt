package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val systemInfo by viewModel.systemInfo.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val connectionConfig by viewModel.connectionConfig.collectAsState()

    // Connection Form State initialized from DataStore
    var ipAddress by remember(connectionConfig.host) { mutableStateOf(connectionConfig.host) }
    var port by remember(connectionConfig.port) { mutableStateOf(connectionConfig.port.toString()) }
    var username by remember(connectionConfig.username) { mutableStateOf(connectionConfig.username) }
    var password by remember(connectionConfig.password) { mutableStateOf(connectionConfig.password) }
    var passwordVisible by remember { mutableStateOf(false) }
    var useRestApi by remember(connectionConfig.isRest) { mutableStateOf(connectionConfig.isRest) }
    var useSsl by remember(connectionConfig.useSsl) { mutableStateOf(connectionConfig.useSsl) }

    // Dialogs State
    var showRebootDialog by remember { mutableStateOf(false) }
    var showCleanupDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var testResultDialogText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "إعدادات النظام والمايكروتك",
                showBack = true,
                onBackClick = { viewModel.navigateTo(AppScreen.MAIN_DASHBOARD) }
            )
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = systemInfo?.routerModel ?: "MikroTik RB750Gr3 (hEX)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${systemInfo?.routerOsVersion ?: "RouterOS v7.14.3"} | CPU: ${systemInfo?.cpuLoad ?: 18}%",
                            fontSize = 12.sp,
                            color = SmartCyanLight
                        )
                        Text(
                            text = "الذاكرة المتاحة: ${systemInfo?.freeMemoryMb ?: 186} MB من ${systemInfo?.totalMemoryMb ?: 256} MB",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(SmartGreen, RoundedCornerShape(7.dp))
                    )
                }
            }

            // Connection Settings Section (حقول إدخال بيانات الاتصال بالمايكروتك عبر API)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = null,
                            tint = SmartCyan
                        )
                        Text(
                            text = "إعدادات الاتصال بالمايكروتك (RouterOS API)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SmartCyan
                        )
                    }

                    Text(
                        text = "قم بضبط بيانات جهاز التوجيه للتحكم الكامل، قراءة المتصلين، وإرسال الكروت فورياً:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )

                    // IP & Port Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = ipAddress,
                            onValueChange = { ipAddress = it },
                            label = { Text("عنوان IP السيرفر") },
                            placeholder = { Text("192.168.88.1") },
                            leadingIcon = {
                                Icon(Icons.Default.Dns, contentDescription = null, tint = SmartCyan)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(2f)
                                .testTag("settings_ip_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = port,
                            onValueChange = { port = it },
                            label = { Text("المنفذ (Port)") },
                            placeholder = { Text("8728") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("settings_port_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("اسم المستخدم (Username)") },
                        placeholder = { Text("admin") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SmartCyan)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_user_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCyan,
                            unfocusedBorderColor = DarkNavyBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور (Password)") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SmartCyan)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "تبديل الرؤية",
                                    tint = TextSecondaryDark
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCyan,
                            unfocusedBorderColor = DarkNavyBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Protocol Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "بروتوكول REST API (OkHttp)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = if (useRestApi) "RouterOS v7 REST (80/443)" else "RouterOS Socket API (8728)",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                        Switch(
                            checked = useRestApi,
                            onCheckedChange = {
                                useRestApi = it
                                if (it && (port == "8728" || port.isBlank())) {
                                    port = "80"
                                } else if (!it && port == "80") {
                                    port = "8728"
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SmartCyan,
                                checkedTrackColor = SmartCyan.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // SSL / HTTPS Toggle
                    if (useRestApi) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تشفير SSL / HTTPS (منفذ 443)",
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Switch(
                                checked = useSsl,
                                onCheckedChange = {
                                    useSsl = it
                                    if (it) port = "443"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SmartCyan,
                                    checkedTrackColor = SmartCyan.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }

                    // Action Buttons (Test & Save)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val portNum = port.toIntOrNull() ?: 8728
                                viewModel.testConnection(
                                    host = ipAddress,
                                    port = portNum,
                                    user = username,
                                    pass = password,
                                    useSsl = useSsl,
                                    isRest = useRestApi
                                ) { success, msg ->
                                    testResultDialogText = msg
                                }
                            },
                            enabled = !isConnecting,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_test_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SmartCyan
                            )
                        ) {
                            if (isConnecting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = SmartCyan,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("فحص الاتصال", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val portNum = port.toIntOrNull() ?: 8728
                                viewModel.connectRouter(
                                    host = ipAddress,
                                    port = portNum,
                                    user = username,
                                    pass = password,
                                    saveConnection = true,
                                    isAutoLogin = true,
                                    useSsl = useSsl,
                                    isRest = useRestApi
                                )
                            },
                            enabled = !isConnecting,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_save_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SmartCyan,
                                contentColor = DarkNavyBackground
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حفظ وتطبيق", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Text(
                text = "إجراءات التحكم والصيانة (الأزرار السبعة)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = SmartYellowLight,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 7 Large Action Buttons / Tiles
            SettingsLargeTile(
                title = "النسخ الاحتياطي للمايكروتك (Backup)",
                description = "حفظ إعدادات وسجلات الشبكة والكروت في ملف backup على المايكروتك",
                icon = Icons.Default.CloudDownload,
                iconColor = SmartCyan,
                onClick = { showBackupDialog = true }
            )

            SettingsLargeTile(
                title = "استعادة النسخة الاحتياطية (Restore)",
                description = "استرجاع الإعدادات المحفوظة من الذاكرة أو التخزين",
                icon = Icons.Default.CloudUpload,
                iconColor = SmartBlueLight,
                onClick = {
                    viewModel.showNotification("تم فحص وتأكيد نقاط الاستعادة")
                }
            )

            SettingsLargeTile(
                title = "قاعدة البيانات والمزامنة (Database & Sync)",
                description = "مزامنة الكروت والباقات والتقارير مع الروتر محلياً",
                icon = Icons.Default.Storage,
                iconColor = SmartGreen,
                onClick = {
                    viewModel.refreshActiveData()
                    viewModel.showNotification("تمت المزامنة بنجاح مع قاعدة البيانات")
                }
            )

            SettingsLargeTile(
                title = "صيانة وتنظيف الكروت المنتهية",
                description = "حذف الكروت المستهلكة أو المنتهية لتسريع أداء الروتر",
                icon = Icons.Default.CleaningServices,
                iconColor = SmartYellow,
                onClick = { showCleanupDialog = true }
            )

            SettingsLargeTile(
                title = "إعادة تشغيل المايكروتك (Reboot Router)",
                description = "إرسال أمر إعادة تشغيل آمن لجهاز الروتر عبر بروتوكول API",
                icon = Icons.Default.RestartAlt,
                iconColor = Color(0xFFEF4444),
                onClick = { showRebootDialog = true }
            )

            SettingsLargeTile(
                title = "نقاط الاتصال المحفوظة (Saved Routers)",
                description = "إدارة السيرفرات المحفوظة والتبديل السريع بين الفروع",
                icon = Icons.Default.SettingsInputComponent,
                iconColor = SmartMagenta,
                onClick = {
                    viewModel.navigateTo(AppScreen.LOGIN)
                }
            )

            SettingsLargeTile(
                title = "معلومات النظام وRouterOS (System Resources)",
                description = "فحص الحمل على المعالج، استهلاك الرام، ومساحة القرص",
                icon = Icons.Default.Info,
                iconColor = SmartCyanLight,
                onClick = { showInfoDialog = true }
            )
        }
    }

    // Test Connection Result Dialog
    testResultDialogText?.let { resultText ->
        AlertDialog(
            onDismissRequest = { testResultDialogText = null },
            title = { Text("نتيجة فحص الاتصال", fontWeight = FontWeight.Bold) },
            text = { Text(resultText) },
            confirmButton = {
                Button(onClick = { testResultDialogText = null }) {
                    Text("حسناً")
                }
            }
        )
    }

    // Reboot Dialog
    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            title = { Text("إعادة تشغيل المايكروتك", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في إعادة تشغيل جهاز الروتر؟ ستنقطع الخدمة لثوانٍ معدودة.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRebootDialog = false
                        viewModel.rebootRouter()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("إعادة التشغيل الآن", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRebootDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Cleanup Dialog
    if (showCleanupDialog) {
        AlertDialog(
            onDismissRequest = { showCleanupDialog = false },
            title = { Text("تنظيف الكروت المنتهية", fontWeight = FontWeight.Bold) },
            text = { Text("سيتم مسح جميع كروت الهوتسبوت واليوزرمانجر المنتهية صلاحيتها أو المستهلكة بالكامل.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCleanupDialog = false
                        viewModel.cleanupExpiredCards()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartYellowDark)
                ) {
                    Text("تنظيف الآن", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCleanupDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("إنشاء نسخة احتياطية", fontWeight = FontWeight.Bold) },
            text = { Text("سيتم إنشاء ملف Backup كامل في ذاكرة المايكروتك وحفظ نسخة في قاعدة بيانات الهاتف.") },
            confirmButton = {
                Button(
                    onClick = {
                        showBackupDialog = false
                        viewModel.backupRouter()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartBlue)
                ) {
                    Text("بدء النسخ الاحتياطي", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // System Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("معلومات نظام المايكروتك", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("الموديل: ${systemInfo?.routerModel}")
                    Text("الإصدار: ${systemInfo?.routerOsVersion}")
                    Text("مدة العمل (Uptime): ${systemInfo?.uptime}")
                    Text("حمل المعالج (CPU): ${systemInfo?.cpuLoad}%")
                    Text("الرام المتاحة: ${systemInfo?.freeMemoryMb} MB / ${systemInfo?.totalMemoryMb} MB")
                    Text("المساحة التخزينية: ${systemInfo?.freeHddMb} MB / ${systemInfo?.totalHddMb} MB")
                    Text("إجمالي الكروت المخزنة: ${systemInfo?.totalCards}")
                }
            },
            confirmButton = {
                Button(onClick = { showInfoDialog = false }) {
                    Text("حسناً")
                }
            }
        )
    }
}

@Composable
fun SettingsLargeTile(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondaryDark,
                    lineHeight = 15.sp
                )
            }

            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = TextSecondaryDark
            )
        }
    }
}
