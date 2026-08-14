package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouterConnection
import com.example.ui.components.FourDotsLogo
import com.example.ui.components.SecureConnectionConfigCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isConnecting by viewModel.isConnecting.collectAsState()
    val savedConnections by viewModel.savedConnections.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FourDotsLogo(size = 30)
                        Text(
                            text = "سمارت كريتور - إعدادات اتصال المايكروتك",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmartBlue,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Promo Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FourDotsLogo(size = 30)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تطبيق سمارت كريتور",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = SmartBlueDark
                            )
                        }

                        Text(
                            text = "إدارة شبكات المايكروتك، الهوتسبوت، واليوزرمانجر",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SmartYellowDark,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        val features = listOf(
                            "• تخزين آمن لبيانات الدخول (IP واسم المستخدم وكلمة السر) عبر DataStore",
                            "• دعم مباشر لبروتوكول RouterOS API و REST API المشفّر",
                            "• توليد وطباعة كروت الهوتسبوت واليوزرمانجر ومراقبة المتصلين"
                        )

                        features.forEach { feat ->
                            Text(
                                text = feat,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.5.dp),
                                textAlign = TextAlign.Right
                            )
                        }
                    }
                }
            }

            // Secure MikroTik Connection Config UI (DataStore Backed)
            item {
                SecureConnectionConfigCard(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Saved Connections List
            if (savedConnections.isNotEmpty()) {
                item {
                    Text(
                        text = "الشبكات والراوترات المحفوظة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SmartCyanLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        textAlign = TextAlign.Right
                    )
                }

                items(savedConnections) { conn ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("saved_conn_${conn.host}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.saveConnectionToDataStore(
                                            host = conn.host,
                                            port = conn.port,
                                            user = conn.username,
                                            pass = conn.password,
                                            autoLogin = conn.isAutoLogin
                                        )
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SmartBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Router,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = conn.host,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${conn.name} (${conn.username}) - منفذ ${conn.port}",
                                        fontSize = 12.sp,
                                        color = TextSecondaryDark
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.connectRouter(
                                            host = conn.host,
                                            port = conn.port,
                                            user = conn.username,
                                            pass = conn.password,
                                            saveConnection = true,
                                            isAutoLogin = conn.isAutoLogin
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "اتصال",
                                        tint = SmartGreen
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteSavedConnection(conn) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

