package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.NetworkProfile
import com.example.data.model.NetworkType
import com.example.ui.components.SmartTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfilesManagerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentType by viewModel.currentNetworkType.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val filteredProfiles = profiles.filter { it.type == currentType }

    var selectedProfileId by remember { mutableStateOf(0L) }
    var name by remember { mutableStateOf("") }
    var allowedHours by remember { mutableStateOf("") }
    var validityDays by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var downloadLimitMb by remember { mutableStateOf("") }

    val isUserManager = currentType == NetworkType.USER_MANAGER
    val screenTitle = if (isUserManager) "إدارة باقات اليوزرمانجر" else "إدارة باقات الهوتسبوت"

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = screenTitle,
                showBack = true,
                onBackClick = {
                    val back = if (isUserManager) AppScreen.USER_MANAGER_MENU else AppScreen.HOTSPOT_MENU
                    viewModel.navigateTo(back)
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
            // Add / Edit Form Card (Dark Navy Theme)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (selectedProfileId == 0L) "➕ إضافة باقة جديدة" else "✏️ تعديل باقة: $name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SmartCyanLight
                    )

                    // Row 1: Name & Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("الاسم [EngOnly]", color = TextSecondaryDark, fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("profile_name_input"),
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
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("السعر [Price]", color = TextSecondaryDark, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_price_input"),
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

                    // Row 2: Allowed Time, Validity Days, Download limit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = allowedHours,
                            onValueChange = { allowedHours = it },
                            label = { Text("الوقت [Hour]", color = TextSecondaryDark, fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            value = validityDays,
                            onValueChange = { validityDays = it },
                            label = { Text("الصلاحية [Days]", color = TextSecondaryDark, fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            value = downloadLimitMb,
                            onValueChange = { downloadLimitMb = it },
                            label = { Text("التحميل [MiB]", color = TextSecondaryDark, fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1.2f),
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

                    // Action Buttons: ➕ إضافة | 🔄 تحديث
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    viewModel.showNotification("يرجى إدخال اسم الباقة", isError = true)
                                    return@Button
                                }
                                viewModel.addOrUpdateProfile(
                                    id = 0L,
                                    name = name.trim(),
                                    timeHours = allowedHours.toIntOrNull() ?: 24,
                                    validityDays = validityDays.toIntOrNull() ?: 30,
                                    price = price.toDoubleOrNull() ?: 200.0,
                                    downloadLimitMb = downloadLimitMb.toLongOrNull() ?: 1024L
                                )
                                name = ""
                                allowedHours = ""
                                validityDays = ""
                                price = ""
                                downloadLimitMb = ""
                                selectedProfileId = 0L
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SmartGreenDark),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("add_profile_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("➕ إضافة", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                if (selectedProfileId == 0L) {
                                    viewModel.showNotification("يرجى اختيار باقة من الجدول لتعديلها", isError = true)
                                    return@Button
                                }
                                viewModel.addOrUpdateProfile(
                                    id = selectedProfileId,
                                    name = name.trim(),
                                    timeHours = allowedHours.toIntOrNull() ?: 24,
                                    validityDays = validityDays.toIntOrNull() ?: 30,
                                    price = price.toDoubleOrNull() ?: 200.0,
                                    downloadLimitMb = downloadLimitMb.toLongOrNull() ?: 1024L
                                )
                                selectedProfileId = 0L
                                name = ""
                                allowedHours = ""
                                validityDays = ""
                                price = ""
                                downloadLimitMb = ""
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("update_profile_button")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🔄 تحديث", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Profiles Table Header & List
            Text(
                text = "جدول عرض الباقات الحالية (${filteredProfiles.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SmartYellowLight
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProfiles) { prof ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedProfileId == prof.id) DarkNavyCard else DarkNavySurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedProfileId = prof.id
                                name = prof.name
                                allowedHours = prof.allowedTimeHours.toString()
                                validityDays = prof.validityDays.toString()
                                price = prof.price.toInt().toString()
                                downloadLimitMb = prof.downloadLimitMb.toString()
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prof.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${prof.price} ريال",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = SmartYellowLight
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = DarkNavyBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الوقت: ${prof.allowedTimeHours} ساعة",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = "الصلاحية: ${prof.validityDays} يوم",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                                Text(
                                    text = "التحميل: ${prof.downloadLimitMb} MiB",
                                    fontSize = 12.sp,
                                    color = SmartCyanLight
                                )
                                Text(
                                    text = "النشطين: ${prof.activeUsersCount}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SmartGreen
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        selectedProfileId = prof.id
                                        name = prof.name
                                        allowedHours = prof.allowedTimeHours.toString()
                                        validityDays = prof.validityDays.toString()
                                        price = prof.price.toInt().toString()
                                        downloadLimitMb = prof.downloadLimitMb.toString()
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = SmartCyan, modifier = Modifier.size(20.dp))
                                }

                                IconButton(
                                    onClick = { viewModel.deleteProfile(prof) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
