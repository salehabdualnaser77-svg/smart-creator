package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkType
import com.example.data.model.VoucherCard
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CardsListScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentType by viewModel.currentNetworkType.collectAsState()
    val allCards by viewModel.allCards.collectAsState()
    val filteredCards = allCards.filter { it.type == currentType }
    val searchQuery by viewModel.searchQuery.collectAsState()

    val isUserManager = currentType == NetworkType.USER_MANAGER
    val screenTitle = if (isUserManager) "كروت اليوزرمانجر (${filteredCards.size})" else "كروت الهوتسبوت (${filteredCards.size})"

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = screenTitle,
                showBack = true,
                onBackClick = {
                    val back = if (isUserManager) AppScreen.USER_MANAGER_MENU else AppScreen.HOTSPOT_MENU
                    viewModel.navigateTo(back)
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (filteredCards.isNotEmpty()) {
                                viewModel.setPrintPreviewVisible(true, filteredCards.take(20))
                            } else {
                                viewModel.showNotification("لا توجد كروت متاحة للطباعة", isError = true)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "طباعة الكروت", tint = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("بحث في الكروت (اسم، سريال، باقة)...", color = TextSecondaryDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SmartCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = TextSecondaryDark)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cards_search_input"),
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

            // Cards Summary Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إجمالي الكروت المعروضة: ${filteredCards.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmartCyanLight
                )

                FilledTonalButton(
                    onClick = {
                        val target = if (isUserManager) AppScreen.ADD_BATCH_CARDS else AppScreen.ADD_BATCH_CARDS
                        viewModel.navigateTo(target, currentType)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SmartBlueDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("توليد دفعة جديدة", fontSize = 12.sp, color = Color.White)
                }
            }

            // Cards Lazy Column
            if (filteredCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CreditCardOff, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد كروت مضافة في هذا القسم",
                            color = TextSecondaryDark,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCards) { card ->
                        VoucherCardItem(
                            card = card,
                            onDelete = { viewModel.deleteCard(card) },
                            onPrintSingle = { viewModel.setPrintPreviewVisible(true, listOf(card)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherCardItem(
    card: VoucherCard,
    onDelete: () -> Unit,
    onPrintSingle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SmartBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مستخدم: ${card.username}",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "كلمة السر: ${card.password}",
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = SmartYellowLight
                        )
                    }
                }

                Text(
                    text = "${card.price} ريال",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = SmartGreen
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = DarkNavyBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "الباقة: ${card.profileName} (${card.allowedTimeHours}h / ${card.validityDays}d)",
                        fontSize = 11.sp,
                        color = SmartCyanLight
                    )
                    Text(
                        text = "SN: ${card.serialNumber}",
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row {
                    IconButton(onClick = onPrintSingle) {
                        Icon(Icons.Default.Print, contentDescription = "طباعة", tint = SmartCyan, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
