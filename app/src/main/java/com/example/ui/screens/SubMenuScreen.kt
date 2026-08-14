package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.NetworkType
import com.example.ui.components.CyanActionCard
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.LightBackground
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SubMenuScreen(
    viewModel: MainViewModel,
    isUserManager: Boolean,
    modifier: Modifier = Modifier
) {
    val screenTitle = if (isUserManager) "كروت اليوزرمانجر" else "كروت الهوتسبوت"
    val networkType = if (isUserManager) NetworkType.USER_MANAGER else NetworkType.HOTSPOT

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = screenTitle,
                showBack = true,
                onBackClick = { viewModel.navigateTo(AppScreen.MAIN_DASHBOARD) }
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 1. إضافة مستخدم (فردي / VIP)
            item {
                CyanActionCard(
                    title = if (isUserManager) "اضافة مستخدم\nيوزرمانجر" else "اضافة مستخدم\nهوتسبوت",
                    icon = Icons.Default.PersonAdd,
                    onClick = {
                        viewModel.navigateTo(AppScreen.ADD_SINGLE_CARD, networkType)
                    }
                )
            }

            // 2. إضافة مجموعة كروت
            item {
                CyanActionCard(
                    title = if (isUserManager) "اضافة مجموعة كروت\nيوزرمانجر" else "اضافة مجموعة كروت\nهوتسبوت",
                    icon = Icons.Default.GroupAdd,
                    onClick = {
                        viewModel.navigateTo(AppScreen.ADD_BATCH_CARDS, networkType)
                    }
                )
            }

            // 3. بحث
            item {
                CyanActionCard(
                    title = "بحث",
                    icon = Icons.Default.Search,
                    onClick = {
                        viewModel.navigateTo(AppScreen.CARDS_LIST, networkType)
                    }
                )
            }

            // 4. كروت اليوزرمانجر / الهوتسبوت
            item {
                CyanActionCard(
                    title = if (isUserManager) "كروت\nاليوزرمانجر" else "كروت\nالهوتسبوت",
                    icon = Icons.Default.Groups,
                    onClick = {
                        viewModel.navigateTo(AppScreen.CARDS_LIST, networkType)
                    }
                )
            }

            // 5. إضافة وتعديل الباقة
            item {
                CyanActionCard(
                    title = "اضافة وتعديل\nالباقة",
                    icon = Icons.Default.AddBox,
                    onClick = {
                        viewModel.navigateTo(AppScreen.PROFILES_MANAGER, networkType)
                    }
                )
            }

            // 6. إدارة وتعديل القوالب
            item {
                CyanActionCard(
                    title = "إدارة وتعديل\nالقوالب",
                    icon = Icons.Default.Style,
                    onClick = {
                        viewModel.navigateTo(AppScreen.TEMPLATE_EDITOR, networkType)
                    }
                )
            }

            // 7. تقارير التحميلات
            item {
                CyanActionCard(
                    title = if (isUserManager) "تقارير التحميلات\n(UM)" else "تقارير التحميلات\n(Hotspot)",
                    icon = Icons.Default.Assessment,
                    onClick = {
                        viewModel.setReportsDialogVisible(true, networkType)
                    }
                )
            }

            // 8. تغيير وتعيين نقاط بيع الكروت
            item {
                CyanActionCard(
                    title = "تغيير وتعيين نقاط\nبيع الكروت",
                    icon = Icons.Default.ContactPage,
                    onClick = {
                        viewModel.navigateTo(AppScreen.POS_POINTS, networkType)
                    }
                )
            }
        }
    }
}
