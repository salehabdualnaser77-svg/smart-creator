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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkType
import com.example.data.model.SalesReportRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsDialog(
    viewModel: MainViewModel
) {
    val showDialog by viewModel.showReportsDialog.collectAsState()
    val initialType by viewModel.selectedReportType.collectAsState()
    var selectedType by remember { mutableStateOf(initialType) }
    var selectedPeriod by remember { mutableStateOf(0) } // 0: اليوم, 1: هذا الأسبوع, 2: هذا الشهر, 3: الكل

    val reports by viewModel.salesReports.collectAsState()
    val filteredReports = reports.filter { it.type == selectedType }

    val totalCards = filteredReports.sumOf { it.cardsCount }
    val totalRevenue = filteredReports.sumOf { it.totalRevenue }

    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setReportsDialogVisible(false) },
            containerColor = DarkNavySurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جلب تقارير المبيعات والتحميلات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    IconButton(onClick = { viewModel.setReportsDialogVisible(false) }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondaryDark)
                    }
                }

                // Type Selector: Hotspot vs User Manager
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = selectedType == NetworkType.HOTSPOT,
                        onClick = { selectedType = NetworkType.HOTSPOT },
                        label = { Text("تقارير الهوتسبوت (Hotspot)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SmartMagenta,
                            selectedLabelColor = Color.White,
                            containerColor = DarkNavyInput,
                            labelColor = TextSecondaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedType == NetworkType.USER_MANAGER,
                        onClick = { selectedType = NetworkType.USER_MANAGER },
                        label = { Text("تقارير اليوزرمانجر (UM)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SmartCyanDark,
                            selectedLabelColor = Color.White,
                            containerColor = DarkNavyInput,
                            labelColor = TextSecondaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Period Filter Chips (اليوم - أسبوع - شهر)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("اليوم", "هذا الأسبوع", "هذا الشهر", "الكل").forEachIndexed { index, period ->
                        FilterChip(
                            selected = selectedPeriod == index,
                            onClick = { selectedPeriod = index },
                            label = { Text(period, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartBlue,
                                selectedLabelColor = Color.White,
                                containerColor = DarkNavyInput,
                                labelColor = TextSecondaryDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Financial Summary Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "إجمالي الإيرادات", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(
                                text = "${totalRevenue.toInt()} ريال",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SmartGreen
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            color = DarkNavyBorder
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "الكروت المباعة", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(
                                text = "$totalCards كرت",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SmartYellowLight
                            )
                        }
                    }
                }

                // Breakdown list
                Text(
                    text = "تفاصيل المبيعات حسب الباقة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SmartCyanLight
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredReports) { item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyInput),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.profileName,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${item.dateString} • ${item.cardsCount} كرت",
                                        fontSize = 12.sp,
                                        color = TextSecondaryDark
                                    )
                                }

                                Text(
                                    text = "${item.totalRevenue.toInt()} ريال",
                                    fontWeight = FontWeight.Bold,
                                    color = SmartYellowLight,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.showNotification("تم تصدير التقرير المالي بنجاح")
                        viewModel.setReportsDialogVisible(false)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تصدير التقرير PDF / Excel", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
