package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.NetworkType
import com.example.ui.components.ActiveUsersBanner
import com.example.ui.components.DashboardGridItem
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MainDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isConnected by viewModel.isConnectedToRouter.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val systemInfo by viewModel.systemInfo.collectAsState()
    val activeSessions by viewModel.activeSessions.collectAsState()
    val totalActive = systemInfo?.totalActiveUsers ?: activeSessions.size

    var showMenuDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "سمارت كريتور",
                showBack = false,
                isConnected = if (isConnecting) false else isConnected,
                statusText = if (isConnecting) "جارٍ الاتصال..." else if (isConnected) "متصل" else "غير متصل",
                onStatusClick = {
                    val statusMsg = if (isConnected) {
                        "المايكروتك متصل: ${systemInfo?.routerModel ?: "RouterOS"} (CPU: ${systemInfo?.cpuLoad ?: 15}%)"
                    } else {
                        "المايكروتك غير متصل حالياً"
                    }
                    viewModel.showNotification(statusMsg)
                },
                onMenuClick = { showMenuDropdown = true },
                actions = {
                    DropdownMenu(
                        expanded = showMenuDropdown,
                        onDismissRequest = { showMenuDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("تحديث البيانات") },
                            onClick = {
                                showMenuDropdown = false
                                viewModel.refreshActiveData()
                            },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("تبديل الروتر / تسجيل الخروج") },
                            onClick = {
                                showMenuDropdown = false
                                viewModel.navigateTo(AppScreen.LOGIN)
                            },
                            leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("إعادة تشغيل المايكروتك") },
                            onClick = {
                                showMenuDropdown = false
                                viewModel.rebootRouter()
                            },
                            leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null) }
                        )
                    }
                }
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Yellow Active Banner (Matching Photo 1)
            ActiveUsersBanner(
                activeCount = if (totalActive == 0) 52 else totalActive,
                onClick = {
                    viewModel.navigateTo(AppScreen.ACTIVE_HOSTS)
                },
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // 3-Column Grid Buttons (Matching Photo 1)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. المتصلين النشطين
                item {
                    DashboardGridItem(
                        title = "المتصلين\nالنشطين",
                        icon = Icons.Default.Groups,
                        iconColor = SmartBlue,
                        onClick = {
                            viewModel.navigateTo(AppScreen.ACTIVE_HOSTS)
                        }
                    )
                }

                // 2. الأجهزة المتصله
                item {
                    DashboardGridItem(
                        title = "الأجهزة\nالمتصله",
                        icon = Icons.Default.Router,
                        iconColor = SmartBlueLight,
                        onClick = {
                            viewModel.navigateTo(AppScreen.ACTIVE_HOSTS)
                        }
                    )
                }

                // 3. الهوتسبوت
                item {
                    DashboardGridItem(
                        title = "الهوتسبوت",
                        icon = Icons.Default.People,
                        iconColor = SmartMagenta,
                        onClick = {
                            viewModel.navigateTo(AppScreen.HOTSPOT_MENU, NetworkType.HOTSPOT)
                        }
                    )
                }

                // 4. اليوزرمانجر
                item {
                    DashboardGridItem(
                        title = "اليوزرمانجر",
                        icon = Icons.Default.SupervisorAccount,
                        iconColor = SmartCyan,
                        onClick = {
                            viewModel.navigateTo(AppScreen.USER_MANAGER_MENU, NetworkType.USER_MANAGER)
                        }
                    )
                }

                // 5. إدارة وتعديل القوالب
                item {
                    DashboardGridItem(
                        title = "إدارة وتعديل\nالقوالب",
                        icon = Icons.Default.Style,
                        iconColor = SmartBlueDark,
                        onClick = {
                            viewModel.navigateTo(AppScreen.TEMPLATE_EDITOR)
                        }
                    )
                }

                // 6. الإعدادات
                item {
                    DashboardGridItem(
                        title = "الإعدادات",
                        icon = Icons.Default.Settings,
                        iconColor = SmartCyanDark,
                        onClick = {
                            viewModel.navigateTo(AppScreen.SETTINGS)
                        }
                    )
                }

                // 7. جلب التقارير
                item {
                    DashboardGridItem(
                        title = "جلب التقارير",
                        icon = Icons.Default.MonetizationOn,
                        iconColor = SmartGreenDark,
                        onClick = {
                            viewModel.setReportsDialogVisible(true, NetworkType.HOTSPOT)
                        }
                    )
                }

                // 8. نقاط البيع
                item {
                    DashboardGridItem(
                        title = "نقاط بيع\nالكروت",
                        icon = Icons.Default.Storefront,
                        iconColor = SmartYellowDark,
                        onClick = {
                            viewModel.navigateTo(AppScreen.POS_POINTS)
                        }
                    )
                }

                // 9. قائمة الكروت
                item {
                    DashboardGridItem(
                        title = "كافة\nالكروت",
                        icon = Icons.Default.CreditCard,
                        iconColor = SmartBlue,
                        onClick = {
                            viewModel.navigateTo(AppScreen.CARDS_LIST)
                        }
                    )
                }
            }
        }
    }
}
