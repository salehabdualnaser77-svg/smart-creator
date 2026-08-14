package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.model.NetworkType
import com.example.ui.screens.*
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val notification by viewModel.notification.collectAsState()
                    val snackbarHostState = remember { SnackbarHostState() }

                    // Show notifications as SnackBar
                    LaunchedEffect(notification) {
                        notification?.let {
                            snackbarHostState.showSnackbar(
                                message = it.message,
                                duration = SnackbarDuration.Short
                            )
                            viewModel.clearNotification()
                        }
                    }

                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    containerColor = if (notification?.isError == true) Color(0xFFDC2626) else Color(0xFF1E293B),
                                    contentColor = Color.White
                                )
                            }
                        },
                        containerColor = DarkNavyBackground
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "ScreenTransition"
                            ) { screen ->
                                when (screen) {
                                    AppScreen.LOGIN -> LoginScreen(viewModel = viewModel)
                                    AppScreen.MAIN_DASHBOARD -> MainDashboardScreen(viewModel = viewModel)
                                    AppScreen.HOTSPOT_MENU -> SubMenuScreen(viewModel = viewModel, isUserManager = false)
                                    AppScreen.USER_MANAGER_MENU -> SubMenuScreen(viewModel = viewModel, isUserManager = true)
                                    AppScreen.ADD_SINGLE_CARD -> AddSingleCardScreen(viewModel = viewModel)
                                    AppScreen.ADD_BATCH_CARDS -> AddBatchCardsScreen(viewModel = viewModel)
                                    AppScreen.ACTIVE_HOSTS -> ActiveHostsScreen(viewModel = viewModel)
                                    AppScreen.PROFILES_MANAGER -> ProfilesManagerScreen(viewModel = viewModel)
                                    AppScreen.TEMPLATE_EDITOR -> TemplateEditorScreen(viewModel = viewModel)
                                    AppScreen.CARDS_LIST -> CardsListScreen(viewModel = viewModel)
                                    AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                                    AppScreen.POS_POINTS -> PosPointsScreen(viewModel = viewModel)
                                }
                            }

                            // Global Bottom Sheets & Dialogs
                            ReportsDialog(viewModel = viewModel)
                            PrintPreviewDialog(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
