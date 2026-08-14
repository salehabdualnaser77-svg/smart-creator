package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MikrotikConnectionConfig
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecureConnectionConfigCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onConnectedSuccess: (() -> Unit)? = null
) {
    val connectionConfig by viewModel.connectionConfig.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()

    var host by remember(connectionConfig.host) { mutableStateOf(connectionConfig.host) }
    var port by remember(connectionConfig.port) { mutableStateOf(connectionConfig.port.toString()) }
    var username by remember(connectionConfig.username) { mutableStateOf(connectionConfig.username) }
    var password by remember(connectionConfig.password) { mutableStateOf(connectionConfig.password) }
    var autoLogin by remember(connectionConfig.autoLogin) { mutableStateOf(connectionConfig.autoLogin) }
    var useSsl by remember(connectionConfig.useSsl) { mutableStateOf(connectionConfig.useSsl) }
    var isRest by remember(connectionConfig.isRest) { mutableStateOf(connectionConfig.isRest) }

    var passwordVisible by remember { mutableStateOf(false) }
    var showAdvancedOptions by remember { mutableStateOf(false) }
    var testResultText by remember { mutableStateOf<String?>(null) }
    var testIsSuccess by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }

    val isIpValid = remember(host) {
        val trimmed = host.trim()
        trimmed.isNotEmpty() && (trimmed.count { it == '.' } == 3 || !trimmed.contains(" ") && trimmed.length >= 3)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Brush.linearGradient(listOf(SmartCyan.copy(alpha = 0.5f), SmartBlue.copy(alpha = 0.2f))), RoundedCornerShape(20.dp))
            .testTag("secure_connection_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Security Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(SmartCyan, SmartBlue))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "الأمان",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "إعدادات اتصال المايكروتك",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SmartGreen)
                            )
                            Text(
                                text = "Jetpack DataStore تخزين آمن",
                                fontSize = 11.sp,
                                color = SmartCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // DataStore status badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmartCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = SmartCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "DataStore",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            HorizontalDivider(color = DarkNavyBorder.copy(alpha = 0.8f))

            // IP Address Input with Quick Presets
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عنوان الـ IP أو اسم النطاق:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    if (!isIpValid && host.isNotBlank()) {
                        Text(
                            text = "تنسيق IP غير قياسي",
                            fontSize = 11.sp,
                            color = SmartRed
                        )
                    }
                }

                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    placeholder = { Text("مثال: 192.168.88.1 أو 5.5.5.5", color = TextSecondaryDark.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Router, contentDescription = null, tint = SmartCyan)
                    },
                    trailingIcon = {
                        if (host.isNotBlank()) {
                            IconButton(onClick = { host = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextSecondaryDark)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("router_ip_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartCyan,
                        unfocusedBorderColor = DarkNavyBorder,
                        focusedContainerColor = DarkNavyInput,
                        unfocusedContainerColor = DarkNavyInput,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Fast IP Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("192.168.88.1", "5.5.5.5", "10.0.0.1", "192.168.1.1").forEach { ipPreset ->
                        SuggestionChip(
                            onClick = { host = ipPreset },
                            label = { Text(ipPreset, fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (host == ipPreset) SmartBlue.copy(alpha = 0.3f) else DarkNavyCard,
                                labelColor = if (host == ipPreset) SmartCyan else TextSecondaryDark
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = if (host == ipPreset) SmartCyan else DarkNavyBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Port and Protocol Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("المنفذ (Port)", color = TextSecondaryDark) },
                    leadingIcon = {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = SmartCyan)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("router_port_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartCyan,
                        unfocusedBorderColor = DarkNavyBorder,
                        focusedContainerColor = DarkNavyInput,
                        unfocusedContainerColor = DarkNavyInput,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Port presets
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = port == "8728" && !useSsl && !isRest,
                            onClick = {
                                port = "8728"
                                useSsl = false
                                isRest = false
                            },
                            label = { Text("8728 API", fontSize = 10.sp) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = port == "8729" && useSsl,
                            onClick = {
                                port = "8729"
                                useSsl = true
                                isRest = false
                            },
                            label = { Text("8729 SSL", fontSize = 10.sp) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = port == "80" && isRest,
                            onClick = {
                                port = "80"
                                isRest = true
                                useSsl = false
                            },
                            label = { Text("80 REST", fontSize = 10.sp) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = port == "443" && isRest && useSsl,
                            onClick = {
                                port = "443"
                                isRest = true
                                useSsl = true
                            },
                            label = { Text("443 HTTPS", fontSize = 10.sp) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Username Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "اسم المستخدم (Username):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("مثال: admin", color = TextSecondaryDark.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = SmartCyan)
                    },
                    trailingIcon = {
                        if (username.isNotBlank() && username != "admin") {
                            TextButton(onClick = { username = "admin" }) {
                                Text("admin", fontSize = 11.sp, color = SmartCyan)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("router_username_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartCyan,
                        unfocusedBorderColor = DarkNavyBorder,
                        focusedContainerColor = DarkNavyInput,
                        unfocusedContainerColor = DarkNavyInput,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Password Input with Security Badge & Visibility Toggle
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "كلمة المرور (Password):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = if (password.isEmpty()) "فارغة (بدون كلمة سر)" else "مخزنة مشفرة محلياً",
                        fontSize = 11.sp,
                        color = if (password.isEmpty()) SmartYellow else SmartGreen
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("أدخل كلمة مرور الراوتر", color = TextSecondaryDark.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = SmartCyan)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                tint = if (passwordVisible) SmartCyan else TextSecondaryDark
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("router_password_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartCyan,
                        unfocusedBorderColor = DarkNavyBorder,
                        focusedContainerColor = DarkNavyInput,
                        unfocusedContainerColor = DarkNavyInput,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Advanced Options Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showAdvancedOptions = !showAdvancedOptions }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = SmartCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "خيارات الأمان والاتصال المتقدمة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SmartCyan
                    )
                }
                Icon(
                    imageVector = if (showAdvancedOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = SmartCyan
                )
            }

            // Advanced Toggles Content
            AnimatedVisibility(visible = showAdvancedOptions) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // SSL Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("تشفير SSL / TLS الآمن", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("تأمين الاتصال عبر منفذ 8729 أو 443", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Switch(
                                checked = useSsl,
                                onCheckedChange = {
                                    useSsl = it
                                    if (it && port == "8728") port = "8729"
                                    if (!it && port == "8729") port = "8728"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SmartCyan,
                                    uncheckedThumbColor = TextSecondaryDark,
                                    uncheckedTrackColor = DarkNavyInput
                                )
                            )
                        }

                        HorizontalDivider(color = DarkNavyBorder.copy(alpha = 0.5f))

                        // REST API Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("استخدام REST API (RouterOS v7+)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("بروتوكول HTTP/HTTPS REST الحديث", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Switch(
                                checked = isRest,
                                onCheckedChange = {
                                    isRest = it
                                    if (it && port == "8728") port = if (useSsl) "443" else "80"
                                    if (!it && (port == "80" || port == "443")) port = if (useSsl) "8729" else "8728"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SmartCyan,
                                    uncheckedThumbColor = TextSecondaryDark,
                                    uncheckedTrackColor = DarkNavyInput
                                )
                            )
                        }

                        HorizontalDivider(color = DarkNavyBorder.copy(alpha = 0.5f))

                        // Auto-login Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("دخول آلي تلقائي", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("تسجيل الدخول فوراً بالبيانات المخزنة", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                            Switch(
                                checked = autoLogin,
                                onCheckedChange = { autoLogin = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SmartGreen,
                                    uncheckedThumbColor = TextSecondaryDark,
                                    uncheckedTrackColor = DarkNavyInput
                                )
                            )
                        }
                    }
                }
            }

            // Connection Test Result Display
            testResultText?.let { resultMsg ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (testIsSuccess) SmartGreenDark.copy(alpha = 0.25f) else SmartRedDark.copy(alpha = 0.25f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (testIsSuccess) SmartGreen else SmartRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (testIsSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (testIsSuccess) SmartGreen else SmartRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = resultMsg,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Last connection timestamp notice
            if (connectionConfig.lastConnected > 0) {
                val dateStr = remember(connectionConfig.lastConnected) {
                    val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
                    sdf.format(Date(connectionConfig.lastConnected))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "آخر اتصال ناجح: $dateStr",
                        fontSize = 11.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            // Primary Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Test Connection Button
                OutlinedButton(
                    onClick = {
                        isTesting = true
                        testResultText = null
                        val portInt = port.toIntOrNull() ?: 8728
                        viewModel.testConnection(
                            host = host.trim(),
                            port = portInt,
                            user = username.trim(),
                            pass = password,
                            useSsl = useSsl,
                            isRest = isRest
                        ) { success, message ->
                            isTesting = false
                            testIsSuccess = success
                            testResultText = message
                        }
                    },
                    enabled = !isTesting && !isConnecting,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SmartCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SmartCyan),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("test_connection_btn")
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = SmartCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فحص الاتصال", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Save to DataStore Button
                Button(
                    onClick = {
                        val portInt = port.toIntOrNull() ?: 8728
                        viewModel.saveConnectionToDataStore(
                            host = host,
                            port = portInt,
                            user = username,
                            pass = password,
                            autoLogin = autoLogin,
                            useSsl = useSsl,
                            isRest = isRest
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SmartBlueDark),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_datastore_btn")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حفظ في DataStore", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Big Connect & Launch Button
            Button(
                onClick = {
                    val portInt = port.toIntOrNull() ?: 8728
                    viewModel.connectRouter(
                        host = host.trim(),
                        port = portInt,
                        user = username.trim(),
                        pass = password,
                        saveConnection = true,
                        isAutoLogin = autoLogin,
                        useSsl = useSsl,
                        isRest = isRest
                    )
                    onConnectedSuccess?.invoke()
                },
                enabled = !isConnecting && !isTesting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("connect_mikrotik_main_btn")
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("جاري الاتصال والتحقق...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Icon(Icons.Default.Login, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("اتصال بجهاز المايكروتك ودخول", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Clear DataStore button (Discrete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        viewModel.clearDataStoreCredentials()
                        host = "192.168.88.1"
                        port = "8728"
                        username = "admin"
                        password = ""
                        autoLogin = false
                        useSsl = false
                        isRest = false
                        testResultText = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "إعادة ضبط ومسح البيانات المحفوظة من DataStore",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }
    }
}
