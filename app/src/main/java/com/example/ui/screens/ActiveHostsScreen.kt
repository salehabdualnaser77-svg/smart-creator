package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveSession
import com.example.data.model.ConnectedHost
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ActiveHostsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = ACTIVE, 1 = HOST
    val activeSessions by viewModel.activeSessions.collectAsState()
    val connectedHosts by viewModel.connectedHosts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredSessions = activeSessions.filter {
        it.username.contains(searchQuery, ignoreCase = true) ||
                it.ipAddress.contains(searchQuery) ||
                it.macAddress.contains(searchQuery, ignoreCase = true)
    }

    val filteredHosts = connectedHosts.filter {
        it.ipAddress.contains(searchQuery) ||
                it.macAddress.contains(searchQuery, ignoreCase = true) ||
                it.comment.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "المتصلين والأجهزة (Hotspot)",
                showBack = true,
                onBackClick = { viewModel.navigateTo(AppScreen.MAIN_DASHBOARD) },
                actions = {
                    IconButton(onClick = { viewModel.refreshActiveData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث", tint = Color.White)
                    }
                }
            )
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tab Switcher (ACTIVE / HOST)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkNavySurface,
                contentColor = SmartCyan,
                indicator = {},
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "المتصلين النشطين (ACTIVE - ${activeSessions.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SmartCyan else TextSecondaryDark
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "الأجهزة المتصلة (HOST - ${connectedHosts.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SmartCyan else TextSecondaryDark
                        )
                    }
                )
            }

            // Center Counter Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (selectedTab == 0) SmartYellowDark else SmartBlueDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedTab == 0) "إجمالي المتصلين النشطين" else "إجمالي الأجهزة بالشبكة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = if (selectedTab == 0) "${activeSessions.size} مستخدم متصل" else "${connectedHosts.size} جهاز مربوط",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { viewModel.refreshActiveData() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تحديث", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث عن اسم، IP، أو MAC...", color = TextSecondaryDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SmartCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = TextSecondaryDark)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_search_input"),
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

            // Table / List Content
            if (selectedTab == 0) {
                // ACTIVE USERS TAB
                if (filteredSessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا يوجد متصلين مطابقين للبحث",
                            color = TextSecondaryDark,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredSessions) { session ->
                            ActiveSessionCard(
                                session = session,
                                onDisconnect = { viewModel.disconnectUser(session) }
                            )
                        }
                    }
                }
            } else {
                // HOSTS TAB
                if (filteredHosts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد أجهزة مطابقة للبحث",
                            color = TextSecondaryDark,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredHosts) { host ->
                            ConnectedHostCard(host = host)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveSessionCard(
    session: ActiveSession,
    onDisconnect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(SmartGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = session.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }

                FilledTonalButton(
                    onClick = onDisconnect,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("فصل", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DarkNavyBorder)

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "عنوان IP:", fontSize = 11.sp, color = TextSecondaryDark)
                    Text(text = session.ipAddress, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SmartCyanLight)
                }
                Column {
                    Text(text = "عنوان MAC:", fontSize = 11.sp, color = TextSecondaryDark)
                    Text(text = session.macAddress, fontSize = 12.sp, color = Color.White)
                }
                Column {
                    Text(text = "مدة الاتصال:", fontSize = 11.sp, color = TextSecondaryDark)
                    Text(text = session.uptime, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SmartYellowLight)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "الاستهلاك (In / Out):", fontSize = 11.sp, color = TextSecondaryDark)
                    Text(text = "${session.bytesInFormatted} / ${session.bytesOutFormatted}", fontSize = 12.sp, color = Color.White)
                }
                Column {
                    Text(text = "الوقت المتبقي:", fontSize = 11.sp, color = TextSecondaryDark)
                    Text(text = session.timeLeft, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartGreen)
                }
                Column {
                    Text(text = "السيرفر:", fontSize = 11.sp, color = TextSecondaryDark)
                    Text(text = session.server, fontSize = 12.sp, color = TextSecondaryDark)
                }
            }
        }
    }
}

@Composable
fun ConnectedHostCard(host: ConnectedHost) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (host.authorized) SmartGreen.copy(alpha = 0.2f) else DarkNavyInput),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (host.authorized) Icons.Default.Wifi else Icons.Default.WifiLock,
                        contentDescription = null,
                        tint = if (host.authorized) SmartGreen else SmartYellow
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = host.ipAddress,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = host.macAddress,
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                    if (host.comment.isNotBlank()) {
                        Text(
                            text = host.comment,
                            fontSize = 11.sp,
                            color = SmartCyanLight
                        )
                    }
                }
            }

            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = if (host.authorized) "مصرح" else if (host.bypassed) "مستثنى" else "غير مسجل",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (host.authorized) SmartGreenDark.copy(alpha = 0.3f) else DarkNavyInput,
                    labelColor = if (host.authorized) SmartGreen else SmartYellow
                )
            )
        }
    }
}
