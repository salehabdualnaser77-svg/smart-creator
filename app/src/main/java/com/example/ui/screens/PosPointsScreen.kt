package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PosPoint
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PosPointsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val posPoints by viewModel.posPoints.collectAsState()
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var commission by remember { mutableStateOf("10") }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "نقاط بيع وتوزيع الكروت",
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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add POS Form Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "إضافة نقطة بيع جديدة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SmartCyanLight
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("اسم النقطة / المحل", color = TextSecondaryDark) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("pos_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = commission,
                            onValueChange = { commission = it },
                            label = { Text("العمولة %", color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("الموقع / الحي", color = TextSecondaryDark) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("رقم الهاتف", color = TextSecondaryDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SmartCyan,
                                unfocusedBorderColor = DarkNavyBorder,
                                focusedContainerColor = DarkNavyInput,
                                unfocusedContainerColor = DarkNavyInput,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                viewModel.showNotification("يرجى إدخال اسم نقطة البيع", isError = true)
                                return@Button
                            }
                            viewModel.addPosPoint(
                                name = name,
                                location = location.ifBlank { "المركز الرئيسي" },
                                phone = phone.ifBlank { "غير محدد" },
                                balance = 0.0,
                                cardsAssigned = commission.toIntOrNull() ?: 10
                            )
                            name = ""
                            location = ""
                            phone = ""
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة نقطة البيع", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // POS List Header
            Text(
                text = "قائمة نقاط البيع الحالية (${posPoints.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SmartYellowLight
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(posPoints) { pos ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = pos.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${pos.location} • ${pos.phone}",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = "الكروت المخصصة: ${pos.cardsAssigned} كرت • الرصيد: ${pos.balance} ر.ي",
                                    fontSize = 11.sp,
                                    color = SmartCyanLight
                                )
                            }

                            Icon(
                                Icons.Default.Store,
                                contentDescription = null,
                                tint = SmartYellow,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
