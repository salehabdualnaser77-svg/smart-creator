package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoucherCard
import com.example.ui.components.FourDotsLogo
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintPreviewDialog(
    viewModel: MainViewModel
) {
    val showDialog by viewModel.showPrintPreviewDialog.collectAsState()
    val previewCards by viewModel.previewCards.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setPrintPreviewVisible(false) },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معاينة طباعة الكروت (${previewCards.size} كرت)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { viewModel.setPrintPreviewVisible(false) }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ورقة الطباعة A4 جاهزة للإرسال إلى الطابعة أو الحفظ كملف PDF",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Printable sheet container (white paper mockup)
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(previewCards) { card ->
                                PrintableCardTile(card = card)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showNotification("تم إرسال ${previewCards.size} كرت إلى الطابعة بنجاح")
                        viewModel.setPrintPreviewVisible(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طباعة الآن", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.showNotification("تم حفظ الكروت في مستند PDF")
                        viewModel.setPrintPreviewVisible(false)
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصدير PDF")
                }
            }
        )
    }
}

@Composable
fun PrintableCardTile(card: VoucherCard) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(SmartBlueDark, SmartBlue)
                )
            )
            .border(1.dp, SmartYellow.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Logo & Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FourDotsLogo(size = 18)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Smart WiFi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = "${card.price.toInt()} ريال",
                    color = SmartYellow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }

            // Middle: User & Pass
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "المستخدم", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
                    Text(
                        text = card.username,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "كلمة السر", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
                    Text(
                        text = card.password,
                        color = SmartYellowLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Footer: Validity & Serial
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${card.allowedTimeHours}h / ${card.validityDays}d",
                    color = SmartCyanLight,
                    fontSize = 8.sp
                )
                Text(
                    text = "SN: ${card.serialNumber}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
